package com.github.windchopper.tools.log.browser.fx

import com.github.windchopper.common.util.Pipeliner
import javafx.beans.property.BooleanProperty
import javafx.scene.control.CheckBox
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.util.Callback
import org.apache.commons.lang3.StringUtils
import java.util.function.Consumer

class FileTreeCell(
    private val selectedStateCallback: (TreeItem<RemoteFile>) -> BooleanProperty,
    private val selectedStateBuffer: Map<String, BooleanProperty>): TreeCell<RemoteFile>() {

    private val checkBox: CheckBox
    private var booleanProperty: BooleanProperty? = null

    override fun updateItem(file: RemoteFile?, empty: Boolean) {
        super.updateItem(file, empty)
        if (empty) {
            text = null
            graphic = null
        } else {
            val treeItem = treeItem
            text = if (treeItem == null) "" else treeItem.value!!.displayName(false)
            checkBox.graphic = treeItem?.graphic
            graphic = checkBox
            if (booleanProperty != null) {
                checkBox.selectedProperty().unbindBidirectional(booleanProperty)
            }
            booleanProperty = selectedStateCallback(treeItem)
            if (booleanProperty != null) {
                checkBox.selectedProperty().bindBidirectional(booleanProperty)
            }
            checkBox.isVisible = file?.directory?:false
            checkBox.isIndeterminate = file != null && !checkBox.isSelected && selectedStateBuffer.entries.stream()
                .anyMatch { entry: Map.Entry<String, BooleanProperty> -> entry.value.get() && StringUtils.startsWith(entry.key, file.path.toString()) }
        }
    }

    init {
        styleClass.add("check-box-tree-cell")
        checkBox = Pipeliner.of { CheckBox() }
            .set({ bean: CheckBox -> Consumer { value: Boolean? -> bean.isFocusTraversable = value!! } }, false)
            .set({ bean: CheckBox -> Consumer { value: Boolean? -> bean.isAllowIndeterminate = value!! } }, false)
            .get()
        graphic = null
    }

}