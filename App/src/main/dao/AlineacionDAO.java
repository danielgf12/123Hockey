package main.dao;

import main.model.Alineacion;
import main.model.AlineacionId;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AlineacionDAO {

    // Guardar nueva alineación
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

    // Buscar alineación concreta
    public Alineacion buscarAlineacion(int idPartido, int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AlineacionId id = new AlineacionId(idPartido, idUsuario);
            return session.get(Alineacion.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar todas las alineaciones
    public List<Alineacion> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Alineacion", Alineacion.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar alineaciones por partido
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

    // Listar alineaciones por jugador
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

    // Eliminar alineación
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
