package com.yyy.server.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yyy.server.domain.repo.Domain;
import com.yyy.server.domain.repo.DomainRepo;

@Service
public class DomainService {
	@Autowired
	private DomainRepo domainRepo;
	public Domain validateDomain(Long domainId){
		Domain domain = domainRepo.findOne(domainId);
		if (domain == null) {
			throw new IllegalArgumentException("Domain not found : " + domainId);
		}
		return domain;
	}
}
