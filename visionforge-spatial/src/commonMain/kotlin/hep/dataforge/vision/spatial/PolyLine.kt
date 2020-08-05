@file:UseSerializers(Point3DSerializer::class)

package hep.dataforge.vision.spatial

import hep.dataforge.meta.Config
import hep.dataforge.meta.number
import hep.dataforge.names.asName
import hep.dataforge.names.plus
import hep.dataforge.vision.AbstractVision
import hep.dataforge.vision.MutableVisionGroup
import hep.dataforge.vision.set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
@SerialName("3d.line")
class PolyLine(var points: List<Point3D>) : AbstractVision(), Vision3D {
    override var ownProperties: Config? = null

    override var position: Point3D? = null
    override var rotation: Point3D? = null
    override var scale: Point3D? = null

    //var lineType by string()
    var thickness by number(1.0, key = Material3D.MATERIAL_KEY + THICKNESS_KEY)

    companion object {
        val THICKNESS_KEY = "thickness".asName()
    }

}

fun MutableVisionGroup.polyline(vararg points: Point3D, name: String = "", action: PolyLine.() -> Unit = {}) =
    PolyLine(points.toList()).apply(action).also { set(name, it) }