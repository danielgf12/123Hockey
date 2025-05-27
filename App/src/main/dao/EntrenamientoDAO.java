package main.dao;

import main.model.Entrenamiento;
import main.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de la entidad Entrenamiento.
 * Utiliza Hibernate para la interacción con la base de datos.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class EntrenamientoDAO {

    /**
     * Guarda un nuevo entrenamiento en la base de datos.
     *
     * @param entrenamiento Entrenamiento a guardar
     */
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

    /**
     * Busca un entrenamiento por su ID.
     *
     * @param id ID del entrenamiento
     * @return El entrenamiento encontrado o null si no existe
     */
    public Entrenamiento buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Entrenamiento.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista todos los entrenamientos registrados.
     *
     * @return Lista de entrenamientos o null si ocurre un error
     */
    public List<Entrenamiento> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Entrenamiento", Entrenamiento.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lista los entrenamientos asociados a un equipo.
     *
     * @param idEquipo ID del equipo
     * @return Lista de entrenamientos o null si ocurre un error
     */
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

    /**
     * Actualiza un entrenamiento existente.
     *
     * @param entrenamiento Entrenamiento a actualizar
     */
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

    /**
     * Elimina un entrenamiento de la base de datos.
     *
     * @param entrenamiento Entrenamiento a eliminar
     */
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

    /**
     * Devuelve el próximo entrenamiento a nivel global (más cercano en el tiempo).
     *
     * @return El próximo entrenamiento o null si no hay ninguno
     */
    public Entrenamiento obtenerProximoEntrenamientoGlobal() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Entrenamiento e WHERE e.fecha >= :ahora ORDER BY e.fecha ASC",
                    Entrenamiento.class)
                    .setParameter("ahora", new Date())
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Devuelve el número total de entrenamientos de un equipo.
     *
     * @param idEquipo ID del equipo
     * @return Número de entrenamientos
     */
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

    /**
     * Devuelve el próximo entrenamiento entre una lista de equipos desde una fecha específica.
     *
     * @param idsEquipos Lista de IDs de equipos
     * @param desde Fecha desde la que buscar
     * @return Próximo entrenamiento o null si no hay resultados
     */
    public Entrenamiento obtenerProximoEntrenamientoPorEquipos(List<Integer> idsEquipos, Date desde) {
        if (idsEquipos == null || idsEquipos.isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Entrenamiento e WHERE e.equipo.id IN :ids AND e.fecha >= :desde ORDER BY e.fecha ASC",
                    Entrenamiento.class)
                    .setParameter("ids", idsEquipos)
                    .setParameter("desde", desde)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

    /**
     * Devuelve el próximo entrenamiento al que está convocado un jugador.
     *
     * @param idJugador ID del jugador
     * @return Próximo entrenamiento o null si no hay ninguno
     */
    public Entrenamiento obtenerProximoEntrenamientoPorJugador(int idJugador) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT e FROM Entrenamiento e, EquipoJugador ej " +
                            "WHERE ej.usuario.id = :idJugador AND ej.equipo.id = e.equipo.id " +
                            "AND e.fecha >= :ahora ORDER BY e.fecha ASC",
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
     * Genera automáticamente las repeticiones pendientes para entrenamientos pasados
     * que tengan una periodicidad semanal o quincenal.
     */
    public void generarRepeticionesPendientes() {
        Date ahora = new Date();
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Entrenamiento> list = session.createQuery(
                    "FROM Entrenamiento e WHERE e.fecha < :ahora AND e.repetir != :ninguno",
                    Entrenamiento.class)
                    .setParameter("ahora", ahora)
                    .setParameter("ninguno", Entrenamiento.Repetir.NINGUNO)
                    .list();

            if (list.isEmpty()) return;

            tx = session.beginTransaction();

            for (Entrenamiento e : list) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(e.getFecha());
                if (e.getRepetir() == Entrenamiento.Repetir.SEMANAL) {
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                } else if (e.getRepetir() == Entrenamiento.Repetir.QUINCENAL) {
                    cal.add(Calendar.WEEK_OF_YEAR, 2);
                } else {
                    continue;
                }
                Date proximaFecha = cal.getTime();

                Entrenamiento nuevo = new Entrenamiento(
                        e.getEquipo(),
                        proximaFecha,
                        e.getUbicacion(),
                        e.getRepetir(),
                        e.getTipoEntrenamiento(),
                        e.getObservaciones()
                );
                session.save(nuevo);

                e.setRepetir(Entrenamiento.Repetir.NINGUNO);
                session.update(e);
            }

            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        }
    }

    /**
     * Sobrecarga que devuelve el próximo entrenamiento de un único equipo.
     *
     * @param idEquipo ID del equipo
     * @return Próximo entrenamiento o null si no hay ninguno
     */
    public Entrenamiento obtenerProximoEntrenamiento(int idEquipo) {
        return obtenerProximoEntrenamientoPorEquipos(
                Collections.singletonList(idEquipo),
                new Date()
        );
    }
}
