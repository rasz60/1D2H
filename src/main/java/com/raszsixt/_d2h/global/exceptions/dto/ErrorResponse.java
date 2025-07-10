package com.raszsixt._d2h.global.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
}
