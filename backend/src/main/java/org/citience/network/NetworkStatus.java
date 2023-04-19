package org.citience.network;

public enum NetworkStatus {
    OFFLINE,
    CONNECTING,
    ONLINE,
    DISCONNECTED,
    ERROR;

    public String getColor() {
        return switch (this) {
            case CONNECTING -> "orange";
            case ONLINE -> "green";
            case DISCONNECTED -> "sky";
            case ERROR -> "red";
            default -> "stone";
        };
    }
}
