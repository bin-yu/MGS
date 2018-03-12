package com.yyy.server.workerIncident.repo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yyy.server.domain.repo.Domain;
import com.yyy.server.worker.repo.Worker;

@Entity
@Table(indexes={@Index(name="idx_inc_domain_and_id",columnList="DOMAIN_ID,ID")})
@JsonIgnoreProperties(ignoreUnknown=true)
public class Incident implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -4247534137824786323L;

    public static enum Category {
        RED(1), BLACK(-3);
        private int factor = 0;

        private Category(int factor) {
            this.factor = factor;
        }

        public int calScore(int severity) {
            return factor * severity;
        }
    }

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private Category category;
    @Column(nullable = false)
    private int severity;
    @JsonIgnore
    @Column(nullable = false)
    private Date recordTime;
    @ManyToOne
    @JoinColumn(name = "WORKER_ID", referencedColumnName = "ID")
    private Worker subject;
    @Column(nullable = false)
    private Date happenTime;
    @Column(nullable = true)
    private String location;
    @Column(nullable = true)
    private String title;
    @Column(nullable = true)
    private String description;
    @JsonIgnore
    @ManyToOne(optional=false,fetch = FetchType.LAZY)
    private Domain domain;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }


    public Worker getSubject() {
        return subject;
    }

    public void setSubject(Worker subject) {
        this.subject = subject;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public Date getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Date happenTime) {
        this.happenTime = happenTime;
    }

    public Long getId() {
        return id;
    }

    

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public int calScore() {
        return this.category.calScore(this.severity);
    }
}
