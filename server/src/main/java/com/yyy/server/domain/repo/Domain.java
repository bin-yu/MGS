package com.yyy.server.domain.repo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yyy.server.door.repo.Door;
import com.yyy.server.user.repo.User;
import com.yyy.server.worker.repo.Worker;
import com.yyy.server.workerIncident.repo.Incident;
@Entity
@JsonIgnoreProperties(ignoreUnknown=true)
public class Domain implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6100826582496870144L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = true)
    private String name;
    @Column(nullable = false)
    private String label;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")
    @NotFound(action=NotFoundAction.IGNORE)
    private Domain parent;
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Domain> children;

    @JsonIgnore
    @OneToMany(mappedBy = "domain", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<User> users;
    @JsonIgnore
    @OneToMany(mappedBy = "domain", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Incident> incidents;
    @JsonIgnore
    @OneToMany(mappedBy = "domain", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Worker> workers;
    @JsonIgnore
    @OneToMany(mappedBy = "domain", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Door> doors;
    
    public Domain() {
		super();
	}

	public Domain(Long id) {
		super();
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public List<Domain> getChildren() {
        return children;
    }


    public Domain getParent() {
        return parent;
    }

    public void setParent(Domain parent) {
        this.parent = parent;
    }

	@Override
	public String toString() {
		return "Domain [id=" + id + ", name=" + name + ", label=" + label + "]";
	}


}
