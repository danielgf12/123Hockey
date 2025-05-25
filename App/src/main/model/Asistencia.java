package main.model;

import javax.persistence.*;

@Entity
@Table(name = "asistencia")
public class Asistencia {

    @EmbeddedId
    private AsistenciaId id;

    @ManyToOne
    @MapsId("entrenamiento")      // coincide con el campo 'entrenamiento' del embeddable
    @JoinColumn(name = "id_entrenamiento")
    private Entrenamiento entrenamiento;

    @ManyToOne
    @MapsId("usuario")            // coincide con el campo 'usuario' del embeddable
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private boolean asistencia;

    public Asistencia() {}

    public Asistencia(Entrenamiento ent, Usuario usr, boolean asist) {
        this.entrenamiento = ent;
        this.usuario       = usr;
        this.asistencia    = asist;
        this.id            = new AsistenciaId(ent.getId(), usr.getId());
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
