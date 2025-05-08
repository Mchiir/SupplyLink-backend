package com.supplylink.services;


import com.supplylink.dtos.AuthReq;
import com.supplylink.dtos.UserReqDTO;
import com.supplylink.dtos.UserResDTO;

public interface AuthService {
    String loginUser(AuthReq authReq);
    UserResDTO registerUser(UserReqDTO userReqDTO);
}