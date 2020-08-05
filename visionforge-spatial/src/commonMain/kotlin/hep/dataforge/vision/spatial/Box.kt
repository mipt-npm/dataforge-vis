@file:UseSerializers(Point3DSerializer::class)
package hep.dataforge.vision.spatial

import hep.dataforge.context.Context
import hep.dataforge.meta.Config
import hep.dataforge.meta.Meta
import hep.dataforge.meta.float
import hep.dataforge.meta.get
import hep.dataforge.vision.AbstractVision
import hep.dataforge.vision.MutableVisionGroup
import hep.dataforge.vision.VisionFactory
import hep.dataforge.vision.set
import hep.dataforge.vision.spatial.Box.Companion.TYPE_NAME
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.reflect.KClass

@Serializable
@SerialName(TYPE_NAME)
class Box(
    val xSize: Float,
    val ySize: Float,
    val zSize: Float
) : AbstractVision(), Vision3D, Shape {

    override var position: Point3D? = null
    override var rotation: Point3D? = null
    override var scale: Point3D? = null

    override var ownProperties: Config? = null

    //TODO add helper for color configuration
    override fun <T : Any> toGeometry(geometryBuilder: GeometryBuilder<T>) {
        val dx = xSize / 2
        val dy = ySize / 2
        val dz = zSize / 2
        val node1 = Point3D(-dx, -dy, -dz)
        val node2 = Point3D(dx, -dy, -dz)
        val node3 = Point3D(dx, dy, -dz)
        val node4 = Point3D(-dx, dy, -dz)
        val node5 = Point3D(-dx, -dy, dz)
        val node6 = Point3D(dx, -dy, dz)
        val node7 = Point3D(dx, dy, dz)
        val node8 = Point3D(-dx, dy, dz)
        geometryBuilder.face4(node1, node4, node3, node2)
        geometryBuilder.face4(node1, node2, node6, node5)
        geometryBuilder.face4(node2, node3, node7, node6)
        geometryBuilder.face4(node4, node8, node7, node3)
        geometryBuilder.face4(node1, node5, node8, node4)
        geometryBuilder.face4(node8, node5, node6, node7)
    }

    companion object : VisionFactory<Box> {

        const val TYPE_NAME = "3d.box"

        override val type: KClass<Box> get() = Box::class

        override fun invoke(meta: Meta, context: Context): Box = Box(
            meta["xSize"].float!!,
            meta["ySize"].float!!,
            meta["zSize"].float!!
        ).apply {
            update(meta)
        }
    }
}

inline fun MutableVisionGroup.box(
    xSize: Number,
    ySize: Number,
    zSize: Number,
    name: String = "",
    action: Box.() -> Unit = {}
) = Box(xSize.toFloat(), ySize.toFloat(), zSize.toFloat()).apply(action).also { set(name, it) }