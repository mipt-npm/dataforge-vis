package ringui

import kotlinext.js.jsObject
import react.RBuilder
import react.RClass
import react.RElementBuilder
import react.ReactElement
import react.dom.WithClassName

public external interface SelectionProperties<T> {
    public var data: Array<dynamic>
    public var selected: Set<T>
    public var focused: T?
    public var getKey: (T) -> dynamic
    public var getChildren: (T) -> Array<T>
    public var isItemSelectable: (T) -> Boolean
}

@JsModule("@jetbrains/ring-ui/components/data-list/selection")
public external class Selection<T>(args: SelectionProperties<T>) {
    constructor()
    public fun select(item: T = definedExternally)
    public fun deselect(item: T = definedExternally)
    public fun toggleSelection(item: T = definedExternally)
    public fun selectAll()
    public fun resetFocus()
    public fun resetSelection()
    public fun reset()
    public fun isFocused(value: T): Boolean
    public fun isSelected(value: T): Boolean
    public fun getFocused(): T
    public fun getSelected(): Set<T>
    public fun getActive(): Set<T>
}

@Suppress("FunctionName")
internal inline fun <T> buildSelection(builder: SelectionProperties<T>.() -> Unit): Selection<T> =
    Selection<T>(jsObject<SelectionProperties<T>>(builder))

public external interface DataListItem<T> : WithClassName {
    public var item: T //PropTypes.object,
    public var title: ReactElement //PropTypes.node
    public var items: Array<T>//PropTypes.array,
    public var level: Number
    public var parentShift: Number

    public var itemFormatter: (T) -> DataListItem<T>

    public var collapsible: Boolean
    public var collapsed: Boolean
    public var onCollapse: () -> Unit
    public var onExpand: () -> Unit

    public var showFocus: Boolean
    public var onFocus: () -> Unit

    public var selection: Selection<T> //PropTypes.object
    public var selectable: Boolean
    public var selected: Boolean
    public var onSelect: (T) -> Unit

    public var showMoreLessButton: Number
    public var onItemMoreLess: () -> Unit
}

//public external interface DataListTitle {
//    public var title: ReactElement //PropTypes.node
//    public var offset: Number
//    public var selectable: Boolean
//    public var selected: Boolean
//    public var onSelect: () -> Unit
//    public var showFocus: Boolean
//    public var collapserExpander: ReactElement //PropTypes.node
//    public var innerRef: dynamic //PropTypes.object|String|PropTypes.func
//
//    // focusSensorHOC
//    public var onFocusRestore: () -> Unit
//}

public external interface DataListProps<T: Any> : WithClassName {
    public var data: Array<T> //IPropTypes.array.isRequired,
    public var loading: Boolean
    public var focused: Boolean
    public var disabledHover: Boolean
    public var selection: Selection<T> //PropTypes.object
    public var selectable: Boolean
    public var shortcutsMap: dynamic //PropTypes.object
    public var innerRef: dynamic //PropTypes.object|String|PropTypes.func

    public var itemFormatter: (T) -> DataListItem<T>//PropTypes.func.isRequired,

    public var onItemMoreLess: (item: T, more: Boolean) -> Unit
    public var itemMoreLessState: (T) -> Unit
    public var onSelect: (Selection<T>) -> Unit

    public var remoteSelection: Boolean
}


@Suppress("FunctionName")
public fun <T> DataListItem(
    item: T,
    getChildren: (T) -> Iterable<T>? = { null },
    block: DataListItem<T>.() -> Unit = {},
): DataListItem<T> = jsObject<DataListItem<T>> {
    this.item = item
    getChildren(item)?.let { children ->
        items = children.toList().toTypedArray()
    }
    block()
}

@JsModule("@jetbrains/ring-ui/components/data-list/data-list")
internal external object DataListModule {
    val default: RClass<DataListProps<out Any>>
}


public fun <T: Any> DataListProps<T>.selection(block: SelectionProperties<T>.()->Unit){
    selection = buildSelection {
        this.data = data
        block()
    }
}

@Suppress("UNCHECKED_CAST")
public fun <T:Any> RBuilder.ringDataList(
    items: Iterable<T>,
    propsBuilder: DataListProps<T>.() -> Unit = {},
    itemBuilder: DataListItem<T>.(T) -> Unit = {},
): ReactElement = DataListModule.default {
    this as RElementBuilder<DataListProps<T>>
    attrs.apply {
        data = items.toList().toTypedArray()
        itemFormatter = {
            DataListItem(it).apply { itemBuilder(it) }
        }
        selectable = false
//        selection = Selection()
        selection = buildSelection{
            isItemSelectable = {false}
        } // set default selection
        propsBuilder()
    }
}