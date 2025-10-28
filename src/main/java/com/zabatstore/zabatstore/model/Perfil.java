package com.zabatstore.zabatstore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "perfiles") // 👈 nombre explícito de tabla, evita choques en JPA
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 👈 Lazy evita cargar usuario completo si no se necesita
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    @Column(length = 255)
    private String direccion = "";

    @Column(length = 30)
    private String telefono = "";

    @Column(length = 255)
    private String cultivos = "";

    // === Getters & Setters ===
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = (direccion != null) ? direccion : "";
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = (telefono != null) ? telefono : "";
    }

    public String getCultivos() {
        return cultivos;
    }
    public void setCultivos(String cultivos) {
        this.cultivos = (cultivos != null) ? cultivos : "";
    }
}
