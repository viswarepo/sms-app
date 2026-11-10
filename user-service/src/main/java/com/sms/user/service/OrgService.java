package com.sms.user.service;

import com.sms.user.domain.EOrgType;
import com.sms.user.domain.OrganizationProfile;
import com.sms.user.dto.OrgProfileRequest;
import com.sms.user.dto.OrgProfileResponse;
import com.sms.user.dto.OrgRequestDTO;
import com.sms.user.dto.OrgResponseDTO;
import com.sms.user.domain.OrgInfo;
import com.sms.user.exception.DuplicateResourceException;
import com.sms.user.exception.ResourceNotFoundException;
import com.sms.user.repository.OrgProfileRepository;
import com.sms.user.repository.OrgRepository;
import com.sms.user.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrgService {

    private final OrgRepository orgRepository;
    private final OrgProfileRepository orgProfileRepository;

    @Value("${tenant.signup.url}")
    private String tenantSignupUrl;

    @Value("${customer.signup.url}")
    private String customerSignupUrl;

    public OrgResponseDTO createOrg(OrgRequestDTO requestDTO) {

        if (orgRepository.existsByOrgname(requestDTO.getOrgname())) {
            throw new DuplicateResourceException("Organization already registered: " + requestDTO.getEmail());
        }
        if (orgRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + requestDTO.getEmail());
        }

        OrgInfo org = OrgInfo.builder()
                .orgname(requestDTO.getOrgname())
                .email(requestDTO.getEmail())
                .orgType(EOrgType.valueOf(EOrgType.CLIENT.name()))
                .custSignupUrl("TENANT")
                .build();

        OrgInfo saved = orgRepository.save(org);

        OrgInfo update = orgRepository.findById(saved.getId())
                .orElseThrow(()->new ResourceNotFoundException("Org not found"));

        update.setCustSignupUrl(customerSignupUrl+"?orgId="+update.getId());
        OrgInfo saveandupdate = orgRepository.save(update);
        return toResponseDTO(saveandupdate);
    }

    @Transactional(readOnly = true)
    public OrgResponseDTO getOrgById(String id) {
        OrgInfo org = findOrgOrThrow(id);
        return toResponseDTO(org);
    }

    public OrgResponseDTO getOrgByOrgname(String orgname) {
        Optional<OrgInfo> orgInfo = orgRepository.findByOrgname(orgname);
        OrgResponseDTO orgResponseDTO = new OrgResponseDTO();

        return toResponseDTO(orgInfo.get());
    }

    @Transactional(readOnly = true)
    public List<OrgResponseDTO> getAllOrg() {
        return orgRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<OrgResponseDTO> getAllOrg(Pageable pageable) {
        return orgRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrgResponseDTO> searchOrg(String keyword, Pageable pageable) {
        return orgRepository
                .findByOrgnameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable)
                .map(this::toResponseDTO);
    }

    public OrgResponseDTO updateOrg(String id, OrgRequestDTO requestDTO) {
        OrgInfo org = findOrgOrThrow(id);

        if (!org.getEmail().equalsIgnoreCase(requestDTO.getEmail())
                && orgRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + requestDTO.getEmail());
        }
        if (!org.getOrgname().equalsIgnoreCase(requestDTO.getOrgname())
                && orgRepository.existsByOrgname(requestDTO.getOrgname())) {
            throw new DuplicateResourceException("Organization name already taken: " + requestDTO.getOrgname());
        }
        org.setOrgname(requestDTO.getOrgname());
        org.setEmail(requestDTO.getEmail());

        org.setOrgType(requestDTO.getOrgType());
        OrgInfo updated = orgRepository.save(org);
        return toResponseDTO(updated);
    }

   public OrgResponseDTO partialUpdateOrg(String id, OrgRequestDTO requestDTO) {
        OrgInfo org = findOrgOrThrow(id);
        if (StringUtils.hasText(requestDTO.getOrgname())) {
            org.setOrgname(requestDTO.getOrgname());
        }
        if (StringUtils.hasText(requestDTO.getEmail())) {
            org.setEmail(requestDTO.getEmail());
        }

       if (StringUtils.hasText(requestDTO.getOrgType().name())) {
           org.setOrgType(requestDTO.getOrgType());
       }
        OrgInfo updated = orgRepository.save(org);

        return null;
    }

    public void deleteOrg(String id) {
        if (!orgRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organization not found with id: " + id);
        }
        orgRepository.deleteById(id);
    }

    private OrgResponseDTO toResponseDTO(OrgInfo org) {
        return OrgResponseDTO.builder()
                .id(org.getId())
                .orgname(org.getOrgname())
                .orgEmail(org.getEmail())
                .phone(org.getPhone())
                .orgType(org.getOrgType().name())
                .customerSignupUrl(org.getCustSignupUrl())
                .createdAt(org.getCreatedAt())
                .updatedAt(org.getUpdatedAt())
                .build();
    }
    private OrgInfo findOrgOrThrow(String id) {
        return orgRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
    }

    public OrgProfileResponse getProfile(String organizationId) {
        OrgInfo orgInfo = orgRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        OrganizationProfile profile = orgProfileRepository.findByOrganizationId(organizationId)
                .orElse(new OrganizationProfile(organizationId)); // empty profile if not set
        return OrgProfileResponse.from(orgInfo, profile);
    }

    public OrgProfileResponse updateProfile(String organizationId, OrgProfileRequest request) {
        OrgInfo orgInfo = orgRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        OrganizationProfile profile = orgProfileRepository.findByOrganizationId(organizationId)
                .orElse(new OrganizationProfile(organizationId));
        profile.setDescription(request.getDescription());
        profile.setLegalName(request.getLegalName());
        profile.setCountry(request.getCountry());
        profile.setState(request.getState());
        profile.setCity(request.getCity());
        profile.setAddress1(request.getAddress1());
        profile.setAddress2(request.getAddress2());
        profile.setPhone(request.getPhone());
        profile.setWebsite(request.getWebsite());
        profile.setPreferredCurrency(request.getPreferredCurrency());
        profile.setLocale(request.getLocale());
        profile.setTaxId(request.getTaxId());
        profile.setAlterMobile(request.getAlterMobile());
        profile.setLandlineNumber(request.getLandlineNumber());

        orgProfileRepository.save(profile);

        return OrgProfileResponse.from(orgInfo, profile);
    }
}
