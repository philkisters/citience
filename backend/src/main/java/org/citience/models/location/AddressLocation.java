package org.citience.models.location;

public record AddressLocation(String district, String borough,
                              String street, int number, int postalCode) {
}
