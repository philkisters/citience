package org.citience.location;

import org.citience.data.ConfigurationRepository;
import org.citience.models.Configuration;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    private static final String CITY = "city";
    private static final String DISTRICT = "district";
    private static final String BOROUGH = "borough";
    private static final String STREET = "street";
    private static final String NUMBER = "number";
    private static final String POSTAL_CODE = "postalCode";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private final ConfigurationRepository configurationRepository;

    private Configuration locationConfig;
    private final GPSLocation gpsLocation;
    private final AddressLocation addressLocation;

    public LocationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;

        configurationRepository.findFirstByModule("location").ifPresentOrElse(this::setConfiguration, () -> {
            Configuration newConfig = new Configuration("location");
            newConfig.addParameter(CITY, "Example City");
            newConfig.addParameter(DISTRICT, "Example District");
            newConfig.addParameter(BOROUGH, "Example Borough");
            newConfig.addParameter(STREET, "Example Street");
            newConfig.addParameter(NUMBER, "1a");
            newConfig.addParameter(POSTAL_CODE, "01523");
            newConfig.addParameter(LATITUDE, "45.123546");
            newConfig.addParameter(LONGITUDE, "15.123546");
            locationConfig = configurationRepository.save(newConfig);
        });

        gpsLocation = new GPSLocation(Double.parseDouble(locationConfig.getParameter(LATITUDE)), Double.parseDouble(locationConfig.getParameter(LONGITUDE)));
        addressLocation = new AddressLocation(locationConfig.getParameter(CITY),
                locationConfig.getParameter(DISTRICT),
                locationConfig.getParameter(BOROUGH),
                locationConfig.getParameter(STREET),
                locationConfig.getParameter(NUMBER),
                locationConfig.getParameter(POSTAL_CODE));


    }

    private void setConfiguration(Configuration configuration) {
        this.locationConfig = configuration;
    }

    public GPSLocation getGpsLocation() {
        return gpsLocation;
    }

    public AddressLocation getAddressLocation() {
        return addressLocation;
    }

    public GPSLocation updateGpsLocation(double lat, double lon) {

        if (GPSLocation.isValidLocation(lat, lon)) {
            this.gpsLocation.setLatitude(lat);
            this.gpsLocation.setLongitude(lon);
            this.locationConfig.addParameter(LATITUDE, String.valueOf(lat));
            this.locationConfig.addParameter(LONGITUDE, String.valueOf(lon));
            locationConfig = configurationRepository.save(locationConfig);
        }
        return this.gpsLocation;
    }

    public AddressLocation updateAddressLocation(String city, String district, String borough, String street, String number, String postalCode) {

        this.addressLocation.setCity(city);
        this.addressLocation.setDistrict(district);
        this.addressLocation.setBorough(borough);
        this.addressLocation.setStreet(street);
        this.addressLocation.setNumber(number);
        this.addressLocation.setPostalCode(postalCode);
        locationConfig.addParameter(CITY, city);
        locationConfig.addParameter(DISTRICT, district);
        locationConfig.addParameter(BOROUGH, borough);
        locationConfig.addParameter(STREET, street);
        locationConfig.addParameter(NUMBER, number);
        locationConfig.addParameter(POSTAL_CODE, postalCode);
        locationConfig = configurationRepository.save(locationConfig);

        return this.addressLocation;
    }
}
