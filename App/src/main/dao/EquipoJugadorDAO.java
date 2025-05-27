package main.dao;

import main.model.EquipoJugador;
import main.model.EquipoJugadorId;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de la relación entre equipos y jugadores (EquipoJugador).
 * Utiliza Hibernate para la persistencia de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class EquipoJugadorDAO {

    /**
     * Guarda una relación entre un equipo y un jugador.
     *
     * @param relacion Objeto EquipoJugador que representa la relación
     */
    public void guardar(EquipoJugador relacion) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(relacion);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Busca una relación concreta entre equipo y jugador por sus IDs.
     *
     * @param idEquipo  ID del equipo
     * @param idUsuario ID del jugador
     * @return La relación encontrada o null si no existe
     */
    public EquipoJugador buscar(int idEquipo, int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            EquipoJugadorId id = new EquipoJugadorId(idEquipo, idUsuario);
            return session.get(EquipoJugador.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todas las relaciones de jugadores de un equipo.
     *
     * @param idEquipo ID del equipo
     * @return Lista de relaciones o null si ocurre un error
     */
    public List<EquipoJugador> listarPorEquipo(int idEquipo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM EquipoJugador WHERE equipo.id = :idEquipo", EquipoJugador.class)
                    .setParameter("idEquipo", idEquipo)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todos los equipos en los que participa un jugador.
     *
     * @param idUsuario ID del jugador
     * @return Lista de relaciones o null si ocurre un error
     */
    public List<EquipoJugador> listarPorJugador(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM EquipoJugador WHERE usuario.id = :idUsuario", EquipoJugador.class)
                    .setParameter("idUsuario", idUsuario)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina una relación entre un equipo y un jugador.
     *
     * @param relacion Relación a eliminar
     */
    public void eliminar(EquipoJugador relacion) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(relacion);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
