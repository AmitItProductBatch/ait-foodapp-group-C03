package com.ait.app.serviceimpl;

import java.time.LocalDateTime;

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
    private UserRepository repository;

    @Autowired
    private AddressRepository addressRepository;


    // REGISTER USER
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


    // LOGIN USER
    @Override
    public User login(LoginRequestDTO dto) {

        User user = repository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }


    // GET USER
    @Override
    public User getUser(int id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
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
			 
			    if (addressDto.getAddressLabel() != null) {
			        address.setAddressLabel(addressDto.getAddressLabel());
			    }

			    if (addressDto.getStreetAddress() != null) { 
			        address.setStreetAddress(addressDto.getStreetAddress()); 
			    }

			    if (addressDto.getApartment() != null) {
			        address.setApartment(addressDto.getApartment());
			    }

			    if (addressDto.getLandmark() != null) {
			        address.setLandmark(addressDto.getLandmark());
			    }
			 
			    if (addressDto.getCity() != null) { 
			        address.setCity(addressDto.getCity()); 
			    } 
			 
			    if (addressDto.getPostalCode() != null) { 
			        address.setPostalCode(addressDto.getPostalCode()); 
			    }

			    if (addressDto.getDeliveryInstructions() != null) {
			        address.setDeliveryInstructions(addressDto.getDeliveryInstructions());
			    }
			 
			    addressRepository.save(address); 
			} 
			 
			return repository.save(user);   
			 
	}

    // UPDATE PROFILE
    @Override
    public User updateProfile(int id, UpdateProfileDto dto) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));


        // Update user name
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }


        // Update phone number
        if (dto.getPhonenumber() != null) {
            user.setPhonenumber(dto.getPhonenumber());
        }


        // Update address
        if (dto.getAdressUpdateDto() != null) {

            AdressUpdateDto addressDto =
                    dto.getAdressUpdateDto();

            Address address = addressRepository
                    .findByIdAndUserId(addressDto.getId(), id)
                    .orElseThrow(() ->
                            new RuntimeException("Address not found"));


            if (addressDto.getAddressLabel() != null) {
                address.setAddressLabel(
                        addressDto.getAddressLabel());
            }

            if (addressDto.getStreetAddress() != null) {
                address.setStreetAddress(
                        addressDto.getStreetAddress());
            }

            if (addressDto.getApartment() != null) {
                address.setApartment(
                        addressDto.getApartment());
            }

            if (addressDto.getLandmark() != null) {
                address.setLandmark(
                        addressDto.getLandmark());
            }

            if (addressDto.getCity() != null) {
                address.setCity(
                        addressDto.getCity());
            }

            if (addressDto.getPostalCode() != null) {
                address.setPostalCode(
                        addressDto.getPostalCode());
            }

            if (addressDto.getDeliveryInstructions() != null) {
                address.setDeliveryInstructions(
                        addressDto.getDeliveryInstructions());
            }


            addressRepository.save(address);
        }


        return repository.save(user);
    }


    // DELETE USER - SOFT DELETE
    @Override
    public void DeletUser(int id) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));


        user.setDeleted(true);

        user.setDeletedAt(LocalDateTime.now());

        repository.save(user);
    }
}