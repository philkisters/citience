package org.citience.location;

import java.util.Objects;

public final class AddressLocation {

    private String city;
    private String district;
    private String borough;
    private String street;
    private String number;
    private String postalCode;

    public AddressLocation(String city, String district, String borough,
                           String street, String number, String postalCode) {
        this.city = city;
        this.district = district;
        this.borough = borough;
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBorough() {
        return borough;
    }

    public void setBorough(String borough) {
        this.borough = borough;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String toShortInfo() {
        return String.format("%s %s, %s %s", this.street, this.number, this.postalCode, this.city);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AddressLocation) obj;
        return Objects.equals(this.city, that.city) &&
                Objects.equals(this.district, that.district) &&
                Objects.equals(this.borough, that.borough) &&
                Objects.equals(this.street, that.street) &&
                Objects.equals(this.number, that.number) &&
                Objects.equals(this.postalCode, that.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, district, borough, street, number, postalCode);
    }

    @Override
    public String toString() {
        return "AddressLocation[" +
                "city=" + city + ", " +
                "district=" + district + ", " +
                "borough=" + borough + ", " +
                "street=" + street + ", " +
                "number=" + number + ", " +
                "postalCode=" + postalCode + ']';
    }

}
