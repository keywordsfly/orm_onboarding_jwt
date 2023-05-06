package com.ankit.springjwt.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users", 
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email") 
    })
public class User {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name="UUID", strategy ="org.hibernate.id.UUIDGenerator")
  private UUID id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(  name = "user_roles", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();
  
  
    @Column
	private String referalCode;
	
	@Column(nullable = false)
	private Long contactNo;
	
	@Column
	private boolean isenabled = false;
	
	@Column
	private String confirmationToken;

  public User() {
  }

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

public String getReferalCode() {
	return referalCode;
}

public void setReferalCode(String referalCode) {
	this.referalCode = referalCode;
}

public Long getContactNo() {
	return contactNo;
}

public void setContactNo(Long contactNo) {
	this.contactNo = contactNo;
}

public boolean isIsenabled() {
	return isenabled;
}

public void setIsenabled(boolean isenabled) {
	this.isenabled = isenabled;
}

public String getConfirmationToken() {
	return confirmationToken;
}

public void setConfirmationToken(String confirmationToken) {
	this.confirmationToken = confirmationToken;
}
  
}
