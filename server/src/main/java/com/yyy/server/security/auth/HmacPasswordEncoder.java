package com.yyy.server.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class HmacPasswordEncoder implements PasswordEncoder {

	// CONSTANTS ------------------------------------------------------

	// CLASS VARIABLES ------------------------------------------------

	// INSTANCE VARIABLES ---------------------------------------------
	@Autowired
	private EncryptionUtil util;

	// CONSTRUCTORS ---------------------------------------------------

	// PUBLIC METHODS -------------------------------------------------

	@Override
	public String encode(CharSequence rawPassword) {
		return util.encryptHMAC(rawPassword.toString());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return util.matchesHMAC(rawPassword.toString(), encodedPassword);
	}
	// PROTECTED METHODS ----------------------------------------------

	// PRIVATE METHODS ------------------------------------------------

	// ACCESSOR METHODS -----------------------------------------------

}
