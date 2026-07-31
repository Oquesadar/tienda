/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;


/**
 *
 * @author Osvaldo
 */
@Controller

public class ErrorController {



    @GetMapping("/403")
    public String e403(Model model) {
        return "/acceso_denegado";

    }
    
    @GetMapping("/error")
    public String errorG(Model model) {
        return "/acceso_denegado";

    }
    
    @PostMapping("/error")
    public String errorP(Model model) {
        return "/acceso_denegado";

    }

}
