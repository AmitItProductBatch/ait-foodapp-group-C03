package com.ait.app.service;

import com.ait.app.dto.UserRequestDTO;
import com.ait.app.entity.User;

public interface Userservice {

	User Registeruser(UserRequestDTO dto);
}
