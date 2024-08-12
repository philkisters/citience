package org.citience.views;

import org.citience.network.NetworkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class HomeView {

    private final NetworkService networkService;

    public HomeView(final NetworkService networkService) {
        this.networkService = networkService;
    }


    @GetMapping
    public ModelAndView getHomeData () {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("home");
        mv.getModel().put("view", "home");
        mv.getModel().put("networkStatus", networkService.getStatus().name());
        mv.getModel().put("name", "Phil");

        return mv;
    }
}
