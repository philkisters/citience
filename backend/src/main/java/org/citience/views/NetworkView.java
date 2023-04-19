package org.citience.views;

import org.citience.network.NetworkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/network")
public class NetworkView {

    private final NetworkService networkService;
    private final ModelAndView modelAndView;

    public NetworkView(final NetworkService networkService) {
        this.networkService = networkService;
        this.modelAndView = new ModelAndView();
        modelAndView.setViewName("network");
        modelAndView.getModel().put("view", "network");
    }


    @GetMapping
    public ModelAndView getNetworkData () {
        modelAndView.getModel().put("nodeName", networkService.getNodeName());
        modelAndView.getModel().put("networkStatus", networkService.getStatus().name());
        modelAndView.getModel().put("referenceAddress", networkService.getReferenceAddress() == null ? null : networkService.getReferenceAddress().getSerializedAddress());

        return modelAndView;
    }
}
