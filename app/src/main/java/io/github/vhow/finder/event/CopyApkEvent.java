package io.github.vhow.finder.event;


import java.io.File;

public class CopyApkEvent {
    public final File apkFile;
    public final File destFile;

    public CopyApkEvent(File apkFile, File destFile) {
        this.apkFile = apkFile;
        this.destFile = destFile;
    }
}
