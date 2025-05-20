package main.dao;

import main.model.JugadorInfo;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class JugadorInfoDAO {

    // Guardar nueva info de jugador
    public void guardarJugadorInfo(JugadorInfo info) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(info);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Buscar por ID
    public JugadorInfo buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(JugadorInfo.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Buscar por ID de usuario
    public JugadorInfo buscarPorUsuarioId(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM JugadorInfo WHERE usuario.id = :idUsuario", JugadorInfo.class)
                    .setParameter("idUsuario", idUsuario)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar todos
    public List<JugadorInfo> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM JugadorInfo", JugadorInfo.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Actualizar
    public void actualizarJugadorInfo(JugadorInfo info) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(info);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Eliminar
    // En JugadorInfoDAO.java
    public void eliminarPorUsuarioId(int usuarioId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // Borra directamente por HQL usando la FK a Usuario
            session.createQuery("delete from JugadorInfo ji where ji.usuario.id = :uid")
                    .setParameter("uid", usuarioId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

}
