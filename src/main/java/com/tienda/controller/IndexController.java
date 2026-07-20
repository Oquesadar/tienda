/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda.controller;

import com.tienda.domain.Producto;
import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 *
 * @author Osvaldo
 */
@Controller
public class IndexController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public IndexController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    

    @GetMapping("/")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalProductos", productos.size());
        return "/index";

    }
    @GetMapping("/consultas/{idCategoria}")
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
}

