package com.github.windchopper.tools.log.browser.fx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class RemoteFile implements Comparable<RemoteFile> {

    private Path path;
    private boolean selected;

    public RemoteFile(Path path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    public Path getPath() {
        return path;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override public int compareTo(RemoteFile anotherFile) {
        return path.compareTo(anotherFile.path);
    }

    public String displayName(boolean appendDirectoryNameWithSlash) {
        String displayName = StringUtils.defaultString(StringUtils.trimToNull(StringUtils.substringAfterLast(path.toString(), "/")), "/");

        if (Files.isDirectory(path) && appendDirectoryNameWithSlash) {
            displayName += "/";
        }

        return displayName;
    }

    public BooleanProperty createSelectedProperty(Map<String, BooleanProperty> selectedStateBuffer, TreeView<?> treeView, ListView<?> listView) {
        BooleanProperty selectedProperty = selectedStateBuffer.computeIfAbsent(path.toString(), missingPath -> new SimpleBooleanProperty(this, "selected"));

        selectedProperty.addListener((observable, oldValue, newValue) -> {
            treeView.refresh();
            listView.refresh();
        });

        return selectedProperty;
    }

}