package com.yyy.server.door.facade;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class CardData implements Serializable{
    public static enum State {
        NORMAL(0), HANG(1), BLACKLIST(2);
        private int value;

        private State(int value) {
            this.value = value;
        }

        public static State valueOf(int value) throws IOException {
            for (State s : State.values()) {
                if (value == s.value) {
                    return s;
                }
            }
            throw new IOException("Invalid state value:" + value);
        }

        public int getValue() {
            return value;
        }

    }

    /**
     * 
     */
    private static final long serialVersionUID = 4015232872188226926L;
    private long cardNo;
    private String password = "";
    private String expireTime = "8812302359";
    private String timePieceIndex = "01010000";
    private long readCount = 0xffff;//有效次数
    private int openLock = 3;//权限 - 0011 (1 and 2 doors)
    private int cardMode = 0;//特权模式 - normal card
    private State state = State.NORMAL;//卡状态
    private String HldPwr = "00000000";//节假日
    private int inputOutputState = 0;//出入标志
    private String lastReadTime = "FFFFFFFFFFFF";;//最近读卡时间

    public CardData() {
        super();
    }

    public CardData(long cardNo) {
        super();
        this.cardNo = cardNo;
    }

    public CardData(byte[] dataRaw, int dataLen) throws IOException {
        if (dataLen < 33) {
            throw new IOException("invalid card data, len=" + dataLen);
        }
        int pos = 0;
        cardNo = ConverterTool.BytesTolong(Arrays.copyOfRange(dataRaw, pos, (pos += 5)), true);
        password = BCD.BCDtoString((Arrays.copyOfRange(dataRaw, pos, (pos += 4))));
        expireTime = BCD.BCDtoString(Arrays.copyOfRange(dataRaw, pos, (pos += 5)));
        timePieceIndex = ConverterTool.Byte2Hex(Arrays.copyOfRange(dataRaw, pos, (pos += 4)));
        readCount = ConverterTool.BytesTolong(Arrays.copyOfRange(dataRaw, pos, (pos += 2)), true);
        byte pri = dataRaw[pos++];
        openLock = pri >>> 4;
        cardMode = pri & 0x0f;
        state = State.valueOf(dataRaw[pos++]);
        HldPwr = ConverterTool.Byte2Hex(Arrays.copyOfRange(dataRaw, pos, (pos += 4)));
        inputOutputState = dataRaw[pos++];
        lastReadTime = ConverterTool.Byte2Hex(Arrays.copyOfRange(dataRaw, pos, (pos += 6)));
    }

    public byte[] toRawData() {
        byte[] data = new byte[33];
        int pos = 0;

        writeLong(cardNo, data, pos, 5);
        pos += 5;
        writeBCDCode(password, data, pos, 4);
        pos += 4;
        writeBCDCode(expireTime, data, pos, 5);
        pos += 5;
        writeString(timePieceIndex, data, pos, 4);
        pos += 4;
        writeLong(readCount, data, pos, 2);
        pos += 2;
        data[pos++] = (byte) ((openLock << 4) | cardMode);
        data[pos++] = (byte) state.getValue();
        writeString(HldPwr, data, pos, 4);
        pos += 4;
        data[pos++] = (byte) inputOutputState;
        writeString(lastReadTime, data, pos, 6);
        pos += 6;

        return data;
    }

    private void writeLong(long value, byte[] buf, int pos, int size) {
        byte[] cntBytes = ConverterTool.longToBytes(value, size, true);
        System.arraycopy(cntBytes, 0, buf, pos, size);
    }

    private void writeString(String value, byte[] buf, int pos, int size) {
        byte[] cntBytes = ConverterTool.Hex2Byte(value);
        System.arraycopy(cntBytes, 0, buf, pos, size);
    }

    private void writeBCDCode(String value, byte[] buf, int pos, int size) {

        for (int i = 0; i < size; i++) {
            buf[pos + i] = (byte) 0xFF;
        }
        if (value != null && value.length() > 0) {
            byte[] cntBytes = BCD.DecimalStrToBCD(value);
            System.arraycopy(cntBytes, 0, buf, pos, cntBytes.length);
        }
    }

    public long getCardNo() {
        return cardNo;
    }

    public void setCardNo(long cardNo) {
        this.cardNo = cardNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getTimePieceIndex() {
        return timePieceIndex;
    }

    public void setTimePieceIndex(String timePieceIndex) {
        this.timePieceIndex = timePieceIndex;
    }

    public long getReadCount() {
        return readCount;
    }

    public void setReadCount(long readCount) {
        this.readCount = readCount;
    }

    public int getOpenLock() {
        return openLock;
    }

    public void setOpenLock(int openLock) {
        this.openLock = openLock;
    }

    public int getCardMode() {
        return cardMode;
    }

    public void setCardMode(int cardMode) {
        this.cardMode = cardMode;
    }



    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getHldPwr() {
        return HldPwr;
    }

    public void setHldPwr(String hldPwr) {
        HldPwr = hldPwr;
    }

    public int getInputOutputState() {
        return inputOutputState;
    }

    public void setInputOutputState(int inputOutputState) {
        this.inputOutputState = inputOutputState;
    }

    public String getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(String lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    @Override
    public String toString() {
        return "CardData [cardNo=" + cardNo + ", password=" + password + ", expireTime=" + expireTime + ", timePieceIndex=" + timePieceIndex + ", readCount=" + readCount
                                        + ", openLock=" + openLock + ", cardMode=" + cardMode + ", state=" + state + ", HldPwr=" + HldPwr + ", inputOutputState=" + inputOutputState
                                        + ", lastReadTime=" + lastReadTime + "]";
    }
    
    
}
