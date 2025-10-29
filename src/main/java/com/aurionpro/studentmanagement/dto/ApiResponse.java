package com.aurionpro.studentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A generic wrapper class for all API responses.
 * This standardized format provides clients with a consistent structure,
 * including a status, a descriptive message, and the actual data payload.
 *
 * @param <T> The type of the data payload to be included in the response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * The status of the response, typically "success" or "error".
     */
    private String status;

    /**
     * A user-friendly message describing the result of the operation.
     */
    private String message;

    /**
     * The actual data payload of the response. The type is generic
     * and can vary depending on the API endpoint.
     */
    private T data;
}