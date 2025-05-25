package main.dao;

import main.model.Asistencia;
import main.model.AsistenciaId;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AsistenciaDAO {

    // Guardar nueva asistencia
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

    // Buscar una asistencia concreta
    public Asistencia buscarAsistencia(int idEntrenamiento, int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AsistenciaId id = new AsistenciaId(idEntrenamiento, idUsuario);
            return session.get(Asistencia.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar todas las asistencias
    public List<Asistencia> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Asistencia", Asistencia.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar asistencias por entrenamiento
    public List<Asistencia> listarPorEntrenamiento(int idEntrenamiento) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Asistencia a " +
                            " WHERE a.entrenamiento.id = :idEntrenamiento",
                    Asistencia.class)
                    .setParameter("idEntrenamiento", idEntrenamiento)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // Listar asistencias por jugador
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

    // Actualizar asistencia
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

    // Eliminar asistencia
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
     * @param idEntrenamiento id del entrenamiento
     * @param idUsuario       id del usuario (jugador)
     * @return la Asistencia si existe, o null si no hay registro
     */
    public Asistencia buscarPorEntrenamientoYJugador(int idEntrenamiento, int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Asistencia a " +
                            " WHERE a.entrenamiento.id = :entId " +
                            "   AND a.usuario.id       = :usrId",
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


    public long contarAsistencias(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT COUNT(a) FROM Asistencia a " +
                            "WHERE a.usuario.id = :idUsuario AND a.asistencia = true",
                    Long.class
            )
                    .setParameter("idUsuario", idUsuario)
                    .uniqueResult();
        }
    }
}
