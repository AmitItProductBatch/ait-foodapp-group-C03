package com.ait.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ait.app.dto.LoginRequestDTO;
import com.ait.app.dto.LoginResponseDTO;
import com.ait.app.dto.UpdateProfileDto;
import com.ait.app.dto.UserRequestDTO;
import com.ait.app.entity.User;
import com.ait.app.service.Userservice;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private Userservice userService;

	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody UserRequestDTO dto) {
		User user = userService.Registeruser(dto);
		return ResponseEntity.ok(user);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {

		LoginResponseDTO response = userService.login(dto);

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("#id == principal.id or hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<User> getUser(@PathVariable int id) {
		return ResponseEntity.ok(userService.getUser(id));
	}

	@PreAuthorize("#id == principal.id")
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody UpdateProfileDto dto) {
		User user = userService.updateProfile(id, dto);
		return ResponseEntity.ok(user);
	}

	@PreAuthorize("#id == principal.id or hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable int id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}