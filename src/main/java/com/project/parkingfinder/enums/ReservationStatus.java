package com.project.parkingfinder.enums;

public enum ReservationStatus {
    PENDING,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED;

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }
        for (ReservationStatus reservationStatus : ReservationStatus.values()) {
            if (reservationStatus.name().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

}
