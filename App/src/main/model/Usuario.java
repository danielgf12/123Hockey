package main.model;

import javax.persistence.*;

/**
 * Entidad que representa a un usuario del sistema, ya sea entrenador, delegado o jugador.
 * Contiene información personal, de acceso y de afiliación al club.
 * 
 * @author Daniel García
 * @version 1.0
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    private String apellidos;

    @Column(unique = true)
    private String email;

    private String telefono;

    private String contrasena;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(unique = true)
    private String usuario;

    @Lob
    @Column(name = "fotoUsuario")
    private byte[] fotoUsuario;

    private String club;

    private String ciudad;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Usuario() {}

    /**
     * Constructor completo para inicializar los campos del usuario.
     * 
     * @param nombre       Nombre del usuario
     * @param apellidos    Apellidos del usuario
     * @param email        Correo electrónico (único)
     * @param telefono     Teléfono de contacto
     * @param contrasena   Contraseña cifrada
     * @param rol          Rol del usuario (ENTRENADOR, DELEGADO, JUGADOR)
     * @param usuario      Nombre de usuario (único)
     * @param fotoUsuario  Foto de perfil (opcional)
     * @param club         Club al que pertenece
     * @param ciudad       Ciudad del usuario
     */
    public Usuario(String nombre, String apellidos, String email, String telefono,
                   String contrasena, Rol rol, String usuario, byte[] fotoUsuario,
                   String club, String ciudad) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.rol = rol;
        this.usuario = usuario;
        this.fotoUsuario = fotoUsuario;
        this.club = club;
        this.ciudad = ciudad;
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public byte[] getFotoUsuario() { return fotoUsuario; }
    public void setFotoUsuario(byte[] fotoUsuario) { this.fotoUsuario = fotoUsuario; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    /**
     * Enumeración interna que define el rol del usuario en la aplicación.
     */
    public enum Rol {
        ENTRENADOR, DELEGADO, JUGADOR
    }
}
