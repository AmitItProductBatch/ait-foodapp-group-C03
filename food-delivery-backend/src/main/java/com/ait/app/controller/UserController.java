package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.UserRequestDTO;
import com.ait.app.entity.User;
import com.ait.app.service.Userservice;

@RestController
public class UserController {
@Autowired
Userservice userservice;
	
	@PostMapping("/user/register")
	private User registerUser(@RequestBody UserRequestDTO dto ) {
		return userservice.Registeruser(dto);
	}
}
