package space.kscience.visionforge.gdml.jupyter

import space.kscience.dataforge.misc.DFExperimental
import space.kscience.visionforge.ring.ThreeWithControlsPlugin
import space.kscience.visionforge.runVisionClient

@DFExperimental
@JsExport
fun main() = runVisionClient {
    plugin(ThreeWithControlsPlugin)
}

