package main.view;

import main.dao.*;
import main.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import main.util.HibernateUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Diálogo que muestra la ficha de un equipo, permitiendo ver y editar sus datos,
 * gestionar jugadores disponibles y convocados, generar informe de asistencia y
 * eliminar el equipo si el usuario es entrenador.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class FichaEquipoVentana extends JDialog {
    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,141,225);

    private final Usuario usuarioLogado;
    private final Equipo equipo;
    private final EquipoDAO equipoDao     = new EquipoDAO();
    private final EquipoJugadorDAO ejDao  = new EquipoJugadorDAO();
    private final JugadorInfoDAO jiDao    = new JugadorInfoDAO();

    private boolean editing = false;
    private JButton btnEditSave;
    private JTextField txtNombre, txtLiga, txtClub, txtCiudad, txtPais;
    private JComboBox<String> cbCategoria;
    private JLabel lblNombre;

    private final JPanel disponiblesPanel = new JPanel();
    private final JPanel convocadosPanel  = new JPanel();

    /**
     * Construye el diálogo de ficha de equipo para el usuario dado y lo inicializa.
     *
     * @param usuarioLogado usuario autenticado que abre la ficha
     * @param equipo        equipo cuyas datos se mostrarán
     */
    public FichaEquipoVentana(Usuario usuarioLogado, Equipo equipo) {
        super((Frame)null, "Ficha de equipo", true);
        this.usuarioLogado = usuarioLogado;
        this.equipo        = equipo;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    /**
     * Inicializa la interfaz de usuario: layout, componentes de datos, listas
     * de jugadores y botones de acción.
     */
    private void initUI() {
        setSize(900, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout(10,10));

        lblNombre = new JLabel(equipo.getNombre());
        lblNombre.setForeground(FG);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel lblFoto = new JLabel(cargarAvatarCircular(equipo, 100, 100));
        if (usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR) {
            lblFoto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblFoto.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(new FileNameExtensionFilter(
                            "Imágenes (jpg, png, gif)", "jpg","jpeg","png","gif"
                    ));
                    if (chooser.showOpenDialog(FichaEquipoVentana.this) == JFileChooser.APPROVE_OPTION) {
                        try {
                            File f = chooser.getSelectedFile();
                            byte[] img = Files.readAllBytes(f.toPath());
                            equipo.setFotoEquipo(img);
                            equipoDao.actualizarEquipo(equipo);
                            lblFoto.setIcon(cargarAvatarCircular(equipo, 100, 100));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(
                                    FichaEquipoVentana.this,
                                    "Error al actualizar la foto:\n" + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
            });
        }

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        header.setBackground(BG);
        header.add(lblNombre);
        header.add(lblFoto);
        getContentPane().add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0,10));
        mainPanel.setBackground(BG);

        JPanel info = new JPanel(new GridLayout(0,2,8,8));
        info.setBackground(BG);
        info.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Datos del equipo", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), FG
        ));
        info.add(createLabel("Nombre:"));
        txtNombre = createField(equipo.getNombre());
        txtNombre.setEditable(false);
        info.add(txtNombre);

        info.add(createLabel("Liga:"));
        txtLiga = createField(equipo.getLiga());
        txtLiga.setEditable(false);
        info.add(txtLiga);

        info.add(createLabel("Categoría:"));
        cbCategoria = new JComboBox<>(new String[]{
                "Prebenjamin","Benjamin","Alevin",
                "Infantil","Juvenil","Junior","Senior"
        });
        cbCategoria.setSelectedItem(equipo.getCategoria());
        cbCategoria.setEnabled(false);
        info.add(cbCategoria);

        info.add(createLabel("Club:"));
        txtClub = createField(equipo.getClub());
        txtClub.setEditable(false);
        info.add(txtClub);

        info.add(createLabel("Ciudad:"));
        txtCiudad = createField(equipo.getCiudad());
        txtCiudad.setEditable(false);
        info.add(txtCiudad);

        info.add(createLabel("País:"));
        txtPais = createField(equipo.getPais());
        txtPais.setEditable(false);
        info.add(txtPais);

        mainPanel.add(info, BorderLayout.NORTH);

        JPanel lists = new JPanel(new GridLayout(1,2,10,0));
        lists.setBackground(BG);

        disponiblesPanel.setBackground(BG);
        disponiblesPanel.setLayout(new BoxLayout(disponiblesPanel, BoxLayout.Y_AXIS));
        convocadosPanel.setBackground(BG);
        convocadosPanel.setLayout(new BoxLayout(convocadosPanel, BoxLayout.Y_AXIS));

        lists.add(wrapInScroll("Jugadores disponibles", disponiblesPanel, 300));
        lists.add(wrapInScroll("Jugadores convocados",  convocadosPanel,  300));

        mainPanel.add(lists, BorderLayout.CENTER);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);

        boolean coach = usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR;
        if (coach) {
            btnEditSave = new JButton("Editar datos");
            btnEditSave.addActionListener(e -> toggleEdit());
            footer.add(btnEditSave);

            JButton btnInforme = new JButton("Ver informe");
            btnInforme.addActionListener(e -> generarInforme());
            footer.add(btnInforme);

            JButton btnEliminar = new JButton("Eliminar equipo");
            btnEliminar.addActionListener(e -> {
                int r = JOptionPane.showConfirmDialog(this,
                        "¿Eliminar equipo \""+equipo.getNombre()+"\"?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    List<EquipoJugador> rels = ejDao.listarPorEquipo(equipo.getId());
                    for (EquipoJugador rel : rels) {
                        ejDao.eliminar(rel);
                    }
                    equipoDao.eliminarEquipo(equipo);
                    dispose();
                }
            });
            footer.add(btnEliminar);
        }

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        footer.add(btnCerrar);

        getContentPane().add(footer, BorderLayout.SOUTH);

        cargarDatos();
    }

    /**
     * Genera y muestra el informe Jasper de asistencia por equipo,
     * solicitando al usuario un rango de fechas.
     */
    private void generarInforme() {
        try {
            InputStream input = getClass().getResourceAsStream("/assets/AsistenciaPorEquipo.jasper");
            if (input == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró AsistenciaPorEquipo.jasper en assets/",
                        "Error de Ruta", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/123Hockey", "root", ""
            );
            JasperReport reporte = (JasperReport) JRLoader.loadObject(input);

            JSpinner spDesde = new JSpinner(new SpinnerDateModel());
            JSpinner spHasta = new JSpinner(new SpinnerDateModel());
            spDesde.setEditor(new JSpinner.DateEditor(spDesde, "yyyy-MM-dd"));
            spHasta.setEditor(new JSpinner.DateEditor(spHasta, "yyyy-MM-dd"));
            Object[] msg = {"Fecha desde:", spDesde, "Fecha hasta:", spHasta};
            if (JOptionPane.showConfirmDialog(
                    this, msg, "Seleccione rango de fechas",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                conexion.close();
                return;
            }
            Date dDesde = (Date) spDesde.getValue();
            Date dHasta = (Date) spHasta.getValue();
            Timestamp tsDesde = new Timestamp(dDesde.getTime());
            Timestamp tsHasta = new Timestamp(dHasta.getTime());

            Map<String,Object> parametros = new HashMap<>();
            parametros.put("Equipo", equipo.getId());
            parametros.put("FechaDesde", tsDesde);
            parametros.put("FechaHasta", tsHasta);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);
            dispose();
            JasperViewer.viewReport(jasperPrint, false);
            conexion.close();
        } catch (ClassNotFoundException | SQLException | JRException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al generar informe:\n" + ex.getMessage(),
                    "Error JasperReport", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Alterna entre modo edición y guardado de datos del equipo.
     * Si se sale de edición, persiste los cambios.
     */
    private void toggleEdit() {
        editing = !editing;
        txtNombre.setEditable(editing);
        txtLiga.setEditable(editing);
        txtClub.setEditable(editing);
        txtCiudad.setEditable(editing);
        txtPais.setEditable(editing);
        cbCategoria.setEnabled(editing);
        btnEditSave.setText(editing ? "Guardar cambios" : "Editar datos");
        if (!editing) {
            equipo.setNombre(txtNombre.getText().trim());
            equipo.setLiga(txtLiga.getText().trim());
            equipo.setClub(txtClub.getText().trim());
            equipo.setCiudad(txtCiudad.getText().trim());
            equipo.setPais(txtPais.getText().trim());
            equipo.setCategoria((String)cbCategoria.getSelectedItem());
            try {
                equipoDao.actualizarEquipo(equipo);
                lblNombre.setText(equipo.getNombre());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar cambios:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Carga los jugadores disponibles y convocados al equipo en sus paneles.
     */
    private void cargarDatos() {
        disponiblesPanel.removeAll();
        convocadosPanel.removeAll();

        List<String> cats = Arrays.asList(
                "Prebenjamin","Benjamin","Alevin",
                "Infantil","Juvenil","Junior","Senior"
        );
        int idx = cats.indexOf(equipo.getCategoria());
        Set<String> filterCats = new HashSet<>();
        if (idx >= 0) {
            filterCats.add(cats.get(idx));
            if (idx > 0) filterCats.add(cats.get(idx - 1));
        }

        List<EquipoJugador> rels = ejDao.listarPorEquipo(equipo.getId());
        Set<Integer> convIds = new HashSet<>();
        rels.forEach(r -> convIds.add(r.getUsuario().getId()));

        jiDao.listarTodos().stream()
                .filter(ji -> filterCats.contains(ji.getCategoria()))
                .map(JugadorInfo::getUsuario)
                .filter(u -> !convIds.contains(u.getId()))
                .forEach(u -> disponiblesPanel.add(createJugadorRow(u, false)));

        rels.forEach(r -> convocadosPanel.add(createJugadorRow(r.getUsuario(), true)));

        disponiblesPanel.revalidate();
        convocadosPanel.revalidate();
    }

    /**
     * Crea la fila de un jugador con avatar, nombre, categoría y casilla para convocar.
     *
     * @param u       usuario jugador
     * @param checked true si el jugador está convocado
     * @return panel representando al jugador
     */
    private JPanel createJugadorRow(Usuario u, boolean checked) {
        JPanel p = new JPanel(null);
        p.setPreferredSize(new Dimension(0,50));
        p.setBackground(BG);

        JLabel av = new JLabel(cargarAvatar(u,36,36));
        av.setBounds(5,7,36,36);
        p.add(av);

        JLabel lblN = new JLabel(u.getNombre()+" "+u.getApellidos());
        styleLabel(lblN);
        lblN.setBounds(50,0,200,50);
        p.add(lblN);

        JugadorInfo ji = jiDao.buscarPorUsuarioId(u.getId());
        JLabel lblC = new JLabel(ji!=null?ji.getCategoria():"–");
        styleLabel(lblC);
        lblC.setBounds(260,0,100,50);
        p.add(lblC);

        JCheckBox cb = new JCheckBox();
        cb.setBackground(BG);
        cb.setForeground(FG);
        cb.setSelected(checked);
        cb.setBounds(380,15,20,20);
        cb.setEnabled(usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR);
        cb.addActionListener(e -> {
            if (cb.isSelected()) ejDao.guardar(new EquipoJugador(equipo, u));
            else {
                EquipoJugador rel = ejDao.buscar(equipo.getId(), u.getId());
                if (rel != null) ejDao.eliminar(rel);
            }
            cargarDatos();
        });
        p.add(cb);

        return p;
    }

    /**
     * Envuelve un panel en un JScrollPane con un título dado.
     *
     * @param title      título del borde
     * @param innerPanel panel a envolver
     * @param height     altura preferida del scroll
     * @return panel contenedor con scroll y título
     */
    private JPanel wrapInScroll(String title, JPanel innerPanel, int height) {
        JPanel cont = new JPanel(new BorderLayout());
        cont.setBackground(BG);
        cont.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), FG
        ));
        JScrollPane sp = new JScrollPane(innerPanel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        sp.setPreferredSize(new Dimension(0, height));
        cont.add(sp, BorderLayout.CENTER);
        return cont;
    }

    /**
     * Crea un JLabel con estilo estándar.
     *
     * @param text texto a mostrar
     * @return JLabel configurado
     */
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    /**
     * Crea un JTextField con estilo estándar.
     *
     * @param text texto inicial
     * @return JTextField configurado
     */
    private JTextField createField(String text) {
        JTextField f = new JTextField(text);
        f.setForeground(FG);
        f.setBackground(BG);
        f.setBorder(BorderFactory.createLineBorder(ACCENT));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    /**
     * Aplica estilo de color y fuente a un JLabel.
     *
     * @param lbl JLabel a estilizar
     */
    private void styleLabel(JLabel lbl) {
        lbl.setForeground(FG);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    /**
     * Carga y recorta circularmente la foto del equipo.
     *
     * @param e equipo cuyo avatar se muestra
     * @param w ancho en píxeles
     * @param h alto en píxeles
     * @return ImageIcon circular o icono por defecto
     */
    private ImageIcon cargarAvatarCircular(Equipo e, int w, int h) {
        try {
            byte[] foto = e.getFotoEquipo();
            if (foto!=null && foto.length>0) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(foto));
                BufferedImage dst = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = dst.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new Ellipse2D.Float(0,0,w,h));
                g2.drawImage(src,0,0,w,h,null);
                g2.dispose();
                return new ImageIcon(dst);
            }
        } catch(Exception ignored){}
        return loadIcon("user_default.png", w, h);
    }

    /**
     * Carga y recorta circularmente el avatar de un usuario.
     *
     * @param u usuario cuyo avatar se muestra
     * @param w ancho en píxeles
     * @param h alto en píxeles
     * @return ImageIcon circular o icono por defecto
     */
    private ImageIcon cargarAvatar(Usuario u, int w, int h) {
        try {
            byte[] f = u.getFotoUsuario();
            if (f!=null && f.length>0) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(f));
                BufferedImage dst = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = dst.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new Ellipse2D.Float(0,0,w,h));
                g2.drawImage(src,0,0,w,h,null);
                g2.dispose();
                return new ImageIcon(dst);
            }
        } catch(Exception ignored){}
        return loadIcon("user_default.png", w, h);
    }

    /**
     * Carga un recurso de icono y lo escala.
     *
     * @param name nombre del recurso en assets
     * @param w    ancho en píxeles
     * @param h    alto en píxeles
     * @return ImageIcon escalado o transparente si no se encuentra
     */
    private ImageIcon loadIcon(String name, int w, int h) {
        URL res = getClass().getClassLoader().getResource("assets/"+name);
        Image img = (res != null)
                ? new ImageIcon(res).getImage()
                : new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }
}
