package com.project.parkingfinder.service;

import com.project.parkingfinder.enums.ReservationStatus;
import com.project.parkingfinder.model.Reservation;
import com.project.parkingfinder.repository.ReservationRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class CancelTicketJob implements Job {


    private final ReservationRepository reservationRepository;

    public CancelTicketJob(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long reservationId = context.getJobDetail().getJobDataMap().getLong("ticketId");
        Reservation ticket = reservationRepository.findById(reservationId).orElse(null);

        if (ticket != null && ticket.getStatus().equals(ReservationStatus.PENDING)) {
            ticket.setStatus(ReservationStatus.CANCELLED);
            System.out.println("ReservationStatus" );
            reservationRepository.save(ticket);
            System.out.println("Cancelled ticket ID: " + reservationId);
        }
    }

}
