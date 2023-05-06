package com.ankit.springjwt.payload.response;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class signupResponse {
	
	
	
	  private String username;
	  private String email;
	  private Set<String> roles;
	  
	  
	  
	public signupResponse( String username, String email, Set<String> roles) {
		super();
		
		this.username = username;
		this.email = email;
		this.roles = roles;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	  
	  
	  
	  

}
