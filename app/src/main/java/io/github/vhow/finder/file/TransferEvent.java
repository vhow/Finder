package io.github.vhow.finder.file;

import java.io.File;

class TransferEvent {
    public final Type type;
    final File src;
    final File dst;

    TransferEvent(Type type, File src, File dst) {
        this.type = type;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return type + ", src: " + src + ", dst: " + dst;
    }

    enum Type {
        COPY_FILE, COPY_TO_DIR, MOVE_TO_DIR
    }
}
