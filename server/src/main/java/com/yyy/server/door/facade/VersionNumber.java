package com.yyy.server.door.facade;

import java.io.IOException;

public class VersionNumber {
    private int ver[] = new int[4];

    public VersionNumber(byte versionBytes[]) throws IOException {
        if (versionBytes.length != 4) {
            throw new IOException("Invalid version data length:" + versionBytes.length);
        }
        for (int i = 0; i < 4; i++) {
            ver[i] = versionBytes[i] & 0x0f;
        }
    }

    @Override
    public String toString() {
        return "" + ver[0] + ver[1] + "." + ver[2] + ver[3];
    }

}
