package main.controller;

import main.dao.UsuarioDAO;
import main.model.Usuario;
import main.util.HashUtil;
import main.view.InicioDelegadoVentana;
import main.view.InicioEntrenadorVentana;
import main.view.InicioJugadorVentana;
// importar aquí InicioDelegadoVentana e InicioJugadorVentana cuando las tengas

import javax.swing.*;

public class LoginController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public String iniciarSesion(String usuarioInput, String contrasenaInput, JFrame loginVentana) {

        // 1. Buscar usuario
        Usuario usuario = usuarioDAO.buscarPorNombreUsuario(usuarioInput);

        if (usuario == null) {
            JOptionPane.showMessageDialog(loginVentana, "Usuario no encontrado.");
            return usuarioInput;
        }

        // 2. Hashear y comparar
        String hashInput = HashUtil.hashSHA256(contrasenaInput);
        if (!usuario.getContrasena().equals(hashInput)) {
            JOptionPane.showMessageDialog(loginVentana, "Contraseña incorrecta.");
            return usuarioInput;
        }

        // 3. Cerrar ventana login
        loginVentana.dispose();

        // 4. Redirigir a la ventana correspondiente según el rol
        switch (usuario.getRol()) {
            case ENTRENADOR:
                new InicioEntrenadorVentana(usuario);
                break;
            case DELEGADO:
                 new InicioDelegadoVentana(usuario);
                break;
            case JUGADOR:
                 new InicioJugadorVentana(usuario);
                break;
        }
        return usuarioInput;
    }
}
