package com.example.myblog.service;

import com.example.myblog.entity.User;
import com.example.myblog.dto.UserDto;
import com.example.myblog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 활성화
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterUserSuccess() {
        // 준비: 정상적인 사용자 데이터 생성
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword("password123");

        // Mock 설정: 정상적으로 중복되지 않은 사용자
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        // 실행
        userService.registerUser(userDto);

        // 검증: Mock 호출 여부
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("testuser@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserEmptyPassword() {
        // 준비: 비밀번호가 빈 값인 사용자 데이터
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword(""); // 빈 비밀번호 설정

        // 실행 및 검증: IllegalArgumentException 발생 여부 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userDto);
        });

        // 검증: 예외 메시지가 정확한지 확인
        assertEquals("Password cannot be empty or null", exception.getMessage());
        verify(userRepository, never()).save(any(User.class)); // save 호출되지 않아야 함
    }

    @Test
    void testRegisterUserUsernameExists() {
        // 준비: 중복된 사용자명을 가진 데이터
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword("password123");

        // Mock 설정: 사용자명이 중복된 상황
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // 실행 및 검증: IllegalArgumentException 발생 여부 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userDto);
        });

        // 검증: 예외 메시지가 정확한지 확인
        assertEquals("Username is already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class)); // save 호출되지 않아야 함
    }

    @Test
    void testRegisterUserEmailExists() {
        // 준비: 중복된 이메일을 가진 데이터
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword("password123");

        // Mock 설정: 이메일이 중복된 상황
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(true);

        // 실행 및 검증: IllegalArgumentException 발생 여부 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userDto);
        });

        // 검증: 예외 메시지가 정확한지 확인
        assertEquals("User email is already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class)); // save 호출되지 않아야 함
    }
}
