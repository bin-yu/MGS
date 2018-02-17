package com.yyy.server.policy.repo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PolicyInDb implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -1505873488490757670L;
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String content;
    
    public PolicyInDb() {
        super();
    }

    public PolicyInDb(Long id, String name, String content) {
        super();
        this.id = id;
        this.name = name;
        this.content = content;
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
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    

}
