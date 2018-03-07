package com.yyy.server.door.facade;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yyy.server.door.facade.CardData.State;

public abstract class AbstractFC8800 implements DoorSystem {
    private static Logger logger = LoggerFactory.getLogger(AbstractFC8800.class);
    protected String sn;
    protected String password = "FFFFFFFF";

    public AbstractFC8800(String sn, String password) {
        super();
        this.sn = sn;
        this.password = password;
    }

    public AbstractFC8800(String sn) {
        super();
        this.sn = sn;
    }


    @Override
    public String getVersion() throws IOException {
        logger.info("[" + sn + "]DOOR GET VERSION...");
        FC8800frame oRet = sendCmd(1, 8, 0, 0, new byte[0], false);
        return new VersionNumber(oRet.GetDatabuff()).toString();
    }



    @Override
    public CardData readCardData(long cardNo) throws IOException {
        return _readCardData(cardNo, false);
    }

    private CardData _readCardData(long cardNo, boolean existingConnection) throws IOException {
        logger.info("[" + sn + "]DOOR READ CARD..." + cardNo);
        FC8800frame oRet = sendCmd(7, 3, 1, 5, ConverterTool.longToBytes(cardNo, 5, true), existingConnection);
        byte[] data = oRet.GetDatabuff();
        int dataLen = oRet.GetDataLen();
        if (isAllFF(data)) {
            return null;
        }
        return new CardData(data, dataLen);
    }



    private boolean isAllFF(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != -1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CardAreaStatus readCardAreaStatus() throws IOException {
        logger.info("[" + sn + "]DOOR READ CARD AREA STATUS...");
        FC8800frame oRet = sendCmd(7, 1, 0, 0, new byte[0], false);
        return new CardAreaStatus(oRet.GetDatabuff(), oRet.GetDataLen());
    }

    @Override
    public void deleteCard(long[] cardNos) throws IOException {
        _deleteCard(cardNos, false);

    }

    private void _deleteCard(long[] cardNos, boolean existingConnection) throws IOException {
        logger.info("[" + sn + "]DOOR DELETE CARDS..." + Arrays.toString(cardNos));
        int cnt = cardNos.length;
        int dataLen = 4 + 5 * cnt;
        byte[] data = new byte[dataLen];
        byte[] cntBytes = ConverterTool.longToBytes(cnt, 4, true);
        int pos = 0;
        System.arraycopy(cntBytes, 0, data, pos, 4);
        pos += 4;
        for (int i = 0; i < cnt; i++) {
            byte[] cardBytes = ConverterTool.longToBytes(cardNos[i], 5, true);
            System.arraycopy(cardBytes, 0, data, pos, 5);
            pos += 5;
        }
        FC8800frame oRet = sendCmd(7, 5, 0, dataLen, data, existingConnection);
    }

    @Override
    public void addCard(CardData[] cards) throws IOException {
        _addCard(cards, false);
    }

    private void _addCard(CardData[] cards, boolean existingConnection) throws IOException {
        logger.info("[" + sn + "]DOOR ADD CARDS..." + Arrays.toString(cards));
        int cnt = cards.length;
        int dataLen = 4 + 33 * cnt;
        byte[] data = new byte[dataLen];
        byte[] cntBytes = ConverterTool.longToBytes(cnt, 4, true);
        int pos = 0;
        System.arraycopy(cntBytes, 0, data, pos, 4);
        pos += 4;
        for (int i = 0; i < cnt; i++) {
            byte[] cardBytes = cards[i].toRawData();
            System.arraycopy(cardBytes, 0, data, pos, 33);
            pos += 33;
        }
        sendCmd(7, 4, 0, dataLen, data, existingConnection);
    }

    @Override
    public void addCardToBlackList(long cardNo) throws IOException {
        connect();
        try {
            CardData card = _readCardData(cardNo, true);
            _deleteCard(new long[] {cardNo}, true);
            card.setState(State.BLACKLIST);
            _addCard(new CardData[] {card}, true);
        } finally {
            disconnect();
        }
    }

    protected FC8800frame sendCmd(int cmdType, int cmdIndex, int cmdPar, int dataLen, byte[] data, boolean existingConnection) throws IOException {
        if (!existingConnection) {
            connect();
        }
        try {
            FC8800frame req = new FC8800frame(sn, password, cmdType, cmdIndex, cmdPar, dataLen, data);
            byte[] request = req.GetFrame();
            sendRequest(request);
            FC8800frame resp = readAndParseResponse();
            printResponse(resp);
            validateResponse(req, resp);
            return resp;
        } finally {
            if (!existingConnection) {
                disconnect();
            }
        }
    }

    protected void validateResponse(FC8800frame req, FC8800frame resp) throws IOException {
        if (resp.GetSource() != req.GetSource()) {
            throw new IOException("Mismatched command source! req:" + req.GetSource() + ",resp:" + resp.GetSource());
        }
    }

    private FC8800frame readAndParseResponse() throws IOException {
        FC8800frameCompile parser = new FC8800frameCompile();
        boolean parseEnd = false;
        byte[] response = new byte[1024];
        do {
            int cnt = receiveResponse(response);
            if (cnt < 0) {
                //reached end
                break;
            }
            for (int i = 0; i < cnt; i++) {
                if (parser.CompileData(response[i])) {
                    //parse reach to end
                    parseEnd = true;
                    break;
                }
            }
        } while (!parseEnd);

        return parser.GetRxFrame();
    }



    private void printResponse(FC8800frame oRet) {
        String sRtn;
        sRtn = "\n----------Parsed Data Begin---------";
        sRtn += "\nSN:" + oRet.GetSN();
        sRtn += "\nPassword:" + oRet.GetPassword();
        sRtn += "\nSource:0x" + Integer.toHexString(oRet.GetSource());
        sRtn += "\nCmdType：0x" + Integer.toHexString(oRet.GetCmdType());
        sRtn += "\nCmdIndex：0x" + Integer.toHexString(oRet.GetCmdIndex());
        sRtn += "\nCmdPar：0x" + Integer.toHexString(oRet.GetCmdPar());
        sRtn += "\nDataLen：" + oRet.GetDataLen();
        if (oRet.GetDataLen() > 0) {
            sRtn += "\nData：" + ConverterTool.Byte2Hex(oRet.GetDatabuff());
        }
        sRtn += "----------Parsed Data End---------";
        logger.info(sRtn);
    }

    protected abstract void connect() throws UnknownHostException, IOException;

    protected abstract void disconnect();

    protected abstract void sendRequest(byte[] request) throws IOException;

    protected abstract int receiveResponse(byte[] buffer) throws IOException;
}
