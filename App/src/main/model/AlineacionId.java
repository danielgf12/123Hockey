package main.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clase embebida que representa la clave primaria compuesta de la entidad Alineacion.
 * Está formada por el ID del partido y el ID del usuario.
 * 
 * @author Daniel García
 * @version 1.0
 */
@Embeddable
public class AlineacionId implements Serializable {

    private int idPartido;
    private int idUsuario;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public AlineacionId() {}

    /**
     * Constructor con parámetros para inicializar la clave compuesta.
     *
     * @param idPartido ID del partido
     * @param idUsuario ID del jugador
     */
    public AlineacionId(int idPartido, int idUsuario) {
        this.idPartido = idPartido;
        this.idUsuario = idUsuario;
    }

    public int getIdPartido() { return idPartido; }
    public void setIdPartido(int idPartido) { this.idPartido = idPartido; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlineacionId)) return false;
        AlineacionId that = (AlineacionId) o;
        return idPartido == that.idPartido && idUsuario == that.idUsuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPartido, idUsuario);
    }
}
