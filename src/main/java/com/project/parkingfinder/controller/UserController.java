package com.project.parkingfinder.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkingfinder.dto.LoginResponse;
import com.project.parkingfinder.enums.RoleEnum;
import com.project.parkingfinder.enums.UserStatus;
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
    @Autowired
    private RoleService roleService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        System.out.println("Getting user with id: " + id);
        User user = userService.getUser(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/merchants")
    public ResponseEntity<List<User>> getMerchants(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "status", required = false) String status) {
        List<User> merchants = userService.getMerchants(size, page, status);
        return ResponseEntity.ok(merchants);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id")  Long id) {
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
            return ResponseEntity.badRequest().body("Thông tin đăng nhập không đúng");
        }
    }
    @PostMapping("/merchant/signin")
    public ResponseEntity<?> loginMerchant(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse resp  = userService.loginMerchant(loginRequest.getPhoneNumber(), loginRequest.getPassword());
        if (resp != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body("Thông tin đăng nhập không đúng");
        }
    }
    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse resp = userService.loginUser(loginRequest.getPhoneNumber(), loginRequest.getPassword());
        if (resp != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body("Thông tin đăng nhập không đúng");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        validateSignUpRequest(signUpRequest);
        if (userService.isPhoneNumberExists(signUpRequest.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Số điện thoại đã được đăng ký");
        }

        User newUser = createUser(signUpRequest);
        User createdUser = userService.saveUser(newUser);

        return ResponseEntity.ok(convertToResponseDTO(createdUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User existingUser = userService.getUser(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateUserRequest.getName() != null) {
            existingUser.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.getEmail() != null) {
            existingUser.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getPassword() != null) {
            existingUser.setPassword(updateUserRequest.getPassword()); // Remember to hash the password
        }
        if (updateUserRequest.getStatus() != null) {
            existingUser.setStatus(updateUserRequest.getStatus());
        }   

        User updatedUser = userService.saveUser(existingUser);
        return ResponseEntity.ok(convertToResponseDTO(updatedUser));
    }

    private void validateSignUpRequest(SignUpRequest signUpRequest) {
        boolean isValidRole = RoleEnum.isValidRole(signUpRequest.getRole());
        if (!isValidRole) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + signUpRequest.getRole());
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
            throw new RuntimeException("Vai trò không tìm thấy: " + signUpRequest.getRole());
        }
        newUser.setRole(role.get());

        if (RoleEnum.MERCHANT.name().equals(signUpRequest.getRole())) {
            newUser.setStatus(UserStatus.INACTIVE);
        } else {
            newUser.setStatus(UserStatus.ACTIVE);
        }

        return newUser;
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setStatus(user.getStatus());    
        return responseDTO;
    }

    @Data
    public class UserResponseDTO {
        private Long id;
        private String name;
        private String email;
        private UserStatus status;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Số điện thoại là bắt buộc")
        private String phoneNumber;

        @NotBlank(message = "Mật khẩu là bắt buộc")
        private String password;
    }

    @Data
    public static class SignUpRequest {
        @NotBlank(message = "Số điện thoại là bắt buộc")
        private String phoneNumber;

        @NotBlank(message = "Mật khẩu là bắt buộc")
        @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
        private String password;

        @NotBlank(message = "Email là bắt buộc")
        @Email(message = "Email phải hợp lệ")
        private String email;

        @NotBlank(message = "Tên là bắt buộc")
        private String name;

        @NotBlank(message = "Vai trò là bắt buộc")
        private String role;
    }

    @Data
    public static class UpdateUserRequest {
        @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
        private String password;

        @Email(message = "Email phải hợp lệ")
        private String email;

        private String name;
        private UserStatus status;
    }
}
