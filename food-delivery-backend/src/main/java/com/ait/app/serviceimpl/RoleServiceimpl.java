package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RoleRequestDTO;
import com.ait.app.dto.RoleResponseDTO;
import com.ait.app.entity.Role;
import com.ait.app.exception.DuplicateRoleException;
import com.ait.app.repository.RoleRepository;
import com.ait.app.service.RoleService;

@Service
public class RoleServiceimpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public RoleResponseDTO createRole(RoleRequestDTO dto) {
		if (roleRepository.existsByName(dto.getName())) {
			throw new DuplicateRoleException("Role with name '" + dto.getName() + "' already exists");
		}

		Role role = new Role();
		role.setName(dto.getName());
		role.setDescription(dto.getDescription());

		Role savedRole = roleRepository.save(role);

		RoleResponseDTO response = new RoleResponseDTO();
		response.setId(savedRole.getId());
		response.setName(savedRole.getName());
		response.setDescription(savedRole.getDescription());

		return response;
	}

}
