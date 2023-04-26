package org.citience.views;

import org.citience.location.LocationService;
import org.citience.network.NetworkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/data")
public class DataView {


    private final NetworkService networkService;
    private final LocationService locationService;
    private final ModelAndView modelAndView;

    public DataView(NetworkService networkService, LocationService locationService) {
        this.networkService = networkService;
        this.locationService = locationService;

        modelAndView = new ModelAndView();
        this.modelAndView.getModel().put("view", "data");
    }

    private void updateModel() {
        this.modelAndView.getModel().put("nodeName", networkService.getNodeName());
        this.modelAndView.getModel().put("gpsLocation", locationService.getGpsLocation());
        this.modelAndView.getModel().put("addressLocation", locationService.getAddressLocation());
    }

    @GetMapping
    public ModelAndView getDataData () {
        modelAndView.setViewName("data/index");
        updateModel();
        return modelAndView;
    }

    @GetMapping("/updateInfo")
    public ModelAndView getUpdateInfoView() {
        modelAndView.setViewName("data/infoSettings");
        updateModel();
        return modelAndView;
    }

    @PostMapping("/updateInfo")
    public RedirectView updateLocationInfo(@RequestParam String city, @RequestParam String district, @RequestParam String borough, @RequestParam String postalCode, @RequestParam String street, @RequestParam String number, @RequestParam String latitude, @RequestParam String longitude) {
        locationService.updateAddressLocation(city, district, borough, street, number, postalCode);
        locationService.updateGpsLocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
        return new RedirectView("/data");
    }
}
