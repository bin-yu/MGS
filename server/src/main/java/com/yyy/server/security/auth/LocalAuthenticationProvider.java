package com.yyy.server.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class LocalAuthenticationProvider extends DaoAuthenticationProvider {
	@Autowired
	  public LocalAuthenticationProvider(UserDetailsService userDetailsService,
	      PasswordEncoder myPasswordEncoder)
	  {
	    super.setUserDetailsService(userDetailsService);
	    super.setPasswordEncoder(myPasswordEncoder);
	}
}
