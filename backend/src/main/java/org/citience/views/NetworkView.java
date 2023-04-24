package org.citience.views;

import org.citience.network.NetworkService;
import org.citience.network.NetworkStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/network")
public class NetworkView {

    private final NetworkService networkService;
    private final ModelAndView modelAndView;

    public NetworkView(final NetworkService networkService) {
        this.networkService = networkService;
        this.modelAndView = new ModelAndView();
        this.modelAndView.getModel().put("view", "network");
    }

    private void updateModel() {
        this.modelAndView.getModel().put("nodeName", networkService.getNodeName());
        this.modelAndView.getModel().put("networkStatus", networkService.getStatus());
        this.modelAndView.getModel().put("referenceAddress", networkService.getReferenceAddress() == null ? null : networkService.getReferenceAddress().getSerializedAddress());
        this.modelAndView.getModel().put("nodeAddress", networkService.getAddress() == null ? null : networkService.getAddress().getSerializedAddress());
        this.modelAndView.getModel().put("notOnline", (networkService.getStatus() != NetworkStatus.ONLINE && networkService.getStatus() != NetworkStatus.CONNECTING));
    }

    @GetMapping
    public ModelAndView getNetworkData () {
        modelAndView.setViewName("network/index");
        modelAndView.getModel().put("referenceAddressError", "");
        this.updateModel();
        return modelAndView;
    }

    @PostMapping("/start")
    public RedirectView startNetwork() {
        networkService.start();
        return new RedirectView("/network");
    }

    @GetMapping("/settings")
    public ModelAndView getSettingsData() {
        modelAndView.setViewName("network/settings");
        this.updateModel();
        return modelAndView;
    }

    @PostMapping("/updateInfo")
    public RedirectView updateInfo(@RequestParam String nodeName, @RequestParam String referenceAddress) {
        networkService.setNodeName(nodeName);

        if (!referenceAddress.isBlank()) {
            try {
                networkService.setReferenceAddress(referenceAddress);
            } catch (IllegalArgumentException e) {
                modelAndView.getModel().put("referenceAddressError", e.getMessage());
                return new RedirectView("/network/settings");
            }
        }

        return new RedirectView("/network");
    }
}
