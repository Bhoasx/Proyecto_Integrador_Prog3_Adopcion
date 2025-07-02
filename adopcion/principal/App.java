package principal;

import modelo.*;
import servicio.GestorArchivos;
import servicio.SistemaGestionAdopcion;

import java.util.*;

public class App {
    private static SistemaGestionAdopcion sistema = new SistemaGestionAdopcion();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            if (sistema.getUsuarioLogueado() == null) {
                mostrarMenuLogin();
            } else {
                mostrarMenuPrincipal();
            }
        }
    }

    private static void mostrarMenuLogin() {
        System.out.println("\n🐾🐾🐾 BIENVENIDO AL SISTEMA DE ADOPCIÓN DE MASCOTAS 🐾🐾🐾");
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Registrarse como Adoptante");
        System.out.println("0. Salir del Sistema");
        System.out.print("Seleccione una opción: ");
        try {
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    iniciarSesion();
                    break;
                case 2:
                    registrarAdoptante();
                    break;
                case 0:
                    System.out.println("Gracias por usar el sistema. ¡Hasta pronto!");
                    sistema.guardarDatos();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            scanner.nextLine();
        }
    }

    private static void iniciarSesion() {
        System.out.print("Nombre de usuario: ");
        String user = scanner.nextLine();
        System.out.print("Contraseña: ");
        String pass = scanner.nextLine();

        Usuario usuario = sistema.iniciarSesion(user, pass);
        if (usuario != null) {
            System.out.println("¡Bienvenido " + usuario.getNombreCompleto() + "!");
        } else {
            System.out.println("Credenciales incorrectas o usuario no encontrado.");
        }
    }

    private static void registrarAdoptante() {
        System.out.println("\n--- Registro de Nuevo Adoptante ---");
        System.out.print("Nombre de usuario: "); String nombreUsuario = scanner.nextLine();
        System.out.print("Contraseña: "); String contrasena = scanner.nextLine();
        System.out.print("Nombre completo: "); String nombreCompleto = scanner.nextLine();
        System.out.print("Dirección: "); String direccion = scanner.nextLine();
        System.out.print("Teléfono: "); String telefono = scanner.nextLine();
        System.out.print("Número de mascotas actuales: "); int numMascotas = pedirEntero();
        System.out.print("Tipo de vivienda (Ej: Casa con patio, Apartamento): "); String tipoVivienda = scanner.nextLine();

        if (sistema.registrarUsuario(nombreUsuario, contrasena, nombreCompleto, direccion, telefono, numMascotas, tipoVivienda, RolUsuario.ADOPTANTE)) {
            System.out.println("Registro exitoso. Ahora puede iniciar sesión.");
        } else {
            System.out.println("No se pudo completar el registro.");
        }
    }


    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- MENÚ PRINCIPAL --- Usuario: " + sistema.getUsuarioLogueado().getNombreUsuario() + " (" + sistema.getUsuarioLogueado().getRol() + ")");
        RolUsuario rol = sistema.getUsuarioLogueado().getRol();

        if (rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.VOLUNTARIO) {
            System.out.println("--- Gestión Fundación ---");
            System.out.println("1. Registrar Mascota Rescatada");
            System.out.println("2. Ver Solicitudes de Adopción Pendientes");
            System.out.println("3. Aprobar/Rechazar Solicitud");
            System.out.println("4. Registrar Visita de Seguimiento");
            System.out.println("5. Ver todas las Mascotas (Adoptadas y No Adoptadas)");
        }
        if (rol == RolUsuario.ADMINISTRADOR) {
            System.out.println("6. Registrar Nuevo Usuario (Voluntario/Admin)");
        }

        System.out.println("--- Adopciones (Adoptante) ---");
        System.out.println("10. Ver Mascotas Disponibles para Adopción");
        System.out.println("11. Solicitar Adopción de Mascota");
        System.out.println("12. Ver Mis Solicitudes de Adopción");


        System.out.println("--- Reportes ---");
        System.out.println("20. Generar Reporte por Mascota");
        System.out.println("21. Generar Reporte por Adoptante");

        System.out.println("--- Sistema ---");
        System.out.println("99. Cerrar Sesión");
        System.out.println("0. Salir del Sistema");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1: if (rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.VOLUNTARIO) registrarMascota(); else accesoDenegado(); break;
                case 2: if (rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.VOLUNTARIO) verSolicitudesPendientes(); else accesoDenegado(); break;
                case 3: if (rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.VOLUNTARIO) procesarSolicitudAdopcion(); else accesoDenegado(); break;
                case 4: if (rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.VOLUNTARIO) registrarVisita(); else accesoDenegado(); break;
                case 5: if (rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.VOLUNTARIO) verTodasLasMascotas(); else accesoDenegado(); break;
                case 6: if (rol == RolUsuario.ADMINISTRADOR) registrarUsuarioFundacion(); else accesoDenegado(); break;

                case 10: verMascotasDisponibles(); break;
                case 11: if (rol == RolUsuario.ADOPTANTE) solicitarAdopcion(); else accesoDenegadoAdoptante(); break;
                case 12: if (rol == RolUsuario.ADOPTANTE) verMisSolicitudes(); else accesoDenegadoAdoptante(); break;

                case 20: generarReporteMascota(); break;
                case 21: generarReporteAdoptante(); break;

                case 99: sistema.cerrarSesion(); break;
                case 0:
                    System.out.println("Guardando datos y saliendo... ¡Hasta pronto!");
                    sistema.guardarDatos();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
            scanner.nextLine();
        }
    }

    private static void accesoDenegado() {
        System.out.println("Acceso denegado. Esta función es solo para personal de la fundación (Administradores/Voluntarios).");
    }
    private static void accesoDenegadoAdoptante() {
        System.out.println("Acceso denegado. Esta función es solo para Adoptantes.");
    }


    private static void registrarMascota() {
        System.out.println("\n--- Registrar Nueva Mascota Rescatada ---");
        System.out.print("Nombre: "); String nombre = scanner.nextLine();
        System.out.print("Especie (Ej: Perro, Gato): "); String especie = scanner.nextLine();
        System.out.print("Raza: "); String raza = scanner.nextLine();
        System.out.print("Edad (años): "); int edad = pedirEntero();
        System.out.print("Estado de salud: "); String estadoSalud = scanner.nextLine();
        System.out.print("¿Esterilizado? (true/false): "); boolean esterilizado = pedirBooleano();

        sistema.registrarMascota(nombre, especie, raza, edad, estadoSalud, esterilizado);
    }

    private static void verTodasLasMascotas() {
        System.out.println("\n--- Todas las Mascotas Registradas ---");
        List<Mascota> mascotas = sistema.getTodasLasMascotas();
        if (mascotas.isEmpty()) {
            System.out.println("No hay mascotas registradas en el sistema.");
            return;
        }
        for (Mascota m : mascotas) {
            System.out.println("--------------------");
            System.out.println(m);
        }
        System.out.println("--------------------");
    }


    private static void verSolicitudesPendientes() {
        System.out.println("\n--- Solicitudes de Adopción Pendientes ---");
        List<SolicitudAdopcion> pendientes = sistema.getSolicitudesPendientes();
        if (pendientes.isEmpty()) {
            System.out.println("No hay solicitudes pendientes.");
            return;
        }
        for (SolicitudAdopcion s : pendientes) {
            System.out.println("--------------------");
            System.out.println(s);
            sistema.buscarMascotaPorId(s.getIdMascota()).ifPresent(m -> System.out.println("  Mascota: " + m.getNombre()));
            sistema.buscarUsuarioPorId(s.getIdUsuarioAdoptante()).ifPresent(u -> System.out.println("  Adoptante: " + u.getNombreCompleto()));
        }
        System.out.println("--------------------");
    }

    private static void procesarSolicitudAdopcion() {
        System.out.println("\n--- Aprobar/Rechazar Solicitud de Adopción ---");
        verSolicitudesPendientes();
        List<SolicitudAdopcion> pendientes = sistema.getSolicitudesPendientes();
        if (pendientes.isEmpty()) return;

        System.out.print("Ingrese el ID de la solicitud a procesar: ");
        int idSolicitud = pedirEntero();

        Optional<SolicitudAdopcion> solicitudOpt = pendientes.stream().filter(s -> s.getId() == idSolicitud).findFirst();
        if(solicitudOpt.isEmpty()){
            System.out.println("ID de solicitud no válido o no está pendiente.");
            return;
        }

        System.out.print("¿Aprobar la solicitud? (true/false): ");
        boolean aprobar = pedirBooleano();

        sistema.procesarSolicitud(idSolicitud, aprobar);
    }

    private static void registrarVisita() {
        System.out.println("\n--- Registrar Visita de Seguimiento ---");
        System.out.println("Adopciones Aprobadas (donde se puede registrar seguimiento):");
        List<SolicitudAdopcion> aprobadas = sistema.getAdopcionesAprobadasParaSeguimiento();
        if (aprobadas.isEmpty()) {
            System.out.println("No hay adopciones aprobadas para registrar seguimiento.");
            return;
        }
        for (SolicitudAdopcion s : aprobadas) {
            System.out.println("--------------------");
            System.out.print("Solicitud ID: " + s.getId());
            sistema.buscarMascotaPorId(s.getIdMascota()).ifPresent(m -> System.out.print(", Mascota: " + m.getNombre()));
            sistema.buscarUsuarioPorId(s.getIdUsuarioAdoptante()).ifPresent(u -> System.out.print(", Adoptante: " + u.getNombreCompleto()));
            System.out.println();
        }
        System.out.println("--------------------");

        System.out.print("Ingrese el ID de la Solicitud de Adopción Aprobada para la visita: ");
        int idSolicitud = pedirEntero();

        Optional<SolicitudAdopcion> solicitudOpt = aprobadas.stream().filter(s -> s.getId() == idSolicitud).findFirst();
        if(solicitudOpt.isEmpty()){
            System.out.println("ID de solicitud no válido o no corresponde a una adopción aprobada.");
            return;
        }

        System.out.print("Reporte de bienestar de la mascota: ");
        String reporte = scanner.nextLine();

        sistema.registrarVisitaSeguimiento(idSolicitud, reporte);
    }

    private static void registrarUsuarioFundacion() {
        System.out.println("\n--- Registrar Nuevo Usuario de Fundación ---");
        System.out.print("Nombre de usuario: "); String nombreUsuario = scanner.nextLine();
        System.out.print("Contraseña: "); String contrasena = scanner.nextLine();
        System.out.print("Nombre completo: "); String nombreCompleto = scanner.nextLine();
        System.out.print("Dirección (de la fundación/oficina): "); String direccion = scanner.nextLine();
        System.out.print("Teléfono de contacto: "); String telefono = scanner.nextLine();

        RolUsuario rolUsuario;
        while (true) {
            System.out.print("Rol (VOLUNTARIO/ADMINISTRADOR): ");
            String rolStr = scanner.nextLine().toUpperCase();
            try {
                rolUsuario = RolUsuario.valueOf(rolStr);
                if (rolUsuario == RolUsuario.ADOPTANTE) {
                    System.out.println("Para registrar adoptantes, use la opción del menú de login.");
                    continue;
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Rol inválido. Intente de nuevo.");
            }
        }

        if(sistema.registrarUsuario(nombreUsuario, contrasena, nombreCompleto, direccion, telefono, 0, "N/A", rolUsuario)) {
            System.out.println("Usuario de fundación registrado exitosamente.");
        } else {
            System.out.println("No se pudo registrar el usuario de fundación.");
        }
    }


    private static void verMascotasDisponibles() {
        System.out.println("\n--- Mascotas Disponibles para Adopción ---");
        List<Mascota> disponibles = sistema.getMascotasDisponibles();
        if (disponibles.isEmpty()) {
            System.out.println("¡Lo sentimos! No hay mascotas disponibles para adopción en este momento.");
            return;
        }
        for (Mascota m : disponibles) {
            System.out.println("--------------------");
            System.out.println(m);
        }
        System.out.println("--------------------");
    }

    private static void solicitarAdopcion() {
        System.out.println("\n--- Solicitar Adopción de Mascota ---");
        verMascotasDisponibles();
        List<Mascota> disponibles = sistema.getMascotasDisponibles();
        if (disponibles.isEmpty()) return;

        System.out.print("Ingrese el ID de la mascota que desea adoptar: ");
        int idMascota = pedirEntero();

        sistema.solicitarAdopcion(idMascota);
    }

    private static void verMisSolicitudes() {
        Usuario currentUser = sistema.getUsuarioLogueado();
        if (currentUser == null || currentUser.getRol() != RolUsuario.ADOPTANTE) {
            System.out.println("Debe estar logueado como adoptante para ver sus solicitudes.");
            return;
        }
        System.out.println("\n--- Mis Solicitudes de Adopción ---");
        boolean encontradas = false;
        for(SolicitudAdopcion s : sistema.getSolicitudesPendientes()){
            if(s.getIdUsuarioAdoptante() == currentUser.getId()){
                System.out.println("--------------------");
                System.out.println(s);
                sistema.buscarMascotaPorId(s.getIdMascota()).ifPresent(m -> System.out.println("  Mascota: " + m.getNombre()));
                encontradas = true;
            }
        }

        List<SolicitudAdopcion> todasLasSolicitudes = sistema.getAdopcionesAprobadasParaSeguimiento(); // Esto trae solo aprobadas



        List<SolicitudAdopcion> solicitudesDelUsuario = new ArrayList<>();
        List<SolicitudAdopcion> todasSol = new ArrayList<>();
        GestorArchivos.cargarDatos(new ArrayList<>(), new ArrayList<>(), todasSol, new ArrayList<>());

        for (SolicitudAdopcion s : todasSol) {
            if (s.getIdUsuarioAdoptante() == currentUser.getId() && s.getEstado() != EstadoSolicitud.PENDIENTE) {
                System.out.println("--------------------");
                System.out.println(s);
                sistema.buscarMascotaPorId(s.getIdMascota()).ifPresent(m -> System.out.println("  Mascota: " + m.getNombre()));
                encontradas = true;
            }
        }


        if (!encontradas) {
            System.out.println("No tienes solicitudes de adopción registradas.");
        }
        System.out.println("--------------------");
    }


    private static void generarReporteMascota() {
        System.out.println("\n--- Generar Reporte por Mascota ---");
        System.out.print("Ingrese el ID de la mascota: ");
        int idMascota = pedirEntero();
        sistema.generarReportePorMascota(idMascota);
    }

    private static void generarReporteAdoptante() {
        System.out.println("\n--- Generar Reporte por Adoptante ---");
        System.out.print("Ingrese el ID del usuario adoptante: ");
        int idAdoptante = pedirEntero();
        sistema.generarReportePorAdoptante(idAdoptante);
    }

    private static int pedirEntero() {
        while (true) {
            try {
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                System.out.print("Entrada inválida. Por favor, ingrese un número entero: ");
                scanner.nextLine();
            }
        }
    }

    private static boolean pedirBooleano() {
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("t") || input.equals("si") || input.equals("s")) {
                return true;
            } else if (input.equals("false") || input.equals("f") || input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.print("Entrada inválida. Por favor, ingrese 'true' o 'false': ");
            }
        }
    }
}