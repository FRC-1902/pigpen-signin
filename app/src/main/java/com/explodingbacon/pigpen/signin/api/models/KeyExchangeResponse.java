package com.explodingbacon.pigpen.signin.api.models;

import lombok.Data;

@Data
public class KeyExchangeResponse {
    Boolean success;
    String token;
    String message;
}
