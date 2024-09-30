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

    public User getUser(String id) {
        return repository.findById(id).orElse(null);
    }
    public List<User> getAllUsers(){
        return repository.findAll();
    }
    public void deleteUser(String id) {
        repository.deleteById(id);
    }
    // Thêm phương thức để lưu user
    public User saveUser(User user) {
        return repository.save(user);
    }
}