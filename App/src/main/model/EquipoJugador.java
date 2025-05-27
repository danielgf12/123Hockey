package main.model;

import javax.persistence.*;

/**
 * Entidad que representa la relación entre un equipo y un jugador.
 * Usa una clave primaria compuesta formada por los IDs de equipo y usuario.
 * 
 * @author Daniel García
 * @version 1.0
 */
@Entity
@Table(name = "equipo_jugador")
public class EquipoJugador {

    @EmbeddedId
    private EquipoJugadorId id;

    @ManyToOne
    @MapsId("idEquipo")
    @JoinColumn(name = "id_equipo")
    private Equipo equipo;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public EquipoJugador() {}

    /**
     * Constructor que establece la relación entre un equipo y un jugador.
     *
     * @param equipo  Equipo asociado
     * @param usuario Jugador asociado
     */
    public EquipoJugador(Equipo equipo, Usuario usuario) {
        this.equipo = equipo;
        this.usuario = usuario;
        this.id = new EquipoJugadorId(equipo.getId(), usuario.getId());
    }

    public EquipoJugadorId getId() { return id; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
