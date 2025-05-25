package com.supplylink.services;


import com.supplylink.dtos.UserReqDTO;
import com.supplylink.dtos.UserResDTO;
import com.supplylink.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    List<UserResDTO> getAllUsers();
    UserResDTO getUserById(UUID id);
    UserResDTO updateUser(UUID id, UserReqDTO userReqDTO);
    void deleteUser(UUID id);
    User getUserByEmail(String email);
    User getUserByPhoneNumber(String phoneNumber);
    User getUserByEmailAndPhoneNumber(String email, String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}