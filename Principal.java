package Vista;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Controlador.Ubicacion;
import Modelo.Vehiculo;

public class Principal {
    // 🔠 Atributos
    private List<Ubicacion> ubicaciones;
    private double valorHora;
    private double saldoCajaDiaria;
    private boolean parqueaderoAbierto;
    private final int CAPACIDAD = 20; // Capacidad fija para el ejemplo

    // 🔨 Constructor
    public Principal() {
        this.ubicaciones = new ArrayList<>();
        this.valorHora = 0.0;
        this.saldoCajaDiaria = 0.0;
        this.parqueaderoAbierto = false;
        // Inicializa las ubicaciones (sin ocupar)
        for (int i = 1; i <= CAPACIDAD; i++) {
            this.ubicaciones.add(new Ubicacion(i));
        }
    }

    // 🔑 Métodos de Gestión

    // 1. Abrir Parqueadero 
    public void abrirParqueadero(Scanner scanner) {
        if (parqueaderoAbierto) {
            System.out.println("⚠️ El parqueadero ya está abierto. Cierre la caja antes de reabrir.");
            return;
        }

        System.out.print("💸 Establecer el valor por hora para hoy: ");
        if (scanner.hasNextDouble()) {
            this.valorHora = scanner.nextDouble();
            scanner.nextLine(); // Limpiar el buffer
            
            // Garantizar que todas las ubicaciones están disponibles
            for (Ubicacion u : ubicaciones) {
                if (!u.isDisponible()) {
                    u.liberar();
                }
            }
            
            this.parqueaderoAbierto = true;
            this.saldoCajaDiaria = 0.0; // Iniciar la caja en 0.0
            System.out.println("\n✅ Parqueadero abierto con éxito!");
            System.out.println("Valor/Hora establecido: $" + valorHora);
            System.out.println("Todas las " + CAPACIDAD + " ubicaciones están disponibles.");
        } else {
            System.out.println("❌ Entrada inválida. Por favor, ingrese un número para el valor hora.");
            scanner.nextLine(); // Limpiar el buffer si la entrada es errónea
        }
    }

    // 2. Ingresar Vehículo 
    public void ingresarVehiculo(Scanner scanner) {
        if (!parqueaderoAbierto) {
            System.out.println("❌ Primero debe abrir el parqueadero (Opción 1).");
            return;
        }

        Ubicacion ubicacionDisponible = null;
        for (Ubicacion u : ubicaciones) {
            if (u.isDisponible()) {
                ubicacionDisponible = u;
                break;
            }
        }

        if (ubicacionDisponible != null) {
            System.out.print("🚗 Ingrese la placa del vehículo: ");
            String placa = scanner.nextLine().toUpperCase();
            Vehiculo nuevoVehiculo = new Vehiculo(placa);
            
            ubicacionDisponible.ocupar(nuevoVehiculo); // Cambia el estado a no disponible 
            System.out.println("\n✅ Vehículo con placa " + placa + " ingresado.");
            System.out.println("Asignado a la " + ubicacionDisponible.toString());
        } else {
            System.out.println("\n🛑 Parqueadero lleno. No hay ubicaciones disponibles.");
        }
    }

