package org.citience.location;

import java.util.Objects;

public final class GPSLocation {
    private static final double MAX_LATITUDE = 90;
    private static final double MAX_LONGITUDE = 180;
    private double latitude;
    private double longitude;

    public GPSLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static boolean isValidLocation(double latitude, double longitude) {
        return Math.abs(latitude) <= MAX_LATITUDE && Math.abs(longitude) <= MAX_LONGITUDE;
    }

    public void setLatitude(double latitude) {
        if (Math.abs(latitude) <= MAX_LATITUDE) {
            this.latitude = latitude;
        }
    }

    public void setLongitude(double longitude) {
        if (Math.abs(longitude) <= MAX_LONGITUDE) {
            this.longitude = longitude;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GPSLocation) obj;
        return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(that.latitude) &&
                Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "GPSLocation[" +
                "latitude=" + latitude + ", " +
                "longitude=" + longitude + ']';
    }

}
