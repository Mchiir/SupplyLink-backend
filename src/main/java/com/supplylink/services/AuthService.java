package com.supplylink.services;


import com.supplylink.dtos.req.UserLoginReqDTO;
import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.dtos.res.UserRegistrationResDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String loginUser(UserLoginReqDTO userLoginReqDTO);
    UserRegistrationResDTO registerUser(UserRegistrationReqDTO userRegistrationReqDTO);
}