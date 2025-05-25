package com.supplylink.services;


import com.supplylink.dtos.req.AuthReq;
import com.supplylink.dtos.req.UserReqDTO;
import com.supplylink.dtos.res.UserResDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String loginUser(AuthReq authReq);
    UserResDTO registerUser(UserReqDTO userReqDTO);
}