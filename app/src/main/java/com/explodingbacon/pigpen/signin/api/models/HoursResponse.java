package com.explodingbacon.pigpen.signin.api.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class HoursResponse {
    @SerializedName("build")
    String build;

    @SerializedName("out")
    String outreach;

    @SerializedName("comp")
    String competition;

    @SerializedName("fun")
    String fun;

    @SerializedName("othr")
    String other;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (build != null) {
            builder.append("Build: ").append(build).append('\n');
        }
        if (outreach != null) {
            builder.append("Outreach: ").append(outreach).append('\n');
        }
        if (fun != null) {
            builder.append("Fun: ").append(fun).append('\n');
        }
        if (other != null) {
            builder.append("Other: ").append(other).append('\n');
        }

        return builder.toString().trim(); //Remove trailing newline
    }
}
