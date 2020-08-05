@file:UseSerializers(Point3DSerializer::class)

package hep.dataforge.vision.spatial

import hep.dataforge.meta.*
import hep.dataforge.meta.descriptors.NodeDescriptor
import hep.dataforge.names.asName
import hep.dataforge.names.plus
import hep.dataforge.output.Renderer
import hep.dataforge.values.ValueType
import hep.dataforge.values.asValue
import hep.dataforge.vision.Vision
import hep.dataforge.vision.enum
import hep.dataforge.vision.spatial.Vision3D.Companion.DETAIL_KEY
import hep.dataforge.vision.spatial.Vision3D.Companion.IGNORE_KEY
import hep.dataforge.vision.spatial.Vision3D.Companion.LAYER_KEY
import hep.dataforge.vision.spatial.Vision3D.Companion.VISIBLE_KEY
import kotlinx.serialization.UseSerializers

/**
 * Interface for 3-dimensional [Vision]
 */
interface Vision3D : Vision {
    var position: Point3D?
    var rotation: Point3D?
    var scale: Point3D?

    override val descriptor: NodeDescriptor? get() = Companion.descriptor

    companion object {

        val VISIBLE_KEY = "visible".asName()

        //        val SELECTED_KEY = "selected".asName()
        val DETAIL_KEY = "detail".asName()
        val LAYER_KEY = "layer".asName()
        val IGNORE_KEY = "ignore".asName()

        val GEOMETRY_KEY = "geometry".asName()

        val X_KEY = "x".asName()
        val Y_KEY = "y".asName()
        val Z_KEY = "z".asName()

        val POSITION_KEY = "pos".asName()

        val X_POSITION_KEY = POSITION_KEY + X_KEY
        val Y_POSITION_KEY = POSITION_KEY + Y_KEY
        val Z_POSITION_KEY = POSITION_KEY + Z_KEY

        val ROTATION = "rotation".asName()

        val X_ROTATION_KEY = ROTATION + X_KEY
        val Y_ROTATION_KEY = ROTATION + Y_KEY
        val Z_ROTATION_KEY = ROTATION + Z_KEY

        val ROTATION_ORDER_KEY = ROTATION + "order"

        val SCALE_KEY = "scale".asName()

        val X_SCALE_KEY = SCALE_KEY + X_KEY
        val Y_SCALE_KEY = SCALE_KEY + Y_KEY
        val Z_SCALE_KEY = SCALE_KEY + Z_KEY

        val descriptor by lazy {
            NodeDescriptor {
                value(VISIBLE_KEY) {
                    type(ValueType.BOOLEAN)
                    default(true)
                }

                //TODO replace by descriptor merge
                value(Vision.STYLE_KEY) {
                    type(ValueType.STRING)
                    multiple = true
                }

                item(Material3D.MATERIAL_KEY.toString(), Material3D.descriptor)

                enum<RotationOrder>(ROTATION_ORDER_KEY,default = RotationOrder.XYZ)
            }
        }
    }
}

/**
 * Count number of layers to the top object. Return 1 if this is top layer
 */
var Vision3D.layer: Int
    get() = getItem(LAYER_KEY).int ?: 0
    set(value) {
        setItem(LAYER_KEY, value.asValue())
    }

fun Renderer<Vision3D>.render(meta: Meta = Meta.EMPTY, action: VisionGroup3D.() -> Unit) =
    render(VisionGroup3D().apply(action), meta)

// Common properties

enum class RotationOrder {
    XYZ,
    YZX,
    ZXY,
    XZY,
    YXZ,
    ZYX
}

/**
 * Rotation order
 */
var Vision3D.rotationOrder: RotationOrder
    get() = getItem(Vision3D.ROTATION_ORDER_KEY).enum<RotationOrder>() ?: RotationOrder.XYZ
    set(value) = setItem(Vision3D.ROTATION_ORDER_KEY, value.name.asValue())


/**
 * Preferred number of polygons for displaying the object. If not defined, uses shape or renderer default. Not inherited
 */
var Vision3D.detail: Int?
    get() = getProperty(DETAIL_KEY, false).int
    set(value) = setItem(DETAIL_KEY, value?.asValue())

var Vision.visible: Boolean?
    get() = getItem(VISIBLE_KEY).boolean
    set(value) = setItem(VISIBLE_KEY, value?.asValue())

/**
 * If this property is true, the object will be ignored on render.
 * Property is not inherited.
 */
var Vision.ignore: Boolean?
    get() = getProperty(IGNORE_KEY, false).boolean
    set(value) = setItem(IGNORE_KEY, value?.asValue())

//var VisualObject.selected: Boolean?
//    get() = getProperty(SELECTED_KEY).boolean
//    set(value) = setProperty(SELECTED_KEY, value)

private fun Vision3D.position(): Point3D =
    position ?: Point3D(0.0, 0.0, 0.0).also { position = it }

var Vision3D.x: Number
    get() = position?.x ?: 0f
    set(value) {
        position().x = value.toDouble()
        propertyInvalidated(Vision3D.X_POSITION_KEY)
    }

var Vision3D.y: Number
    get() = position?.y ?: 0f
    set(value) {
        position().y = value.toDouble()
        propertyInvalidated(Vision3D.Y_POSITION_KEY)
    }

var Vision3D.z: Number
    get() = position?.z ?: 0f
    set(value) {
        position().z = value.toDouble()
        propertyInvalidated(Vision3D.Z_POSITION_KEY)
    }

private fun Vision3D.rotation(): Point3D =
    rotation ?: Point3D(0.0, 0.0, 0.0).also { rotation = it }

var Vision3D.rotationX: Number
    get() = rotation?.x ?: 0f
    set(value) {
        rotation().x = value.toDouble()
        propertyInvalidated(Vision3D.X_ROTATION_KEY)
    }

var Vision3D.rotationY: Number
    get() = rotation?.y ?: 0f
    set(value) {
        rotation().y = value.toDouble()
        propertyInvalidated(Vision3D.Y_ROTATION_KEY)
    }

var Vision3D.rotationZ: Number
    get() = rotation?.z ?: 0f
    set(value) {
        rotation().z = value.toDouble()
        propertyInvalidated(Vision3D.Z_ROTATION_KEY)
    }

private fun Vision3D.scale(): Point3D =
    scale ?: Point3D(1.0, 1.0, 1.0).also { scale = it }

var Vision3D.scaleX: Number
    get() = scale?.x ?: 1f
    set(value) {
        scale().x = value.toDouble()
        propertyInvalidated(Vision3D.X_SCALE_KEY)
    }

var Vision3D.scaleY: Number
    get() = scale?.y ?: 1f
    set(value) {
        scale().y = value.toDouble()
        propertyInvalidated(Vision3D.Y_SCALE_KEY)
    }

var Vision3D.scaleZ: Number
    get() = scale?.z ?: 1f
    set(value) {
        scale().z = value.toDouble()
        propertyInvalidated(Vision3D.Z_SCALE_KEY)
    }