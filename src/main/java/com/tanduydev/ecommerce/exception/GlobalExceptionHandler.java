package com.tanduydev.ecommerce.exception;

import com.tanduydev.ecommerce.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. XỬ LÝ LỖI ĐĂNG NHẬP (SAI EMAIL HOẶC MẬT KHẨU)
    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(Exception ex) {
        log.warn("Login failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(ApiResponse.<Void>error(HttpStatus.UNAUTHORIZED.value(), "Email hoặc mật khẩu không chính xác."));
    }

    // 2. XỬ LÝ LỖI THIẾU QUYỀN TRUY CẬP (Ví dụ: Customer gọi API của Admin)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(ApiResponse.<Void>error(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền truy cập vào chức năng này."));
    }

    // 3. XỬ LÝ LỖI JWT (TOKEN HẾT HẠN HOẶC SAI)
    @ExceptionHandler({ExpiredJwtException.class, JwtException.class})
    public ResponseEntity<ApiResponse<Void>> handleJwtException(Exception ex) {
        log.warn("JWT Authentication Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(ApiResponse.<Void>error(HttpStatus.UNAUTHORIZED.value(), "Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại."));
    }

    // 4. XỬ LÝ LỖI VALIDATION TỪ @Valid (Ví dụ: thiếu field, sai định dạng email)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation Error: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(ApiResponse.<Map<String, String>>error(HttpStatus.BAD_REQUEST.value(), "Dữ liệu đầu vào không hợp lệ.", errors));
    }

    // 5. XỬ LÝ CÁC LỖI LOGIC NGHIỆP VỤ (Ví dụ: Email đã tồn tại)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Business Logic Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(ApiResponse.<Void>error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    // 6. XỬ LÝ CÁC LỖI KHÔNG TÌM THẤY RESOURCE
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    // 7. XỬ LÝ CÁC LỖI HỆ THỐNG CÒN LẠI (Fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("System Error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(ApiResponse.<Void>error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã có lỗi hệ thống xảy ra. Vui lòng thử lại sau."));
    }
}