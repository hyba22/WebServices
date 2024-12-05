package com.webservices.projectweb.services;


import com.webservices.projectweb.dto.UserDto;
import com.webservices.projectweb.model.User;

public interface UserService {
	
	User save (UserDto userDto);
}
