package com.github.windchopper.tools.log.browser.fx

import com.github.windchopper.common.util.Pipeliner
import javafx.beans.property.BooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.util.Callback
import org.apache.commons.lang3.StringUtils
import java.util.function.Consumer

@Suppress("UNUSED_PARAMETER") class FileListCell(
    private val selectedStateCallback: Callback<RemoteFile, BooleanProperty>,
    private val selectedStateBuffer: Map<String, BooleanProperty>): ListCell<RemoteFile>() {
    private val checkBox: CheckBox
    private var booleanProperty: BooleanProperty? = null

    override fun updateItem(file: RemoteFile?, empty: Boolean) {
        super.updateItem(file, empty)
        if (empty) {
            graphic = null
            text = null
        } else {
            graphic = checkBox
            text = if (file == null) "" else file.displayName(true)
            if (booleanProperty != null) {
                checkBox.selectedProperty().unbindBidirectional(booleanProperty)
            }
            booleanProperty = selectedStateCallback.call(file)
            if (booleanProperty != null) {
                checkBox.selectedProperty().bindBidirectional(booleanProperty)
            }
            checkBox.isVisible = file != null && file.directory
            checkBox.isIndeterminate = file != null && !checkBox.isSelected && selectedStateBuffer.entries.stream()
                .anyMatch { entry: Map.Entry<String, BooleanProperty> -> entry.value.get() && StringUtils.startsWith(entry.key, file.path.toString()) }
        }
    }

    init {
        styleClass.add("check-box-list-cell")
        checkBox = Pipeliner.of { CheckBox() }
            .set({ bean: CheckBox -> Consumer { value: Boolean? -> bean.isFocusTraversable = value!! } }, false)
            .set({ bean: CheckBox -> Consumer { value: Boolean? -> bean.isAllowIndeterminate = value!! } }, false)
            .get()
        alignment = Pos.CENTER_LEFT
        contentDisplay = ContentDisplay.LEFT
        graphic = null
    }

}