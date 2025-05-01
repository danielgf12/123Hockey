package main.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AlineacionId implements Serializable {

    private int idPartido;
    private int idUsuario;

    public AlineacionId() {}

    public AlineacionId(int idPartido, int idUsuario) {
        this.idPartido = idPartido;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters

    public int getIdPartido() { return idPartido; }
    public void setIdPartido(int idPartido) { this.idPartido = idPartido; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    // equals y hashCode

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
