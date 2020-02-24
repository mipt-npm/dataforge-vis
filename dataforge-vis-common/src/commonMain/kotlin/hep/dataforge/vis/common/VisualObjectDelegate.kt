package hep.dataforge.vis.common

import hep.dataforge.meta.*
import hep.dataforge.names.Name
import hep.dataforge.names.asName
import hep.dataforge.values.Value
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * A delegate for display object properties
 */
class VisualObjectDelegate(
    val key: Name?,
    val default: MetaItem<*>?,
    val inherited: Boolean
) : ReadWriteProperty<VisualObject, MetaItem<*>?> {
    override fun getValue(thisRef: VisualObject, property: KProperty<*>): MetaItem<*>? {
        val name = key ?: property.name.asName()
        return if (inherited) {
            thisRef.getProperty(name)
        } else {
            thisRef.config[name]
        } ?: default
    }

    override fun setValue(thisRef: VisualObject, property: KProperty<*>, value: MetaItem<*>?) {
        val name = key ?: property.name.asName()
        thisRef.config[name] = value
    }
}

class VisualObjectDelegateWrapper<T>(
    val obj: VisualObject,
    val key: Name?,
    val default: T,
    val inherited: Boolean,
    val write: Config.(name: Name, value: T) -> Unit = { name, value -> set(name, value) },
    val read: (MetaItem<*>?) -> T?
) : ReadWriteProperty<Any?, T> {

    //private var cachedName: Name? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val name = key ?: property.name.asName()
        return read(obj.getProperty(name, inherited)) ?: default
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val name = key ?: property.name.asName()
        obj.config[name] = value
    }
}


fun VisualObject.value(default: Value? = null, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.value }

fun VisualObject.string(default: String? = null, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.string }

fun VisualObject.boolean(default: Boolean? = null, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.boolean }

fun VisualObject.number(default: Number? = null, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.number }

fun VisualObject.double(default: Double? = null, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.double }

fun VisualObject.int(default: Int? = null, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.int }


fun VisualObject.node(name: Name? = null, inherited: Boolean = true) =
    VisualObjectDelegateWrapper(this, name, null, inherited) { it.node }

fun VisualObject.item(name: Name? = null, inherited: Boolean = true) =
    VisualObjectDelegateWrapper(this, name, null, inherited) { it }

//fun <T : Configurable> Configurable.spec(spec: Specification<T>, key: String? = null) = ChildConfigDelegate<T>(key) { spec.wrap(this) }

@JvmName("safeString")
fun VisualObject.string(default: String, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.string }

@JvmName("safeBoolean")
fun VisualObject.boolean(default: Boolean, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.boolean }

@JvmName("safeNumber")
fun VisualObject.number(default: Number, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.number }

@JvmName("safeDouble")
fun VisualObject.double(default: Double, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.double }

@JvmName("safeInt")
fun VisualObject.int(default: Int, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) { it.int }


inline fun <reified E : Enum<E>> VisualObject.enum(default: E, name: Name? = null, inherited: Boolean = false) =
    VisualObjectDelegateWrapper(this, name, default, inherited) {
            item -> item.string?.let { enumValueOf<E>(it) }
    }

//merge properties

fun <T> VisualObject.merge(
    name: Name? = null,
    transformer: (Sequence<MetaItem<*>>) -> T
): ReadOnlyProperty<VisualObject, T> {
    return object : ReadOnlyProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val actualName = name ?: property.name.asName()
            val sequence = sequence<MetaItem<*>> {
                var thisObj: VisualObject? = this@merge
                while (thisObj != null) {
                    thisObj.config[actualName]?.let { yield(it) }
                    thisObj = thisObj.parent
                }
            }
            return transformer(sequence)
        }
    }
}