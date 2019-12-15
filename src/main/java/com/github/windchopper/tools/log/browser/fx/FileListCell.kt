package com.github.windchopper.tools.log.browser.fx;

import com.github.windchopper.common.util.Pipeliner;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class FileListCell extends ListCell<RemoteFile> {

    private final CheckBox checkBox;
    private BooleanProperty booleanProperty;
    private Callback<RemoteFile, BooleanProperty> selectedStateCallback;
    private Map<String, BooleanProperty> selectedStateBuffer;

    public FileListCell(
        Callback<RemoteFile, BooleanProperty> selectedStateCallback,
        Map<String, BooleanProperty> selectedStateBuffer) {

        this.selectedStateCallback = selectedStateCallback;
        this.selectedStateBuffer = selectedStateBuffer;

        getStyleClass().add("check-box-list-cell");

        checkBox = Pipeliner.of(CheckBox::new)
            .set(bean -> bean::setFocusTraversable, false)
            .set(bean -> bean::setAllowIndeterminate, false)
            .get();

        setAlignment(Pos.CENTER_LEFT);
        setContentDisplay(ContentDisplay.LEFT);
        setGraphic(null);
    }

    @Override public void updateItem(RemoteFile file, boolean empty) {
        super.updateItem(file, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(checkBox);
            setText(file == null ? "" : file.displayName(true));

            if (booleanProperty != null) {
                checkBox.selectedProperty().unbindBidirectional(booleanProperty);
            }

            booleanProperty = selectedStateCallback.call(file);

            if (booleanProperty != null) {
                checkBox.selectedProperty().bindBidirectional(booleanProperty);
            }

            checkBox.setVisible(file != null && file.isDirectory());
            checkBox.setIndeterminate(file != null && !checkBox.isSelected() && selectedStateBuffer.entrySet().stream()
                .anyMatch(entry -> entry.getValue().get() && StringUtils.startsWith(entry.getKey(), file.getPath().toString())));
        }
    }

}
