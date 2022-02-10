package com.lepse.email_sender.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Surrogate {

    @JsonProperty("uid")
    private String uid;
    @JsonProperty("id")
    private String userId;
    @JsonProperty("name")
    private String userName;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;

    public String getStartDate() {
        return startDate.equals("unknown") ? "" : startDate;
    }

    public String getEndDate() {
        return endDate.equals("unknown") ? "" : endDate;
    }
}
