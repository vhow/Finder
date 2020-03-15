package com.gmail.dailyefforts.filemanager.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileActionEvent {

    private static final String TAG = "FileActionEvent";
    final Action action;
    List<File> fileList = new ArrayList<>();
    boolean fail;
    private int pos;

    FileActionEvent(Action action, File... files) {
        this.action = action;
        Collections.addAll(this.fileList, files);
    }

    @Override
    public String toString() {
        return "FileActionEvent{" +
                "fileList=" + fileList +
                ", action=" + action +
                ", pos=" + pos +
                '}';
    }

    public enum Action {
        OPEN, DELETE, RENAME, SHARE, COPY_TO, MOVE_TO, INFO, GET_FILE_LIST, COPY_APK
    }
}
