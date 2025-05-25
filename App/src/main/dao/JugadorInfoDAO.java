package main.dao;

import main.model.JugadorInfo;
import main.model.Partido;
import main.model.EquipoJugador;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
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

    // Buscar por ID de JugadorInfo
    public JugadorInfo buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(JugadorInfo.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Buscar por ID de usuario (versión original)
    public JugadorInfo buscarPorUsuarioId(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM JugadorInfo ji WHERE ji.usuario.id = :idUsuario",
                    JugadorInfo.class)
                    .setParameter("idUsuario", idUsuario)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Nuevo alias para que puedas llamar buscarPorUsuario(int)
    public JugadorInfo buscarPorUsuario(int idUsuario) {
        return buscarPorUsuarioId(idUsuario);
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

    // Actualizar JugadorInfo
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

    // Eliminar por usuario
    public void eliminarPorUsuarioId(int usuarioId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery("delete from JugadorInfo ji where ji.usuario.id = :uid")
                    .setParameter("uid", usuarioId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Recalcula y actualiza el campo partidos_jugados en JugadorInfo
     * contando todos los partidos ya pasados en los que el jugador estuvo convocado
     * (i.e. perteneciente a cualquier equipo en la tabla EquipoJugador).
     */
    public void actualizarPartidosJugados(int idUsuario) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Long count = session.createQuery(
                    "SELECT COUNT(p) " +
                            "FROM Partido p, EquipoJugador ej " +
                            " WHERE ej.equipo.id = p.equipo.id " +
                            "   AND ej.usuario.id = :uid " +
                            "   AND p.fecha < :ahora",
                    Long.class)
                    .setParameter("uid", idUsuario)
                    .setParameter("ahora", new Date())
                    .uniqueResult();

            int partidos = (count != null ? count.intValue() : 0);

            JugadorInfo ji = buscarPorUsuario(idUsuario);
            if (ji != null) {
                ji.setPartidosJugados(partidos);
                session.update(ji);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
