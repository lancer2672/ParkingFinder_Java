package com.project.parkingfinder.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkingfinder.dto.LoginResponse;
import com.project.parkingfinder.enums.RoleEnum;
import com.project.parkingfinder.model.Role;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.service.RoleService;
import com.project.parkingfinder.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    private RoleService repoService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUser(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // New login methods

    @PostMapping("/admin/signin")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse resp = userService.loginAdmin(loginRequest.getPhoneNumber(), loginRequest.getPassword());
        if (resp != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }
    @PostMapping("/merchant/signin")
    public ResponseEntity<?> loginMerchant(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse resp  = userService.loginMerchant(loginRequest.getPhoneNumber(), loginRequest.getPassword());
        if (resp != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }
    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse resp = userService.loginUser(loginRequest.getPhoneNumber(), loginRequest.getPassword());
        if (resp != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        validateSignUpRequest(signUpRequest);
        if (userService.isPhoneNumberExists(signUpRequest.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Phone number already registered");
        }

        User newUser = createUser(signUpRequest);
        User createdUser = userService.saveUser(newUser);

        return ResponseEntity.ok(convertToResponseDTO(createdUser));
    }

    private void validateSignUpRequest(SignUpRequest signUpRequest) {
        boolean isValidRole = RoleEnum.isValidRole(signUpRequest.getRole());
        if (!isValidRole) {
            throw new IllegalArgumentException("Invalid role: " + signUpRequest.getRole());
        }
    }



    private User createUser(SignUpRequest signUpRequest) {
        User newUser = new User();
        newUser.setPhoneNumber(signUpRequest.getPhoneNumber());
        newUser.setPassword(signUpRequest.getPassword()); // Remember to hash the password
        newUser.setName(signUpRequest.getName());
        newUser.setEmail(signUpRequest.getEmail());

        Optional<Role> role = roleService.getRoleByName(signUpRequest.getRole().toUpperCase());
        if (role.isEmpty()) {
            throw new RuntimeException("Role not found: " + signUpRequest.getRole());
        }
        newUser.setRole(role.get());

        return newUser;
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());

        return responseDTO;
    }


    @Data
    public class UserResponseDTO {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class SignUpRequest {

        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Role is required")
        private String role;
        // Getters and Setters
    }

}

