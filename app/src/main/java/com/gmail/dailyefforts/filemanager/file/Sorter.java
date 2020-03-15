package com.gmail.dailyefforts.filemanager.file;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.Comparator;

class Sorter {
    private static Sorter ourInstance = new Sorter();
    private Comparator<File> comparator = LastModifiedFileComparator.LASTMODIFIED_REVERSE;

    private Sorter() {
    }

    public static Sorter getInstance() {
        return ourInstance;
    }

    Comparator<File> get() {
        return this.comparator;
    }

    void set(Comparator<File> comparator) {
        this.comparator = comparator;
    }
}
