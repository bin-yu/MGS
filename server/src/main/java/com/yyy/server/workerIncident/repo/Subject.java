package com.yyy.server.workerIncident.repo;

import javax.persistence.Embeddable;

@Embeddable
public class Subject {
    private Long workerId;
    private String name;

    public Subject() {
        super();
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
