package com.supplylink.repositories;

import com.supplylink.models.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);
    Optional<User> findByEmailAndPhoneNumber(String email, String phoneNumber);

    boolean existsByEmail(@NotBlank String email);
    boolean existsByPhoneNumber(@NotBlank String phoneNumber);
    boolean existsByEmailAndPhoneNumber(@NotBlank String email, String phoneNumber);
}