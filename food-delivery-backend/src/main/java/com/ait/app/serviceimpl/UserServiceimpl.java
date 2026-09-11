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

        Optional<User> optionalUser = repository.findByEmail(dto.getEmail());

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    @Override
    public User getUser(int id) {

        Optional<User> optionalUser = repository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }

        return optionalUser.get();
    }

    @Override
    public User updateProfile(int id, UpdateProfileDto dto) {

        Optional<User> optionalUser = repository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

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

            AdressUpdateDto addressDto = dto.getAdressUpdateDto();

            Optional<Address> optionalAddress =
                    addressRepository.findByIdAndUserId(
                            addressDto.getId(), id);

            if (!optionalAddress.isPresent()) {
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
                address.setDeliveryInstructions(
                        addressDto.getDeliveryInstructions());
            }

            addressRepository.save(address);
        }

        return repository.save(user);
    }
}