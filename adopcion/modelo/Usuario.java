package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 2L;
    private static int nextId = 1;

    private int identificador;
    private String nombreUsuario;
    private String contrasena;
    private String nombreCompleto;
    private String direccion;
    private String telefono;
    private int numeroMascotasActuales;
    private String tipoVivienda;
    private RolUsuario rol;

    public Usuario(String nombreUsuario, String contrasena, String nombreCompleto, String direccion, String telefono,
                   int numeroMascotasActuales, String tipoVivienda, RolUsuario rol) {
        this.identificador = nextId++;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.direccion = direccion;
        this.telefono = telefono;
        this.numeroMascotasActuales = numeroMascotasActuales;
        this.tipoVivienda = tipoVivienda;
        this.rol = rol;
    }

    public int getId() {
        return identificador;
    }
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public String getContrasena() {
        return contrasena;
    }
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    public String getDireccion() {
        return direccion;
    }
    public String getTelefono() {
        return telefono;
    }
    public int getNumeroMascotasActuales() {
        return numeroMascotasActuales;
    }
    public String getTipoVivienda() {
        return tipoVivienda;
    }
    public RolUsuario getRol() {
        return rol;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public void setNumeroMascotasActuales(int numeroMascotasActuales) {
        this.numeroMascotasActuales = numeroMascotasActuales;
    }
    public void setTipoVivienda(String tipoVivienda) {
        this.tipoVivienda = tipoVivienda;
    }
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
    public static void setNextId(int id) {
        nextId = id;
    }
    public static int getNextIdToSave() {
        return nextId;
    }


    @Override
    public String toString() {
        return "Usuario ID: " + identificador +
                "\n  Nombre de Usuario: " + nombreUsuario +
                "\n  Nombre Completo: " + nombreCompleto +
                "\n  Dirección: " + direccion +
                "\n  Teléfono: " + telefono +
                "\n  Mascotas Actuales: " + numeroMascotasActuales +
                "\n  Tipo de Vivienda: " + tipoVivienda +
                "\n  Rol: " + rol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return identificador == usuario.identificador;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }
}