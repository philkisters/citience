package org.citience.views;

import org.citience.network.NetworkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/data")
public class DataView {

    @GetMapping
    public ModelAndView getDataData () {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("data");
        mv.getModel().put("view", "data");

        return mv;
    }
}
