package main.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clave primaria compuesta para la entidad EquipoJugador.
 * Compuesta por el ID del equipo y el ID del usuario (jugador).
 * 
 * @author Daniel García
 * @version 1.0
 */
@Embeddable
public class EquipoJugadorId implements Serializable {

    private int idEquipo;
    private int idUsuario;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public EquipoJugadorId() {}

    /**
     * Constructor para inicializar la clave compuesta.
     *
     * @param idEquipo  ID del equipo
     * @param idUsuario ID del jugador
     */
    public EquipoJugadorId(int idEquipo, int idUsuario) {
        this.idEquipo = idEquipo;
        this.idUsuario = idUsuario;
    }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EquipoJugadorId)) return false;
        EquipoJugadorId that = (EquipoJugadorId) o;
        return idEquipo == that.idEquipo && idUsuario == that.idUsuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEquipo, idUsuario);
    }
}
