package main.dao;

import main.model.Equipo;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de la entidad Equipo.
 * Utiliza Hibernate para acceder a la base de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class EquipoDAO {

    /**
     * Guarda un nuevo equipo en la base de datos.
     *
     * @param equipo Equipo a guardar
     */
    public void guardarEquipo(Equipo equipo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(equipo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Busca un equipo por su ID.
     *
     * @param id ID del equipo
     * @return El equipo encontrado o null si no existe
     */
    public Equipo buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Equipo.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todos los equipos registrados en la base de datos.
     *
     * @return Lista de equipos o null si ocurre un error
     */
    public List<Equipo> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Equipo", Equipo.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Actualiza un equipo existente.
     *
     * @param equipo Equipo con los datos actualizados
     */
    public void actualizarEquipo(Equipo equipo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(equipo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Elimina un equipo de la base de datos.
     *
     * @param equipo Equipo a eliminar
     */
    public void eliminarEquipo(Equipo equipo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(equipo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Lista los equipos en los que está asociado un entrenador (por medio de la relación con EquipoJugador).
     *
     * @param idEntrenador ID del entrenador
     * @return Lista de equipos o una lista vacía si ocurre un error
     */
    public List<Equipo> listarPorEntrenador(int idEntrenador) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT e FROM Equipo e JOIN EquipoJugador ej ON e.id = ej.equipo.id WHERE ej.usuario.id = :idEntrenador",
                    Equipo.class)
                    .setParameter("idEntrenador", idEntrenador)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
