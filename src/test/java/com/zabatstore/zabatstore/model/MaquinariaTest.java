package com.zabatstore.zabatstore.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class MaquinariaTest {

    @Test
    public void testMaquinariaGettersAndSetters() {
        // 1. Crear la instancia
        Maquinaria maq = new Maquinaria();

        // 2. Asignar valores (Setters)
        maq.setId(1L);
        maq.setTipo("Tractor");
        maq.setUbicacion("Zona Sur");
        maq.setDisponibleDesde(LocalDate.of(2023, 10, 1));
        maq.setPrecioDiario(new BigDecimal("150000"));
        maq.setMarca("Caterpillar");
        maq.setAnio(2022);
        maq.setCapacidad("5 Toneladas");
        maq.setMantenciones("Al día");
        maq.setCondiciones("Excelentes");
        maq.setMediosPago("Transferencia");

        // 3. Verificar valores (Getters)
        assertEquals(1L, maq.getId());
        assertEquals("Tractor", maq.getTipo());
        assertEquals("Zona Sur", maq.getUbicacion());
        assertEquals(LocalDate.of(2023, 10, 1), maq.getDisponibleDesde());
        assertEquals(new BigDecimal("150000"), maq.getPrecioDiario());
        assertEquals("Caterpillar", maq.getMarca());
        assertEquals(2022, maq.getAnio());
        assertEquals("5 Toneladas", maq.getCapacidad());
        assertEquals("Al día", maq.getMantenciones());
        assertEquals("Excelentes", maq.getCondiciones());
        assertEquals("Transferencia", maq.getMediosPago());
    }
}