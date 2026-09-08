package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}