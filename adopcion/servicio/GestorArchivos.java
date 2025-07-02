package servicio;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import modelo.*;


public class GestorArchivos {

    private static final String MASCOTAS_FILE = "mascotas.dat";
    private static final String USUARIOS_FILE = "usuarios.dat";
    private static final String SOLICITUDES_FILE = "solicitudes.dat";
    private static final String VISITAS_FILE = "visitas.dat";
    private static final String IDS_FILE = "ids.dat"; // Para guardar los próximos IDs

    public static void guardarDatos(List<Mascota> mascotas, List<Usuario> usuarios,
                                    List<SolicitudAdopcion> solicitudes, List<VisitaSeguimiento> visitas) {
        escribirObjeto(MASCOTAS_FILE, new ArrayList<>(mascotas));
        escribirObjeto(USUARIOS_FILE, new ArrayList<>(usuarios));
        escribirObjeto(SOLICITUDES_FILE, new ArrayList<>(solicitudes));
        escribirObjeto(VISITAS_FILE, new ArrayList<>(visitas));

        Map<String, Integer> nextIds = Map.of(
                "mascotaNextId", Mascota.getNextIdToSave(),
                "usuarioNextId", Usuario.getNextIdToSave(),
                "solicitudNextId", SolicitudAdopcion.getNextIdToSave(),
                "visitaNextId", VisitaSeguimiento.getNextIdToSave()
        );
        escribirObjeto(IDS_FILE, nextIds);
    }

    @SuppressWarnings("unchecked")
    public static void cargarDatos(List<Mascota> mascotas, List<Usuario> usuarios,
                                   List<SolicitudAdopcion> solicitudes, List<VisitaSeguimiento> visitas) {
        mascotas.clear();
        usuarios.clear();
        solicitudes.clear();
        visitas.clear();

        List<Mascota> mascotasCargadas = (List<Mascota>) leerObjeto(MASCOTAS_FILE);
        if (mascotasCargadas != null) mascotas.addAll(mascotasCargadas);

        List<Usuario> usuariosCargados = (List<Usuario>) leerObjeto(USUARIOS_FILE);
        if (usuariosCargados != null) usuarios.addAll(usuariosCargados);

        List<SolicitudAdopcion> solicitudesCargadas = (List<SolicitudAdopcion>) leerObjeto(SOLICITUDES_FILE);
        if (solicitudesCargadas != null) solicitudes.addAll(solicitudesCargadas);

        List<VisitaSeguimiento> visitasCargadas = (List<VisitaSeguimiento>) leerObjeto(VISITAS_FILE);
        if (visitasCargadas != null) visitas.addAll(visitasCargadas);

        // Cargar y establecer los próximos IDs
        Map<String, Integer> nextIds = (Map<String, Integer>) leerObjeto(IDS_FILE);
        if (nextIds != null) {
            Mascota.setNextId(nextIds.getOrDefault("mascotaNextId", 1));
            Usuario.setNextId(nextIds.getOrDefault("usuarioNextId", 1));
            SolicitudAdopcion.setNextId(nextIds.getOrDefault("solicitudNextId", 1));
            VisitaSeguimiento.setNextId(nextIds.getOrDefault("visitaNextId", 1));
        } else {
            Mascota.setNextId(mascotas.stream().mapToInt(Mascota::getId).max().orElse(0) + 1);
            Usuario.setNextId(usuarios.stream().mapToInt(Usuario::getId).max().orElse(0) + 1);
            SolicitudAdopcion.setNextId(solicitudes.stream().mapToInt(SolicitudAdopcion::getId).max().orElse(0) + 1);
            VisitaSeguimiento.setNextId(visitas.stream().mapToInt(VisitaSeguimiento::getId).max().orElse(0) + 1);
        }
    }

    private static void escribirObjeto(String filename, Object object) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(object);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo " + filename + ": " + e.getMessage());
        }
    }

    private static Object leerObjeto(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al leer el archivo " + filename + ": " + e.getMessage());
            return null;
        }
    }
}