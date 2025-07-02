package modelo;
import java.io.Serializable;
import java.util.Objects;

public class Mascota implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;

    private int identificador;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private String estadoSalud;
    private boolean esterilizado;
    private boolean adoptada;

    public Mascota(String nombre, String especie, String raza, int edad, String estadoSalud, boolean esterilizado) {
        this.identificador = nextId++;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.estadoSalud = estadoSalud;
        this.esterilizado = esterilizado;
        this.adoptada = false;
    }

    public int getId() {
        return identificador;
    }
    public String getNombre() {
        return nombre;
    }
    public String getEspecie() {
        return especie;
    }
    public String getRaza() {
        return raza;
    }
    public int getEdad() {
        return edad;
    }
    public String getEstadoSalud() {
        return estadoSalud;
    }
    public boolean isEsterilizado() {
        return esterilizado;
    }
    public boolean isAdoptada() {
        return adoptada;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setEspecie(String especie) {
        this.especie = especie;
    }
    public void setRaza(String raza) {
        this.raza = raza;
    }
    public void setEdad(int edad) {
        this.edad = edad;
    }
    public void setEstadoSalud(String estadoSalud) {
        this.estadoSalud = estadoSalud;
    }
    public void setEsterilizado(boolean esterilizado) {
        this.esterilizado = esterilizado;
    }
    public void setAdoptada(boolean adoptada) {
        this.adoptada = adoptada;
    }

    public static void setNextId(int id) {
        nextId = id;
    }
    public static int getNextIdToSave() {
        return nextId;
    }

    @Override
    public String toString() {
        return "Mascota ID: " + identificador +
                "\n  Nombre: " + nombre +
                "\n  Especie: " + especie + ", Raza: " + raza +
                "\n  Edad: " + edad + " años" +
                "\n  Estado de Salud: " + estadoSalud +
                "\n  Esterilizado: " + (esterilizado ? "Sí" : "No") +
                "\n  Adoptada: " + (adoptada ? "Sí" : "No");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mascota mascota = (Mascota) o;
        return identificador == mascota.identificador;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }
}