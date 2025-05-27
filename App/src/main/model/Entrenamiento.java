package main.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entidad que representa un entrenamiento de un equipo.
 * Incluye información sobre fecha, ubicación, tipo y configuración de repetición.
 * También gestiona la relación con las asistencias.
 * 
 * @author Daniel García
 * @version 1.0
 */
@Entity
@Table(name = "entrenamiento")
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    private String ubicacion;

    @Enumerated(EnumType.STRING)
    private Repetir repetir;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoEntrenamiento")
    private TipoEntrenamiento tipoEntrenamiento;

    private String observaciones;

    @OneToMany(
            mappedBy = "entrenamiento",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Asistencia> asistencias = new ArrayList<>();

    /**
     * Enumeración que define las posibles repeticiones del entrenamiento.
     */
    public enum Repetir {
        NINGUNO, SEMANAL, QUINCENAL
    }

    /**
     * Enumeración para los tipos de entrenamiento posibles.
     */
    public enum TipoEntrenamiento {
        PISTA, FISICO, TACTICA
    }

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Entrenamiento() {}

    /**
     * Constructor completo para inicializar todos los campos del entrenamiento.
     *
     * @param equipo            Equipo que realiza el entrenamiento
     * @param fecha             Fecha y hora del entrenamiento
     * @param ubicacion         Lugar donde se realiza
     * @param repetir           Tipo de repetición (NINGUNO, SEMANAL, QUINCENAL)
     * @param tipoEntrenamiento Tipo de entrenamiento (PISTA, FISICO, TACTICA)
     * @param observaciones     Observaciones opcionales
     */
    public Entrenamiento(Equipo equipo, Date fecha, String ubicacion,
                         Repetir repetir, TipoEntrenamiento tipoEntrenamiento,
                         String observaciones) {
        this.equipo = equipo;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.repetir = repetir;
        this.tipoEntrenamiento = tipoEntrenamiento;
        this.observaciones = observaciones;
    }

    public int getId() { return id; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Repetir getRepetir() { return repetir; }
    public void setRepetir(Repetir repetir) { this.repetir = repetir; }

    public TipoEntrenamiento getTipoEntrenamiento() { return tipoEntrenamiento; }
    public void setTipoEntrenamiento(TipoEntrenamiento tipoEntrenamiento) { this.tipoEntrenamiento = tipoEntrenamiento; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<Asistencia> getAsistencias() { return asistencias; }
    public void setAsistencias(List<Asistencia> asistencias) { this.asistencias = asistencias; }
}
