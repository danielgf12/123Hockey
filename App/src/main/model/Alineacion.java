package main.model;

import javax.persistence.*;

/**
 * Entidad que representa la alineación de un jugador en un partido.
 * Está compuesta por una clave primaria embebida (id) que relaciona partido y usuario.
 * 
 * @author Daniel García
 * @version 1.0
 */
@Entity
@Table(name = "alineacion")
public class Alineacion {

    @EmbeddedId
    private AlineacionId id;

    @ManyToOne
    @MapsId("idPartido")
    @JoinColumn(name = "id_partido")
    private Partido partido;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Alineacion() {}

    /**
     * Crea una nueva alineación entre un partido y un usuario.
     *
     * @param partido Partido en el que participa el jugador
     * @param usuario Jugador alineado
     */
    public Alineacion(Partido partido, Usuario usuario) {
        this.partido = partido;
        this.usuario = usuario;
        this.id = new AlineacionId(partido.getId(), usuario.getId());
    }

    public AlineacionId getId() { return id; }

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
