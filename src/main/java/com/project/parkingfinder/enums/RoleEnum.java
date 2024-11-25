package com.project.parkingfinder.enums;

public enum RoleEnum {
    USER,
    MERCHANT,
    ADMIN,
    STAFF;
    public static boolean isValidRole(String role) {
        if (role == null) {
            return false;
        }
        for (RoleEnum r : RoleEnum.values()) {
            if (r.name().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }
}
