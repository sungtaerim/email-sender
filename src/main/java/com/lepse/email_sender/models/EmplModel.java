package com.lepse.email_sender.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmplModel {

    private List<Employee> employee = new ArrayList<>();
}
