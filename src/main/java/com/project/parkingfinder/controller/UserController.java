package com.project.parkingfinder.controller;

import com.project.parkingfinder.model.User;
import com.project.parkingfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")  // Đường dẫn API sẽ bắt đầu với /api/users
public class UserController {

    @Autowired
    private UserService userService;

    // API để lấy thông tin user theo id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUser(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // API để lấy danh sách tất cả users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // API để xóa user theo id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();  // Trả về mã 204 (No Content) sau khi xóa thành công
    }

    // API để tạo mới user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Giả sử bạn có thêm phương thức `saveUser` trong UserService để lưu user mới
        User createdUser = userService.saveUser(user);  // Bạn cần viết thêm phương thức này
        return ResponseEntity.ok(createdUser);  // Trả về thông tin user sau khi tạo
    }
}
