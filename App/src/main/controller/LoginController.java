package main.controller;

import main.dao.UsuarioDAO;
import main.model.Usuario;
import main.util.HashUtil;
import main.view.InicioDelegadoVentana;
import main.view.InicioEntrenadorVentana;
import main.view.InicioJugadorVentana;

import javax.swing.*;

/**
 * Controlador encargado de gestionar el inicio de sesión de los usuarios.
 *
 * @author Daniel García
 * @version 1.0
 */
public class LoginController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Inicia sesión con las credenciales proporcionadas. Si las credenciales son correctas,
     * redirige al usuario a la ventana correspondiente según su rol.
     *
     * @param usuarioInput   Nombre de usuario introducido
     * @param contrasenaInput Contraseña introducida (sin hash)
     * @param loginVentana    Referencia a la ventana de login actual (para mostrar mensajes o cerrarla)
     * @return El nombre de usuario introducido
     */
    public String iniciarSesion(String usuarioInput, String contrasenaInput, JFrame loginVentana) {
        Usuario usuario = usuarioDAO.buscarPorNombreUsuario(usuarioInput);

        if (usuario == null) {
            JOptionPane.showMessageDialog(loginVentana, "Usuario no encontrado.");
            return usuarioInput;
        }

        String hashInput = HashUtil.hashSHA256(contrasenaInput);
        if (!usuario.getContrasena().equals(hashInput)) {
            JOptionPane.showMessageDialog(loginVentana, "Contraseña incorrecta.");
            return usuarioInput;
        }

        loginVentana.dispose();

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
