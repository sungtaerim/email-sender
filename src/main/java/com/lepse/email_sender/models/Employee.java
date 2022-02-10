package com.lepse.email_sender.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Employee {

    @JsonProperty("uid")
    private String uid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private String id;
    @JsonProperty("is_out")
    private String isOut;
    @JsonProperty("last_login")
    private String lastLogin;
    @JsonProperty("surrogate")
    private Surrogate surrogate;
    @JsonProperty("group")
    private String group;
    @JsonProperty("status")
    private String status;
    @JsonProperty("fired_date")
    private String firedDate;

    public void setFiredDate(Date firedDate) {
        this.firedDate = new SimpleDateFormat("yyyy-MM-dd").format(firedDate);
    }
}
