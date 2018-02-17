package com.yyy.server.user.repo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 5950196877221201966L;

    public static enum Role {
        ADMIN, USER
    }
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    protected String name;
    @Column(nullable = false)
    protected String displayName;
    @Column(nullable = false)
    protected String password;
    @Column(nullable = false)
    private Role role = Role.ADMIN;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


}
