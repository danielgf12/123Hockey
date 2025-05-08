package main.dao;

import main.model.Equipo;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class EquipoDAO {

    // Guardar un nuevo equipo
    public void guardarEquipo(Equipo equipo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(equipo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Buscar un equipo por ID
    public Equipo buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Equipo.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar todos los equipos
    public List<Equipo> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Equipo", Equipo.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Actualizar un equipo
    public void actualizarEquipo(Equipo equipo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(equipo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Eliminar un equipo
    public void eliminarEquipo(Equipo equipo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(equipo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Equipo> listarPorEntrenador(int idEntrenador) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT e FROM Equipo e JOIN EquipoJugador ej ON e.id = ej.equipo.id WHERE ej.usuario.id = :idEntrenador",
                    Equipo.class)
                    .setParameter("idEntrenador", idEntrenador)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
