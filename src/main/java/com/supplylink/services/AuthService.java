package com.supplylink.services;


import com.supplylink.dtos.req.AuthReq;
import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.dtos.res.UserRegistrationResDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String loginUser(AuthReq authReq);
    UserRegistrationResDTO registerUser(UserRegistrationReqDTO userRegistrationReqDTO);
}