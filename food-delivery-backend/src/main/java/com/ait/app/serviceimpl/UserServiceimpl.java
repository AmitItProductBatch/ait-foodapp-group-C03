package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.UserRequestDTO;
import com.ait.app.entity.User;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.Userservice;

@Service
public class UserServiceimpl implements Userservice{
@Autowired
UserRepository repository;

 @Override
	public User Registeruser(UserRequestDTO dto) {
		// TODO Auto-generated method stub
	 User user = new User();
	 user.setName(dto.getName());
	 user.setAddress(dto.getAddress());
	 user.setEmail(dto.getEmail());
	 user.setPassword(dto.getPassword());
	 user.setPhonenumber(dto.getPhonenumber());
	 user.setRole(dto.getRole());
	 
		return repository.save(user);
	}

}
