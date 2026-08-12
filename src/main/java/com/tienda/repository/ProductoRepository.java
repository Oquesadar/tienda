/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tienda.repository;

import com.tienda.domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Osvaldo
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{
    public List<Producto> findByActivoTrue();
    
    //Consulta derivada que recupera los productos dentro de un rango de precio 
    // y los ordena por el precio del producto ascendentemente
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(BigDecimal precioInf, BigDecimal precioSup);
    
    
    //Consulta jpql que recupera los productos dentro de un rango de precio 
    // y los ordena por el precio del producto ascendentemente
    @Query(value="SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(BigDecimal precioInf, BigDecimal precioSup);
    
    
    //Consulta sql que recupera los productos dentro de un rango de precio 
    // y los ordena por el precio del producto ascendentemente
    @Query(nativeQuery=true,
            value="SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaSQL(BigDecimal precioInf, BigDecimal precioSup);
    
    
    //Consulta derivada que recupera los productos dentro de un rango de precio
    // pero con existencias mayores a cero
    public List<Producto> findByPrecioBetweenAndExistenciasGreaterThanOrderByPrecioAsc(
        BigDecimal precioInf, BigDecimal precioSup, Integer existencias);
    
    
}
