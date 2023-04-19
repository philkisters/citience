package org.citience.views;

import org.citience.network.NetworkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
        modelAndView.getModel().put("networkStatus", networkService.getStatus());
        modelAndView.getModel().put("referenceAddress", networkService.getReferenceAddress() == null ? null : networkService.getReferenceAddress().getSerializedAddress());
        modelAndView.getModel().put("nodeAddress", networkService.getAddress() == null ? null : networkService.getAddress().getSerializedAddress());

        return modelAndView;
    }

    @PostMapping("/start")
    public RedirectView startNetwork() {
        networkService.start();
        return new RedirectView("/network");
    }
}
