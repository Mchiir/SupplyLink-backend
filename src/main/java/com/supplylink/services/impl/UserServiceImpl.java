package com.supplylink.services.impl;

import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.validations.UserReqDTOValidator;
import com.supplylink.exceptions.InvalidRequestException;
import com.supplylink.models.User;
import com.supplylink.repositories.UserRepository;
import com.supplylink.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserReqDTOValidator validator;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserReqDTOValidator validator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(UUID id) {
         return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));
    }
    @Override
    public User getUserByEmailAndPhoneNumber(String email, String phoneNumber) {
        return userRepository.findByEmailAndPhoneNumber(email, phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email + " and phone number: " + phoneNumber));
    }

    @Override
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
    @Override
    public boolean existsByPhoneNumber(String phoneNumber){
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

        @Override
    public User updateUser(UUID id, UserRegistrationReqDTO userRegistrationReqDTO) {
        try{
            // Validate input first
            validator.validate(userRegistrationReqDTO);

            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if new email/phone conflicts with others
            if (userRegistrationReqDTO.getEmail() != null &&
                    !userRegistrationReqDTO.getEmail().equals(existingUser.getEmail()) &&
                    userRepository.existsByEmail(userRegistrationReqDTO.getEmail())) {
                throw new InvalidRequestException("Email already in use");
            }

            if (userRegistrationReqDTO.getPhoneNumber() != null &&
                    !userRegistrationReqDTO.getPhoneNumber().equals(existingUser.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(userRegistrationReqDTO.getPhoneNumber())) {
                throw new InvalidRequestException("Phone number already in use");
            }

            // Update fields
            existingUser.setFirstName(userRegistrationReqDTO.getFirstName());
            existingUser.setLastName(userRegistrationReqDTO.getLastName());
            existingUser.setEmail(userRegistrationReqDTO.getEmail());
            existingUser.setPhoneNumber(userRegistrationReqDTO.getPhoneNumber());

            // Only update password if provided
            if (userRegistrationReqDTO.getPassword() != null) {
                existingUser.setPassword(passwordEncoder.encode(userRegistrationReqDTO.getPassword()));
            }

            User updatedUser = userRepository.save(existingUser);
            return updatedUser;
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}