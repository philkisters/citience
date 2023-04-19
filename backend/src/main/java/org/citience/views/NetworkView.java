package org.citience.views;

import org.citience.network.NetworkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class NetworkView {

    private final NetworkService networkService;

    public NetworkView(final NetworkService networkService) {
        this.networkService = networkService;
    }


    @GetMapping
    public ModelAndView getHomeData () {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("network");
        mv.getModel().put("view", "network");
        mv.getModel().put("networkStatus", networkService.getStatus().name());

        return mv;
    }
}
