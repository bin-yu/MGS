package com.yyy.server.worker.repo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.yyy.server.workerIncident.repo.Incident;
import com.yyy.server.workerIncident.repo.Incident.Category;

@Entity
@Table(indexes = { @Index(name = "idx_worker_domain_and_id", columnList = "DOMAIN_ID,ID") })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Worker implements Serializable {
	private static final int INITIAL_SCORE = 100;

	private static final long serialVersionUID = -8743126316266346607L;

	static enum TrainingState {
		NOT_TRAINED, TRAINED, RETRAINED
	}

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String idNo;

	@Column(nullable = true)
	private String idType;

	@Column(nullable = true)
	private String types;

	@Column(nullable = true)
	private String[] phoneNums;

	@Column(nullable = true)
	private String employer;
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean inBlackList = false;
	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private TrainingState trainingState = TrainingState.NOT_TRAINED;
	@JsonIgnore()
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Domain domain;
	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private int score = 0;

	@JsonIgnore
	@OneToMany(mappedBy = "subject", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Incident> incidents;

	@JsonIgnore
	@OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
	private List<Card> cards;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String[] getPhoneNums() {
		return phoneNums;
	}

	public void setPhoneNums(String[] phoneNums) {
		this.phoneNums = phoneNums;
	}

	public Long getId() {
		return id;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public List<Incident> getIncidents() {
		return incidents;
	}

	public void setIncidents(List<Incident> incidents) {
		this.incidents = incidents;
	}

	public TrainingState getTrainingState() {
		return trainingState;
	}

	public void setTrainingState(TrainingState trainingState) {
		this.trainingState = trainingState;
	}

	public Boolean getInBlackList() {
		return inBlackList;
	}

	public void setInBlackList(Boolean inBlackList) {
		this.inBlackList = inBlackList;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Domain getDomain() {
		return domain;
	}

	@JsonGetter
	public int getScore() {
		return score;
	}

	@JsonIgnore
	public void setScore(int score) {
		this.score = score;
	}

	@JsonGetter
	public Map<Category, Integer> getIncidentCnts() {
		Map<Category, Integer> cntMap = new HashMap<Category, Integer>();
		for (Category cat : Category.values()) {
			cntMap.put(cat, Integer.valueOf(0));
		}
		if (this.getIncidents() == null) {
			return cntMap;
		}
		for (Incident inc : this.getIncidents()) {
			Integer cnt = cntMap.get(inc.getCategory());
			cntMap.put(inc.getCategory(), Integer.valueOf(cnt + 1));
		}
		return cntMap;
	}

	@Override
	public String toString() {
		return "Worker [id=" + id + ", name=" + name + ", idNo=" + idNo + ", inBlackList=" + inBlackList + "]";
	}

	public void passTraining() {
		// 1. change training state
		switch (this.trainingState) {
		case NOT_TRAINED:
			this.trainingState = TrainingState.TRAINED;
			break;
		case TRAINED:
		case RETRAINED:
			this.trainingState = TrainingState.RETRAINED;
			break;
		}
		// 2. pull from blacklist
		this.setInBlackList(false);
		// 3. reset the score
		if (this.getScore() < INITIAL_SCORE) {
			this.setScore(INITIAL_SCORE);
		}
	}
}
