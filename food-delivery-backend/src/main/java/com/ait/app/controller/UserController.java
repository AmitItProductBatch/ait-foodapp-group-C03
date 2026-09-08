
package com.ait.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.LoginRequestDTO;
import com.ait.app.dto.UserRequestDTO;
import com.ait.app.entity.User;
import com.ait.app.service.Userservice;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private Userservice userService;

	public UserController(Userservice userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody UserRequestDTO dto) {

		User user = userService.Registeruser(dto);

		return ResponseEntity.ok(user);
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody LoginRequestDTO dto) {

		User user = userService.login(dto);

		return ResponseEntity.ok(user);
	}

}
