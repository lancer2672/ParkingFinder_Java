package com.project.parkingfinder.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.dto.LoginResponse;
import com.project.parkingfinder.dto.UserDTO;
import com.project.parkingfinder.enums.RoleEnum;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.repository.RoleRepository;
import com.project.parkingfinder.repository.UserRepository;
import com.project.parkingfinder.security.ApiKey;
import com.project.parkingfinder.security.ApiKeyLoader;
import com.project.parkingfinder.security.JwtTokenProvider;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<UserDTO> getMerchants(int size, int page, String status) {
        Long merchantRoleId = roleRepository.findByName(RoleEnum.MERCHANT.name())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò người bán")).getId();
        
        List<User> users = userRepository.findByRoleIdAndStatus(merchantRoleId, status, 
                org.springframework.data.domain.PageRequest.of(page,size))
                .getContent();
            
        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setName(user.getName());
                    userDTO.setPhoneNumber(user.getPhoneNumber());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setStatus(user.getStatus());
                    // Map to API keys to check if there's a corresponding userId -> get apiKey to attach to userDTO, otherwise assign an empty string
                    try {
                        String apiKey = ApiKeyLoader.loadApiKeys("api_keys.json").stream()
                                .filter(key -> key.getUserId().equals(user.getId().toString()))
                                .findFirst()
                                .map(ApiKey::getApiKey)
                                .orElse("");
                        userDTO.setApiKey(apiKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                        userDTO.setApiKey("");
                    }
                    return userDTO;
                })
                .collect(Collectors.toList());
        return userDTOs;
    }
   

    public  Page<User>  getStaffs(int size, int page, String status) {
        Long staffId = roleRepository.findByName(RoleEnum.STAFF.name())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò")).getId();
        
        return userRepository.findByRoleIdAndStatus(staffId, status, 
                org.springframework.data.domain.PageRequest.of(page,size));
    }
    public  Page<User>  getStaffsByMerchant(long merchantId,int size, int page) {
        Long staffId = roleRepository.findByName(RoleEnum.STAFF.name())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò")).getId();

        return userRepository.findStaffByMerchant(merchantId,staffId,
                org.springframework.data.domain.PageRequest.of(page,size));
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    public User saveUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public LoginResponse loginUser(String phoneNumber, String password) {
        return login(phoneNumber, password);
    }

    public LoginResponse login(String phoneNumber, String password) {
        User user = userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("Số điện thoại hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Số điện thoại hoặc mật khẩu không đúng");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
            
        return new LoginResponse(user.getId(), user.getRole().getName(),accessToken, refreshToken);
    }

    public LoginResponse loginMerchant(String phoneNumber , String password) {
        return login(phoneNumber, password);
    }
    public LoginResponse loginStaff(String phoneNumber , String password) {
        return login(phoneNumber, password);
    }


    public LoginResponse loginAdmin(String phoneNumber, String password) {
        return login(phoneNumber, password);
    }
    public boolean isPhoneNumberExists(String phoneNumber) {
        Optional<User> optionalUser = userRepository.findUserByPhoneNumber(phoneNumber);
        return optionalUser.isPresent();
    }
    
}