package ru.mephi.test.exceptions;

import java.time.OffsetDateTime;

public class ApiError {
    private final String timestamp;
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;
    private final Object details;

    public ApiError(int status, String error, String code, String message, String path, Object details) {
        this.timestamp = OffsetDateTime.now().toString();
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Object getDetails() {
        return details;
    }
}
