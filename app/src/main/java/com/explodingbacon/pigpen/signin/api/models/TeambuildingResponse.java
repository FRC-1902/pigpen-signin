package com.explodingbacon.pigpen.signin.api.models;

import lombok.Data;

@Data
public class TeambuildingResponse {
    Boolean active;
    String id;
    String question;
    String option_one;
    String option_two;
}
