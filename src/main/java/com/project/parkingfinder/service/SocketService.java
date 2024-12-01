package com.project.parkingfinder.service;


import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketService {

    @Autowired
    private SocketIOServer socketServer;

    @PostConstruct
    private void initSocketEvents() {
        // Lắng nghe event từ client
        socketServer.addEventListener("payment", ChatMessage.class, (client, data, ackRequest) -> {
            // Broadcast message tới tất cả client
            socketServer.getBroadcastOperations().sendEvent("chat_message", data);
            log.info("Received message: " + data);
        });
    }

    public void sendToRoom(String roomName, ChatMessage message) {
        socketServer.getRoomOperations(roomName).sendEvent("chat_message", message);
    }

    public void emitPaymentMessage(String userId, String paymentStatus) {
        PaymentMessage paymentMessage = new PaymentMessage(userId, paymentStatus);
        socketServer.getBroadcastOperations().sendEvent("payment", paymentMessage);
        log.info("Payment message sent to all clients: " + paymentMessage);
    }
    @Data
    public class PaymentMessage {
        private String userId;
        private String paymentStatus;

        public PaymentMessage(String userId, String paymentStatus) {
            this.userId = userId;
            this.paymentStatus = paymentStatus;
        }
    }
    @Data
    public class ChatMessage {
        private String content;
        private String sender;
        private MessageType type;

        public enum MessageType {
            CHAT,
            JOIN,
            LEAVE
        }
    }
}