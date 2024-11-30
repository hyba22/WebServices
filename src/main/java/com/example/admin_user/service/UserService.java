package com.example.admin_user.service;

import com.example.admin_user.dto.UserDto;
import com.example.admin_user.model.User;

public interface UserService {
	
	User save (UserDto userDto);
	

}
