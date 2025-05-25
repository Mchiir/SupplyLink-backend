package com.supplylink.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String district;
    private String province;
    private String country;

    public Location() {}
    public Location(String district, String province, String country) {
        this.district = district;
        this.province = province;
        this.country = country;
    }

    @Override
    public String toString() {
        return String.format("Location district=%s, province=%s, country=%s", district, province, country);
    }

    public UUID getId() {
        return id;
    }

//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
