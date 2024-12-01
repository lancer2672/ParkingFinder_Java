package com.project.parkingfinder.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.parkingfinder.service.SocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.parkingfinder.dto.ParkingLotDTO;
import com.project.parkingfinder.dto.ParkingSlotDTO;
import com.project.parkingfinder.dto.VehicleDTO;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.enums.VehicleTypeEnum;
import com.project.parkingfinder.service.ParkingLotService;
import com.project.parkingfinder.service.ParkingSlotService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SocketService socketIOHandler;

    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@RequestBody SocketService.ChatMessage message) {
        socketIOHandler.sendToRoom("room_name", message);
        return ResponseEntity.ok("Message sent");
    }


}