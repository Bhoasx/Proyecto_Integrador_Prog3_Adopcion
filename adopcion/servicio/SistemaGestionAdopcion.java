package servicio;

import modelo.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SistemaGestionAdopcion {
    private List<Mascota> mascotas;
    private List<Usuario> usuarios;
    private List<SolicitudAdopcion> solicitudesAdopcion;
    private List<VisitaSeguimiento> visitasSeguimiento;
    private Usuario usuarioLogueado;

    public SistemaGestionAdopcion() {
        mascotas = new ArrayList<>();
        usuarios = new ArrayList<>();
        solicitudesAdopcion = new ArrayList<>();
        visitasSeguimiento = new ArrayList<>();
        cargarDatos();

        if (usuarios.stream().noneMatch(u -> u.getRol() == RolUsuario.ADMINISTRADOR)) {
            Usuario admin = new Usuario("admin", "admin123", "Administrador del Sistema",
                    "Fundación Central", "000000000", 0, "Oficina", RolUsuario.ADMINISTRADOR);
            usuarios.add(admin);
            System.out.println("Usuario administrador por defecto creado (admin/admin123).");
            guardarDatos();
        }
    }

    private void cargarDatos() {
        GestorArchivos.cargarDatos(mascotas, usuarios, solicitudesAdopcion, visitasSeguimiento);
    }

    public void guardarDatos() {
        GestorArchivos.guardarDatos(mascotas, usuarios, solicitudesAdopcion, visitasSeguimiento);
    }

    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {
        Optional<Usuario> usuario = usuarios.stream()
                .filter(u -> u.getNombreUsuario().equals(nombreUsuario) && u.getContrasena().equals(contrasena))
                .findFirst();
        if (usuario.isPresent()) {
            usuarioLogueado = usuario.get();
            return usuarioLogueado;
        }
        return null;
    }

    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public boolean registrarMascota(String nombre, String especie, String raza, int edad, String estadoSalud, boolean esterilizado) {
        if (usuarioLogueado == null || (usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR && usuarioLogueado.getRol() != RolUsuario.VOLUNTARIO)) {
            System.out.println("Acceso denegado. Solo administradores o voluntarios pueden registrar mascotas.");
            return false;
        }
        Mascota nuevaMascota = new Mascota(nombre, especie, raza, edad, estadoSalud, esterilizado);
        if (!nuevaMascota.isEsterilizado()) {
            System.out.println("Advertencia: La mascota debe estar esterilizada para ser registrada para adopción.");
        }
        mascotas.add(nuevaMascota);
        guardarDatos();
        System.out.println("Mascota registrada exitosamente: " + nuevaMascota.getNombre());
        return true;
    }

    public List<Mascota> getMascotasDisponibles() {
        return mascotas.stream().filter(m -> !m.isAdoptada()).collect(Collectors.toList());
    }

    public List<Mascota> getTodasLasMascotas() {
        return new ArrayList<>(mascotas);
    }


    public Optional<Mascota> buscarMascotaPorId(int idMascota) {
        return mascotas.stream().filter(m -> m.getId() == idMascota).findFirst();
    }

    public boolean registrarUsuario(String nombreUsuario, String contrasena, String nombreCompleto, String direccion,
                                    String telefono, int numMascotas, String tipoVivienda, RolUsuario rol) {
        if (usuarios.stream().anyMatch(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))) {
            System.out.println("Error: El nombre de usuario '" + nombreUsuario + "' ya existe.");
            return false;
        }

        if (rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.VOLUNTARIO) {
            if (usuarioLogueado == null || usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR) {
                System.out.println("Acceso denegado. Solo un administrador puede registrar otros administradores o voluntarios.");
                return false;
            }
        }
        Usuario nuevoUsuario = new Usuario(nombreUsuario, contrasena, nombreCompleto, direccion, telefono, numMascotas, tipoVivienda, rol);
        usuarios.add(nuevoUsuario);
        guardarDatos();
        System.out.println("Usuario " + rol + " registrado exitosamente: " + nuevoUsuario.getNombreCompleto());
        return true;
    }

    public Optional<Usuario> buscarUsuarioPorId(int idUsuario) {
        return usuarios.stream().filter(u -> u.getId() == idUsuario).findFirst();
    }

    public boolean solicitarAdopcion(int idMascota) {
        if (usuarioLogueado == null || usuarioLogueado.getRol() != RolUsuario.ADOPTANTE) {
            System.out.println("Debe iniciar sesión como adoptante para solicitar una adopción.");
            return false;
        }

        Optional<Mascota> mascotaOpt = buscarMascotaPorId(idMascota);
        if (mascotaOpt.isEmpty() || mascotaOpt.get().isAdoptada()) {
            System.out.println("Mascota no encontrada o ya adoptada.");
            return false;
        }

        long solicitudesPendientesMascota = solicitudesAdopcion.stream()
                .filter(s -> s.getIdMascota() == idMascota && s.getEstado() == EstadoSolicitud.PENDIENTE)
                .count();
        if (solicitudesPendientesMascota >= 3) {
            System.out.println("Esta mascota ya tiene el máximo de solicitudes pendientes.");
            return false;
        }

        boolean tieneAdopcionActiva = solicitudesAdopcion.stream()
                .anyMatch(s -> s.getIdUsuarioAdoptante() == usuarioLogueado.getId() &&
                        (s.getEstado() == EstadoSolicitud.APROBADA || s.getEstado() == EstadoSolicitud.PENDIENTE));
        if (tieneAdopcionActiva) {
            System.out.println("Ya tiene una adopción aprobada o una solicitud pendiente. No puede solicitar otra.");
            return false;
        }

        Mascota mascota = mascotaOpt.get();
        if (!mascota.isEsterilizado()) { // Requerimiento implícito
            System.out.println("Error: Esta mascota aún no está esterilizada y no puede ser solicitada para adopción.");
            return false;
        }

        SolicitudAdopcion nuevaSolicitud = new SolicitudAdopcion(idMascota, usuarioLogueado.getId());
        solicitudesAdopcion.add(nuevaSolicitud);
        guardarDatos();
        System.out.println("Solicitud de adopción para " + mascota.getNombre() + " enviada exitosamente.");
        return true;
    }

    public List<SolicitudAdopcion> getSolicitudesPendientes() {
        return solicitudesAdopcion.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                .collect(Collectors.toList());
    }

    public boolean procesarSolicitud(int idSolicitud, boolean aprobar) {
        if (usuarioLogueado == null || (usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR && usuarioLogueado.getRol() != RolUsuario.VOLUNTARIO)) {
            System.out.println("Acceso denegado. Solo administradores o voluntarios pueden procesar solicitudes.");
            return false;
        }

        Optional<SolicitudAdopcion> solicitudOpt = solicitudesAdopcion.stream()
                .filter(s -> s.getId() == idSolicitud && s.getEstado() == EstadoSolicitud.PENDIENTE)
                .findFirst();

        if (solicitudOpt.isEmpty()) {
            System.out.println("Solicitud no encontrada o ya procesada.");
            return false;
        }
        SolicitudAdopcion solicitud = solicitudOpt.get();
        Optional<Mascota> mascotaOpt = buscarMascotaPorId(solicitud.getIdMascota());

        if (mascotaOpt.isEmpty()) { // Debería existir si la solicitud es válida
            System.out.println("Error interno: Mascota asociada a la solicitud no encontrada.");
            return false;
        }
        Mascota mascota = mascotaOpt.get();

        if (aprobar) {
            if (mascota.isAdoptada()) {
                System.out.println("Error: La mascota " + mascota.getNombre() + " ya fue adoptada (posiblemente por otra solicitud procesada).");
                solicitud.setEstado(EstadoSolicitud.RECHAZADA); // Rechazar esta automáticamente
                guardarDatos();
                return false;
            }
            solicitud.setEstado(EstadoSolicitud.APROBADA);
            mascota.setAdoptada(true);
            solicitud.setContratoFirmado(true); // "Contrato legal de adopción obligatorio"
            System.out.println("Solicitud " + idSolicitud + " APROBADA. Mascota " + mascota.getNombre() + " adoptada por usuario ID " + solicitud.getIdUsuarioAdoptante());

            solicitudesAdopcion.stream()
                    .filter(s -> s.getIdMascota() == mascota.getId() && s.getEstado() == EstadoSolicitud.PENDIENTE && s.getId() != solicitud.getId())
                    .forEach(s -> {
                        s.setEstado(EstadoSolicitud.RECHAZADA);
                        System.out.println("Solicitud " + s.getId() + " para " + mascota.getNombre() + " rechazada automáticamente.");
                    });

        } else {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            System.out.println("Solicitud " + idSolicitud + " RECHAZADA.");
        }
        guardarDatos();
        return true;
    }

    public boolean registrarVisitaSeguimiento(int idSolicitudAprobada, String reporteBienestar) {
        if (usuarioLogueado == null || (usuarioLogueado.getRol() != RolUsuario.ADMINISTRADOR && usuarioLogueado.getRol() != RolUsuario.VOLUNTARIO)) {
            System.out.println("Acceso denegado. Solo administradores o voluntarios pueden registrar visitas.");
            return false;
        }

        Optional<SolicitudAdopcion> solicitudOpt = solicitudesAdopcion.stream()
                .filter(s -> s.getId() == idSolicitudAprobada && s.getEstado() == EstadoSolicitud.APROBADA)
                .findFirst();

        if (solicitudOpt.isEmpty()) {
            System.out.println("No se encontró una solicitud de adopción aprobada con el ID: " + idSolicitudAprobada);
            return false;
        }

        SolicitudAdopcion solicitud = solicitudOpt.get();
        if (solicitud.getFechaDecision() == null || solicitud.getFechaDecision().plusMonths(6).isBefore(LocalDate.now())) {
            System.out.println("El periodo de seguimiento de 6 meses para esta adopción ha concluido o la fecha de decisión no está registrada.");
        }

        VisitaSeguimiento nuevaVisita = new VisitaSeguimiento(idSolicitudAprobada, reporteBienestar, usuarioLogueado.getId());
        visitasSeguimiento.add(nuevaVisita);
        guardarDatos();
        System.out.println("Visita de seguimiento registrada para la adopción ID " + idSolicitudAprobada);
        return true;
    }

    public List<SolicitudAdopcion> getAdopcionesAprobadasParaSeguimiento() {
        return solicitudesAdopcion.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.APROBADA)
                .collect(Collectors.toList());
    }

    public void generarReportePorMascota(int idMascota) {
        Optional<Mascota> mascotaOpt = buscarMascotaPorId(idMascota);
        if (mascotaOpt.isEmpty()) {
            System.out.println("Mascota con ID " + idMascota + " no encontrada.");
            return;
        }
        Mascota mascota = mascotaOpt.get();
        System.out.println("\n--- Reporte para Mascota: " + mascota.getNombre() + " (ID: " + mascota.getId() + ") ---");
        System.out.println(mascota);

        System.out.println("\nSolicitudes de Adopción Asociadas:");
        solicitudesAdopcion.stream()
                .filter(s -> s.getIdMascota() == idMascota)
                .forEach(s -> {
                    System.out.println("  ID Solicitud: " + s.getId() + ", Estado: " + s.getEstado() +
                            ", Adoptante ID: " + s.getIdUsuarioAdoptante() +
                            ", Fecha: " + s.getFechaSolicitud());
                    if (s.getEstado() == EstadoSolicitud.APROBADA) {
                        System.out.println("  Visitas de Seguimiento para esta adopción (Solicitud ID " + s.getId() + "):");
                        visitasSeguimiento.stream()
                                .filter(v -> v.getIdSolicitudAprobada() == s.getId())
                                .forEach(v -> System.out.println("    Visita ID: " + v.getId() + ", Fecha: " + v.getFechaVisita() + ", Reporte: " + v.getReporteBienestar()));
                    }
                });
        System.out.println("--- Fin del Reporte ---");
    }

    public void generarReportePorAdoptante(int idUsuarioAdoptante) {
        Optional<Usuario> usuarioOpt = usuarios.stream().filter(u -> u.getId() == idUsuarioAdoptante && u.getRol() == RolUsuario.ADOPTANTE).findFirst();
        if (usuarioOpt.isEmpty()) {
            System.out.println("Adoptante con ID " + idUsuarioAdoptante + " no encontrado.");
            return;
        }
        Usuario adoptante = usuarioOpt.get();
        System.out.println("\n--- Reporte para Adoptante: " + adoptante.getNombreCompleto() + " (ID: " + adoptante.getId() + ") ---");
        System.out.println(adoptante);

        System.out.println("\nSolicitudes de Adopción Realizadas:");
        solicitudesAdopcion.stream()
                .filter(s -> s.getIdUsuarioAdoptante() == idUsuarioAdoptante)
                .forEach(s -> {
                    Optional<Mascota> mascotaAsociadaOpt = buscarMascotaPorId(s.getIdMascota());
                    String nombreMascota = mascotaAsociadaOpt.isPresent() ? mascotaAsociadaOpt.get().getNombre() : "Mascota Desconocida";
                    System.out.println("  ID Solicitud: " + s.getId() + ", Mascota: " + nombreMascota + " (ID: " + s.getIdMascota() + ")" +
                            ", Estado: " + s.getEstado() + ", Fecha: " + s.getFechaSolicitud());
                    if (s.getEstado() == EstadoSolicitud.APROBADA) {
                        System.out.println("  Visitas de Seguimiento para esta adopción (Solicitud ID " + s.getId() + "):");
                        visitasSeguimiento.stream()
                                .filter(v -> v.getIdSolicitudAprobada() == s.getId())
                                .forEach(v -> System.out.println("    Visita ID: " + v.getId() + ", Fecha: " + v.getFechaVisita() + ", Voluntario ID: " + v.getIdVoluntario() + ", Reporte: " + v.getReporteBienestar()));
                    }
                });
        System.out.println("--- Fin del Reporte ---");
    }
}