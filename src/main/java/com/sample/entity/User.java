package com.sample.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class User {
	
	private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private long id;
    
	private String firstName;
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String countryCode;
	
}
