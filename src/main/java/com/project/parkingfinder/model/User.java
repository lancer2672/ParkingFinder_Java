package com.project.parkingfinder.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@ToString
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String age;
    // Constructors, getters, and setters
}
