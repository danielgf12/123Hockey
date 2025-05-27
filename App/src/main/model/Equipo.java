package main.model;

import javax.persistence.*;

/**
 * Entidad que representa un equipo.
 * Incluye información como nombre, liga, categoría, club, ciudad, país y una imagen opcional.
 * 
 * @author Daniel García
 * @version 1.0
 */
@Entity
@Table(name = "equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    private String liga;

    private String categoria;

    private String club;

    private String ciudad;

    private String pais;

    @Lob
    @Column(name = "fotoEquipo")
    private byte[] fotoEquipo;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Equipo() {}

    /**
     * Constructor que inicializa todos los atributos del equipo, excepto el ID.
     *
     * @param nombre      Nombre del equipo
     * @param liga        Liga en la que participa
     * @param categoria   Categoría del equipo
     * @param club        Club al que pertenece
     * @param ciudad      Ciudad del equipo
     * @param pais        País del equipo
     * @param fotoEquipo  Imagen del equipo (opcional)
     */
    public Equipo(String nombre, String liga, String categoria, String club,
                  String ciudad, String pais, byte[] fotoEquipo) {
        this.nombre = nombre;
        this.liga = liga;
        this.categoria = categoria;
        this.club = club;
        this.ciudad = ciudad;
        this.pais = pais;
        this.fotoEquipo = fotoEquipo;
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getLiga() { return liga; }
    public void setLiga(String liga) { this.liga = liga; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public byte[] getFotoEquipo() { return fotoEquipo; }
    public void setFotoEquipo(byte[] fotoEquipo) { this.fotoEquipo = fotoEquipo; }
}
