package space.kscience.visionforge.ring

import kotlinx.css.*
import react.*
import ringui.grid.ringCol
import ringui.grid.ringGrid
import ringui.grid.ringRow
import ringui.tabs.ringTab
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.names.Name
import space.kscience.visionforge.react.ThreeCanvasComponent
import space.kscience.visionforge.solid.Solid
import space.kscience.visionforge.solid.specifications.Canvas3DOptions
import styled.css
import styled.styledDiv

public external interface ThreeCanvasWithControlsProps : RProps {
    public var context: Context
    public var solid: Solid?
    public var selected: Name?
    public var additionalTabs: Map<String, RBuilder.() -> Unit>?
}

public fun ThreeCanvasWithControlsProps.tab(title: String, block: RBuilder.()->Unit){
    additionalTabs = (additionalTabs?: emptyMap()) + (title to block)
}

@JsExport
public val ThreeCanvasWithControls: (props: ThreeCanvasWithControlsProps) -> dynamic =
    functionalComponent("ThreeViewWithControls") { props ->
        var selected by useState { props.selected }
        val onSelect: (Name?) -> Unit = {
            selected = it
        }
        val options = useMemo {
            Canvas3DOptions.invoke {
                this.onSelect = onSelect
            }
        }

        styledDiv {
            css {
                height = 100.pct
                width = 100.pct
                maxHeight = 100.vh
                maxWidth = 100.vw
            }
            ringGrid {
                ringRow {
                    ringCol {
                        attrs {
                            xs = 12
                            sm = 12
                            md = 8
                            lg = 9
                        }
                        child(ThreeCanvasComponent) {
                            attrs {
                                this.context = props.context
                                this.solid = props.solid
                                this.selected = selected
                                this.options = options
                            }
                        }
                    }
                    ringCol {
                        attrs {
                            xs = 12
                            sm = 12
                            md = 4
                            lg = 3
                        }
                        styledDiv {
                            css {
                                padding(top = 4.px)
                                width = 100.pct
                                //border(1.px, BorderStyle.solid, Color.lightGray)
                            }
                            ringThreeControls(options, props.solid, selected, onSelect) {
                                props.additionalTabs?.forEach { (title, builder) ->
                                    ringTab(title, title, builder)
                                }
                            }
                        }
                    }
                }
            }
        }
    }