package com.yyy.server.domain.service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yyy.server.domain.repo.Domain;
import com.yyy.server.domain.repo.DomainRepo;
import com.yyy.server.door.service.DoorController;
import com.yyy.server.user.repo.User;
import com.yyy.server.user.repo.UserRepo;

@RestController
@RequestMapping({ "/domains" })
public class DomainController {
	private Logger logger = LoggerFactory.getLogger(DomainController.class);
	@Autowired
	private DomainRepo repo;
	@Autowired
	private UserRepo userRepo;

	@GetMapping
	public Domain getRootDomain() throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			throw new Exception("User not authenticated!");
		}
		User thisUser = (User) auth.getPrincipal();
		thisUser = userRepo.findOne(thisUser.getId());
		logger.info("Current user" + thisUser);
		Domain domain = thisUser.getDomain();
		if (domain == null) {
			throw new EntityNotFoundException("Root Domain not found for user " + thisUser.getDisplayName());
		}
		logger.info("Current Root Domain:" + domain.toString());
		return domain;
	}

	@PostMapping
	public Domain addRootDomain(@RequestBody Domain domain) {
		domain.setParent(null);// root domain has no parent
		return repo.save(domain);
	}

	@PostMapping("/{pid}/children")
	public Domain addChildDomain(@PathVariable Long pid, @RequestBody Domain domain) {
		Domain parent = repo.findOne(pid);
		if (parent == null) {
			throw new IllegalArgumentException("parent domain id not found!");
		}
		domain.setParent(parent);
		domain.setName(null);// use null name for child domain
		return repo.save(domain);
	}

	@GetMapping("/{id}")
	public Domain getDomain(@PathVariable Long id) {
		Domain domain = repo.findOne(id);
		if (domain == null) {
			throw new IllegalArgumentException("domain id not found!");
		}
		return domain;
	}

	@PutMapping("/{id}")
	public Domain updateDomain(@PathVariable Long id, @RequestBody Domain domain) {
		Domain db = repo.findOne(id);
		if (db == null) {
			throw new IllegalArgumentException("domain id not found!");
		}
		// only allow update the label
		db.setLabel(domain.getLabel());
		return repo.save(db);
	}

	@DeleteMapping("/{id}")
	public void deleteDomain(@PathVariable Long id) {
		repo.delete(id);
	}
}
