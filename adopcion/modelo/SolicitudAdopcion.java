package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class SolicitudAdopcion implements Serializable {
    private static final long serialVersionUID = 3L;
    private static int nextId = 1;

    private int identificador;
    private int idMascota;
    private int idUsuarioAdoptante;
    private LocalDate fechaSolicitud;
    private EstadoSolicitud estado;
    private LocalDate fechaDecision;
    private boolean contratoFirmado;

    public SolicitudAdopcion(int idMascota, int idUsuarioAdoptante) {
        this.identificador = nextId++;
        this.idMascota = idMascota;
        this.idUsuarioAdoptante = idUsuarioAdoptante;
        this.fechaSolicitud = LocalDate.now();
        this.estado = EstadoSolicitud.PENDIENTE;
        this.contratoFirmado = false;
    }

    public int getId() {
        return identificador;
    }
    public int getIdMascota() {
        return idMascota;
    }
    public int getIdUsuarioAdoptante() {
        return idUsuarioAdoptante;
    }
    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }
    public EstadoSolicitud getEstado() {
        return estado;
    }
    public LocalDate getFechaDecision() {
        return fechaDecision;
    }
    public boolean isContratoFirmado() {
        return contratoFirmado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
        if (estado == EstadoSolicitud.APROBADA || estado == EstadoSolicitud.RECHAZADA) {
            this.fechaDecision = LocalDate.now();
        }
    }
    public void setContratoFirmado(boolean contratoFirmado) {
        this.contratoFirmado = contratoFirmado;
    }

    public static void setNextId(int id) {
        nextId = id;
    }
    public static int getNextIdToSave() {
        return nextId;
    }

    @Override
    public String toString() {
        return "Solicitud ID: " + identificador +
                "\n  ID Mascota: " + idMascota +
                "\n  ID Adoptante: " + idUsuarioAdoptante +
                "\n  Fecha Solicitud: " + fechaSolicitud +
                "\n  Estado: " + estado +
                (fechaDecision != null ? "\n  Fecha Decisión: " + fechaDecision : "") +
                (estado == EstadoSolicitud.APROBADA ? "\n  Contrato Firmado: " + (contratoFirmado ? "Sí" : "Pendiente") : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitudAdopcion that = (SolicitudAdopcion) o;
        return identificador == that.identificador;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }
}