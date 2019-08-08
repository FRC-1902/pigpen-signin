package com.explodingbacon.pigpen.signin.beans;

import lombok.Data;

@Data
public class Member {
    String name;
    String position;
    Boolean active;
    Integer id;
    Boolean isIn;
}
