package org.citience.models.sensors;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.citience.network.DrasylAddress;

import java.util.Objects;

/**
 * This class represents the information known about a single sensor.
 * This can be a local sensor or a sensor discovered on a different node in the network.
 *
 */
@Entity
@Table(name = "sensors")
public final class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * A self defined sensor name.
     * This name is just used for user interaction, so the name in itself doesn't have to be unique,
     * but it needs to be unique in combination with the type and the node address hosting this sensor.
     */
    @NotBlank(message = "Name must contain at least one letter.")
    @Column(name = "name")
    private String name;

    /**
     * A self defined sensor type.
     * This type is used in the SkABNet Sensor representation, so it should comply with common sensor types (e.g. Temperature, Humidity etc.).
     * There can be any number of sensors with the same type provided by a node, but they need different names.
     */
    @NotBlank(message = "Type must contain at least one letter.")
    @Column(name = "type")
    private String type;

    /**
     * A self defined location of a given sensor.
     * This is only used locally for user interaction and can contain any description of the location of the sensor (e.g. garden house, garage, etc.).
     *
     */
    @Column(name = "location")
    private String location;

    /**
     * Defines if the sensor is a self-managed local sensor.
     * This makes it easy to differentiate between remote sensors and local sensors for various user interactions.
     */
    @Column(name = "local")
    private boolean local;

    /**
     * String representation of the {@code DrasylAddress} hosting the sensor.
     * If it is a local sensor the address is empty.
     */
    @Column(name = "address")
    private String address;

    /**
     * Empty constructor for automated data creation.
     * Note: Shouldn't be used by other classes.
     */
    protected Sensor() {
        this("empty", "none", "none", true, null);
    }

    /**
     * Constructor to create a local sensor.
     *
     * @param name Name of the sensor, must contain at least one letter
     * @param type Type of the sensor, must contain at least one letter
     * @param location Custom location of the sensor
     */
    public Sensor(String name, String type, String location) {
        this(name, type, location, true, null);
    }

    /**
     * Constructor to create a remote sensor.
     *
     * @param name Name of the sensor, must contain at least one letter
     * @param type Type of the sensor, must contain at least one letter
     * @param address The {@code DrasylAddress} of the node that manages the sensor.
     */
    public Sensor(String name, String type, DrasylAddress address) {
        this(name, type, "", false, address);
    }

    /**
     * All-Args Constructor used for testing and by the other constructors.
     *
     * @param name Name of the sensor, must contain at least one letter
     * @param type Type of the sensor, must contain at least one letter
     * @param location Custom location of the sensor
     * @param local Indicates if the created sensor is hosted by this node or a remote node
     * @param address If the sensor is managed by a remote node this contains the {@code DrasylAddress} of the node that manages the sensor.
     */
    protected Sensor(String name, String type, String location, boolean local, DrasylAddress address) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.local = local;
        this.address = address != null ? address.getSerializedAddress() : "";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Sensor) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.location, that.location) &&
                Objects.equals(this.address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, location, address);
    }

    @Override
    public String toString() {
        return "SensorInfo {" +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "location=" + location + ", " +
                "local=" + local + ", " +
                "address=" + address + '}';
    }
}
