package main.dao;

import main.model.JugadorInfo;
import main.model.Partido;
import main.model.EquipoJugador;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD y cálculos específicos de la entidad JugadorInfo.
 * Utiliza Hibernate para interactuar con la base de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class JugadorInfoDAO {

    /**
     * Guarda la información de un nuevo jugador.
     *
     * @param info Objeto JugadorInfo a guardar
     */
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

    /**
     * Busca la información de un jugador por su ID.
     *
     * @param id ID de la entidad JugadorInfo
     * @return Objeto JugadorInfo o null si no existe
     */
    public JugadorInfo buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(JugadorInfo.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca la información de un jugador por ID de usuario.
     *
     * @param idUsuario ID del usuario
     * @return Objeto JugadorInfo o null si no existe
     */
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

    /**
     * Alias de {@link #buscarPorUsuarioId(int)} para simplificar llamadas.
     *
     * @param idUsuario ID del usuario
     * @return Objeto JugadorInfo o null si no existe
     */
    public JugadorInfo buscarPorUsuario(int idUsuario) {
        return buscarPorUsuarioId(idUsuario);
    }

    /**
     * Lista todos los registros de JugadorInfo existentes.
     *
     * @return Lista de JugadorInfo o null si ocurre un error
     */
    public List<JugadorInfo> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM JugadorInfo", JugadorInfo.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Actualiza un registro de JugadorInfo existente.
     *
     * @param info Objeto JugadorInfo con los datos actualizados
     */
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

    /**
     * Elimina la información de un jugador a partir de su ID de usuario.
     *
     * @param usuarioId ID del usuario
     */
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
     * Recalcula y actualiza el campo "partidosJugados" en JugadorInfo
     * contando los partidos pasados en los que el jugador estuvo convocado.
     *
     * @param idUsuario ID del usuario (jugador)
     */
    public void actualizarPartidosJugados(int idUsuario) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Long count = session.createQuery(
                    "SELECT COUNT(p) " +
                            "FROM Partido p, EquipoJugador ej " +
                            "WHERE ej.equipo.id = p.equipo.id " +
                            "AND ej.usuario.id = :uid " +
                            "AND p.fecha < :ahora",
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
