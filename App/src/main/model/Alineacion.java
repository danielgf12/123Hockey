package main.model;

import javax.persistence.*;

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

    public Alineacion() {}

    public Alineacion(Partido partido, Usuario usuario) {
        this.partido = partido;
        this.usuario = usuario;
        this.id = new AlineacionId(partido.getId(), usuario.getId());
    }

    // Getters y Setters

    public AlineacionId getId() { return id; }

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
