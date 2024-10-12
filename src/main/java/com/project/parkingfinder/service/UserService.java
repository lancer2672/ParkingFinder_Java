package com.project.parkingfinder.service;

import com.project.parkingfinder.enums.RoleEnum;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.repository.RoleRepository;
import com.project.parkingfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    private RoleRepository roleRepo;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getUser(Long id) {
        return repository.findById(id).orElse(null);
    }
    public List<User> getAllUsers(){
        return repository.findAll();
    }
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
    public User saveUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return repository.save(user);
    }

    public User loginUser(String phoneNumber, String password) {
        return login(phoneNumber, password, RoleEnum.USER);
    }

    private User login(String phoneNumber, String password, RoleEnum expectedRole) {
        try {
            System.out.println("Attempting to login user with phone number: " + phoneNumber);

            Optional<User> optionalUser = repository.findUserByPhoneNumber(phoneNumber);
            System.out.println("User retrieved: " + optionalUser);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                System.out.println("User found: " + user.getPhoneNumber() + " with role: " + user.getRole().getName());

                if (passwordEncoder.matches(password, user.getPassword())) {
                    System.out.println("Password matches for user: " + user.getPhoneNumber());
                    if (user.getRole().getName().equalsIgnoreCase(expectedRole.name())) {
                        System.out.println("User role matches expected role: " + expectedRole);
                        return user;
                    } else {
                        System.out.println("User role does not match expected role. User role: '" +
                                user.getRole().getName() + "', Expected role: '" + expectedRole.name() + "'");
                    }
                } else {
                    System.out.println("Password does not match for user: " + user.getPhoneNumber());
                }
            } else {
                System.out.println("No user found with phone number: " + phoneNumber);
            }
        } catch (Exception e) {
            System.err.println("An error occurred during login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public User loginMerchant(String phoneNumber , String password) {
        return login(phoneNumber, password, RoleEnum.MERCHANT);
    }


    public User loginAdmin(String phoneNumber, String password) {
        return login(phoneNumber, password, RoleEnum.ADMIN);
    }
    public boolean isPhoneNumberExists(String phoneNumber) {
        Optional<User> optionalUser = repository.findUserByPhoneNumber(phoneNumber);
        return optionalUser.isPresent();
    }
}