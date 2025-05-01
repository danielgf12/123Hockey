package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_equipo")
    private Equipo equipo;

    private String rival;

    private String lugar;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoPartido")
    private TipoPartido tipoPartido;

    private String info;

    @Temporal(TemporalType.TIME)
    private Date horaQuedada;

    // Enum para tipo de partido
    public enum TipoPartido {
        AMISTOSO, LIGA, TORNEO, CAMPEONATO
    }

    // Constructor vacío
    public Partido() {}

    // Constructor completo
    public Partido(Equipo equipo, String rival, String lugar, Date fecha,
                   TipoPartido tipoPartido, String info, Date horaQuedada) {
        this.equipo = equipo;
        this.rival = rival;
        this.lugar = lugar;
        this.fecha = fecha;
        this.tipoPartido = tipoPartido;
        this.info = info;
        this.horaQuedada = horaQuedada;
    }

    // Getters y Setters

    public int getId() { return id; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public String getRival() { return rival; }
    public void setRival(String rival) { this.rival = rival; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public TipoPartido getTipoPartido() { return tipoPartido; }
    public void setTipoPartido(TipoPartido tipoPartido) { this.tipoPartido = tipoPartido; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public Date getHoraQuedada() { return horaQuedada; }
    public void setHoraQuedada(Date horaQuedada) { this.horaQuedada = horaQuedada; }
}
