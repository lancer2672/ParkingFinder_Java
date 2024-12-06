package com.project.parkingfinder.service;

import com.project.parkingfinder.enums.ReservationStatus;
import com.project.parkingfinder.model.Reservation;
import com.project.parkingfinder.repository.ReservationRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CancelTicketJob implements Job {


    private final ReservationRepository reservationRepository;

    @Autowired
    private final SocketService socketService;

    public CancelTicketJob(ReservationRepository reservationRepository, SocketService socketService) {
        this.reservationRepository = reservationRepository;
        this.socketService = socketService;
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long reservationId = context.getJobDetail().getJobDataMap().getLong("ticketId");
        Reservation ticket = reservationRepository.findById(reservationId).orElse(null);

        if (ticket != null && ticket.getStatus().equals(ReservationStatus.PENDING)) {
            LocalDateTime now = LocalDateTime.now();

            // Sử dụng định dạng ISO 8601 (local time)
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            String formattedTime = now.format(formatter);

            ticket.setStatus(ReservationStatus.CANCELLED);
            ticket.setCheckOutTime(LocalDateTime.parse(formattedTime));
            System.out.println("ReservationStatus" );
            reservationRepository.save(ticket);
            System.out.println("Cancelled ticket ID: " + reservationId);
            socketService.emitCancelMessage(ticket.getUser().getId().toString(), reservationId.toString());
        }
    }

}
