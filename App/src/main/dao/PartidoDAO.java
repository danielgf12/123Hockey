package main.dao;

import main.model.Alineacion;
import main.model.Partido;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD y consultas especiales de la entidad Partido.
 * Utiliza Hibernate para acceder a la base de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class PartidoDAO {

    /**
     * Guarda un nuevo partido en la base de datos.
     *
     * @param partido Partido a guardar
     */
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

    /**
     * Busca un partido por su ID.
     *
     * @param id ID del partido
     * @return Partido encontrado o null si no existe
     */
    public Partido buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Partido.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todos los partidos registrados.
     *
     * @return Lista de partidos o null si ocurre un error
     */
    public List<Partido> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Partido", Partido.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista los partidos que pertenecen a un equipo concreto.
     *
     * @param idEquipo ID del equipo
     * @return Lista de partidos o null si ocurre un error
     */
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

    /**
     * Actualiza un partido existente.
     *
     * @param partido Partido a actualizar
     */
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

    /**
     * Elimina un partido de la base de datos.
     *
     * @param partido Partido a eliminar
     */
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

    /**
     * Devuelve la cantidad de partidos asociados a un equipo.
     *
     * @param idEquipo ID del equipo
     * @return Número total de partidos
     */
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

    /**
     * Devuelve el próximo partido entre una lista de equipos, a partir de una fecha dada.
     *
     * @param idsEquipos Lista de IDs de equipos
     * @param desde      Fecha mínima
     * @return El próximo partido o null si no hay ninguno
     */
    public Partido obtenerProximoPartidoPorEquipos(List<Integer> idsEquipos, Date desde) {
        if (idsEquipos == null || idsEquipos.isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Partido p WHERE p.equipo.id IN :ids AND p.fecha >= :desde ORDER BY p.fecha ASC",
                    Partido.class)
                    .setParameter("ids", idsEquipos)
                    .setParameter("desde", desde)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

    /**
     * Devuelve el próximo partido de un equipo concreto.
     *
     * @param idEquipo ID del equipo
     * @return Próximo partido o null si no hay ninguno
     */
    public Partido obtenerProximoPartido(int idEquipo) {
        return obtenerProximoPartidoPorEquipos(
                Collections.singletonList(idEquipo),
                new Date()
        );
    }

    /**
     * Devuelve el próximo partido registrado en general (global), ordenado por fecha.
     *
     * @return El partido más próximo o null si no hay ninguno
     */
    public Partido obtenerProximoPartidoGlobal() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Partido p WHERE p.fecha >= :hoy ORDER BY p.fecha ASC",
                    Partido.class)
                    .setParameter("hoy", new Date())
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Devuelve el próximo partido al que el jugador está alineado según sus equipos.
     *
     * @param idUsuario ID del jugador
     * @return Próximo partido en el que puede estar convocado
     */
    public Partido obtenerProximoPartidoPorJugador(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT p FROM Partido p " +
                            "WHERE p.fecha >= :hoy " +
                            "AND p.equipo.id IN (" +
                            "   SELECT ej.equipo.id FROM EquipoJugador ej WHERE ej.usuario.id = :uid" +
                            ") ORDER BY p.fecha ASC",
                    Partido.class)
                    .setParameter("hoy", new Date())
                    .setParameter("uid", idUsuario)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cuenta el número total de partidos en los que un jugador ha sido alineado.
     *
     * @param idJugador ID del jugador
     * @return Número total de alineaciones del jugador
     */
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
