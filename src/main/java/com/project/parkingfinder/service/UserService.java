package com.project.parkingfinder.service;

import com.project.parkingfinder.model.User;
import com.project.parkingfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public User getUser(Long id) {
        return repository.findById(id).orElse(null);
    }
    public List<User> getAllUsers(){
        return repository.findAll();
    }
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
    // Thêm phương thức để lưu user
    public User saveUser(User user) {
        return repository.save(user);
    }

    public User loginUser(String username, String password) {
//        // Thực hiện xác thực và kiểm tra vai trò user
//        User user = repository.findByUsername(username); // Giả sử có phương thức tìm kiếm theo username
//        if (user != null && user.getPassword().equals(password) && user.getRole().equals("USER")) {
//            return user;
//        }
        return null;
    }

    // Phương thức đăng nhập cho merchant
    public User loginMerchant(String username, String password) {
//        User merchant = repository.findByUsername(username);
//        if (merchant != null && merchant.getPassword().equals(password) && merchant.getRole().equals("MERCHANT")) {
//            return merchant;
//        }
        return null;
    }


    public User loginAdmin(String username, String password) {
//        User admin = repository.findByUsername(username);
//        if (admin != null && admin.getPassword().equals(password) && admin.getRole().equals("ADMIN")) {
//            return admin;
//        }
        return null;
    }
}