package com.github.windchopper.tools.log.browser.fx;

import com.github.windchopper.tools.log.browser.fs.RemoteFile;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class FileTreeCell extends TreeCell<RemoteFile> {

    private final CheckBox checkBox;
    private BooleanProperty booleanProperty;
    private Callback<TreeItem<RemoteFile>, BooleanProperty> selectedStateCallback;
    private Map<String, BooleanProperty> selectedStateBuffer;

    public FileTreeCell(
        Callback<TreeItem<RemoteFile>, BooleanProperty> selectedStateCallback,
        Map<String, BooleanProperty> selectedStateBuffer) {

        this.selectedStateCallback = selectedStateCallback;
        this.selectedStateBuffer = selectedStateBuffer;

        getStyleClass().add("check-box-tree-cell");

        checkBox = new CheckBox();
        checkBox.setAllowIndeterminate(true);

        setGraphic(null);
    }

    @Override public void updateItem(RemoteFile file, boolean empty) {
        super.updateItem(file, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            TreeItem<RemoteFile> treeItem = getTreeItem();
            setText(treeItem == null ? "" : treeItem.getValue().displayName(false));
            checkBox.setGraphic(treeItem == null ? null : treeItem.getGraphic());
            setGraphic(checkBox);

            if (booleanProperty != null) {
                checkBox.selectedProperty().unbindBidirectional(booleanProperty);
            }

            booleanProperty = selectedStateCallback.call(treeItem);

            if (booleanProperty != null) {
                checkBox.selectedProperty().bindBidirectional(booleanProperty);
            }

            checkBox.setVisible(file != null && file.directory());
            checkBox.setIndeterminate(file != null && !checkBox.isSelected() && selectedStateBuffer.entrySet().stream()
                .anyMatch(entry -> entry.getValue().get() && StringUtils.startsWith(entry.getKey(), file.path())));
        }
    }

}
