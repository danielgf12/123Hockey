package main.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Clase de utilidad para configurar y proporcionar acceso a la {@link SessionFactory} de Hibernate.
 * Inicializa la configuración a partir del archivo hibernate.cfg.xml.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al crear el SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Devuelve la única instancia de {@link SessionFactory} para interactuar con Hibernate.
     *
     * @return Instancia de SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
