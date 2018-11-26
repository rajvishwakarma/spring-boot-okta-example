package com.sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.entity.User;
import com.sample.repository.UserRepository;

@Service
public class UserService implements IUserService {

	@Autowired
	UserRepository userRepo;
	
	@Override
	public User getUser(Long userId) {
		return userRepo.findOne(userId);
	}

	@Override
	public List<User> getUsers() {
		return (List<User>)userRepo.findAll();
	}

	@Override
	public User addUser(User user) {		
		return userRepo.save(user);
	}

}
