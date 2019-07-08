package com.github.windchopper.tools.log.browser.fs;

import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import org.apache.commons.lang3.StringUtils;

public abstract class RemoteFile implements Comparable<RemoteFile> {

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

    public static void updateCell(Cell<RemoteFile> cell, RemoteFile file, boolean empty) {
        if (empty) {
            cell.setText(null);
        } else {
            String text = StringUtils.defaultString(StringUtils.trimToNull(StringUtils.substringAfterLast(file.path(), "/")), "/");

            if (file.directory() && cell instanceof ListCell) {
                text += "/";
            }

            cell.setText(text);
        }
    }

}
