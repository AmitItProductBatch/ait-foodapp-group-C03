package com.ait.app.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.AdressUpdateDto;
import com.ait.app.dto.LoginRequestDTO;
import com.ait.app.dto.LoginResponseDTO;
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

		Optional<User> phoneUser = repository.findByPhonenumber(dto.getPhonenumber());

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
	public LoginResponseDTO login(LoginRequestDTO dto) {

		Optional<User> optionalUser = repository.findByEmail(dto.getEmail());

		if (optionalUser.isEmpty()) {
			throw new RuntimeException("Invalid email or password");
		}

		User user = optionalUser.get();

		if (user.getActive() == null || !user.getActive()) {
			throw new RuntimeException("User account is deleted");
		}

		if (!user.getPassword().equals(dto.getPassword())) {
			throw new RuntimeException("Invalid email or password");
		}

		LoginResponseDTO response = new LoginResponseDTO();

		response.setId(user.getId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setPhonenumber(user.getPhonenumber());
		response.setRole(user.getRole());
		response.setActive(user.getActive());

		List<AdressUpdateDto> addressList = new ArrayList<>();

		if (user.getAddresses() != null) {

			for (Address address : user.getAddresses()) {

				AdressUpdateDto addressDto = new AdressUpdateDto();

				addressDto.setId(address.getId());
				addressDto.setAddressLabel(address.getAddressLabel());
				addressDto.setStreetAddress(address.getStreetAddress());
				addressDto.setApartment(address.getApartment());
				addressDto.setLandmark(address.getLandmark());
				addressDto.setCity(address.getCity());
				addressDto.setPostalCode(address.getPostalCode());
				addressDto.setDeliveryInstructions(address.getDeliveryInstructions());

				addressList.add(addressDto);
			}
		}

		response.setAddresses(addressList);

		return response;
	}

	@Override
	public User getUser(int id) {

		Optional<User> optionalUser = repository.findById(id);

		if (optionalUser.isPresent()) {
			return optionalUser.get();
		}

		throw new RuntimeException("User not found");
	}

	@Override
	public User updateProfile(int id, UpdateProfileDto dto) {

		Optional<User> optionalUser = repository.findById(id);

		if (optionalUser.isEmpty()) {
			throw new RuntimeException("User not found");
		}

		User user = optionalUser.get();

		if (dto.getName() != null) {
			user.setName(dto.getName());
		}

		if (dto.getPhonenumber() != null) {

			boolean phoneExists = repository.existsByPhonenumberAndIdNot(dto.getPhonenumber(), id);

			if (phoneExists) {
				throw new RuntimeException("Mobile number already exists");
			}

			user.setPhonenumber(dto.getPhonenumber());
		}

		if (dto.getAdressUpdateDto() != null) {

			AdressUpdateDto addressDto = dto.getAdressUpdateDto();

			Optional<Address> optionalAddress = addressRepository.findByIdAndUserId(addressDto.getId(), id);

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

	@Override
	public void DeletUser(int id) {

		Optional<User> optionalUser = repository.findById(id);

		if (optionalUser.isEmpty()) {
			throw new RuntimeException("User not found");
		}

		User user = optionalUser.get();

		user.setActive(false);

		repository.save(user);
	}
}