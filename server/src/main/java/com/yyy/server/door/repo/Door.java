package com.yyy.server.door.repo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Door implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5357656467356800050L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    protected String protocol = "TCP";
    @Column(nullable = false)
    protected String sn;
    @Column(nullable = true)
    protected String password = "FFFFFFFF";
    @Column(nullable = false)
    private String ip;
    @Column(nullable = false)
    private int port;
    @Column(nullable = true)
    private String label;
    @Column(nullable = false, columnDefinition ="char(36) DEFAULT 'TEST'")
    private String secret;


    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
