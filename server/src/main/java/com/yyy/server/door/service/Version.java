package com.yyy.server.door.service;

import java.io.Serializable;

public class Version implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1942734745892767049L;
    private String version;

    public Version(String version) {
        super();
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
}
