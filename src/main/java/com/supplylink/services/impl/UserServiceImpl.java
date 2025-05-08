package com.supplylink.services.impl;

import com.supplylink.dtos.UserReqDTO;
import com.supplylink.dtos.UserResDTO;
import com.supplylink.validations.UserReqDTOValidator;
import com.supplylink.exceptions.InvalidRequestException;
import com.supplylink.models.User;
import com.supplylink.repositories.UserRepository;
import com.supplylink.services.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserReqDTOValidator validator;

    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder,
                           UserReqDTOValidator validator) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public List<UserResDTO> getAllUsers() {
        return ((List<User>) userRepository.findAll()).stream()
                .map(user -> modelMapper.map(user, UserResDTO.class))
                .toList();
    }

    @Override
    public UserResDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserResDTO.class);
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
    public UserResDTO updateUser(UUID id, UserReqDTO userReqDTO) {
        // Validate input first
        validator.validate(userReqDTO);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if new email/phone conflicts with others
        if (userReqDTO.getEmail() != null &&
                !userReqDTO.getEmail().equals(existingUser.getEmail()) &&
                userRepository.existsByEmail(userReqDTO.getEmail())) {
            throw new InvalidRequestException("Email already in use");
        }

        if (userReqDTO.getPhoneNumber() != null &&
                !userReqDTO.getPhoneNumber().equals(existingUser.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(userReqDTO.getPhoneNumber())) {
            throw new InvalidRequestException("Phone number already in use");
        }

        // Update fields
        existingUser.setFirstName(userReqDTO.getFirstName());
        existingUser.setLastName(userReqDTO.getLastName());
        existingUser.setEmail(userReqDTO.getEmail());
        existingUser.setPhoneNumber(userReqDTO.getPhoneNumber());

        // Only update password if provided
        if (userReqDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userReqDTO.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserResDTO.class);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}