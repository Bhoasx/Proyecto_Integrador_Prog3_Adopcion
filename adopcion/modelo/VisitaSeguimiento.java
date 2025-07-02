package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class VisitaSeguimiento implements Serializable {
    private static final long serialVersionUID = 4L;
    private static int nextId = 1;

    private int identificador;
    private int idSolicitudAprobada;

    private LocalDate fechaVisita;
    private String reporteBienestar;
    private int idVoluntario;


    public VisitaSeguimiento(int idSolicitudAprobada, String reporteBienestar, int idVoluntario) {
        this.identificador = nextId++;
        this.idSolicitudAprobada = idSolicitudAprobada;
        this.fechaVisita = LocalDate.now();
        this.reporteBienestar = reporteBienestar;
        this.idVoluntario = idVoluntario;
    }

    public int getId() {
        return identificador;
    }
    public int getIdSolicitudAprobada() {
        return idSolicitudAprobada;
    }
    public LocalDate getFechaVisita() {
        return fechaVisita; }
    public String getReporteBienestar() {
        return reporteBienestar;
    }
    public int getIdVoluntario() {
        return idVoluntario;
    }

    public void setReporteBienestar(String reporteBienestar) {
        this.reporteBienestar = reporteBienestar; }

    public static void setNextId(int id) {
        nextId = id;
    }
    public static int getNextIdToSave() {
        return nextId;
    }


    @Override
    public String toString() {
        return "Visita ID: " + identificador +
                "\n  ID Solicitud Adopción: " + idSolicitudAprobada +
                "\n  Fecha Visita: " + fechaVisita +
                "\n  ID Voluntario: " + idVoluntario +
                "\n  Reporte: " + reporteBienestar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitaSeguimiento that = (VisitaSeguimiento) o;
        return identificador == that.identificador;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }
}