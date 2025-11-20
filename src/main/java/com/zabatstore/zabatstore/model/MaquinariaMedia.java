package com.zabatstore.zabatstore.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "maquinaria_media")
public class MaquinariaMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Maquinaria maquinaria;

    @Column(nullable = false)
    private String tipo; // "FOTO" o "VIDEO"

    @Column(nullable = false)
    private String url;  // Para simplificar: usamos URL en vez de archivo físico

    private LocalDateTime fechaSubida = LocalDateTime.now();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario autor;

    public MaquinariaMedia() {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }
}
