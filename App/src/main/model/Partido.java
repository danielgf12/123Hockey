package main.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Entidad que representa un partido disputado por un equipo.
 * Contiene información como rival, lugar, fecha, tipo y hora de quedada.
 * 
 * @author Daniel García
 * @version 1.0
 */
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

    /**
     * Enumeración para los tipos de partido posibles.
     */
    public enum TipoPartido {
        AMISTOSO, LIGA, TORNEO, CAMPEONATO
    }

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Partido() {}

    /**
     * Constructor completo para inicializar todos los campos del partido.
     *
     * @param equipo       Equipo que juega el partido
     * @param rival        Nombre del equipo rival
     * @param lugar        Lugar donde se juega
     * @param fecha        Fecha y hora del partido
     * @param tipoPartido  Tipo de partido (AMISTOSO, LIGA, etc.)
     * @param info         Información adicional del partido
     * @param horaQuedada  Hora de quedada previa al partido
     */
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
