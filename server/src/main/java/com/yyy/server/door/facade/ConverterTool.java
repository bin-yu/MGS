package com.yyy.server.door.facade;

import java.util.Arrays;

public class ConverterTool {
    public static byte[] longToBytes(long arg, int arg1, boolean arg2) {
        byte[] arg3 = new byte[8];
        boolean arg4 = false;
        boolean arg5 = false;
        int arg7;
        if (arg2) {
            arg7 = 56;
        } else {
            arg7 = 0;
        }

        for (int arg8 = 0; arg8 < 8; ++arg8) {
            arg3[arg8] = (byte) ((int) (arg >> arg7));
            if (arg2) {
                arg7 -= 8;
            } else {
                arg7 += 8;
            }
        }

        if (arg2) {
            if (arg1 > 8) {
                byte[] arg6 = new byte[arg1];
                System.arraycopy(arg3, 0, arg6, arg1 - 8, 8);
                return arg6;
            } else {
                return Arrays.copyOfRange(arg3, 8 - arg1, 8 - arg1 + arg1);
            }
        } else {
            return Arrays.copyOf(arg3, arg1);
        }
    }

    public static long BytesTolong(byte[] arg, boolean arg0) {
        boolean arg1 = false;
        long arg2 = 0L;
        int arg5;
        if (arg0) {
            if (arg.length > 8) {
                arg = Arrays.copyOfRange(arg, arg.length - 8, arg.length);
            } else {
                byte[] arg4 = new byte[8];
                System.arraycopy(arg, 0, arg4, 8 - arg.length, arg.length);
                arg = Arrays.copyOf(arg4, 8);
            }

            arg5 = 0;
        } else {
            arg = Arrays.copyOf(arg, 8);
            arg5 = 7;
        }

        for (int arg6 = 0; arg6 < 8; ++arg6) {
            arg2 += (long) uByte(arg[arg5]);
            if (arg6 < 7) {
                arg2 <<= 8;
            }

            if (arg0) {
                ++arg5;
            } else {
                --arg5;
            }
        }

        return arg2;
    }

    public static int uByte(byte arg) {
        return arg & 255;
    }

    public static long uInt(int arg) {
        return Long.parseLong(Integer.toHexString(arg), 16);
    }

    public static byte[] BytetoBits(byte arg, boolean arg0) {
        int arg1 = uByte(arg);
        byte[] arg2 = new byte[8];

        for (int arg3 = 0; arg3 < 8; ++arg3) {
            if (arg0) {
                arg2[7 - arg3] = (byte) (arg1 % 2);
            } else {
                arg2[arg3] = (byte) (arg1 % 2);
            }

            arg1 /= 2;
        }

        return arg2;
    }

    public static byte BitstoByte(byte[] arg, boolean arg0) {
        boolean arg1 = false;
        boolean arg2 = false;
        int arg3 = 0;
        int arg6;
        if (arg0) {
            if (arg.length > 8) {
                arg = Arrays.copyOfRange(arg, arg.length - 8, arg.length);
            } else {
                byte[] arg4 = new byte[8];
                System.arraycopy(arg, 0, arg4, 8 - arg.length, arg.length);
                arg = Arrays.copyOf(arg4, 8);
            }

            arg6 = 0;
        } else {
            arg = Arrays.copyOf(arg, 8);
            arg6 = 7;
        }

        for (int arg5 = 0; arg5 < 8; ++arg5) {
            arg3 += arg[arg6];
            if (arg5 < 7) {
                arg3 <<= 1;
            }

            if (arg0) {
                ++arg6;
            } else {
                --arg6;
            }
        }

        return (byte) arg3;
    }

    public static void BytestoBits(byte[] arg, byte[] arg0, int arg1) {
        BytestoBits(arg, arg0, arg1, true);
    }

    public static void BytestoBits(byte[] arg, byte[] arg0, int arg1, boolean arg2) {
        int arg4 = 0;
        //Arrays.fill(arg0, 0);

        for (int arg6 = 0; arg6 < arg.length; ++arg6) {
            byte[] arg5 = BytetoBits(arg[arg6], arg2);
            if (arg4 + 8 <= arg1) {
                System.arraycopy(arg5, 0, arg0, arg4, 8);
            } else {
                System.arraycopy(arg5, 0, arg0, arg4, arg1 - arg4);
            }

            arg4 += 8;
            if (arg4 >= arg1) {
                break;
            }
        }

    }

    public static void BitsToBytes(byte[] arg, byte[] arg0, int arg1) {
        BitsToBytes(arg, arg0, arg1, true);
    }

    public static void BitsToBytes(byte[] arg, byte[] arg0, int arg1, boolean arg2) {
        boolean arg3 = false;
        int arg4 = 0;
        //Arrays.fill(arg, 0);
        byte[] arg5 = new byte[8];

        for (int arg6 = 0; arg6 < arg1; arg6 += 8) {
            System.arraycopy(arg0, arg6, arg5, 0, 8);
            arg[arg4++] = BitstoByte(arg5, arg2);
        }

    }

    public static String Byte2Hex(byte[] arg) {
        int size = arg.length;
        return Byte2Hex(arg, size);
    }

    public static String Byte2Hex(byte[] array, int size) {
        char[] arg0 = new char[size * 2];
        int arg1 = 0;
        char[] arg3 = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            for (int arg4 = 0; arg4 < size; ++arg4) {
                int arg2 = uByte(array[arg4]);
                arg0[arg1++] = arg3[arg2 / 16];
                arg0[arg1++] = arg3[arg2 % 16];
            }

            return (new String(arg0)).toUpperCase();
        } catch (Exception arg5) {
            return new String();
        }
    }

    public static byte[] Hex2Byte(String arg) {
        int arg1 = 0;
        if (arg.length() % 2 == 1) {
            arg = "0" + arg;
        }

        byte[] arg3 = new byte[128];
        byte[] arg4 = "0123456789abcdef".getBytes();

        int arg0;
        for (arg0 = 0; arg0 < arg4.length; ++arg0) {
            arg3[arg4[arg0]] = (byte) arg0;
        }

        arg4 = "ABCDEF".getBytes();

        for (arg0 = 0; arg0 < arg4.length; ++arg0) {
            arg3[arg4[arg0]] = (byte) (arg0 + 10);
        }

        byte[] arg5 = new byte[arg.length() / 2];
        byte[] arg6 = arg.getBytes();

        for (arg0 = 0; arg0 < arg6.length; ++arg0) {
            int arg2 = arg3[arg6[arg0++]] * 16;
            arg2 += arg3[arg6[arg0]];
            arg5[arg1] = (byte) arg2;
            ++arg1;
        }

        return arg5;
    }

    public static String BytesToString(byte[] arg) {
        String arg0 = "";

        for (int arg2 = 0; arg2 < arg.length; ++arg2) {
            int arg1 = uByte(arg[arg2]);
            if (arg2 != arg.length - 1) {
                arg0 = arg0 + arg1 + ",";
            } else {
                arg0 = arg0 + arg1;
            }
        }

        return arg0;
    }
}
