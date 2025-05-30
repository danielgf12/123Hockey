package main.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Entidad que representa la información adicional de un jugador.
 * Está vinculada uno a uno con un usuario y contiene datos como fecha de nacimiento,
 * posición, asistencias, partidos jugados y una descripción personal.
 * 
 * @author Daniel García
 * @version 1.0
 */
@Entity
@Table(name = "jugador_info")
public class JugadorInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(name = "telefono_padres")
    private String telefonoPadres;

    @Enumerated(EnumType.STRING)
    private Posicion posicion;

    private String categoria;

    @Column(name = "asistencia_obligatoria")
    private int asistenciaObligatoria;

    @Column(name = "asistencia_voluntaria")
    private int asistenciaVoluntaria;

    @Column(name = "partidos_jugados")
    private int partidosJugados;

    private String descripcion;

    /**
     * Enumeración que define si el jugador es portero o jugador de campo.
     */
    public enum Posicion {
        PORTERO, JUGADOR
    }

    /**
     * Constructor por defecto requerido por JPA.
     */
    public JugadorInfo() {}

    /**
     * Constructor completo para inicializar todos los campos del modelo.
     *
     * @param usuario               Usuario asociado
     * @param fechaNacimiento       Fecha de nacimiento
     * @param telefonoPadres        Teléfono de contacto de los padres (si aplica)
     * @param posicion              Posición del jugador (PORTERO o JUGADOR)
     * @param categoria             Categoría del jugador
     * @param asistenciaObligatoria Número de asistencias obligatorias
     * @param asistenciaVoluntaria  Número de asistencias voluntarias
     * @param partidosJugados       Número de partidos jugados
     * @param descripcion           Descripción adicional del jugador
     */
    public JugadorInfo(Usuario usuario, Date fechaNacimiento, String telefonoPadres,
                       Posicion posicion, String categoria,
                       int asistenciaObligatoria, int asistenciaVoluntaria,
                       int partidosJugados, String descripcion) {
        this.usuario = usuario;
        this.fechaNacimiento = fechaNacimiento;
        this.telefonoPadres = telefonoPadres;
        this.posicion = posicion;
        this.categoria = categoria;
        this.asistenciaObligatoria = asistenciaObligatoria;
        this.asistenciaVoluntaria = asistenciaVoluntaria;
        this.partidosJugados = partidosJugados;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefonoPadres() { return telefonoPadres; }
    public void setTelefonoPadres(String telefonoPadres) { this.telefonoPadres = telefonoPadres; }

    public Posicion getPosicion() { return posicion; }
    public void setPosicion(Posicion posicion) { this.posicion = posicion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getAsistenciaObligatoria() { return asistenciaObligatoria; }
    public void setAsistenciaObligatoria(int asistenciaObligatoria) { this.asistenciaObligatoria = asistenciaObligatoria; }

    public int getAsistenciaVoluntaria() { return asistenciaVoluntaria; }
    public void setAsistenciaVoluntaria(int asistenciaVoluntaria) { this.asistenciaVoluntaria = asistenciaVoluntaria; }

    public int getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(int partidosJugados) { this.partidosJugados = partidosJugados; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
