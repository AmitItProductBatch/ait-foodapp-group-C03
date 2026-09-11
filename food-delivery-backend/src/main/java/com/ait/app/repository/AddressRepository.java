package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
	Optional<Address> findByIdAndUserId(int addressId, int userId);

}