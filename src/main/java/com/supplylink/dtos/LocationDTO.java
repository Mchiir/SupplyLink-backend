package com.supplylink.dtos;

public class LocationDTO {
    private String district;
    private String province;
    private String country;

    public LocationDTO() {}
    public LocationDTO(String district, String province, String country) {
        this.district = district;
        this.province = province;
        this.country = country;
    }

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
