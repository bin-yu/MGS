package com.yyy.server.card.repo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.yyy.server.card.repo.Card.CardKey;
import com.yyy.server.worker.repo.Worker;

@Entity
@IdClass(CardKey.class)
public class Card implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6427404249706463042L;

    @Id
    private long doorId;
    @Id
    private long cardNo;
    @ManyToOne
    @JoinColumn(name = "WORKER_ID", referencedColumnName = "ID")
    private Worker worker;
    @Column(nullable = false)
    private Boolean inBlackList = false;

    public Card() {
        super();
    }

    public Card(long doorId, long cardNo, Worker worker) {
        super();
        this.doorId = doorId;
        this.cardNo = cardNo;
        this.worker = worker;
    }



    public long getDoorId() {
        return doorId;
    }

    public void setDoorId(long doorId) {
        this.doorId = doorId;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public long getCardNo() {
        return cardNo;
    }

    public void setCardNo(long cardNo) {
        this.cardNo = cardNo;
    }



    public Boolean getInBlackList() {
        return inBlackList;
    }

    public void setInBlackList(Boolean inBlackList) {
        this.inBlackList = inBlackList;
    }

    @Override
    public String toString() {
        return "Card [doorId=" + doorId + ", cardNo=" + cardNo + ", worker=" + worker + "]";
    }



    public static class CardKey implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 4104102254344379694L;
        private long doorId;
        private long cardNo;

        public CardKey() {
            super();
        }

        public CardKey(long door, long cardNo) {
            super();
            this.doorId = door;
            this.cardNo = cardNo;
        }

        @Override
        public String toString() {
            return "CardKey [door=" + doorId + ", cardNo=" + cardNo + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (cardNo ^ (cardNo >>> 32));
            result = prime * result + (int) (doorId ^ (doorId >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CardKey other = (CardKey) obj;
            if (cardNo != other.cardNo)
                return false;
            if (doorId != other.doorId)
                return false;
            return true;
        }

    }
}
