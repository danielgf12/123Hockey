package main.model;

import javax.persistence.*;

@Entity
@Table(name = "asistencia")
public class Asistencia {

    @EmbeddedId
    private AsistenciaId id;

    @ManyToOne
    @MapsId("idEntrenamiento")
    @JoinColumn(name = "id_entrenamiento")
    private Entrenamiento entrenamiento;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private boolean asistencia;

    public Asistencia() {}

    public Asistencia(Entrenamiento entrenamiento, Usuario usuario, boolean asistencia) {
        this.entrenamiento = entrenamiento;
        this.usuario = usuario;
        this.asistencia = asistencia;
        this.id = new AsistenciaId(entrenamiento.getId(), usuario.getId());
    }

    // Getters y Setters

    public AsistenciaId getId() { return id; }

    public Entrenamiento getEntrenamiento() { return entrenamiento; }
    public void setEntrenamiento(Entrenamiento entrenamiento) { this.entrenamiento = entrenamiento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public boolean isAsistencia() { return asistencia; }
    public void setAsistencia(boolean asistencia) { this.asistencia = asistencia; }
}
