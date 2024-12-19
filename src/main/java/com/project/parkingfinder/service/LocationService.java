package com.project.parkingfinder.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public class LocationService {
    private static final Map<String, String> provinceMap = new HashMap<>();
    private static final String BASE_URL = "https://open.oapi.vn/location";

    static {
        provinceMap.put("92", "Thành phố Cần Thơ");
        provinceMap.put("48", "Thành phố Đà Nẵng");
        provinceMap.put("01", "Thành phố Hà Nội");
        provinceMap.put("31", "Thành phố Hải Phòng");
        provinceMap.put("79", "Thành phố Hồ Chí Minh");
        provinceMap.put("89", "Tỉnh An Giang");
        provinceMap.put("77", "Tỉnh Bà Rịa - Vũng Tàu");
        provinceMap.put("95", "Tỉnh Bạc Liêu");
        provinceMap.put("24", "Tỉnh Bắc Giang");
        provinceMap.put("06", "Tỉnh Bắc Kạn");
        provinceMap.put("27", "Tỉnh Bắc Ninh");
        provinceMap.put("83", "Tỉnh Bến Tre");
        provinceMap.put("74", "Tỉnh Bình Dương");
        provinceMap.put("52", "Tỉnh Bình Định");
        provinceMap.put("70", "Tỉnh Bình Phước");
        provinceMap.put("60", "Tỉnh Bình Thuận");
        provinceMap.put("96", "Tỉnh Cà Mau");
        provinceMap.put("04", "Tỉnh Cao Bằng");
        provinceMap.put("66", "Tỉnh Đắk Lắk");
        provinceMap.put("67", "Tỉnh Đắk Nông");
        provinceMap.put("11", "Tỉnh Điện Biên");
        provinceMap.put("75", "Tỉnh Đồng Nai");
        provinceMap.put("87", "Tỉnh Đồng Tháp");
        provinceMap.put("64", "Tỉnh Gia Lai");
        provinceMap.put("02", "Tỉnh Hà Giang");
        provinceMap.put("35", "Tỉnh Hà Nam");
        provinceMap.put("42", "Tỉnh Hà Tĩnh");
        provinceMap.put("30", "Tỉnh Hải Dương");
        provinceMap.put("93", "Tỉnh Hậu Giang");
        provinceMap.put("17", "Tỉnh Hoà Bình");
        provinceMap.put("33", "Tỉnh Hưng Yên");
        provinceMap.put("56", "Tỉnh Khánh Hòa");
        provinceMap.put("91", "Tỉnh Kiên Giang");
        provinceMap.put("62", "Tỉnh Kon Tum");
        provinceMap.put("12", "Tỉnh Lai Châu");
        provinceMap.put("20", "Tỉnh Lạng Sơn");
        provinceMap.put("10", "Tỉnh Lào Cai");
        provinceMap.put("68", "Tỉnh Lâm Đồng");
        provinceMap.put("80", "Tỉnh Long An");
        provinceMap.put("36", "Tỉnh Nam Định");
        provinceMap.put("40", "Tỉnh Nghệ An");
        provinceMap.put("37", "Tỉnh Ninh Bình");
        provinceMap.put("58", "Tỉnh Ninh Thuận");
        provinceMap.put("25", "Tỉnh Phú Thọ");
        provinceMap.put("54", "Tỉnh Phú Yên");
        provinceMap.put("44", "Tỉnh Quảng Bình");
        provinceMap.put("49", "Tỉnh Quảng Nam");
        provinceMap.put("51", "Tỉnh Quảng Ngãi");
        provinceMap.put("22", "Tỉnh Quảng Ninh");
        provinceMap.put("45", "Tỉnh Quảng Trị");
        provinceMap.put("94", "Tỉnh Sóc Trăng");
        provinceMap.put("14", "Tỉnh Sơn La");
        provinceMap.put("72", "Tỉnh Tây Ninh");
        provinceMap.put("34", "Tỉnh Thái Bình");
        provinceMap.put("19", "Tỉnh Thái Nguyên");
        provinceMap.put("38", "Tỉnh Thanh Hóa");
        provinceMap.put("46", "Tỉnh Thừa Thiên Huế");
        provinceMap.put("82", "Tỉnh Tiền Giang");
        provinceMap.put("84", "Tỉnh Trà Vinh");
        provinceMap.put("08", "Tỉnh Tuyên Quang");
        provinceMap.put("86", "Tỉnh Vĩnh Long");
        provinceMap.put("26", "Tỉnh Vĩnh Phúc");
        provinceMap.put("15", "Tỉnh Yên Bái");
    }

    public static String getProvinceName(String provinceId) {
        return provinceMap.getOrDefault(provinceId, "Unknown Province");
    }

    public static String getDistrictName(String provinceId, String districtId) {
        try {
            String apiUrl = BASE_URL + "/districts/" + provinceId + "?size=1000";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<DistrictResponse> response = restTemplate.getForEntity(apiUrl, DistrictResponse.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                DistrictResponse districtResponse = response.getBody();
                return districtResponse.getData().stream()
                    .filter(district -> district.getDistrictId().equals(districtId))
                    .findFirst()
                    .map(District::getDistrictName)
                    .orElse("Unknown District");
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error fetching district name: " + e.getMessage());
        }
        return "Unknown District";
    }

    private static class DistrictResponse {
        private List<District> data;

        public List<District> getData() {
            return data;
        }
    }

    @Data
    private static class District {
        @JsonProperty("id")
        private String districtId;
        
        @JsonProperty("name")
        private String districtName;

        public String getDistrictId() {
            return districtId;
        }

        public String getDistrictName() {
            return districtName;
        }
    }

    public static String getWardName(String districtId, String wardId) {
        try {
            String apiUrl = BASE_URL + "/wards/" + districtId + "?size=1000";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<WardResponse> response = restTemplate.getForEntity(apiUrl, WardResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                WardResponse wardResponse = response.getBody();
                return wardResponse.getData().stream()
                        .filter(ward -> ward.getWardId().equals(wardId))
                        .findFirst()
                        .map(Ward::getWardName)
                        .orElse("Unknown Ward");
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error fetching ward name: " + e.getMessage());
        }
        return "Unknown Ward";
    }

    private static class WardResponse {
        private List<Ward> data;

        public List<Ward> getData() {
            return data;
        }

    }

    @Data
    private static class Ward {
        @JsonProperty("id")
        private String wardId;

        @JsonProperty("name")
        private String wardName;

        public String getWardId() {
            return wardId;
        }

        public String getWardName() {
            return wardName;
        }
    }

}
