package com.yyy.server.door.facade;

import java.util.Arrays;

public class FC8800frameCompile {
    private int iRXDStep = 0;
    private byte[] bRXDbuf = new byte[16];
    private boolean bRXDTranslate = false;
    private int iRXDDataIndex = 0;
    private FC8800frame oRxFrame;

    private void IniRXDValue() {
        iRXDStep = 0;
        //Arrays.fill(bRXDbuf, 0);
        bRXDTranslate = false;
        iRXDDataIndex = 0;
        oRxFrame = new FC8800frame();
    }

    private void AddRxData(byte arg) {
        if (iRXDDataIndex == 0) {
            //Arrays.fill(bRXDbuf, 0);
        }

        bRXDbuf[iRXDDataIndex++] = arg;
    }

    public FC8800frame GetRxFrame() {
        return oRxFrame;
    }

    public boolean CompileData(byte arg) {
        int arg0 = ConverterTool.uByte(arg);
        if (arg0 == 126) {
            IniRXDValue();
            return false;
        } else if (arg0 == 127) {
            bRXDTranslate = true;
            return false;
        } else {
            if (bRXDTranslate) {
                switch (arg0) {
                    case 1:
                        arg0 = 126;
                        break;
                    case 2:
                        arg0 = 127;
                        break;
                    default:
                        IniRXDValue();
                        return false;
                }

                bRXDTranslate = false;
            }

            switch (iRXDStep) {
                case 0:
                    if (iRXDDataIndex != 4) {
                        AddRxData(arg);
                        if (iRXDDataIndex == 4) {
                            oRxFrame.SetSource((int) ConverterTool.BytesTolong(Arrays.copyOfRange(bRXDbuf, 0, 4), true));
                            iRXDDataIndex = 0;
                            ++iRXDStep;
                        }
                    } else {
                        iRXDDataIndex = 0;
                    }

                    return false;
                case 1:
                    if (iRXDDataIndex != 16) {
                        AddRxData(arg);
                        if (iRXDDataIndex == 16) {
                            oRxFrame.SetSN(new String(bRXDbuf, 0, 16));
                            iRXDDataIndex = 0;
                            ++iRXDStep;
                        }
                    } else {
                        iRXDDataIndex = 0;
                    }

                    return false;
                case 2:
                    if (iRXDDataIndex != 4) {
                        AddRxData(arg);
                        if (iRXDDataIndex == 4) {
                            oRxFrame.SetPassword(ConverterTool.Byte2Hex(Arrays.copyOfRange(bRXDbuf, 0, 4)));
                            iRXDDataIndex = 0;
                            ++iRXDStep;
                        }
                    } else {
                        iRXDDataIndex = 0;
                    }

                    return false;
                case 3:
                    oRxFrame.SetCmdType((short) arg);
                    ++iRXDStep;
                    return false;
                case 4:
                    oRxFrame.SetCmdIndex((short) arg);
                    ++iRXDStep;
                    return false;
                case 5:
                    oRxFrame.SetCmdPar((short) arg);
                    ++iRXDStep;
                    return false;
                case 6:
                    if (iRXDDataIndex != 4) {
                        AddRxData(arg);
                        if (iRXDDataIndex == 4) {
                            oRxFrame.SetDataLen((int) ConverterTool.BytesTolong(Arrays.copyOfRange(bRXDbuf, 0, 4), true));
                            iRXDDataIndex = 0;
                            ++iRXDStep;
                            if (oRxFrame.GetDataLen() != 0) {
                                oRxFrame.SetDatabuffSize(oRxFrame.GetDataLen());
                            } else {
                                ++iRXDStep;
                            }
                        }

                        return false;
                    }

                    iRXDDataIndex = 0;
                    return false;
                case 7:
                    if (iRXDDataIndex != oRxFrame.GetDataLen()) {
                        oRxFrame.SetDataValue(iRXDDataIndex++, arg);
                        if (iRXDDataIndex == oRxFrame.GetDataLen()) {
                            iRXDDataIndex = 0;
                            ++iRXDStep;
                        }
                    } else {
                        iRXDDataIndex = 0;
                    }

                    return false;
                case 8:
                    ++iRXDStep;
                    oRxFrame.Create();
                    if (oRxFrame.GetCheckData() == arg0) {
                        return true;
                    }

                    return false;
                default:
                    IniRXDValue();
                    return false;
            }
        }
    }
}
