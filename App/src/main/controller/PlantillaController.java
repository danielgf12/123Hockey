package main.controller;

import main.dao.UsuarioDAO;
import main.model.Usuario;
import main.model.Usuario.Rol;

import java.util.List;
import java.util.stream.Collectors;

public class PlantillaController {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Devuelve todos los usuarios con rol JUGADOR */
    public List<Usuario> getJugadores() {
        return usuarioDAO.listarTodos()
                .stream()
                .filter(u -> u.getRol() == Rol.JUGADOR)
                .collect(Collectors.toList());
    }

    /** Devuelve todos los usuarios con rol DELEGADO */
    public List<Usuario> getDelegados() {
        return usuarioDAO.listarTodos()
                .stream()
                .filter(u -> u.getRol() == Rol.DELEGADO)
                .collect(Collectors.toList());
    }
}
