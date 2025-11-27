package com.iseeyou.fortunetelling.controllers;

import com.iseeyou.fortunetelling.controllers.base.AbstractBaseController;
import com.iseeyou.fortunetelling.dtos.PageResponse;
import com.iseeyou.fortunetelling.models.Notification;
import com.iseeyou.fortunetelling.services.PushNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.iseeyou.fortunetelling.dtos.SingleResponse;
import com.iseeyou.fortunetelling.dtos.SuccessResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "001. Notification", description = "Notification API")
@Slf4j
public class NotificationController extends AbstractBaseController {
    private final PushNotificationService pushNotificationService;

    @GetMapping
    @Operation(
            summary = "Get all notifications by user id with pagination",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PageResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<PageResponse<Notification>> getAllNotificationsByUserId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int limit,
            @RequestParam(defaultValue = "desc") String sortType,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam String recipientId
    ) {
        Pageable pageable = createPageable(page, limit, sortType, sortBy);

        return responseFactory.successPage(
                pushNotificationService.getNotificationsByRecipientId(recipientId, pageable),
                "Notifications retrieved successfully");
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get all my notifications with pagination (using JWT authentication)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PageResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<PageResponse<Notification>> getAllMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int limit,
            @RequestParam(defaultValue = "desc") String sortType,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        Pageable pageable = createPageable(page, limit, sortType, sortBy);

        return responseFactory.successPage(
                pushNotificationService.getAllMyNotifications(pageable),
                "My notifications retrieved successfully");
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(
            summary = "Mark notification as read",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notification marked as read successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SingleResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<SingleResponse<Notification>> markNotificationAsRead(
            @PathVariable String notificationId
    ) {
        Notification notification = pushNotificationService.read(notificationId);
        return responseFactory.success(notification, "Notification marked as read successfully");
    }

    @DeleteMapping("/{notificationId}")
    @Operation(
            summary = "Delete notification by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notification deleted successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SuccessResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<SuccessResponse> deleteNotification(
            @PathVariable String notificationId
    ) {
        pushNotificationService.delete(notificationId);
        return responseFactory.success("Notification deleted successfully");
    }
}
