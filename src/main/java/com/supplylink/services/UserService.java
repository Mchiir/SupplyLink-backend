package com.supplylink.services;


import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.dtos.res.UserRegistrationResDTO;
import com.supplylink.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    Page<User> getAllUsers(Pageable pageable);
    User getUserById(UUID id);
    User updateUser(UUID id, UserRegistrationReqDTO userRegistrationReqDTO);
    void deleteUser(UUID id);
    User getUserByEmail(String email);
    User getUserByPhoneNumber(String phoneNumber);
    User getUserByEmailAndPhoneNumber(String email, String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}