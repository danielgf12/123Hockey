package main.dao;

import main.model.Alineacion;
import main.model.AlineacionId;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de la entidad Alineacion.
 * Utiliza Hibernate para interactuar con la base de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class AlineacionDAO {

    /**
     * Guarda una nueva alineación en la base de datos.
     *
     * @param alineacion Alineación a guardar
     */
    public void guardarAlineacion(Alineacion alineacion) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(alineacion);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Busca una alineación concreta por id de partido y de usuario.
     *
     * @param idPartido ID del partido
     * @param idUsuario ID del usuario
     * @return La alineación encontrada o null si no existe
     */
    public Alineacion buscarAlineacion(int idPartido, int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AlineacionId id = new AlineacionId(idPartido, idUsuario);
            return session.get(Alineacion.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todas las alineaciones existentes en la base de datos.
     *
     * @return Lista de todas las alineaciones o null si ocurre un error
     */
    public List<Alineacion> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Alineacion", Alineacion.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todas las alineaciones asociadas a un partido concreto.
     *
     * @param idPartido ID del partido
     * @return Lista de alineaciones del partido o null si ocurre un error
     */
    public List<Alineacion> listarPorPartido(int idPartido) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Alineacion WHERE partido.id = :id", Alineacion.class)
                    .setParameter("id", idPartido)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todas las alineaciones en las que ha participado un jugador.
     *
     * @param idUsuario ID del jugador
     * @return Lista de alineaciones del jugador o null si ocurre un error
     */
    public List<Alineacion> listarPorJugador(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Alineacion WHERE usuario.id = :id", Alineacion.class)
                    .setParameter("id", idUsuario)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina una alineación de la base de datos.
     *
     * @param alineacion Alineación a eliminar
     */
    public void eliminarAlineacion(Alineacion alineacion) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(alineacion);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
