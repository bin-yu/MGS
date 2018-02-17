package com.yyy.server.policy.repo;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Policy implements Serializable {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.addMixIn(Policy.class, PolicyMixin.class);
    }
    /**
     * 
     */
    private static final long serialVersionUID = -3564208110816689366L;
    private Long id;
    private String name;
    private Condition condition;
    private Action action;

    public Policy() {
        super();
    }

    public PolicyInDb toDbObject() throws JsonProcessingException {
        String content = OBJECT_MAPPER.writeValueAsString(this);
        return new PolicyInDb(id, name, content);
    }

    public static Policy fromDbObject(PolicyInDb dbObj) throws IOException {
        Policy policy = OBJECT_MAPPER.readValue(dbObj.getContent(), Policy.class);
        policy.setId(dbObj.getId());
        policy.setName(dbObj.getName());
        return policy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }


}
