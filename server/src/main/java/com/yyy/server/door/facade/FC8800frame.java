package com.yyy.server.door.facade;

import java.util.Arrays;
import java.util.Random;

public class FC8800frame {
    private static int MAX_DATA_COUNT = 340;
    byte[] mFrame;
    private String mSN;
    private String mPassword;
    private int mSource;
    private byte mCmdType;
    private byte mCmdIndex;
    private byte mCmdPar;
    private int mDataLen;
    private byte[] mCmdData;
    private byte mCheckData;

    FC8800frame() {}

    FC8800frame(String sn, String password, int cmdType, int cmdIndex, int cmdPar) {
        int source = this.GetRndNum();
        this.CreateFrame(sn, password, source, cmdType, cmdIndex, cmdPar, 0, new byte[0]);
    }

    FC8800frame(String sn, String password, int cmdType, int cmdIndex, int cmdPar, int dataLen, byte[] data) {
        this.CreateFrame(sn, password, this.GetRndNum(), cmdType, cmdIndex, cmdPar, dataLen, data);
    }

    /*FC8800frame(String sn, String password, int source, int cmdType, int cmdIndex, int cmdPar, int dataLen, byte[] data) {
        this.CreateFrame(sn, password, source, cmdType, cmdIndex, cmdPar, dataLen, data);
    }*/

    public void CreateFrame(String sn, String password, int source, int cmdType, int cmdIndex, int cmdPar, int dataLen, byte[] data) {
        this.SetSN(sn);
        this.SetPassword(password);
        this.SetSource(source);
        this.SetCmdType((short) cmdType);
        this.SetCmdIndex((short) cmdIndex);
        this.SetCmdPar((short) cmdPar);
        this.SetDataLen(dataLen);
        if (dataLen > 0) {
            this.SetDatabuff(data);
        }

        this.Create();
    }

    public void SetSN(String arg0) {
        this.mSN = arg0;
    }

    public String GetSN() {
        return this.mSN;
    }

    public void SetPassword(String arg0) {
        this.mPassword = arg0;
    }

    public String GetPassword() {
        return this.mPassword;
    }

    public void SetSource(int arg0) {
        this.mSource = arg0;
    }

    public int GetSource() {
        return this.mSource;
    }

    public void SetCmdType(short arg0) {
        this.mCmdType = (byte) arg0;
    }

    public int GetCmdType() {
        return ConverterTool.uByte(this.mCmdType);
    }

    public void SetCmdIndex(short arg0) {
        this.mCmdIndex = (byte) arg0;
    }

    public int GetCmdIndex() {
        return ConverterTool.uByte(this.mCmdIndex);
    }

    public void SetCmdPar(short arg0) {
        this.mCmdPar = (byte) arg0;
    }

    public int GetCmdPar() {
        return ConverterTool.uByte(this.mCmdPar);
    }

    public void SetDataLen(int arg0) {
        this.mDataLen = arg0;
    }

    public int GetDataLen() {
        return this.mDataLen;
    }

    public void SetDatabuff(byte[] arg0) {
        this.mCmdData = Arrays.copyOf(arg0, arg0.length);
    }

    public byte[] GetDatabuff() {
        return Arrays.copyOf(this.mCmdData, this.mCmdData.length);
    }

    public int GetCheckData() {
        return ConverterTool.uByte(this.mCheckData);
    }

    public void SetDatabuffSize(int arg0) {
        this.mCmdData = new byte[arg0];
    }

    public void SetDataValue(int arg0, byte arg1) {
        this.mCmdData[arg0] = arg1;
    }

    public void SetDataValue(int arg0, byte[] arg1) {
        for (int arg2 = 0; arg2 < arg1.length; ++arg2) {
            this.mCmdData[arg0 + arg2] = arg1[arg2];
        }

    }

    public byte[] GetFrame() {
        return Arrays.copyOf(this.mFrame, this.mFrame.length);
    }

    private int GetRndNum() {
        Random arg0 = new Random(System.currentTimeMillis());
        long arg1 = (long) (arg0.nextDouble() * 2.146369536E9D);
        return (int) (arg1 + 268435456L);
    }

    public boolean Create() {
        boolean arg2 = false;
        boolean arg3 = false;
        int arg7 = 31 + this.mDataLen + 1 + 2;
        byte[] arg0 = new byte[arg7];
        arg0[0] = 126;
        arg0[arg7 - 1] = 126;
        byte arg8 = 1;
        byte[] arg1 = this.mSN.getBytes();
        System.arraycopy(arg1, 0, arg0, arg8, 16);
        int arg9 = arg8 + 16;
        arg1 = ConverterTool.Hex2Byte(this.mPassword);
        System.arraycopy(arg1, 0, arg0, arg9, 4);
        arg9 += 4;
        arg1 = ConverterTool.longToBytes(ConverterTool.uInt(this.mSource), 4, true);
        System.arraycopy(arg1, 0, arg0, arg9, 4);
        arg9 += 4;
        arg0[arg9++] = this.mCmdType;
        arg0[arg9++] = this.mCmdIndex;
        arg0[arg9++] = this.mCmdPar;
        arg1 = ConverterTool.longToBytes(ConverterTool.uInt(this.mDataLen), 4, true);
        System.arraycopy(arg1, 0, arg0, arg9, 4);
        arg9 += 4;
        int arg4;
        if (this.mDataLen > 0) {
            for (arg4 = 0; arg4 < this.mDataLen; ++arg4) {
                arg0[arg9++] = this.mCmdData[arg4];
            }
        }

        long arg5 = 0L;

        for (arg4 = 1; arg4 < arg7 - 2; ++arg4) {
            arg5 += (long) ConverterTool.uByte(arg0[arg4]);
        }

        this.mCheckData = (byte) ((int) (arg5 % 256L));
        arg0[arg9] = this.mCheckData;
        this.mFrame = this.TranslateCommandFrame(arg0);
        return true;
    }

    private byte[] TranslateCommandFrame(byte[] arg0) {
        int arg2 = 1;
        byte[] arg1 = new byte[arg0.length * 2];
        arg1[0] = arg0[0];

        int arg3;
        for (arg3 = 1; arg3 < arg0.length - 1; ++arg3) {
            int arg4 = ConverterTool.uByte(arg0[arg3]);
            switch (arg4) {
                case 126:
                    arg1[arg2++] = 127;
                    arg1[arg2++] = 1;
                    break;
                case 127:
                    arg1[arg2++] = 127;
                    arg1[arg2++] = 2;
                    break;
                default:
                    arg1[arg2++] = arg0[arg3];
            }
        }

        arg1[arg2++] = arg0[arg3];
        return Arrays.copyOf(arg1, arg2);
    }
}
