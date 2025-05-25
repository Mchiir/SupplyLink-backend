package com.supplylink.services;


import com.supplylink.dtos.AuthReq;
import com.supplylink.dtos.UserReqDTO;
import com.supplylink.dtos.UserResDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String loginUser(AuthReq authReq);
    UserResDTO registerUser(UserReqDTO userReqDTO);
}