package org.citience.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class HomeView {
    @GetMapping
    public ModelAndView getHomeData () {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("home");
        mv.getModel().put("name", "Phil");

        return mv;
    }
}
