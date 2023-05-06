package com.ankit.springjwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ankit.springjwt.models.User;
import com.ankit.springjwt.repository.UserRepository;


@RestController
@RequestMapping("/mail")
public class emailverficationController {
	
	@Autowired
    UserRepository userRepository;
	
	
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
