package com.iseeyou.fortunetelling.controllers.base;

import com.iseeyou.fortunetelling.dtos.PageResponse;
import com.iseeyou.fortunetelling.dtos.SingleResponse;
import com.iseeyou.fortunetelling.dtos.SuccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseFactory {

    public <T> SingleResponse<T> createSingleResponse(
            HttpStatus status,
            String message,
            T data
    ) {
        return SingleResponse.<T>builder()
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
    }

    public <T> PageResponse<T> createPageResponse(
            HttpStatus status,
            String message,
            List<T> data,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        return PageResponse.<T>builder()
                .statusCode(status.value())
                .message(message)
                .data(data)
                .paging(new PageResponse.PagingResponse(page, size, totalElements, totalPages))
                .build();
    }

    public <T> ResponseEntity<SingleResponse<T>> successSingle(
            T data,
            String message
    ) {
        return ResponseEntity.ok(
                createSingleResponse(HttpStatus.OK, message, data)
        );
    }

    public <T> ResponseEntity<SingleResponse<T>> failedSingle(
            T data,
            String message
    ) {
        return ResponseEntity.ok(
                createSingleResponse(HttpStatus.BAD_REQUEST, message, data)
        );
    }

    public <T> ResponseEntity<PageResponse<T>> successPage(
            Page<T> page,
            String message
    ) {
        PageResponse<T> response = createPageResponse(
                HttpStatus.OK,
                message,
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    public <T> ResponseEntity<SingleResponse<T>> success(
            T data,
            String message,
            HttpStatus status
    ) {
        return ResponseEntity.status(status).body(
                createSingleResponse(status, message, data)
        );
    }

    public <T> ResponseEntity<SingleResponse<T>> success(
            T data,
            String message
    ) {
        return successSingle(data, message);
    }

    public ResponseEntity<SuccessResponse> success(String message) {
        SuccessResponse response = SuccessResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .build();
        return ResponseEntity.ok(response);
    }
}