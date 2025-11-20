package com.zabatstore.zabatstore.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Maquinaria maquinaria;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario autor;

    @Column(nullable = false, length = 1000)
    private String contenido;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Comentario() {
    }

    public Long getId() {
        return id;
    }

    public Maquinaria getMaquinaria() {
        return maquinaria;
    }

    public void setMaquinaria(Maquinaria maquinaria) {
        this.maquinaria = maquinaria;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
