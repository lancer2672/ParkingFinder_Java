package com.project.parkingfinder;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class ParkingFinderApplication {

    @RestController
    public class HelloController {

        @GetMapping("/health")
        public String sayHello() {
            return "Hello, World!";
        }
    }
    public static void main(String[] args) {
        SpringApplication.run(ParkingFinderApplication.class, args);
        
    }

}
