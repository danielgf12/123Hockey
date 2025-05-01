package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "entrenamiento")
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_equipo")
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

    // Enum para "repetir"
    public enum Repetir {
        NINGUNO, SEMANAL, QUINCENAL
    }

    // Enum para tipo de entrenamiento
    public enum TipoEntrenamiento {
        PISTA, FISICO, TACTICA
    }

    // Constructor vacío
    public Entrenamiento() {}

    // Constructor completo
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

    // Getters y Setters

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
}
