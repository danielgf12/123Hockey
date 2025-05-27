package main.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clave primaria compuesta para la entidad Asistencia.
 * Contiene el ID del entrenamiento y el ID del usuario (jugador).
 * 
 * @author Daniel García
 * @version 1.0
 */
@Embeddable
public class AsistenciaId implements Serializable {

    @Column(name = "id_entrenamiento")
    private int entrenamiento;

    @Column(name = "id_usuario")
    private int usuario;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public AsistenciaId() {}

    /**
     * Constructor para inicializar la clave compuesta.
     *
     * @param entrenamiento ID del entrenamiento
     * @param usuario       ID del usuario
     */
    public AsistenciaId(int entrenamiento, int usuario) {
        this.entrenamiento = entrenamiento;
        this.usuario = usuario;
    }

    public int getEntrenamiento() {
        return entrenamiento;
    }

    public void setEntrenamiento(int entrenamiento) {
        this.entrenamiento = entrenamiento;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsistenciaId that = (AsistenciaId) o;
        return entrenamiento == that.entrenamiento &&
               usuario == that.usuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entrenamiento, usuario);
    }
}
