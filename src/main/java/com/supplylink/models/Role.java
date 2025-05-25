package com.supplylink.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    public Role() {}
    public Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
            this.name = name;
    }
    public String getName() {
        return name;
    }
}