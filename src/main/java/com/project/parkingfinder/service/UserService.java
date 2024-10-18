package com.project.parkingfinder.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.dto.LoginResponse;
import com.project.parkingfinder.enums.RoleEnum;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.repository.UserRepository;
import com.project.parkingfinder.security.JwtTokenProvider;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
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
        return login(phoneNumber, password, RoleEnum.USER);
    }

    public LoginResponse login(String phoneNumber, String password, RoleEnum expectedRole) {
        User user = userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone number or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid phone number or password");
        }

        if (!user.getRole().getName().equals(expectedRole.name())) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
            
        return new LoginResponse(user.getId(), accessToken, refreshToken);
    }

    public LoginResponse loginMerchant(String phoneNumber , String password) {
        return login(phoneNumber, password, RoleEnum.MERCHANT);
    }


    public LoginResponse loginAdmin(String phoneNumber, String password) {
        return login(phoneNumber, password, RoleEnum.ADMIN);
    }
    public boolean isPhoneNumberExists(String phoneNumber) {
        Optional<User> optionalUser = userRepository.findUserByPhoneNumber(phoneNumber);
        return optionalUser.isPresent();
    }
}