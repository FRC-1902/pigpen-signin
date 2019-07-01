package com.explodingbacon.pigpen.signin.api.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SignedInResponse {
    @SerializedName("signed_in")
    Boolean isSignedIn;
}
