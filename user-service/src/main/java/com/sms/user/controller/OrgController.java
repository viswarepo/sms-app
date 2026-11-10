package com.sms.user.controller;

import com.sms.user.client.SubscriptionServiceClient;
import com.sms.user.dto.*;
import com.sms.user.service.OrgService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/org")
@RequiredArgsConstructor
public class OrgController {

    private final OrgService orgService;
    private final SubscriptionServiceClient subscriptionServiceClient;

    // CREATE
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/register/{userRole}")
    public ResponseEntity<OrgResponseDTO> register(
            @RequestBody OrgRequestDTO requestDTO,
            @PathVariable String userRole) {
        OrgResponseDTO orgResponse = orgService.createOrg(requestDTO);

        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setOrganizationId(orgResponse.getId());
        customerRequest.setName(orgResponse.getOrgname());
        customerRequest.setEmail(orgResponse.getOrgEmail());
        customerRequest.setMobile(requestDTO.getMobile());
        customerRequest.setCustomerType(requestDTO.getCustomerType());
        customerRequest.setUsername(requestDTO.getUsername());
        customerRequest.setPassword(requestDTO.getPassword());

        subscriptionServiceClient.createCustomer(customerRequest,userRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(orgResponse);
    }

    // READ - single
    @GetMapping("/{id}")
    public ResponseEntity<OrgResponseDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(orgService.getOrgById(id));
    }

    // READ - all (simple list, no paging)
    @GetMapping("/all")
    public ResponseEntity<List<OrgResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(orgService.getAllOrg());
    }

    // READ - paged/sorted, e.g. GET /api/v1/users?page=0&size=10&sort=username,asc
    @GetMapping
    public ResponseEntity<Page<OrgResponseDTO>> getUsersPaged(Pageable pageable) {
        return ResponseEntity.ok(orgService.getAllOrg(pageable));
    }

    @GetMapping("/info")
    public ResponseEntity<OrgResponseDTO> userByUsername(
            @RequestParam String username) {
        return ResponseEntity.ok(orgService.getOrgByOrgname(username));
    }

    // READ - search by username/email keyword
    @GetMapping("/search")
    public ResponseEntity<Page<OrgResponseDTO>> searchUsers(
            @RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(orgService.searchOrg(keyword, pageable));
    }

    // UPDATE - full replace
    @PutMapping("/{id}")
    public ResponseEntity<OrgResponseDTO> updateUser(
            @PathVariable String id, @Valid @RequestBody OrgRequestDTO requestDTO) {
        return ResponseEntity.ok(orgService.updateOrg(id, requestDTO));
    }

    // UPDATE - partial
    @PatchMapping("/{id}")
    public ResponseEntity<OrgResponseDTO> partialUpdateUser(
            @PathVariable String id, @RequestBody OrgRequestDTO requestDTO) {
        return ResponseEntity.ok(orgService.partialUpdateOrg(id, requestDTO));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        orgService.deleteOrg(id);
        return ResponseEntity.noContent().build();
    }

    //Org Based Apis

    // 🔎 View customer profile
    @GetMapping("/{organizationId}/profile")
    public ResponseEntity<OrgProfileResponse> getProfile(@PathVariable String organizationId) {
        return ResponseEntity.ok(orgService.getProfile(organizationId));
    }

    // ✏️ Update customer profile
    @PutMapping("/{organizationId}/profile/update")
    public ResponseEntity<OrgProfileResponse> updateProfile(@PathVariable String organizationId,
                                                 @Valid @RequestBody OrgProfileRequest request) {
         return ResponseEntity.ok(orgService.updateProfile(organizationId, request));
    }

}
