/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda.controller;

import com.tienda.domain.Producto;
import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author Osvaldo
 */
@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ConsultaController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/{idCategoria}")
    public String listado(@PathVariable("idCategoria") Integer idCategoria, Model model) {
        var categoriaOpt = categoriaService.getCategoria(idCategoria);
        List<Producto> productos = new ArrayList<Producto>();
        if (categoriaOpt.isPresent()){
            productos = categoriaOpt.get().getProductos();
        }
        model.addAttribute("productos", productos);
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "/index";
    }
    
    
     @GetMapping("/listado")
    public String listado(Model model) {
        var productos= productoService.getProductos(false);
        model.addAttribute("productos", productos);
        return "/consultas/listado";
    }
    
    @GetMapping("/consultaDerivada")
    public String consultaDerivada(@RequestParam() BigDecimal precioInf, 
            @RequestParam() BigDecimal precioSup, Model model) {
        var productos= productoService.consultaDerivada(precioInf, precioSup);
        model.addAttribute("productos", productos);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    
    @GetMapping("/consultaJPQL")
    public String consultaJPQL(@RequestParam() BigDecimal precioInf, 
            @RequestParam() BigDecimal precioSup, Model model) {
        var productos= productoService.consultaJPQL(precioInf, precioSup);
        model.addAttribute("productos", productos);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    
    @GetMapping("/consultaSQL")
    public String consultaSQL(@RequestParam() BigDecimal precioInf, 
            @RequestParam() BigDecimal precioSup, Model model) {
        var productos= productoService.consultaSQL(precioInf, precioSup);
        model.addAttribute("productos", productos);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    
}

