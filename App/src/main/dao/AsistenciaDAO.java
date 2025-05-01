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
            return session.createQuery("FROM Asistencia WHERE entrenamiento.id = :id", Asistencia.class)
                    .setParameter("id", idEntrenamiento)
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
    public void eliminarAsistencia(Asistencia asistencia) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(asistencia);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
