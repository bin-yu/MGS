package com.yyy.server.user.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yyy.server.user.repo.User;
import com.yyy.server.user.repo.UserRepo;

@RestController
@RequestMapping({ "/users" })
public class UserController {
	@Autowired
	private UserRepo repo;

	@GetMapping
	public Page<User> getUsers(Pageable pageable) throws Exception {
		Page<User> users = repo.findAll(pageable);
		return users;
	}

	@GetMapping("/search")
	public Page<User> findUsers(String nameLike, Pageable pageable) throws Exception {
		return repo.findByDisplayNameLike(nameLike, pageable);
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable Long id) {
		User user = repo.findOne(id);
		if (user == null) {
			throw new EntityNotFoundException("User not found for id : " + id);
		}
		return user;
	}

	@PostMapping()
	public User addUser(@RequestBody User user) {
		return repo.save(user);
	}

	@PutMapping("/{id}")
	public User updateUser(@PathVariable Long id, @RequestBody User user) {
		if (!id.equals(user.getId())) {
			throw new IllegalArgumentException("Mismatched id between path variable and request body.");
		}
		return repo.save(user);
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
		repo.delete(id);
	}
}
