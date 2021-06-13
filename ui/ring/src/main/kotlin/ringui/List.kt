package ringui

import kotlinext.js.jsObject
import react.RBuilder
import react.RClass
import react.RProps
import react.ReactElement
import react.dom.WithClassName

public external interface ListItem :RProps {
    public var scrolling: Boolean
    public var hover: Boolean
    public var details: String
    public var disabled: Boolean
    public var tabIndex: Int
    public var checkbox: Boolean
    public var description: dynamic //String|PropTypes.element|PropTypes.array
    public var avatar: String
    public var subavatar: String
    public var glyph: dynamic //String|PropTypes.elementType
    public var icon: String
    public var iconSize: Number
    public var rightNodes: dynamic//String|PropTypes.element|PropTypes.array
    public var leftNodes: dynamic//String|PropTypes.element|PropTypes.array
    public var label: dynamic //String|PropTypes.elementType
    public var title: String
    public var level: Number
    public var rgItemType: Number
    public var rightGlyph: dynamic //String|PropTypes.elementType
    public var compact: Boolean
    public var onClick: () -> Unit
    public var onCheckboxChange: () -> Unit
    public var onMouseOver: () -> Unit
    public var onMouseDown: () -> Unit
    public var onMouseUp: () -> Unit
    //public var `data-test`: String
}

public external interface ListProps : WithClassName {
    public var id: String
    public var hint: ReactElement //PropTypes.node
    public var hintOnSelection: String
    public var maxHeight: dynamic // String|Number
    public var activeIndex: Int
    public var restoreActiveIndex: Boolean
    public var activateSingleItem: Boolean
    public var activateFirstItem: Boolean
    public var shortcuts: Boolean
    public var onMouseOut: () -> Unit
    public var onSelect: () -> Unit
    public var onScrollToBottom: () -> Unit
    public var onResize: () -> Unit
    public var useMouseUp: Boolean
    public var visible: Boolean
    public var renderOptimization: Boolean
    public var disableMoveOverflow: Boolean
    public var disableMoveDownOverflow: Boolean
    public var compact: Boolean
    public var disableScrollToActive: Boolean
    public var hidden: Boolean
    public var ariaLabel: String

    public var data: Array<ListItem>
}

public fun ListProps.item(block: ListItem.() -> Unit) {
    data += jsObject<ListItem>(block)
}

//@JsModule("@jetbrains/ring-ui/components/list/list")
//internal external object ListModule {
//    val List: RClass<ListProps>
//}

@JsModule("@jetbrains/ring-ui/components/list/list")
private external val ringList: RClass<ListProps>

public val RingUI.List: RClass<ListProps> get() = ringList

public fun RBuilder.ringList(propsBuilder: ListProps.()->Unit){
    RingUI.List{
        attrs(propsBuilder)
    }
}