package com.yyy.server.policy.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yyy.server.worker.repo.Worker;

public enum Action {
    ADD_TO_BLACKLIST {

        @Override
        public boolean apply(Worker worker) {
            if (worker.getInBlackList()) {
                return false;
            }
            worker.setInBlackList(true);
            logger.warn("Put worker into blacklist:" + worker);
            return true;
        }

    },
    MONITOR {

        @Override
        public boolean apply(Worker worker) {
            //TODO: implement monitor
            return false;
        }

    };
    private static Logger logger = LoggerFactory.getLogger(Action.class);
    public abstract boolean apply(Worker worker);
}
