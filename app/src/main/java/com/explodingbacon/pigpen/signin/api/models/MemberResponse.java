package com.explodingbacon.pigpen.signin.api.models;

import com.explodingbacon.pigpen.signin.beans.Member;

import java.util.List;

import lombok.Data;

@Data
public class MemberResponse {
    List<Member> members;
}
