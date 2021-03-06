@file:UseSerializers(Point3DSerializer::class)

package hep.dataforge.vision.solid.demo

import hep.dataforge.meta.int
import hep.dataforge.meta.number
import hep.dataforge.meta.setItem
import hep.dataforge.names.plus
import hep.dataforge.names.startsWith
import hep.dataforge.values.asValue
import hep.dataforge.vision.getProperty
import hep.dataforge.vision.set
import hep.dataforge.vision.solid.*
import hep.dataforge.vision.solid.Solid.Companion.GEOMETRY_KEY
import hep.dataforge.vision.solid.demo.VariableBoxThreeFactory.Z_SIZE_KEY
import hep.dataforge.vision.solid.three.*
import hep.dataforge.vision.solid.three.ThreeMaterials.getMaterial
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.objects.Mesh
import kotlinx.serialization.UseSerializers
import kotlin.math.max
import kotlin.reflect.KClass

internal var Solid.variableZSize: Number
    get() = getProperty(Z_SIZE_KEY, false).number ?: 0f
    set(value) {
        setItem(Z_SIZE_KEY, value.asValue())
    }

internal var Solid.value: Int
    get() = getProperty("value", false).int ?: 0
    set(value) {
        setItem("value", value.asValue())
        val size = value.toFloat() / 255f * 20f
        scaleZ = size
        z = -size / 2

        val b = max(0, 255 - value)
        val r = max(0, value - 255)
        val g = 255 - b - r
        color(r.toUByte(), g.toUByte(), b.toUByte())
    }

fun SolidGroup.varBox(
    xSize: Number,
    ySize: Number,
    zSize: Number,
    name: String = "",
    action: Solid.() -> Unit = {}
) = CustomThreeVision(VariableBoxThreeFactory).apply {
    scaleX = xSize
    scaleY = ySize
    scaleZ = zSize
}.apply(action).also { set(name, it) }

private object VariableBoxThreeFactory : ThreeFactory<Solid> {
    val X_SIZE_KEY = GEOMETRY_KEY + "xSize"
    val Y_SIZE_KEY = GEOMETRY_KEY + "ySize"
    val Z_SIZE_KEY = GEOMETRY_KEY + "zSize"

    override val type: KClass<in Solid> get() = Solid::class

    override fun invoke(obj: Solid): Object3D {
        val xSize = obj.getProperty(X_SIZE_KEY, false).number?.toDouble() ?: 1.0
        val ySize = obj.getProperty(Y_SIZE_KEY, false).number?.toDouble() ?: 1.0
        val zSize = obj.getProperty(Z_SIZE_KEY, false).number?.toDouble() ?: 1.0
        val geometry = BoxBufferGeometry(1, 1, 1)

        //JS sometimes tries to pass Geometry as BufferGeometry
        @Suppress("USELESS_IS_CHECK") if (geometry !is BufferGeometry) error("BufferGeometry expected")

        val mesh = Mesh(geometry, getMaterial(obj)).apply {
            applyEdges(obj)
            applyWireFrame(obj)

            //set position for mesh
            updatePosition(obj)

            layers.enable(obj.layer)
            children.forEach {
                it.layers.enable(obj.layer)
            }
        }

        mesh.scale.set(xSize, ySize, zSize)

        //add listener to object properties
        obj.onPropertyChange(this) { name ->
            when {
//                name.startsWith(GEOMETRY_KEY) -> {
//                    val newXSize = obj.getProperty(X_SIZE_KEY, false).number?.toDouble() ?: 1.0
//                    val newYSize = obj.getProperty(Y_SIZE_KEY, false).number?.toDouble() ?: 1.0
//                    val newZSize = obj.getProperty(Z_SIZE_KEY, false).number?.toDouble() ?: 1.0
//                    mesh.scale.set(newXSize, newYSize, newZSize)
//                    mesh.updateMatrix()
//                }
                name.startsWith(MeshThreeFactory.WIREFRAME_KEY) -> mesh.applyWireFrame(obj)
                name.startsWith(MeshThreeFactory.EDGES_KEY) -> mesh.applyEdges(obj)
                else -> mesh.updateProperty(obj, name)
            }
        }
        return mesh
    }
}