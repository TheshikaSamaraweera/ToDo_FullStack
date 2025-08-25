package com.democode.todo.service;

import com.democode.todo.dto.UserCreateDTO;
import com.democode.todo.dto.UserResponseDTO;
import com.democode.todo.entity.Role;
import com.democode.todo.entity.Users;
import com.democode.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserCreateDTO createDTO) {
        Users user = new Users();
        user.setUsername(createDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword())); // encode the password
        user.setEmail(createDTO.getEmail());
        user.setRole(createDTO.getRole());

        Users savedUser = userRepository.save(user);
        return convertToResponseDTO(savedUser);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    public Optional<UserResponseDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToResponseDTO);
    }

    public List<UserResponseDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO updateUser(Long id, UserCreateDTO updateDTO) {
        Users user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(updateDTO.getUsername());
            user.setEmail(updateDTO.getEmail());
            user.setRole(updateDTO.getRole());
            if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateDTO.getPassword())); //encode the password
            }
            Users savedUser = userRepository.save(user);
            return convertToResponseDTO(savedUser);
        }
        return null;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserResponseDTO convertToResponseDTO(Users user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

}
