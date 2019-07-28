package hep.dataforge.vis.spatial.three

import hep.dataforge.meta.Meta
import hep.dataforge.meta.get
import hep.dataforge.meta.int
import hep.dataforge.vis.spatial.GeometryBuilder
import hep.dataforge.vis.spatial.Point2D
import hep.dataforge.vis.spatial.Point3D
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Face3
import info.laht.threekt.core.Geometry
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3

// TODO use unsafe cast instead
fun Point3D.asVector(): Vector3 = Vector3(this.x, this.y, this.z)

fun Point2D.asVector(): Vector2 = Vector2(this.x, this.y)

class ThreeGeometryBuilder : GeometryBuilder<BufferGeometry> {

    private val vertices = ArrayList<Point3D>()
    private val faces = ArrayList<Face3>()

    private val vertexCache = HashMap<Point3D, Int>()

    private fun append(vertex: Point3D): Int {
        val index = vertexCache[vertex] ?: -1//vertices.indexOf(vertex)
        return if (index > 0) {
            index
        } else {
            vertices.add(vertex)
            vertexCache[vertex] = vertices.size - 1
            vertices.size - 1
        }
    }

    override fun face(vertex1: Point3D, vertex2: Point3D, vertex3: Point3D, normal: Point3D?, meta: Meta) {
        val face = Face3(append(vertex1), append(vertex2), append(vertex3), normal?.asVector() ?: Vector3(0, 0, 0))
        meta["materialIndex"].int?.let { face.materialIndex = it }
        meta["color"]?.color()?.let { face.color = it }
        faces.add(face)
    }


    override fun build(): BufferGeometry {
        return Geometry().apply {
            vertices = this@ThreeGeometryBuilder.vertices.map { it.asVector() }.toTypedArray()
            faces = this@ThreeGeometryBuilder.faces.toTypedArray()
            computeBoundingSphere()
            computeFaceNormals()
        }.toBufferGeometry()
    }
}