
package com.ait.app.service;

import com.ait.app.dto.LoginRequestDTO;
import com.ait.app.dto.LoginResponseDTO;
import com.ait.app.dto.UpdateProfileDto;
import com.ait.app.dto.UserRequestDTO;
import com.ait.app.entity.User;

public interface Userservice {

    User Registeruser(UserRequestDTO dto);

    LoginResponseDTO login(LoginRequestDTO dto);

    User getUser(int id);

    User updateProfile(int id, UpdateProfileDto dto);

    void DeletUser(int id);
}