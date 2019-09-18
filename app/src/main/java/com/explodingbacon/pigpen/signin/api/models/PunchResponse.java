package com.explodingbacon.pigpen.signin.api.models;

import lombok.Data;

@Data
public class PunchResponse {
    Boolean success;
    String punch;
    String member;
    String time;
    String duration;
    String error;
}
