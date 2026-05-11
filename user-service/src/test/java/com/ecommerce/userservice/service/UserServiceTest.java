package com.ecommerce.userservice.service;

import com.ecommerce.common.dto.UserDto;
import com.ecommerce.common.exception.ConflictException;
import com.ecommerce.common.exception.NotFoundException;
import com.ecommerce.userservice.domain.UserEntity;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.web.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldPersistUserAndReturnDto() {
        CreateUserRequest request = new CreateUserRequest("john@example.com", "John", "Doe");

        when(userRepository.existsByEmailIgnoreCase("john@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", 10L);
            return entity;
        });

        UserDto result = userService.create(request);

        assertEquals(10L, result.id());
        assertEquals("john@example.com", result.email());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
    }

    @Test
    void createShouldFailWhenEmailExists() {
        CreateUserRequest request = new CreateUserRequest("john@example.com", "John", "Doe");
        when(userRepository.existsByEmailIgnoreCase("john@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(request));
    }

    @Test
    void getByIdShouldThrowWhenMissing() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }
}
