package com.yyy.server.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yyy.server.user.repo.User;
import com.yyy.server.user.repo.UserRepo;
@Service
public class LocalUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepo repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.findOneByName(username);
		if (user == null) {
			throw new UsernameNotFoundException("Username " + username + " not found.");
		}
		return user;
	}

}
