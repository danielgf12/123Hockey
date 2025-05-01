package main.controller;

import main.dao.UsuarioDAO;
import main.model.Usuario;
import main.model.Usuario.Rol;
import main.util.HashUtil;

public class RegistroController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public String registrarEntrenador(String nombre, String apellido1, String apellido2,
                                      String email, String telefono,
                                      String contrasena, String confirmarContrasena) {

        // Validar campos vacíos
        if (nombre.isEmpty() || apellido1.isEmpty() || apellido2.isEmpty() ||
                email.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            return "⚠ Todos los campos son obligatorios.";
        }

        // Validar contraseñas iguales
        if (!contrasena.equals(confirmarContrasena)) {
            return "❌ Las contraseñas no coinciden.";
        }

        // Validar email único
        if (usuarioDAO.listarTodos().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            return "❌ El correo electrónico ya está registrado.";
        }

        // Hashear contraseña
        String hash = HashUtil.hashSHA256(contrasena);

        // Concatenar apellidos
        String apellidos = apellido1 + " " + apellido2;

        // Crear objeto Usuario con usuario temporal
        Usuario nuevo = new Usuario(
                nombre,
                apellidos,
                email,
                telefono,
                hash,
                Rol.ENTRENADOR,
                "temporal", // se actualizará luego
                null,
                null,
                null
        );

        usuarioDAO.guardarUsuario(nuevo); // se genera el ID

        // Generar nombre de usuario real: nombre + inicial A1 + inicial A2 + ID
        String usuarioGenerado = nombre.toLowerCase()
                + apellido1.substring(0, 1).toLowerCase()
                + apellido2.substring(0, 1).toLowerCase()
                + nuevo.getId();

        nuevo.setUsuario(usuarioGenerado);
        usuarioDAO.actualizarUsuario(nuevo);

        return "✅ Registro exitoso. Tu nombre de usuario es: " + usuarioGenerado;
    }
}
