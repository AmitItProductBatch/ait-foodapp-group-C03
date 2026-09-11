package com.ait.app.service;

import com.ait.app.dto.RoleRequestDTO;
import com.ait.app.dto.RoleResponseDTO;

public interface RoleService {

	RoleResponseDTO createRole(RoleRequestDTO dto);

}
