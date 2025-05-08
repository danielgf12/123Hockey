package main.controller;

import main.dao.UsuarioDAO;
import main.model.Usuario;
import main.model.Usuario.Rol;
import main.util.HashUtil;
import main.view.InicioDelegadoVentana;
import main.view.InicioEntrenadorVentana;
import main.view.InicioJugadorVentana;

public class RegistroController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario registrarEntrenador(String nombre, String apellido1, String apellido2,
                                       String email, String telefono,
                                       String contrasena, String confirmarContrasena) {

        // Validar campos vacíos
        if (nombre.isEmpty() || apellido1.isEmpty() || apellido2.isEmpty() ||
                email.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            return null;
        }

        // Validar contraseñas iguales
        if (!contrasena.equals(confirmarContrasena)) {
            return null;
        }

        // Validar email único
        if (usuarioDAO.listarTodos().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            return null;
        }

        // Hashear contraseña
        String hash = HashUtil.hashSHA256(contrasena);
        String apellidos = apellido1 + " " + apellido2;

        Usuario nuevo = new Usuario(
                nombre,
                apellidos,
                email,
                telefono,
                hash,
                Rol.ENTRENADOR,
                "temporal", // se actualiza luego
                null, null, null
        );

        usuarioDAO.guardarUsuario(nuevo);

        String usuarioGenerado = nombre.toLowerCase()
                + apellido1.substring(0, 1).toLowerCase()
                + apellido2.substring(0, 1).toLowerCase()
                + nuevo.getId();

        nuevo.setUsuario(usuarioGenerado);
        usuarioDAO.actualizarUsuario(nuevo);

        return nuevo;
    }
}
