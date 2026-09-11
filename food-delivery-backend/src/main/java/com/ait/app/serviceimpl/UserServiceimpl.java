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
    private UserRepository repository;

    @Autowired
    private AddressRepository addressRepository;


    
    @Override
    public User Registeruser(UserRequestDTO dto) {
    	
        Optional<User> emailUser = repository.findByEmail(dto.getEmail());

        if (emailUser.isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        Optional<User> phoneUser =repository.findByPhonenumber(dto.getPhonenumber());

        if (phoneUser.isPresent()) {
            throw new RuntimeException("Mobile number already exists");
        }


        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhonenumber(dto.getPhonenumber());
        user.setRole(dto.getRole());
        user.setActive(true);


        return repository.save(user);
    }


  
    @Override
    public User login(LoginRequestDTO dto) {

    	Optional<User> optionalUser =repository.findByEmail(dto.getEmail());

    	User user;

    	if (optionalUser.isPresent()) {
    	    user = optionalUser.get();
    	} else {
    	    throw new RuntimeException("Invalid email or password");
    	}

      
    	if (user.getActive() == null || !user.getActive()) {
    	    throw new RuntimeException("User account is deleted");
    	}


    	if (!user.getPassword().equals(dto.getPassword())) {
    	    throw new RuntimeException("Invalid email or password");
    	}
        return user;
    }


    @Override
    public User getUser(int id) {

        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new RuntimeException("User not found");
        }
    }

   
    @Override
    public User updateProfile(int id, UpdateProfileDto dto) {
    	Optional<User> optionalUser = repository.findById(id);

    	User user;

    	if (optionalUser.isPresent()) {
    	    user = optionalUser.get();
    	} else {
    	    throw new RuntimeException("User not found");
    	}

        if (dto.getName() != null) {

            user.setName(dto.getName());
        }


        if (dto.getPhonenumber() != null) {

            boolean phoneExists =repository.existsByPhonenumberAndIdNot( dto.getPhonenumber(),id);

            if (phoneExists) {
                throw new RuntimeException("Mobile number already exists");
            }

            user.setPhonenumber(dto.getPhonenumber());
        }

        if (dto.getAdressUpdateDto() != null) {

            AdressUpdateDto addressDto = dto.getAdressUpdateDto();


          
            Optional<Address> optionalAddress = addressRepository.findByIdAndUserId(addressDto.getId(), id);

            Address address;

            if (optionalAddress.isPresent()) {
                address = optionalAddress.get();
            } else {
                throw new RuntimeException("Address not found");
            }
            
            
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

  @Override
  public void DeletUser(int id) {

	    Optional<User> optionalUser = repository.findById(id);

	    User user;

	    if (optionalUser.isPresent()) {
	        user = optionalUser.get();
	    } else {
	        throw new RuntimeException("User not found");
	    }

	    user.setActive(false);

	    repository.save(user);
	}
}