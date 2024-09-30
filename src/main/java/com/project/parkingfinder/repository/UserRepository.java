package com.project.parkingfinder.repository;


import com.project.parkingfinder.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

}