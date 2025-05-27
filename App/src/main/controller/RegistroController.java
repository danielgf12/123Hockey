package main.controller;

import main.dao.UsuarioDAO;
import main.model.Usuario;
import main.model.Usuario.Rol;
import main.util.HashUtil;
import main.view.InicioDelegadoVentana;
import main.view.InicioEntrenadorVentana;
import main.view.InicioJugadorVentana;

/**
 * Controlador encargado del proceso de registro de entrenadores.
 * Valida los datos introducidos, crea un nuevo usuario y lo guarda en la base de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class RegistroController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Registra un nuevo entrenador si los datos proporcionados son válidos.
     *
     * @param nombre               Nombre del entrenador
     * @param apellido1            Primer apellido
     * @param apellido2            Segundo apellido
     * @param email                Correo electrónico
     * @param telefono             Teléfono de contacto
     * @param contrasena           Contraseña elegida
     * @param confirmarContrasena  Confirmación de la contraseña
     * @return El objeto Usuario creado si el registro fue exitoso, o null si hubo algún error de validación
     */
    public Usuario registrarEntrenador(String nombre, String apellido1, String apellido2,
                                       String email, String telefono,
                                       String contrasena, String confirmarContrasena) {

        if (nombre.isEmpty() || apellido1.isEmpty() || apellido2.isEmpty() ||
                email.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            return null;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            return null;
        }

        if (usuarioDAO.listarTodos().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            return null;
        }

        String hash = HashUtil.hashSHA256(contrasena);
        String apellidos = apellido1 + " " + apellido2;

        Usuario nuevo = new Usuario(
                nombre,
                apellidos,
                email,
                telefono,
                hash,
                Rol.ENTRENADOR,
                "temporal",
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
