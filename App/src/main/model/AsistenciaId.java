package main.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AsistenciaId implements Serializable {

    private int idEntrenamiento;
    private int idUsuario;

    public AsistenciaId() {}

    public AsistenciaId(int idEntrenamiento, int idUsuario) {
        this.idEntrenamiento = idEntrenamiento;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters

    public int getIdEntrenamiento() { return idEntrenamiento; }
    public void setIdEntrenamiento(int idEntrenamiento) { this.idEntrenamiento = idEntrenamiento; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    // equals y hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AsistenciaId)) return false;
        AsistenciaId that = (AsistenciaId) o;
        return idEntrenamiento == that.idEntrenamiento &&
                idUsuario == that.idUsuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEntrenamiento, idUsuario);
    }
}
