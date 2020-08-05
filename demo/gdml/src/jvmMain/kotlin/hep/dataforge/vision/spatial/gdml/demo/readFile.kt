package hep.dataforge.vision.spatial.gdml.demo

import hep.dataforge.meta.setItem
import hep.dataforge.values.asValue
import hep.dataforge.vision.spatial.Material3D
import hep.dataforge.vision.spatial.SpatialVisionManager
import hep.dataforge.vision.spatial.VisionGroup3D
import hep.dataforge.vision.spatial.gdml.LUnit
import hep.dataforge.vision.spatial.gdml.readFile
import hep.dataforge.vision.spatial.gdml.toVision
import scientifik.gdml.GDML
import java.io.File
import java.util.zip.GZIPInputStream
import java.util.zip.ZipInputStream

fun SpatialVisionManager.Companion.readFile(file: File): VisionGroup3D = when {
    file.extension == "gdml" || file.extension == "xml" -> {
        GDML.readFile(file.toPath()).toVision {
            lUnit = LUnit.CM

            solidConfiguration = { parent, solid ->
                if (solid.name == "cave") {
                    setItem(Material3D.MATERIAL_WIREFRAME_KEY, true.asValue())
                }
                if (parent.physVolumes.isNotEmpty()) {
                    useStyle("opaque") {
                        Material3D.MATERIAL_OPACITY_KEY put 0.3
                    }
                }
            }
        }
    }
    file.extension == "json" -> VisionGroup3D.parseJson(file.readText())
    file.name.endsWith("json.zip") -> {
        file.inputStream().use {
            val unzip = ZipInputStream(it, Charsets.UTF_8)
            val text = unzip.readBytes().decodeToString()
            VisionGroup3D.parseJson(text)
        }
    }
    file.name.endsWith("json.gz") -> {
        file.inputStream().use {
            val unzip = GZIPInputStream(it)
            val text = unzip.readBytes().decodeToString()
            VisionGroup3D.parseJson(text)
        }
    }
    else -> error("Unknown extension ${file.extension}")
}

fun SpatialVisionManager.Companion.readFile(fileName: String): VisionGroup3D = readFile(File(fileName))