package com.github.windchopper.tools.log.browser.fs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public abstract class RemoteFile implements Comparable<RemoteFile> {

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public abstract String path();
    public abstract boolean directory();

    @Override public int compareTo(RemoteFile anotherFile) {
        int result = directory() == anotherFile.directory() ? 0 : directory() ? -1 : +1;

        if (result != 0) {
            return result;
        }

        String name = StringUtils.substringAfterLast(path(), "/");
        String anotherName = StringUtils.substringAfterLast(anotherFile.path(), "/");

        result = StringUtils.equals(name, "..") == StringUtils.equals(anotherName, "..") ? 0 : StringUtils.equals(name, "..") ? -1 : +1;

        if (result != 0) {
            return result;
        }

        return name.compareTo(anotherName);
    }

    public String displayName(boolean appendDirectoryNameWithSlash) {
        String displayName = StringUtils.defaultString(StringUtils.trimToNull(StringUtils.substringAfterLast(path(), "/")), "/");

        if (directory() && appendDirectoryNameWithSlash) {
            displayName += "/";
        }

        return displayName;
    }

    public BooleanProperty createSelectedProperty(Map<String, BooleanProperty> selectedStateBuffer, TreeView<?> treeView, ListView<?> listView) {
        BooleanProperty selectedProperty = selectedStateBuffer.computeIfAbsent(path(), missingPath -> new SimpleBooleanProperty(this, "selected"));

        selectedProperty.addListener((observable, oldValue, newValue) -> {
            treeView.refresh();
            listView.refresh();
        });

        return selectedProperty;
    }

}
