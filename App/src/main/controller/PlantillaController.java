package main.controller;

import main.dao.UsuarioDAO;
import main.model.Usuario;
import main.model.Usuario.Rol;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador encargado de obtener las listas de jugadores y delegados desde la base de datos.
 *
 * @author Daniel García
 * @version 1.0
 */
public class PlantillaController {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Devuelve una lista de todos los usuarios con rol JUGADOR.
     *
     * @return Lista de usuarios con rol JUGADOR
     */
    public List<Usuario> getJugadores() {
        return usuarioDAO.listarTodos()
                .stream()
                .filter(u -> u.getRol() == Rol.JUGADOR)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve una lista de todos los usuarios con rol DELEGADO.
     *
     * @return Lista de usuarios con rol DELEGADO
     */
    public List<Usuario> getDelegados() {
        return usuarioDAO.listarTodos()
                .stream()
                .filter(u -> u.getRol() == Rol.DELEGADO)
                .collect(Collectors.toList());
    }
}
