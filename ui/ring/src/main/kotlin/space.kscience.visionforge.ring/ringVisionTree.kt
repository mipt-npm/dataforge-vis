package space.kscience.visionforge.ring

import react.RBuilder
import react.dom.a
import ringui.ringDataList
import space.kscience.dataforge.meta.transformations.MetaConverter.Companion.item
import space.kscience.dataforge.names.Name
import space.kscience.dataforge.names.lastOrNull
import space.kscience.dataforge.names.plus
import space.kscience.dataforge.names.startsWith
import space.kscience.visionforge.Vision
import space.kscience.visionforge.VisionGroup

private data class NamedVision(val name: Name, val vision: Vision)

private inline val NamedVision.children: List<NamedVision>?
    get() = (item as? VisionGroup)?.children?.map { (childName, childVision) ->
        NamedVision(name + childName, childVision)
    }

public fun RBuilder.ringVisionTree(
    vision: Vision,
    selectedVision: Name? = null,
    onVisionSelect: (Name?) -> Unit = {},
) {
    val rootItem = NamedVision(Name.EMPTY, vision)
    val items = rootItem.children ?: listOf(rootItem)
    ringDataList(items) { (name, vision) ->
        collapsible = vision is VisionGroup
        collapsed = (selectedVision == null || !selectedVision.startsWith(name))
        onFocus = { onVisionSelect(name) }
        title = a { +(name.lastOrNull()?.toString() ?: "World") }
    }
}

