package main.dao;

import main.model.Partido;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

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

    public Partido obtenerProximoPartido(int idEquipo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // asumimos que Partido tiene un campo Date fecha
            return session.createQuery(
                    "FROM Partido p " +
                            " WHERE p.equipo.id = :idEquipo " +
                            "   AND p.fecha >= :hoy " +
                            " ORDER BY p.fecha ASC",
                    Partido.class)
                    .setParameter("idEquipo", idEquipo)
                    .setParameter("hoy", new Date())
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
