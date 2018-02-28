package com.yyy.server.site.repo;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
@Entity
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
    private String label;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")
    private Domain parent;
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Domain> children;

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

    @JsonIgnore
    public Domain getParent() {
        return parent;
    }

    @JsonSetter
    public void setParent(Domain parent) {
        this.parent = parent;
    }


}
