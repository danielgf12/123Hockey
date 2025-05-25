package main.dao;

import main.model.Entrenamiento;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EntrenamientoDAO {

    // Guardar entrenamiento
    public void guardarEntrenamiento(Entrenamiento entrenamiento) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(entrenamiento);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Buscar por ID
    public Entrenamiento buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Entrenamiento.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar todos los entrenamientos
    public List<Entrenamiento> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Entrenamiento", Entrenamiento.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar entrenamientos por equipo
    public List<Entrenamiento> listarPorEquipo(int idEquipo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Entrenamiento WHERE equipo.id = :idEquipo", Entrenamiento.class)
                    .setParameter("idEquipo", idEquipo)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Actualizar entrenamiento
    public void actualizarEntrenamiento(Entrenamiento entrenamiento) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(entrenamiento);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Eliminar entrenamiento
    public void eliminarEntrenamiento(Entrenamiento entrenamiento) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(entrenamiento);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
// --- En EntrenamientoDAO.java ---

    /**
     * Devuelve el siguiente entrenamiento de todos los guardados, ordenado por fecha ascendente.
     */
    public Entrenamiento obtenerProximoEntrenamientoGlobal() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Entrenamiento e " +
                            " WHERE e.fecha >= :ahora " +
                            " ORDER BY e.fecha ASC",
                    Entrenamiento.class)
                    .setParameter("ahora", new Date())
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int contarPorEquipo(int idEquipo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                    "SELECT COUNT(e) FROM Entrenamiento e WHERE e.equipo.id = :idEquipo",
                    Long.class)
                    .setParameter("idEquipo", idEquipo)
                    .uniqueResult();
            return total != null ? total.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public Entrenamiento obtenerProximoEntrenamientoPorEquipos(List<Integer> idsEquipos, Date desde) {
        if (idsEquipos == null || idsEquipos.isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Entrenamiento e " +
                            " WHERE e.equipo.id IN :ids " +
                            "   AND e.fecha >= :desde " +
                            " ORDER BY e.fecha ASC",
                    Entrenamiento.class)
                    .setParameter("ids",   idsEquipos)
                    .setParameter("desde", desde)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

// --- En EntrenamientoDAO.java ---

    /**
     * Devuelve el siguiente entrenamiento al que el jugador está convocado.
     */
    public Entrenamiento obtenerProximoEntrenamientoPorJugador(int idJugador) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Cruza Entrenamiento con EquipoJugador para filtrar solo sus equipos
            return session.createQuery(
                    "SELECT e FROM Entrenamiento e, EquipoJugador ej " +
                            " WHERE ej.usuario.id = :idJugador " +
                            "   AND ej.equipo.id  = e.equipo.id  " +
                            "   AND e.fecha >= :ahora " +
                            " ORDER BY e.fecha ASC",
                    Entrenamiento.class)
                    .setParameter("idJugador", idJugador)
                    .setParameter("ahora", new Date())
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Sobrecarga de compatibilidad para un solo equipo
     */
    public Entrenamiento obtenerProximoEntrenamiento(int idEquipo) {
        return obtenerProximoEntrenamientoPorEquipos(
                Collections.singletonList(idEquipo),
                new Date()
        );
    }
    }
