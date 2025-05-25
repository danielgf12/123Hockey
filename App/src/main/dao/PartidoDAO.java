package main.dao;

import main.model.Alineacion;
import main.model.Partido;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PartidoDAO {

    // Guardar un partido
    public void guardarPartido(Partido partido) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(partido);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Buscar partido por ID
    public Partido buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Partido.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar todos los partidos
    public List<Partido> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Partido", Partido.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar partidos de un equipo
    public List<Partido> listarPorEquipo(int idEquipo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Partido WHERE equipo.id = :idEquipo", Partido.class)
                    .setParameter("idEquipo", idEquipo)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Actualizar partido
    public void actualizarPartido(Partido partido) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(partido);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Eliminar partido
    public void eliminarPartido(Partido partido) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(partido);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public int contarPorEquipo(int idEquipo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                    "SELECT COUNT(p) FROM Partido p WHERE p.equipo.id = :idEquipo",
                    Long.class)
                    .setParameter("idEquipo", idEquipo)
                    .uniqueResult();
            return total != null ? total.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public Partido obtenerProximoPartidoPorEquipos(List<Integer> idsEquipos, Date desde) {
        if (idsEquipos == null || idsEquipos.isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Partido p " +
                            " WHERE p.equipo.id IN :ids " +
                            "   AND p.fecha >= :desde " +
                            " ORDER BY p.fecha ASC",
                    Partido.class)
                    .setParameter("ids",   idsEquipos)
                    .setParameter("desde", desde)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

    public Partido obtenerProximoPartido(int idEquipo) {
        return obtenerProximoPartidoPorEquipos(
                Collections.singletonList(idEquipo),
                new Date()
        );
    }
    // --- En PartidoDAO.java ---

    /**
     * Devuelve el siguiente partido de todos los guardados, ordenado por fecha ascendente.
     */
    public Partido obtenerProximoPartidoGlobal() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Partido p " +
                            " WHERE p.fecha >= :hoy " +
                            " ORDER BY p.fecha ASC",
                    Partido.class)
                    .setParameter("hoy", new Date())
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- En PartidoDAO.java ---

    /**
     * Devuelve el siguiente partido al que el jugador está alineado.
     */
// PartidoDAO.java

    /**
     * Devuelve el siguiente partido al que el jugador está alineado.
     */
    public Partido obtenerProximoPartidoPorJugador(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT p FROM Partido p " +
                            " WHERE p.fecha >= :hoy " +
                            "   AND p.equipo.id IN (" +
                            "       SELECT ej.equipo.id FROM EquipoJugador ej WHERE ej.usuario.id = :uid" +
                            "   ) " +
                            " ORDER BY p.fecha ASC",
                    Partido.class
            )
                    .setParameter("hoy", new Date())
                    .setParameter("uid", idUsuario)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // import main.model.Alineacion;  // la entidad que mapea la tabla Alineacion
    public int contarPorJugador(int idJugador) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long c = session.createQuery(
                    "SELECT COUNT(a) FROM Alineacion a WHERE a.usuario.id = :idJugador",
                    Long.class)
                    .setParameter("idJugador", idJugador)
                    .uniqueResult();
            return c != null ? c.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
