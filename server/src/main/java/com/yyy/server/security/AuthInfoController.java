package com.yyy.server.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yyy.server.user.repo.User;

@RestController
@RequestMapping({ "/authInfo" })
public class AuthInfoController {
	@GetMapping
	public User myAuthInfo() throws Exception{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth==null){
			throw new Exception("User not authenticated!");
		}
		return (User) auth.getPrincipal();
	}
}
