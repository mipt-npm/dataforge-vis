@file:UseSerializers(
    Point3DSerializer::class
)

package hep.dataforge.vision.spatial

import hep.dataforge.meta.Config
import hep.dataforge.names.Name
import hep.dataforge.names.NameToken
import hep.dataforge.names.asName
import hep.dataforge.vision.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.collections.set

interface PrototypeHolder {
    val parent: VisionGroup?
    val prototypes: MutableVisionGroup?
}

/**
 * Represents 3-dimensional Visual Group
 */
@Serializable
@SerialName("group.3d")
class VisionGroup3D : AbstractVisionGroup(), Vision3D, PrototypeHolder {

    override var styleSheet: StyleSheet? = null

    /**
     * A container for templates visible inside this group
     */
    @Serializable(PrototypesSerializer::class)
    override var prototypes: MutableVisionGroup? = null
        private set

    /**
     * Create or edit prototype node as a group
     */
    fun prototypes(builder: MutableVisionGroup.() -> Unit): Unit {
        (prototypes ?: Prototypes().also {
            prototypes = it
            attach(it)
        }).run(builder)
    }

    //FIXME to be lifted to AbstractVisualGroup after https://github.com/Kotlin/kotlinx.serialization/issues/378 is fixed
    override var ownProperties: Config? = null

    override var position: Point3D? = null
    override var rotation: Point3D? = null
    override var scale: Point3D? = null

    @SerialName("children")
    private val _children = HashMap<NameToken, Vision>()
    override val children: Map<NameToken, Vision> get() = _children

    override fun attachChildren() {
        prototypes?.parent = this
        prototypes?.attachChildren()
        super.attachChildren()
    }

    override fun removeChild(token: NameToken) {
        _children.remove(token)?.apply { parent = null }
    }

    override fun setChild(token: NameToken, child: Vision) {
        _children[token] = child
    }

//    /**
//     * TODO add special static group to hold statics without propagation
//     */
//    override fun addStatic(child: VisualObject) = setChild(NameToken("@static(${child.hashCode()})"), child)

    override fun createGroup(): VisionGroup3D = VisionGroup3D()


    companion object {
//        val PROTOTYPES_KEY = NameToken("@prototypes")

        fun parseJson(json: String): VisionGroup3D =
            SpatialVisionManager.json.parse(serializer(), json).also { it.attachChildren() }
    }
}

/**
 * Ger a prototype redirecting the request to the parent if prototype is not found
 */
tailrec fun PrototypeHolder.getPrototype(name: Name): Vision3D? =
    prototypes?.get(name) as? Vision3D ?: (parent as? PrototypeHolder)?.getPrototype(name)

/**
 * Define a group with given [name], attach it to this parent and return it.
 */
fun MutableVisionGroup.group(name: String = "", action: VisionGroup3D.() -> Unit = {}): VisionGroup3D =
    VisionGroup3D().apply(action).also {
        set(name, it)
    }

internal class Prototypes(
    override var children: MutableMap<NameToken, Vision> = LinkedHashMap()
) : AbstractVisionGroup(), MutableVisionGroup, PrototypeHolder {

    override var styleSheet: StyleSheet?
        get() = null
        set(_) {
            error("Can't define stylesheet for prototypes block")
        }

    override fun removeChild(token: NameToken) {
        children.remove(token)
        childrenChanged(token.asName(), null)
    }

    override fun setChild(token: NameToken, child: Vision) {
        children[token] = child
    }

    override fun createGroup() = SimpleVisionGroup()

    override var ownProperties: Config?
        get() = null
        set(_) {
            error("Can't define properties for prototypes block")
        }

    override val prototypes: MutableVisionGroup get() = this

    override fun attachChildren() {
        children.values.forEach {
            it.parent = parent
            (it as? VisionGroup)?.attachChildren()
        }
    }
}