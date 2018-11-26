package com.sample.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.sample.entity.User;

@RepositoryRestResource(path = "users", exported = true)
public interface UserRepository extends CrudRepository<User, Long>{
	
}
