package com.project.parkingfinder.service;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.socket.socketio.server.SocketIoServer;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SocketService {

    @Autowired
    private SocketIoServer socketServer;

    @PostConstruct
    private void initSocketEvents() {
        var namespace = socketServer.namespace("/socketio");
//        // Lắng nghe event từ client
//        namespace.emit("payment", ChatMessage.class, (client, data, ackRequest) -> {
//            // Broadcast message tới tất cả client
//            socketServer.getBroadcastOperations().sendEvent("chat_message", data);
//            log.info("Received message: " + data);
//        });
    }

    public void sendToRoom(String roomName, ChatMessage message) {
//        socketServer.getRoomOperations(roomName).sendEvent("chat_message", message);
    }

    public void emitPaymentMessage(String userId, String paymentStatus, Double amount, String reservatinId) {
        var namespace = socketServer.namespace("/socketio");

        PaymentMessage paymentMessage = new PaymentMessage(userId, paymentStatus, amount,reservatinId);

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("userId", paymentMessage.getUserId());
        jsonMessage.put("paymentStatus", paymentMessage.getPaymentStatus());
        jsonMessage.put("amount", paymentMessage.getAmount());
        jsonMessage.put("reservationId", paymentMessage.getReservationId());

        namespace.broadcast(null,"payment", jsonMessage);
        log.info("Payment message sent to all clients: " + paymentMessage);
    }
    public void emitCancelMessage(String userId, String isCancelled) {
        var namespace = socketServer.namespace("/socketio");

        CancelMessage paymentMessage = new CancelMessage(userId, isCancelled);
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("userId", paymentMessage.getUserId());
        jsonMessage.put("reservationId", paymentMessage.getReservationId());

        namespace.broadcast(null,"cancel-reservation", jsonMessage);
        log.info("Payment message sent to all clients: CancelMessage " + paymentMessage);
    }
     public void emitUpdateStatusMsg(String userId, String resId, String status, Double price, String parkingLotID) {
         var namespace = socketServer.namespace("/socketio");

        UpdateReservationMsg paymentMessage = new UpdateReservationMsg(userId, resId,status);
        paymentMessage.setAmount(price);
         JSONObject jsonMessage = new JSONObject();
         jsonMessage.put("userId", paymentMessage.getUserId());
         jsonMessage.put("status", paymentMessage.getStatus());
         jsonMessage.put("reservationId", paymentMessage.getReservationId());
         jsonMessage.put("parkinglotId", parkingLotID);
         jsonMessage.put("amount", paymentMessage.getAmount());
         namespace.broadcast(null,"update-reservation", jsonMessage);

        log.info("Payment message sent to all clients: emitUpdateStatusMsg " + paymentMessage);
    }
    @Data
    public class PaymentMessage {
        private String userId;
        private String paymentStatus;
        
        private Double amount;
        private String reservationId;
        public PaymentMessage(String userId, String paymentStatus, Double amount, String reservatinId) {
            this.userId = userId;
            this.paymentStatus = paymentStatus;
            this.amount = amount;
            this.reservationId = reservatinId;
        }
    }
    @Data
    public class CancelMessage {
        private String userId;
        private String reservationId;

        public CancelMessage(String userId, String reservationId) {
            this.userId = userId;
            this.reservationId = reservationId;
        }
    }
    @Data
    public class UpdateReservationMsg {
        private String userId;
        private String reservationId;
        private String status;
        private Double amount;

        public UpdateReservationMsg(String userId, String reservationId, String status) {
            this.userId = userId;
            this.reservationId = reservationId;
            this.status = status;
        }
        public void setAmount(Double amount){
            this.amount = amount;
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