package main.dao;

import main.model.EquipoJugador;
import main.model.EquipoJugadorId;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EquipoJugadorDAO {

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

    public EquipoJugador buscar(int idEquipo, int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            EquipoJugadorId id = new EquipoJugadorId(idEquipo, idUsuario);
            return session.get(EquipoJugador.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
