package com.arvinsichuan.helloidea;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.json.Json;


@Controller
@RequestMapping("/hello")
public class HelloIdea {

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String helloIdea(){
        return "hello_idea";
    }


}
