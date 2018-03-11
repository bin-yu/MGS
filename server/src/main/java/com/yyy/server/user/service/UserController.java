package com.yyy.server.user.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.yyy.server.domain.service.DomainService;
import com.yyy.server.user.repo.User;
import com.yyy.server.user.repo.UserRepo;

@RestController
@RequestMapping({ "/{domainId}/users" })
public class UserController {
	@Autowired
	private UserRepo repo;

	@GetMapping
	public Page<User> getUsers(@PathVariable Long domainId,Pageable pageable) throws Exception {
		return repo.findByDomain(new Domain(domainId), pageable);
	}

	@GetMapping("/search")
	public Page<User> searchUsers(@PathVariable Long domainId,Filter filter, Pageable pageable) throws Exception {
		return repo.findByDomainAndDisplayNameLike(new Domain(domainId), filter.getNameLike(), pageable);
	}
	

	@GetMapping("/{id}")
	public User getUser(@PathVariable Long domainId,@PathVariable Long id) {
		User user = repo.getByIdAndDomain(id, new Domain(domainId));
		if (user == null) {
			throw new EntityNotFoundException("User not found for id : " + id);
		}
		return user;
	}

	@PostMapping()
	public User addUser(@PathVariable Long domainId,@RequestBody User user) {
		user.setDomain(new Domain(domainId));
		return repo.save(user);
	}

	@PutMapping("/{id}")
	public User updateUser(@PathVariable Long domainId,@PathVariable Long id, @RequestBody User user) {
		if (!id.equals(user.getId())) {
			throw new IllegalArgumentException("Mismatched id between path variable and request body.");
		}
		user.setDomain(new Domain(domainId));
		return repo.save(user);
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long domainId,@PathVariable Long id) {
		repo.delete(getUser(domainId,id));
	}
	
}
