package com.project.parkingfinder.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import java.io.Serializable;
import java.util.List;

/**
 * Lớp ResponseTemplate dùng để trả về dữ liệu thống nhất với thông tin phân trang
 * @param <T> Kiểu dữ liệu của danh sách đối tượng
 */
@Data
public class ResponseTemplate<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    // Danh sách dữ liệu
    private List<T> data;

    // Thông tin phân trang
    private PaginationMetadata pagination;

    // Mã trạng thái của response
    private int status;

    // Thông báo
    private String message;

    // Constructor mặc định
    public ResponseTemplate() {}

    // Constructor từ Page
    public ResponseTemplate(Page<T> page) {
        this.data = page.getContent();
        this.pagination = new PaginationMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        this.status = 200;
        this.message = "Success";
    }

    // Constructor cho danh sách không phân trang
    public ResponseTemplate(List<T> data) {
        this.data = data;
        this.status = 200;
        this.message = "Success";
    }

    // Constructor đầy đủ
    public ResponseTemplate(List<T> data, int status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    /**
     * Lớp nested để lưu trữ metadata của phân trang
     */
    @Data
    public static class PaginationMetadata implements Serializable {
        private int currentPage;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        public PaginationMetadata(int currentPage, int pageSize,
                                  long totalElements, int totalPages) {
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }

    // Phương thức factory để tạo response thành công
    public static <T> ResponseTemplate<T> success(List<T> data) {
        return new ResponseTemplate<>(data);
    }

    // Phương thức factory để tạo response thành công từ Page
    public static <T> ResponseTemplate<T> success(Page<T> page) {
        return new ResponseTemplate<>(page);
    }

    // Phương thức factory để tạo response lỗi
    public static <T> ResponseTemplate<T> error(int status, String message) {
        ResponseTemplate<T> response = new ResponseTemplate<>();
        response.setStatus(status);
        response.setMessage(message);
        return response;
    }
}
