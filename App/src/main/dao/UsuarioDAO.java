package main.dao;

import main.model.Usuario;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de la entidad Usuario.
 * Utiliza Hibernate para acceder a la base de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class UsuarioDAO {

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario Usuario a guardar
     */
    public void guardarUsuario(Usuario usuario) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(usuario);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario
     * @return Usuario encontrado o null si no existe
     */
    public Usuario buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Usuario.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario Nombre de usuario
     * @return Usuario encontrado o null si no existe
     */
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario WHERE usuario = :usuario", Usuario.class)
                    .setParameter("usuario", nombreUsuario)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todos los usuarios registrados.
     *
     * @return Lista de usuarios o null si ocurre un error
     */
    public List<Usuario> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario", Usuario.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Actualiza un usuario existente en la base de datos.
     *
     * @param usuario Usuario con los datos actualizados
     */
    public void actualizarUsuario(Usuario usuario) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(usuario);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param usuario Usuario a eliminar
     */
    public void eliminarUsuario(Usuario usuario) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(usuario);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
