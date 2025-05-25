package com.supplylink.dtos.res;

import java.util.UUID;

public class CategoryResDTO {

    private UUID Id;
    private String Name;

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}