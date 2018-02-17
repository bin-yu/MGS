package com.yyy.server.door.facade;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class CardAreaStatus implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -8445454877645781924L;
    private long orderedCap;
    private long orderedUsed;
    private long disorderedCap;
    private long disorderedUsed;

    public CardAreaStatus(byte[] dataRaw, int dataLen) throws IOException {
        if (dataLen < 16) {
            throw new IOException("invalid card area status data, len=" + dataLen);
        }
        orderedCap = ConverterTool.BytesTolong(Arrays.copyOfRange(dataRaw, 0, 4), true);
        orderedUsed = ConverterTool.BytesTolong(Arrays.copyOfRange(dataRaw, 4, 8), true);
        disorderedCap = ConverterTool.BytesTolong(Arrays.copyOfRange(dataRaw, 8, 12), true);
        disorderedUsed = ConverterTool.BytesTolong(Arrays.copyOfRange(dataRaw, 12, 16), true);
    }

    public long getOrderedCap() {
        return orderedCap;
    }

    public long getOrderedUsed() {
        return orderedUsed;
    }

    public long getDisorderedCap() {
        return disorderedCap;
    }

    public long getDisorderedUsed() {
        return disorderedUsed;
    }

    @Override
    public String toString() {
        return "CardAreaStatus [orderedCap=" + orderedCap + ", orderedUsed=" + orderedUsed + ", disorderedCap=" + disorderedCap + ", disorderedUsed=" + disorderedUsed + "]";
    }

}
