package com.supplylink.controllers;

import com.supplylink.dtos.res.UserResDTO;
import com.supplylink.models.Location;
import com.supplylink.models.Role;
import com.supplylink.models.User;
import com.supplylink.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final String ADMIN_FIRST_NAME = "admin";
    private static final String ADMIN_LAST_NAME = "admin";
    private static final String ADMIN_EMAIL = "admin@admin.com";
    private static final String ADMIN_PHONE = "0781234567";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private static final String ADMIN_DISTRICT = "Nyarugenge";
    private static final String ADMIN_PROVINCE = "Kigali";
    private static final String ADMIN_COUNTRY = "Rwanda";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            var admin = new User();
            admin.setFirstName(ADMIN_FIRST_NAME);
            admin.setLastName(ADMIN_LAST_NAME);
            admin.setEmail(ADMIN_EMAIL);
            admin.setPhoneNumber(ADMIN_PHONE);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));

            admin.setRoles(Set.of(new Role(ROLE_ADMIN)));

            admin.setLocation(new Location(ADMIN_DISTRICT, ADMIN_PROVINCE, ADMIN_COUNTRY));

            userRepository.save(admin);
            var cleanAdmin = modelMapper.map(admin, UserResDTO.class);
            System.out.println("Admin account created successfully:\n" + cleanAdmin);
        }
    }
}