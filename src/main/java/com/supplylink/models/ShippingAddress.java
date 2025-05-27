package com.supplylink.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

// destination of order
@Embeddable
public class ShippingAddress {
    @NotBlank
    private String province;
    @NotBlank
    private String district;
    @NotBlank
    private String sector;
    @NotBlank
    private String village;
    @NotBlank
    private String description; // e.g., "Near Nyabugogo bus park"

    public ShippingAddress() {}
    public ShippingAddress(String province, String district, String sector, String village, String description) {
        this.province = province;
        this.district = district;
        this.sector = sector;
        this.village = village;
        this.description = description;
    }

    public @NotBlank String getProvince() {
        return province;
    }

    public void setProvince(@NotBlank String province) {
        this.province = province;
    }

    public @NotBlank String getDistrict() {
        return district;
    }

    public void setDistrict(@NotBlank String district) {
        this.district = district;
    }

    public @NotBlank String getSector() {
        return sector;
    }

    public void setSector(@NotBlank String sector) {
        this.sector = sector;
    }

    public @NotBlank String getVillage() {
        return village;
    }

    public void setVillage(@NotBlank String village) {
        this.village = village;
    }

    public @NotBlank String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank String description) {
        this.description = description;
    }
}