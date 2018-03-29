package com.yyy.server.door.repo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yyy.server.card.repo.Card;
import com.yyy.server.domain.repo.Domain;

@Entity
@Table(indexes={@Index(name="idx_door_domain_and_id",columnList="DOMAIN_ID,ID")})
@JsonIgnoreProperties(ignoreUnknown=true)
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
    @JsonIgnore()
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Domain domain;
    
    @JsonIgnore
    @OneToMany(mappedBy = "doorId", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Card> cards;

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
    @JsonGetter
	public String getSecret() {
		return secret;
	}
	@JsonIgnore
	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Domain getDomain() {
		return domain;
	}
}
