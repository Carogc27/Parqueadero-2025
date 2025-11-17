package Controlador;
import java.time.LocalDateTime;

import Modelo.Vehiculo;

public class Ubicacion {
    // 🔠 Atributos
    private int numero;
    private boolean disponible;
    private Vehiculo vehiculoParqueado;
    private LocalDateTime horaIngreso;

    // 🔨 Constructor
    public Ubicacion(int numero) {
        this.numero = numero;
        this.disponible = true; // Inicialmente, toda ubicación está disponible [cite: 9]
        this.vehiculoParqueado = null;
        this.horaIngreso = null;
    }

    // 🗣️ Getters (Métodos de Acceso)
    public int getNumero() {
        return numero;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public Vehiculo getVehiculoParqueado() {
        return vehiculoParqueado;
    }

    public LocalDateTime getHoraIngreso() {
        return horaIngreso;
    }

    // ✍️ Setters (Métodos de Modificación)

    // Método para ocupar la ubicación al ingresar un vehículo [cite: 10]
    public void ocupar(Vehiculo vehiculo) {
        this.vehiculoParqueado = vehiculo;
        this.disponible = false;
        this.horaIngreso = LocalDateTime.now();
    }

    // Método para liberar la ubicación al retirar un vehículo [cite: 12]
    public void liberar() {
        this.vehiculoParqueado = null;
        this.disponible = true;
        this.horaIngreso = null;
    }

    @Override
    public String toString() {
        return "Ubicacion #" + numero + (disponible ? " (DISPONIBLE)" : " (OCUPADA)");
    }
}