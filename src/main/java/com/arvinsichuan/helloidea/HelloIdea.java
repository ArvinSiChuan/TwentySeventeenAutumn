package com.arvinsichuan.helloidea;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/hello")
public class HelloIdea {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloIdea(Model model) {
        model.addAttribute("Name", "Value");
        return "hello_idea";
    }
}
