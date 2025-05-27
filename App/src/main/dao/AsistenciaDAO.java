package main.dao;

import main.model.Asistencia;
import main.model.AsistenciaId;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de la entidad Asistencia.
 * Utiliza Hibernate para el acceso a la base de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class AsistenciaDAO {

    /**
     * Guarda una nueva asistencia en la base de datos.
     *
     * @param asistencia Asistencia a guardar
     */
    public void guardarAsistencia(Asistencia asistencia) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(asistencia);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Busca una asistencia concreta por entrenamiento y usuario.
     *
     * @param idEntrenamiento ID del entrenamiento
     * @param idUsuario       ID del usuario
     * @return La asistencia encontrada o null si no existe
     */
    public Asistencia buscarAsistencia(int idEntrenamiento, int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AsistenciaId id = new AsistenciaId(idEntrenamiento, idUsuario);
            return session.get(Asistencia.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todas las asistencias de la base de datos.
     *
     * @return Lista de asistencias o null si ocurre un error
     */
    public List<Asistencia> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Asistencia", Asistencia.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista las asistencias asociadas a un entrenamiento.
     *
     * @param idEntrenamiento ID del entrenamiento
     * @return Lista de asistencias o null si ocurre un error
     */
    public List<Asistencia> listarPorEntrenamiento(int idEntrenamiento) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Asistencia a WHERE a.entrenamiento.id = :idEntrenamiento",
                    Asistencia.class)
                    .setParameter("idEntrenamiento", idEntrenamiento)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista las asistencias asociadas a un jugador.
     *
     * @param idUsuario ID del jugador
     * @return Lista de asistencias o null si ocurre un error
     */
    public List<Asistencia> listarPorJugador(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Asistencia WHERE usuario.id = :id", Asistencia.class)
                    .setParameter("id", idUsuario)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Actualiza una asistencia existente en la base de datos.
     *
     * @param asistencia Asistencia a actualizar
     */
    public void actualizarAsistencia(Asistencia asistencia) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(asistencia);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Elimina todas las asistencias asociadas a un usuario.
     *
     * @param usuarioId ID del usuario
     */
    public void eliminarPorUsuario(int usuarioId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery("delete from Asistencia a where a.usuario.id = :uid")
                    .setParameter("uid", usuarioId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Calcula el porcentaje medio de asistencia de un equipo.
     *
     * @param idEquipo ID del equipo
     * @return Porcentaje de asistencia (0 si no hay datos)
     */
    public double calcularAsistenciaMediaPorEquipo(int idEquipo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                    "SELECT COUNT(a) FROM Asistencia a WHERE a.entrenamiento.equipo.id = :idEquipo",
                    Long.class)
                    .setParameter("idEquipo", idEquipo)
                    .uniqueResult();

            Long asistieron = session.createQuery(
                    "SELECT COUNT(a) FROM Asistencia a WHERE a.entrenamiento.equipo.id = :idEquipo AND a.asistencia = true",
                    Long.class)
                    .setParameter("idEquipo", idEquipo)
                    .uniqueResult();

            if (total == null || total == 0) return 0;
            return (double) asistieron / total * 100;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Busca la asistencia de un jugador a un entrenamiento concreto.
     *
     * @param idEntrenamiento ID del entrenamiento
     * @param idUsuario       ID del usuario (jugador)
     * @return Asistencia si existe, o null si no hay registro
     */
    public Asistencia buscarPorEntrenamientoYJugador(int idEntrenamiento, int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Asistencia a WHERE a.entrenamiento.id = :entId AND a.usuario.id = :usrId",
                    Asistencia.class
            )
                    .setParameter("entId", idEntrenamiento)
                    .setParameter("usrId", idUsuario)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cuenta el número de asistencias confirmadas de un jugador.
     *
     * @param idUsuario ID del jugador
     * @return Número de asistencias
     */
    public long contarAsistencias(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT COUNT(a) FROM Asistencia a WHERE a.usuario.id = :idUsuario AND a.asistencia = true",
                    Long.class
            )
                    .setParameter("idUsuario", idUsuario)
                    .uniqueResult();
        }
    }
}
