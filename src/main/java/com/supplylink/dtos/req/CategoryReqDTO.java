package com.supplylink.dtos.req;

import jakarta.validation.constraints.NotBlank;

public class CategoryReqDTO {
    
    @NotBlank(message = "Name is mandatory")
    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}