    // 3. Consultar Ubicaciones Disponibles 
    public void consultarUbicacionesDisponibles() {
        List<Ubicacion> disponibles = new ArrayList<>();
        for (Ubicacion u : ubicaciones) {
            if (u.isDisponible()) {
                disponibles.add(u);
            }
        }

        System.out.println("\n🔎 Ubicaciones Disponibles:");
        System.out.println("Total disponibles: " + disponibles.size());
        
        if (disponibles.isEmpty()) {
            System.out.println("  * No hay ubicaciones disponibles.");
        } else {
            System.out.print("  * Números disponibles: ");
            for (int i = 0; i < disponibles.size(); i++) {
                System.out.print(disponibles.get(i).getNumero());
                if (i < disponibles.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
    }

    // 4. Consultar Vehículos y Ocupación 
    public void consultarOcupacion() {
        System.out.println("\n📊 Estado de Ocupación del Parqueadero:");
        boolean ocupadoEncontrado = false;
        for (Ubicacion u : ubicaciones) {
            if (!u.isDisponible()) {
                ocupadoEncontrado = true;
                
                // Calcular tiempo y cobro [cite: 11, 14]
                double cobro = calcularCobro(u.getHoraIngreso());
                Duration duracion = calcularTiempo(u.getHoraIngreso());
                
                System.out.println("--- Ubicación #" + u.getNumero() + " ---");
                System.out.println("  * Ocupado por: " + u.getVehiculoParqueado().getPlaca());
                System.out.println("  * Hora de Ingreso: " + u.getHoraIngreso().toLocalTime().toString());
                System.out.printf("  * Tiempo Parqueado: %d horas y %d minutos\n", duracion.toHours(), duracion.toMinutes() % 60);
                System.out.printf("  * Valor a Cobrar (Estimado): $%.2f\n", cobro);
            }
        }

        if (!ocupadoEncontrado) {
            System.out.println("  * El parqueadero está totalmente desocupado.");
        }
    }

    // 5. Retirar Vehículo 
    public void retirarVehiculo(Scanner scanner) {
        if (!parqueaderoAbierto) {
            System.out.println("❌ Primero debe abrir el parqueadero (Opción 1).");
            return;
        }
        
        System.out.print("🔢 Ingrese el número de ubicación a liberar: ");
        if (!scanner.hasNextInt()) {
            System.out.println("❌ Entrada inválida. Por favor, ingrese un número.");
            scanner.nextLine();
            return;
        }
        int numUbicacion = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        if (numUbicacion < 1 || numUbicacion > CAPACIDAD) {
            System.out.println("❌ Ubicación inválida.");
            return;
        }
        
        Ubicacion ubicacion = ubicaciones.get(numUbicacion - 1);

        if (ubicacion.isDisponible()) {
            System.out.println("❌ La Ubicación #" + numUbicacion + " ya está disponible. No hay vehículo para retirar.");
        } else {
            // Calcular cobro final 
            double cobro = calcularCobro(ubicacion.getHoraIngreso());
            
            // Retirar y afectar la caja 
            ubicacion.liberar(); // Pasa a estado disponible 
            saldoCajaDiaria += cobro; // Afecta el saldo en la caja 
            
            System.out.println("\n✅ Vehículo retirado de la Ubicación #" + numUbicacion);
            System.out.printf("💸 Cobro total: $%.2f\n", cobro);
            System.out.printf("💰 Saldo actual de la caja: $%.2f\n", saldoCajaDiaria);
        }
    }

    // 6. Cierre de Caja Diario 
    public void cierreDeCaja() {
        System.out.println("\n🛑 Realizando Cierre de Caja Diario...");
        System.out.printf("💶 Ingreso Total del Día: $%.2f\n", saldoCajaDiaria);
        
        // El proceso de cierre incluye dejar la caja en cero y el parqueadero en estado de "cerrado"
        this.saldoCajaDiaria = 0.0;
        this.parqueaderoAbierto = false;
        
        System.out.println("✅ Cierre completado. La caja ha sido reiniciada a $0.0.");
        System.out.println("El parqueadero está ahora cerrado hasta que se vuelva a abrir con la opción 1.");
    }

    // 🛠️ Métodos Auxiliares para Cálculo (Parte del Requerimiento 14)

    /**
     * Calcula el tiempo transcurrido desde la hora de ingreso hasta el momento actual.
     * @param horaIngreso La hora de ingreso del vehículo.
     * @return Duración (tiempo) parqueado.
     */
    private Duration calcularTiempo(LocalDateTime horaIngreso) {
        return Duration.between(horaIngreso, LocalDateTime.now()); // Tiempo actual
    }

    /**
     * Calcula el cobro basado en el tiempo parqueado, redondeando al entero de hora superior.
     * @param horaIngreso La hora de ingreso del vehículo.
     * @return El valor total a cobrar.
     */
    private double calcularCobro(LocalDateTime horaIngreso) {
        Duration duracion = calcularTiempo(horaIngreso);
        long minutosParqueados = duracion.toMinutes();

        // Calcular las horas, redondeando hacia arriba.
        // Ejemplo: 1 hora y 1 minuto se cobra como 2 horas.
        long horasCobradas = (minutosParqueados + 59) / 60; 

        if (horasCobradas < 1) {
            horasCobradas = 1; // Se cobra un mínimo de 1 hora
        }

        return horasCobradas * valorHora;
    }


    // 💻 Método Principal y Menú (Parte del Requerimiento 8)
    public static void main(String[] args) {
        Principal miParqueadero = new Principal();
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n==================================");
            System.out.println("    🅿️  SISTEMA DE PARQUEADERO");
            System.out.println("==================================");
            System.out.println("1. Abrir Parqueadero y Fijar Valor/Hora");
            System.out.println("2. Ingresar Vehículo");
            System.out.println("3. Consultar Ubicaciones Disponibles");
            System.out.println("4. Consultar Estado de Ocupación (Vehículos)");
            System.out.println("5. Retirar Vehículo y Cobrar");
            System.out.println("6. Realizar Cierre de Caja Diario");
            System.out.println("0. Salir del Programa");
            System.out.println("----------------------------------");
            System.out.print("👉 Seleccione una opción: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer
                
                switch (opcion) {
                    case 1:
                        miParqueadero.abrirParqueadero(scanner); // Se ejecuta antes de iniciar la operación diaria [cite: 9]
                        break;
                    case 2:
                        miParqueadero.ingresarVehiculo(scanner);
                        break;
                    case 3:
                        miParqueadero.consultarUbicacionesDisponibles();
                        break;
                    case 4:
                        miParqueadero.consultarOcupacion();
                        break;
                    case 5:
                        miParqueadero.retirarVehiculo(scanner);
                        break;
                    case 6:
                        miParqueadero.cierreDeCaja();
                        break;
                    case 0:
                        System.out.println("\n👋 Gracias por usar el sistema. ¡Adiós!");
                        break;
                    default:
                        System.out.println("❌ Opción no válida. Por favor, intente de nuevo.");
                }
            } else {
                System.out.println("❌ Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine(); // Limpiar el buffer si la entrada no es un entero
                opcion = -1;
            }
        }
        scanner.close();
    }
}