package com.yyy.server.door.facade;

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockFC8800 extends AbstractFC8800 {
    private static Logger logger = LoggerFactory.getLogger(MockFC8800.class);

    private static enum Cmd {
        GET_VERSION, READ_CARD, READ_CARD_AREA_STATUS, DELETE_CARD, ADD_CARD
    }

    private Cmd currentCmd;
    private boolean responseSent = false;

    public MockFC8800(String sn) {
        super(sn);
    }

    @Override
    protected void connect() throws UnknownHostException, IOException {
        logger.info("[" + sn + "]DOOR CONNECTED!");
    }

    @Override
    protected void disconnect() {
        logger.info("[" + sn + "]DOOR DISCONNECTED!");
    }



    @Override
    public String getVersion() throws IOException {
        currentCmd = Cmd.GET_VERSION;
        return super.getVersion();
    }

    @Override
    public CardData readCardData(long cardNo) throws IOException {
        currentCmd = Cmd.READ_CARD;
        return super.readCardData(cardNo);
    }

    @Override
    public CardAreaStatus readCardAreaStatus() throws IOException {
        currentCmd = Cmd.READ_CARD_AREA_STATUS;
        return super.readCardAreaStatus();
    }

    @Override
    public void deleteCard(long[] cardNos) throws IOException {
        currentCmd = Cmd.DELETE_CARD;
        super.deleteCard(cardNos);
    }

    @Override
    public void addCard(CardData[] cards) throws IOException {
        currentCmd = Cmd.ADD_CARD;
        super.addCard(cards);
    }

    @Override
    protected void sendRequest(byte[] request) throws IOException {
        responseSent = false;
        logger.info("[" + sn + "]DOOR SEND DATA:" + ConverterTool.Byte2Hex(request));
    }

    @Override
    protected int receiveResponse(byte[] buffer) throws IOException {
        if (responseSent) {
            return -1;
        }
        responseSent = true;
        switch (currentCmd) {
            case GET_VERSION:
                //v99.22
                return writeString(buffer, constructRespData("31", "08", "00", "00000004", "39393232"));
            case READ_CARD_AREA_STATUS:
                //16(1),32(2)
                return writeString(buffer, constructRespData("37", "01", "00", "00000010", "00000010000000010000002000000002"));
            case READ_CARD:
                //1-12345678-9912312359-01020304-000F-30-00-00000000-00-FFFFFFFFFFFF
                return writeString(buffer, constructRespData("37", "03", "01", "00000021", "000000000112345678991231235901020304000F30000000000000FFFFFFFFFFFF"));
            case DELETE_CARD:
            case ADD_CARD:
                //ok
                return writeString(buffer, constructRespData("21", "01", "00", "00000000", ""));

        }
        return -1;
    }

    private String constructRespData(String cmdType, String cmdIndex, String cmdPar, String dataLen, String data) {
        String allbytesStr = "00000000" + ConverterTool.Byte2Hex(sn.getBytes()) + password + cmdType + cmdIndex + cmdPar + dataLen + data;
        byte[] allbytes = ConverterTool.Hex2Byte(allbytesStr);
        long checksum = 0;
        for (byte b : allbytes) {
            checksum += b;
        }
        checksum %= 256;
        String ck = ConverterTool.Byte2Hex(ConverterTool.longToBytes(checksum, 1, true));
        return "7E" + allbytesStr + ck + "7E";
    }

    @Override
    protected void validateResponse(FC8800frame req, FC8800frame resp) throws IOException {
        //do nothing
    }

    private int writeString(byte[] buffer, String value) {
        logger.info("[" + sn + "]DOOR RECV DATA:" + value);
        byte[] bytes = ConverterTool.Hex2Byte(value);
        System.arraycopy(bytes, 0, buffer, 0, bytes.length);
        return bytes.length;
    }

}
