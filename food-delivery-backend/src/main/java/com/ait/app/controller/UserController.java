/*package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	public User registerUser(@RequestBody UserRequestDTO dto) {

		return userservice.Registeruser(dto);
		
		
		
	}
	
	@GetMapping("/user/getuser/{id}")
	
	public User getUser(@PathVariable int id) {
		
		return userservice.getUser(id);
		
	}
	

}*//*
package com.ait.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.LoginRequestDTO;
import com.ait.app.entity.User;
import com.ait.app.service.Userservice;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private Userservice userService;

    public UserController(Userservice userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(
            @RequestBody LoginRequestDTO dto) {

        User user = userService.login(dto);

        return ResponseEntity.ok(user);
    }
}*/
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

// Register User
@PostMapping("/register")
public ResponseEntity<User> registerUser(
        @RequestBody UserRequestDTO dto) {

    User user = userService.Registeruser(dto);

    return ResponseEntity.ok(user);
}

// Login User
@PostMapping("/login")
public ResponseEntity<User> login(
        @RequestBody LoginRequestDTO dto) {

    User user = userService.login(dto);

    return ResponseEntity.ok(user);
}


}
