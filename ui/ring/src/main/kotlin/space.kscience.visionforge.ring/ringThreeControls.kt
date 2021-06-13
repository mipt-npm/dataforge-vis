package space.kscience.visionforge.ring

import kotlinx.css.BorderStyle
import kotlinx.css.Color
import kotlinx.css.padding
import kotlinx.css.properties.border
import kotlinx.css.px
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import react.*
import react.dom.attrs
import react.dom.button
import ringui.island.ringIsland
import ringui.tabs.ringSmartTabs
import ringui.tabs.ringTab
import space.kscience.dataforge.meta.withDefault
import space.kscience.dataforge.names.Name
import space.kscience.dataforge.names.isEmpty
import space.kscience.visionforge.Vision
import space.kscience.visionforge.VisionGroup
import space.kscience.visionforge.encodeToString
import space.kscience.visionforge.react.flexColumn
import space.kscience.visionforge.react.flexRow
import space.kscience.visionforge.react.propertyEditor
import space.kscience.visionforge.solid.specifications.Canvas3DOptions
import styled.css

internal fun saveData(event: Event, fileName: String, mimeType: String = "text/plain", dataBuilder: () -> String) {
    event.stopPropagation();
    event.preventDefault();

    val fileSaver = kotlinext.js.require("file-saver")
    val blob = Blob(arrayOf(dataBuilder()), BlobPropertyBag("$mimeType;charset=utf-8"))
    fileSaver.saveAs(blob, fileName)
}

internal fun RBuilder.canvasControls(options: Canvas3DOptions, vision: Vision?): ReactElement {
    return child(CanvasControls) {
        attrs {
            this.options = options
            this.vision = vision
        }
    }
}

internal external interface CanvasControlsProps : RProps {
    public var options: Canvas3DOptions
    public var vision: Vision?
}

internal val CanvasControls: FunctionalComponent<CanvasControlsProps> = functionalComponent("CanvasControls") { props ->
    flexColumn {
        flexRow {
            css {
                border(1.px, BorderStyle.solid, Color.blue)
                padding(4.px)
            }
            props.vision?.let { vision ->
                button {
                    +"Export"
                    attrs {
                        onClickFunction = {
                            val json = vision.encodeToString()
                            saveData(it, "object.json", "text/json") {
                                json
                            }

                        }
                    }
                }
            }
        }
        propertyEditor(
            ownProperties = props.options,
            allProperties = props.options.withDefault(Canvas3DOptions.descriptor.defaultMeta),
            descriptor = Canvas3DOptions.descriptor,
            expanded = false
        )

    }
}


public external interface ThreeControlsProps : RProps {
    public var canvasOptions: Canvas3DOptions
    public var vision: Vision?
    public var selected: Name?
    public var onSelect: (Name?) -> Unit
}

@JsExport
public val ThreeControls: FunctionalComponent<ThreeControlsProps> = functionalComponent { props ->
    ringSmartTabs(if (props.selected != null) "Properties" else "Tree") {
        ringTab("Canvas") {
            ringIsland("Canvas configuration") {
                canvasControls(props.canvasOptions, props.vision)
            }
        }
        props.vision?.let {
            ringTab("Tree") {
                ringIsland("Vision tree") {
                    ringVisionTree(it, props.selected, props.onSelect)
                }
            }
        }
        props.selected?.let { selected ->
            ringTab("Properties") {
                val selectedObject: Vision? = when {
                    selected.isEmpty() -> props.vision
                    else -> (props.vision as? VisionGroup)?.get(selected)
                }
                if (selectedObject != null) {
                    ringPropertyEditor(selectedObject, key = selected)
                }
            }
        }
        props.children()
    }
}

public fun RBuilder.ringThreeControls(
    canvasOptions: Canvas3DOptions,
    vision: Vision?,
    selected: Name?,
    onSelect: (Name?) -> Unit = {},
    builder: RBuilder.() -> Unit = {},
): ReactElement = child(ThreeControls) {
    attrs {
        this.canvasOptions = canvasOptions
        this.vision = vision
        this.selected = selected
        this.onSelect = onSelect
    }
    builder()
}