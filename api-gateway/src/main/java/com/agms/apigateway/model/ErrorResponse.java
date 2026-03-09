package com.agms.apigateway.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ErrorResponse
 * ==============
 * Standardized JSON error response returned by the Gateway
 * when JWT validation fails or a service is unavailable.
 *
 * Example JSON:
 * {
 *   "status"    : 401,
 *   "error"     : "Unauthorized",
 *   "message"   : "JWT token has expired",
 *   "path"      : "/api/zones",
 *   "timestamp" : "2026-02-22T08:30:00"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}