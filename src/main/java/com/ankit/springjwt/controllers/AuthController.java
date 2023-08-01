package com.ankit.springjwt.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ankit.springjwt.models.ERole;
import com.ankit.springjwt.models.Mail;
import com.ankit.springjwt.models.Role;
import com.ankit.springjwt.models.User;
import com.ankit.springjwt.payload.request.LoginRequest;
import com.ankit.springjwt.payload.request.SignupRequest;
import com.ankit.springjwt.payload.request.updateRequest;
import com.ankit.springjwt.payload.response.JwtResponse;
import com.ankit.springjwt.payload.response.MessageResponse;
import com.ankit.springjwt.payload.response.signupResponse;
import com.ankit.springjwt.repository.RoleRepository;
import com.ankit.springjwt.repository.UserRepository;
import com.ankit.springjwt.security.jwt.JwtUtils;
import com.ankit.springjwt.security.services.MailServiceImpl;
import com.ankit.springjwt.security.services.UserDetailsImpl;





@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;
  
  @Autowired
  private MailServiceImpl mailService;
  
  @Value("${ankit.app.url.uat}")
  private String appurl;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
	  System.out.println("reached here");
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!", "404"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!", "404"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), 
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_CLIENT)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "vendor":
          Role modRole = roleRepository.findByName(ERole.ROLE_VENDOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_CLIENT)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
   // user.setConfirmationToken(UUID.randomUUID().toString());
	    user.setConfirmationToken("Otp@1234");
    user.setContactNo(signUpRequest.getContactNo());
    user.setIsenabled(Boolean.FALSE);
    user.setReferalCode(signUpRequest.getReferalCode());
    userRepository.save(user);
    
  /*  Mail mail = new Mail();
	mail.setMailFrom("xyz");
	System.out.println("reached here");
	mail.setMailTo(user.getEmail());
	mail.setMailSubject("Email Confirmation");
	mail.setMailContent("To confirm you email-address, please copy and paste below token on portal:\n"
			+"token=" + user.getConfirmationToken());
	
	mailService.sendEmail(mail); */

   // return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    //List<String> roles1 = new ArrayList<>();
     return ResponseEntity.ok(new signupResponse(signUpRequest.getUsername(), 
            signUpRequest.getEmail(),signUpRequest.getRole())); 
           
            
  }
  
	 @PostMapping("/update")
	    public ResponseEntity<?>  updateClient( @RequestBody updateRequest request){

	
			User existingUser = userRepository.findByEmail(request.getEmail());
			if(existingUser == null) 
			{
				 return ResponseEntity
				          .badRequest()
				          .body(new MessageResponse("Error: Email is not registered", "404"));
			}
			else {
				existingUser.setPassword(request.getPassword());
				if(request.getContactNo()!=null && !request.getContactNo().isEmpty()) {
				existingUser.setContactNo(Long.parseLong(request.getContactNo()));
				}
				
				Set<String> s1 = null;
					
					
				userRepository.save(existingUser);
				 return ResponseEntity.ok(new signupResponse(existingUser.getUsername(), 
						 existingUser.getEmail(),s1));  
				
				
			}
			}
	 
		@GetMapping("/confirm")
		public ResponseEntity<?> confirmEmail(@RequestParam String token)
		{
			User user = userRepository.findByConfirmationToken(token);
			if(user == null) 
			{
				return ResponseEntity
						.badRequest()
						.body("Invalid token");
			}
			user.setIsenabled(true);
			userRepository.save(user);
			return ResponseEntity
					.status(HttpStatus.OK)
					.body("User email successfully verified!");
		}

}
