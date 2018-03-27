package com.yyy.server.door.proxy.aio;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ProxyRequest {
    public static final int MAX_REQ_SIZE = 4096;
    private static CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    public static String parseRequest(ByteBuffer buf) {
        buf.flip(); // 进入读走模式
        try {
            // 将buf数据转成字符串
            String msg = decoder.decode(buf).toString();
            buf.compact(); // compact buf for next write
            return msg;
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isRequestComplete(ByteBuffer bb) {
        int p = bb.position() - 4;
        if (p < 0)
            return false;
        return (((bb.get(p + 0) == '\r') && (bb.get(p + 1) == '\n') && (bb.get(p + 2) == '\r') && (bb.get(p + 3) == '\n')));
    }
}
