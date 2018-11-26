package com.sample.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sample.entity.User;
import com.sample.service.IUserService;

@RestController
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	IUserService userService;
	
	@GetMapping("/users/{user-id}")
	public ResponseEntity<User> getUser(@PathVariable("user-id") Long userId){
		return new ResponseEntity<User>(userService.getUser(userId), HttpStatus.OK);
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers(){
		
		List<User> users = userService.getUsers();
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
	
	@PostMapping("/users")
	public ResponseEntity<User> addUser(@RequestBody User user){
		
		User userObject = userService.addUser(user);
		
		System.out.println("not found");
		return new ResponseEntity<User>(userObject, HttpStatus.OK);
	}
}
