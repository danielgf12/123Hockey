package main.controller;

import main.dao.UsuarioDAO;
import main.model.Usuario;
import main.util.HashUtil;

public class LoginController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public String iniciarSesion(String usuarioInput, String contrasenaInput) {

        // 1. Buscar el usuario por nombre de usuario
        Usuario usuario = usuarioDAO.buscarPorNombreUsuario(usuarioInput);

        if (usuario == null) {
            return "❌ Usuario no encontrado.";
        }

        // 2. Hashear la contraseña introducida
        String hashInput = HashUtil.hashSHA256(contrasenaInput);

        // 3. Comparar con la guardada
        if (!usuario.getContrasena().equals(hashInput)) {
            return "❌ Contraseña incorrecta.";
        }

        // 4. Acceso correcto
        return "✅ Bienvenido, " + usuario.getNombre() + " (" + usuario.getRol() + ")";
    }
}
