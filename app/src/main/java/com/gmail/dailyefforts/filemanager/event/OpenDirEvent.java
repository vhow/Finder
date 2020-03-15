package com.gmail.dailyefforts.filemanager.event;

import java.io.File;

public class OpenDirEvent {
    public final File file;

    public OpenDirEvent(File file) {
        this.file = file;
    }
}
