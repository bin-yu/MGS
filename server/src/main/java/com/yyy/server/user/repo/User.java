package com.yyy.server.user.repo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yyy.server.domain.repo.Domain;

@Entity
@Table(indexes={@Index(name="idx_user_domain_and_id",columnList="DOMAIN_ID,ID")})
@JsonIgnoreProperties(ignoreUnknown=true)
public class User implements UserDetails,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5950196877221201966L;

	public static enum Role {
		ADMIN, USER
	}

	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable = false)
	protected String name;
	@Column(nullable = false)
	protected String displayName;
	@Column(nullable = false)
	protected String password;
	@Column(nullable = false)
	private Role role = Role.USER;
	@JsonIgnore()
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Domain domain;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	@JsonSetter
	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Domain getDomain() {
		return domain;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", displayName=" + displayName + "]";
	}
	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role.name()));
	}
	@JsonIgnore
	@Override
	public String getUsername() {
		return name;
	}
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}

}
