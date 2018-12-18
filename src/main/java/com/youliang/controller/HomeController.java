package com.youliang.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping({"/"})
public class HomeController {
    public HomeController() {
    }

    @GetMapping
    public String index() {
        return "it works!";
    }
}
