package com.yyy.server.door.facade;

import java.io.IOException;

public interface DoorSystem {
    String getVersion() throws IOException;

    CardData readCardData(long cardNo) throws IOException;

    CardAreaStatus readCardAreaStatus() throws IOException;

    void deleteCard(long cardNos[]) throws IOException;

    void addCard(CardData[] card) throws IOException;

    void addCardToBlackList(long cardNo) throws IOException;
}
