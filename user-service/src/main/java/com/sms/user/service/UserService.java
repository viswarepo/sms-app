package com.sms.user.service;


import com.sms.user.domain.ERole;
import com.sms.user.domain.UserInfo;
import com.sms.user.dto.UserRequestDTO;
import com.sms.user.dto.UserResponseDTO;
import com.sms.user.exception.DuplicateResourceException;
import com.sms.user.exception.ResourceNotFoundException;
import com.sms.user.repository.UserRepository;
import com.sms.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    //private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(String organizationId,UserRequestDTO requestDTO, String userRole) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + requestDTO.getEmail());
        }
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + requestDTO.getUsername());
        }
        requestDTO.setRole(ERole.valueOf(ERole.ROLE_ADMIN.name()));
        if(userRole.equals(ERole.ROLE_ADMIN_SUPER.name())){
            if (userRepository.existsByOrgIdAndRole(organizationId, ERole.valueOf(ERole.ROLE_ADMIN_SUPER.name()))) {
                throw new IllegalStateException("SuperAdmin already exists, can not create more than one.");
            }
            if(requestDTO.getCustomerId()==null || requestDTO.getCustomerId().isEmpty()) {
                requestDTO.setRole(ERole.valueOf(ERole.ROLE_ADMIN.name()));
            }else{
                requestDTO.setRole(ERole.valueOf(ERole.ROLE_ADMIN_SUPER.name()));
            }
        }
        if(userRole.equals(ERole.ROLE_OWNER.name())){
            requestDTO.setRole(ERole.valueOf(ERole.ROLE_OWNER.name()));
        }
        if(userRole.equals(ERole.ROLE_CUSTOMER.name())){
            requestDTO.setRole(ERole.valueOf(ERole.ROLE_CUSTOMER.name()));
        }
        UserInfo user = UserInfo.builder()
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .fullName(requestDTO.getFullName())
                .role(requestDTO.getRole())
                .orgId(organizationId)
                .customerId(requestDTO.getCustomerId())
                .build();
        UserInfo saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(String id) {
        UserInfo user = findUserOrThrow(id);
        return toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        Optional<UserResponseDTO> userResponseDTO= userRepository.getUserAndOrgInfo(username);
        return userResponseDTO.get();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers(String userRole, String organizationId) {

        if(userRole.equals(ERole.ROLE_OWNER.name())){
            return userRepository.findByRole(ERole.valueOf(ERole.ROLE_OWNER.name())).stream()
                    .map(this::toResponseDTO)
                    .toList();

        }else if(userRole.equals(ERole.ROLE_ADMIN_SUPER.name())){
            return userRepository.findByRoleAndOrgId(ERole.valueOf(ERole.ROLE_ADMIN.name()),organizationId)
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
        }else if(userRole.equals(ERole.ROLE_ADMIN.name())){
            return userRepository.findByRoleAndOrgId(ERole.valueOf(ERole.ROLE_OWNER.name()),organizationId)
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
        }else{
            return userRepository.findAll()
                    .stream()
                    .map(this::toResponseDTO)
                    .toList();
        }

    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String keyword, Pageable pageable) {
        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable)
                .map(this::toResponseDTO);
    }

    public UserResponseDTO updateUser(String id, UserRequestDTO requestDTO) {
        UserInfo user = findUserOrThrow(id);

        if (!user.getEmail().equalsIgnoreCase(requestDTO.getEmail())
                && userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + requestDTO.getEmail());
        }
        if (!user.getUsername().equalsIgnoreCase(requestDTO.getUsername())
                && userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + requestDTO.getUsername());
        }

        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setFullName(requestDTO.getFullName());
        if (StringUtils.hasText(requestDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        UserInfo updated = userRepository.save(user);
        return toResponseDTO(updated);
    }

    public UserResponseDTO partialUpdateUser(String id, UserRequestDTO requestDTO) {
        UserInfo user = findUserOrThrow(id);

        if (StringUtils.hasText(requestDTO.getUsername())) {
            user.setUsername(requestDTO.getUsername());
        }
        if (StringUtils.hasText(requestDTO.getEmail())) {
            user.setEmail(requestDTO.getEmail());
        }
        if (StringUtils.hasText(requestDTO.getFullName())) {
            user.setFullName(requestDTO.getFullName());
        }

        if (StringUtils.hasText(requestDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }
        UserInfo updated = userRepository.save(user);
        return toResponseDTO(updated);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserInfo findUserOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponseDTO toResponseDTO(UserInfo user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().toString())
                .organizationId(user.getOrgId())
                .customerId(user.getCustomerId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void resetPassword(String username, String newPassword) {
        UserInfo user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in org"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserResponseDTO findUser(String orgId, String username) {
        UserInfo user = userRepository.findByUsernameAndOrgId(username, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponseDTO(user);
    }

    public UserResponseDTO findUserByEmail(String email) {
        UserInfo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponseDTO(user);
    }
    public UserResponseDTO findUserByUsername(String username) {
        UserInfo user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponseDTO(user);
    }
}
