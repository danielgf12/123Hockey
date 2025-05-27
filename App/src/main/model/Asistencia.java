package main.model;

import javax.persistence.*;

/**
 * Entidad que representa la asistencia de un jugador a un entrenamiento.
 * Usa una clave compuesta formada por el ID del entrenamiento y del usuario.
 * 
 * @author Daniel García
 * @version 1.0
 */
@Entity
@Table(name = "asistencia")
public class Asistencia {

    @EmbeddedId
    private AsistenciaId id;

    @ManyToOne
    @MapsId("entrenamiento")
    @JoinColumn(name = "id_entrenamiento")
    private Entrenamiento entrenamiento;

    @ManyToOne
    @MapsId("usuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private boolean asistencia;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Asistencia() {}

    /**
     * Constructor que inicializa la asistencia con los datos del entrenamiento, jugador y valor booleano.
     *
     * @param ent   Entrenamiento al que corresponde la asistencia
     * @param usr   Usuario que asiste
     * @param asist Valor de asistencia (true si asistió, false si no)
     */
    public Asistencia(Entrenamiento ent, Usuario usr, boolean asist) {
        this.entrenamiento = ent;
        this.usuario       = usr;
        this.asistencia    = asist;
        this.id            = new AsistenciaId(ent.getId(), usr.getId());
    }

    public AsistenciaId getId() { return id; }

    public Entrenamiento getEntrenamiento() { return entrenamiento; }
    public void setEntrenamiento(Entrenamiento entrenamiento) { this.entrenamiento = entrenamiento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public boolean isAsistencia() { return asistencia; }
    public void setAsistencia(boolean asistencia) { this.asistencia = asistencia; }
}
