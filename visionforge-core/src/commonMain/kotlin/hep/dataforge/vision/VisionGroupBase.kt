package hep.dataforge.vision

import hep.dataforge.names.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


/**
 * Abstract implementation of mutable group of [Vision]
 */
@Serializable
@SerialName("vision.group")
public open class VisionGroupBase : VisionBase(), MutableVisionGroup {

    //protected abstract val _children: MutableMap<NameToken, T>

    @SerialName("children")
    private val childrenInternal = LinkedHashMap<NameToken, Vision>()

    /**
     * A map of top level named children
     */
    override val children: Map<NameToken, Vision> get() = childrenInternal

    final override var styleSheet: StyleSheet? = null
        private set

    /**
     * Update or create stylesheet
     */
    public open fun styleSheet(block: StyleSheet.() -> Unit) {
        if (styleSheet == null) {
            styleSheet = StyleSheet(this@VisionGroupBase)
        }
        styleSheet!!.block()
    }

    override fun propertyChanged(name: Name) {
        super.propertyChanged(name)
        for (obj in this) {
            obj.propertyChanged(name)
        }
    }

    private data class StructureChangeListener(val owner: Any?, val callback: (NameToken, Vision?) -> Unit)

    @Transient
    private val structureChangeListeners = HashSet<StructureChangeListener>()

    /**
     * Add listener for children change
     */
    override fun onChildrenChange(owner: Any?, action: (NameToken, Vision?) -> Unit) {
        structureChangeListeners.add(
            StructureChangeListener(
                owner,
                action
            )
        )
    }

    /**
     * Remove children change listener
     */
    override fun removeChildrenChangeListener(owner: Any?) {
        structureChangeListeners.removeAll { it.owner === owner }
    }

    /**
     * Propagate children change event upwards
     */
    protected fun childrenChanged(name: NameToken, child: Vision?) {
        structureChangeListeners.forEach { it.callback(name, child) }
    }

    /**
     * Remove a child with given name token
     */
    public fun removeChild(token: NameToken) {
        childrenInternal.remove(token)
    }

    /**
     * Add a static child. Statics could not be found by name, removed or replaced. Changing statics also do not trigger events.
     */
    protected open fun addStatic(child: Vision): Unit {
        attach(NameToken("@static", index = child.hashCode().toString()), child)
    }

    protected open fun createGroup(): VisionGroupBase = VisionGroupBase()

    /**
     * Set parent for given child and attach it
     */
    private fun attach(token: NameToken, child: Vision) {
        if (child.parent == null) {
            child.parent = this
            childrenInternal[token] = child
        } else if (child.parent !== this) {
            error("Can't reassign existing parent for $child")
        }
    }

    /**
     * Recursively create a child group
     */
    private fun createGroups(name: Name): VisionGroupBase {
        return when {
            name.isEmpty() -> error("Should be unreachable")
            name.length == 1 -> {
                val token = name.tokens.first()
                when (val current = children[token]) {
                    null -> createGroup().also { child ->
                        attach(token, child)
                    }
                    is VisionGroupBase -> current
                    else -> error("Can't create group with name $name because it exists and not a group")
                }
            }
            else -> createGroups(name.tokens.first().asName()).createGroups(name.cutFirst())
        }
    }

    /**
     * Add named or unnamed child to the group. If key is null the child is considered unnamed. Both key and value are not
     * allowed to be null in the same time. If name is present and [child] is null, the appropriate element is removed.
     */
    override fun set(name: Name, child: Vision?): Unit {
        when {
            name.isEmpty() -> {
                if (child != null) {
                    addStatic(child)
                }
            }
            name.length == 1 -> {
                val token = name.tokens.first()
                if (child == null) {
                    removeChild(token)
                } else {
                    attach(token, child)
                }
                childrenChanged(token, child)
            }
            else -> {
                //TODO add safety check
                val parent = (get(name.cutLast()) as? MutableVisionGroup) ?: createGroups(name.cutLast())
                parent[name.tokens.last().asName()] = child
            }
        }
    }

    override fun update(change: Vision) {
        if (change is VisionGroup) {
            //update stylesheet
            val changeStyleSheet = change.styleSheet
            if (changeStyleSheet != null) {
                styleSheet {
                    update(changeStyleSheet)
                }
            }
            change.children.forEach { (token, child) ->
                when {
                    child is NullVision -> removeChild(token)
                    children.containsKey(token) -> children[token]!!.update(child)
                    else -> attach(token, child)
                }
            }
        }
        super.update(change)
    }
}