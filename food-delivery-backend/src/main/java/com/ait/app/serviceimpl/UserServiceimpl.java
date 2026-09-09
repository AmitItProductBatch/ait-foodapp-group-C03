package com.ait.app.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.AdressUpdateDto;
import com.ait.app.dto.LoginRequestDTO;
import com.ait.app.dto.UpdateProfileDto;
import com.ait.app.dto.UserRequestDTO;
import com.ait.app.entity.Address;
import com.ait.app.entity.User;
import com.ait.app.repository.AddressRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.Userservice;

@Service
public class UserServiceimpl implements Userservice {

    @Autowired
    UserRepository repository;

    @Autowired
    AddressRepository addressRepository;
    
    @Override
    public User Registeruser(UserRequestDTO dto) {

        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhonenumber(dto.getPhonenumber());
        user.setRole(dto.getRole());

        return repository.save(user);
    }

    @Override
    public User login(LoginRequestDTO dto) {

        User user = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    @Override
    public User getUser(int id) {
        return repository.findById(id).get();
    }

	@Override
	public User updateProfile(int id, UpdateProfileDto dto) {
		Optional<User> optionaluser = repository.findById(id);
		
		if(optionaluser.isEmpty()) {
			throw new RuntimeException("User not found");
		}
		 User user = optionaluser.get();
		 
		 if (dto.getName() != null) {        
			 user.setName(dto.getName());   
			 }  
		 
		 if (dto.getPhonenumber() != null) {        
			 user.setPhonenumber(dto.getPhonenumber());   
			 }
		 
		 if (dto.getName() != null) {        
			 user.setName(dto.getName());   
			 }  
		 if (dto.getAdressUpdateDto() != null) {        
			 
			 
			    AdressUpdateDto addressDto = dto.getAdressUpdateDto(); 
			 
			    Optional<Address> optionalAddress = 
			    		addressRepository.findByIdAndUserId(addressDto.getId(), id);			 
			    if (optionalAddress.isEmpty()) { 
			        throw new RuntimeException("Address not found"); 
			    } 
			 
			    Address address = optionalAddress.get(); 
			 
			    if (addressDto.getCity() != null) { 
			        address.setCity(addressDto.getCity()); 
			    } 
			 
			    if (addressDto.getStreetAddress() != null) { 
			        address.setStreetAddress(addressDto.getStreetAddress()); 
			    } 
			 
			    if (addressDto.getPostalCode() != null) { 
			        address.setPostalCode(addressDto.getPostalCode()); 
			    } 
			    
			    
			 
			    addressRepository.save(address); 
			} 
			 
			return repository.save(user);   
			 
	}

}