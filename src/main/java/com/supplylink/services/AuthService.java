package com.supplylink.services;

import com.supplylink.dtos.req.UserLoginReqDTO;
import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.models.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String loginUser(UserLoginReqDTO userLoginReqDTO);
    User registerUser(UserRegistrationReqDTO userRegistrationReqDTO);
}