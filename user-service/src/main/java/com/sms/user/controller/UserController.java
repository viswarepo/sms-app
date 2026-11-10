package com.sms.user.controller;

import com.sms.user.dto.ResetPasswordRequest;
import com.sms.user.dto.UserRequestDTO;
import com.sms.user.dto.UserResponseDTO;
import com.sms.user.exception.ResourceNotFoundException;
import com.sms.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // CREATE
    @PostMapping("/register/{userRole}")
    public ResponseEntity<UserResponseDTO> createUser(
            //@RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Valid @RequestBody UserRequestDTO requestDTO,
            @PathVariable String userRole) {
        UserResponseDTO created = userService.createUser(requestDTO.getOraganizationId(),requestDTO,userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ - single
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // READ - all (simple list, no paging)
    @GetMapping("/all/{userRole}")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @PathVariable String userRole,
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId) {
        return ResponseEntity.ok(userService.getAllUsers(userRole,organizationId));
    }

    // READ - paged/sorted, e.g. GET /api/v1/users?page=0&size=10&sort=username,asc
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsersPaged(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/info")
    public ResponseEntity<UserResponseDTO> userByUsername(
            @RequestParam String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    // READ - search by username/email keyword
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(
            @RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(keyword, pageable));
    }

    // UPDATE - full replace
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String id, @Valid @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateUser(id, requestDTO));
    }

    // UPDATE - partial
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> partialUpdateUser(
            @PathVariable String id, @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.partialUpdateUser(id, requestDTO));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password/{username}")
    public ResponseEntity<?> resetPassword(
            @PathVariable String username,
            @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getUsername(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found in organization"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to reset password"));
        }
    }

    @GetMapping("/{orgId}/{username}")
    public ResponseEntity<UserResponseDTO> findUser(
            @PathVariable String orgId,
            @PathVariable String username) {
        UserResponseDTO user = userService.findUser(orgId, username);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> findUserByEmail(
            @PathVariable String email) {
        UserResponseDTO user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> findUserByUsername(
            @PathVariable String username) {
        UserResponseDTO user = userService.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    //Org based users

}
