package com.ecommerce.userservice.service;

import com.ecommerce.common.dto.UserDto;
import com.ecommerce.common.exception.ConflictException;
import com.ecommerce.common.exception.NotFoundException;
import com.ecommerce.userservice.domain.UserEntity;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.web.CreateUserRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto create(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("User already exists with email: " + request.email());
        }

        UserEntity entity = new UserEntity();
        entity.setEmail(request.email().trim().toLowerCase());
        entity.setFirstName(request.firstName().trim());
        entity.setLastName(request.lastName().trim());
        entity.setActive(true);

        UserEntity saved = userRepository.save(entity);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        return toDto(entity);
    }

    private UserDto toDto(UserEntity entity) {
        return new UserDto(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isActive()
        );
    }
}
