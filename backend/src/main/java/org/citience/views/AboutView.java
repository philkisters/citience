package org.citience.views;

import org.citience.network.NetworkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/about")
public class AboutView {

    private final NetworkService networkService;

    public AboutView(final NetworkService networkService) {
        this.networkService = networkService;
    }


    @GetMapping
    public ModelAndView getAboutData () {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("about");
        mv.getModel().put("view", "about");
        mv.getModel().put("networkStatus", networkService.getStatus().name());

        return mv;
    }
}
