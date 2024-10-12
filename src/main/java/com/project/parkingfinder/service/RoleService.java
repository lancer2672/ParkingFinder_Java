package com.project.parkingfinder.service;

import com.project.parkingfinder.model.Role;
import com.project.parkingfinder.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;


    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }


    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }


    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }


    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
