package com.ait.app.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ait.app.dto.RoleRequestDTO;
import com.ait.app.dto.RoleResponseDTO;
import com.ait.app.entity.Role;
import com.ait.app.exception.DuplicateRoleException;
import com.ait.app.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceimplTest {

	@Mock
	private RoleRepository roleRepository;

	@InjectMocks
	private RoleServiceimpl roleService;

	private RoleRequestDTO roleRequestDTO;
	private Role role;

	@BeforeEach
	void setUp() {
		roleRequestDTO = new RoleRequestDTO();
		roleRequestDTO.setName("ADMIN");
		roleRequestDTO.setDescription("Administrator role with full access");

		role = new Role();
		role.setId(1);
		role.setName("ADMIN");
		role.setDescription("Administrator role with full access");
	}

	@Test
	void createRole_Success() {
		when(roleRepository.existsByName("ADMIN")).thenReturn(false);
		when(roleRepository.save(any(Role.class))).thenReturn(role);

		RoleResponseDTO response = roleService.createRole(roleRequestDTO);

		assertNotNull(response);
		assertEquals(1, response.getId());
		assertEquals("ADMIN", response.getName());
		assertEquals("Administrator role with full access", response.getDescription());

		verify(roleRepository, times(1)).existsByName("ADMIN");
		verify(roleRepository, times(1)).save(any(Role.class));
	}

	@Test
	void createRole_DuplicateName_ThrowsException() {
		when(roleRepository.existsByName("ADMIN")).thenReturn(true);

		DuplicateRoleException exception = assertThrows(DuplicateRoleException.class, () -> {
			roleService.createRole(roleRequestDTO);
		});

		assertEquals("Role with name 'ADMIN' already exists", exception.getMessage());
		verify(roleRepository, times(1)).existsByName("ADMIN");
		verify(roleRepository, never()).save(any(Role.class));
	}

	@Test
	void createRole_InvalidNameFormat_ValidationHandledByController() {
		roleRequestDTO.setName("admin"); // lowercase should fail validation

		when(roleRepository.existsByName("admin")).thenReturn(false);
		when(roleRepository.save(any(Role.class))).thenReturn(role);

		RoleResponseDTO response = roleService.createRole(roleRequestDTO);

		assertNotNull(response);
		verify(roleRepository, times(1)).save(any(Role.class));
	}

	@Test
	void createRole_NullDescription_ValidationHandledByController() {
		roleRequestDTO.setDescription(null);

		when(roleRepository.existsByName("ADMIN")).thenReturn(false);
		when(roleRepository.save(any(Role.class))).thenReturn(role);

		RoleResponseDTO response = roleService.createRole(roleRequestDTO);

		assertNotNull(response);
		verify(roleRepository, times(1)).save(any(Role.class));
	}

}
