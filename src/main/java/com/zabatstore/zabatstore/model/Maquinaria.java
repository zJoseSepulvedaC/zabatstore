package com.zabatstore.zabatstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Maquinaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String ubicacion;
    private LocalDate disponibleDesde;
    private BigDecimal precioDiario;

    // Campos de detalle (para vista privada)
    private String marca;
    private Integer anio;
    private String capacidad;
    private String mantenciones;
    private String condiciones;
    private String mediosPago;

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public LocalDate getDisponibleDesde() { return disponibleDesde; }
    public void setDisponibleDesde(LocalDate disponibleDesde) { this.disponibleDesde = disponibleDesde; }

    public BigDecimal getPrecioDiario() { return precioDiario; }
    public void setPrecioDiario(BigDecimal precioDiario) { this.precioDiario = precioDiario; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getCapacidad() { return capacidad; }
    public void setCapacidad(String capacidad) { this.capacidad = capacidad; }

    public String getMantenciones() { return mantenciones; }
    public void setMantenciones(String mantenciones) { this.mantenciones = mantenciones; }

    public String getCondiciones() { return condiciones; }
    public void setCondiciones(String condiciones) { this.condiciones = condiciones; }

    public String getMediosPago() { return mediosPago; }
    public void setMediosPago(String mediosPago) { this.mediosPago = mediosPago; }
}
