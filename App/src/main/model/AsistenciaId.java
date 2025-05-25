package main.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clave compuesta para Asistencia (entrenamiento + jugador).
 */
@Embeddable
public class AsistenciaId implements Serializable {
    @Column(name="id_entrenamiento")
    private int entrenamiento;

    @Column(name="id_usuario")
    private int usuario;

    public AsistenciaId() {}

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
