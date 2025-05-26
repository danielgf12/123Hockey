package main.view;

import main.dao.EquipoDAO;
import main.dao.EquipoJugadorDAO;
import main.dao.JugadorInfoDAO;
import main.model.Equipo;
import main.model.EquipoJugador;
import main.model.JugadorInfo;
import main.model.Usuario;

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
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class FichaEquipoVentana extends JDialog {
    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,141,225);

    private final Usuario usuarioLogado;
    private final Equipo equipo;
    private final EquipoDAO equipoDao     = new EquipoDAO();
    private final EquipoJugadorDAO ejDao  = new EquipoJugadorDAO();
    private final JugadorInfoDAO jiDao    = new JugadorInfoDAO();

    // campos para edición inline
    private boolean editing = false;
    private JButton btnEditSave;
    private JTextField txtNombre, txtLiga, txtClub, txtCiudad, txtPais;
    private JComboBox<String> cbCategoria;
    private JLabel lblNombre; // para actualizar al guardar

    private final JPanel disponiblesPanel = new JPanel();
    private final JPanel convocadosPanel  = new JPanel();

    public FichaEquipoVentana(Usuario usuarioLogado, Equipo equipo) {
        super((Frame)null, "Ficha de equipo", true);
        this.usuarioLogado = usuarioLogado;
        this.equipo        = equipo;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setSize(900, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout(10,10));

        // --- Header con nombre y foto ---
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

        // --- Panel central con datos del equipo arriba ---
        JPanel mainPanel = new JPanel(new BorderLayout(0,10));
        mainPanel.setBackground(BG);

        // DATOS INLINE: sustituimos JLabels por campos deshabilitados
        JPanel info = new JPanel(new GridLayout(0,2,8,8));
        info.setBackground(BG);
        info.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Datos del equipo", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), FG
        ));
        // Nombre
        info.add(createLabel("Nombre:"));
        txtNombre = createField(equipo.getNombre());
        txtNombre.setEditable(false);
        info.add(txtNombre);

        // Liga
        info.add(createLabel("Liga:"));
        txtLiga = createField(equipo.getLiga());
        txtLiga.setEditable(false);
        info.add(txtLiga);

        // Categoría
        info.add(createLabel("Categoría:"));
        cbCategoria = new JComboBox<>(new String[]{
                "Prebenjamin","Benjamin","Alevin",
                "Infantil","Juvenil","Junior","Senior"
        });
        cbCategoria.setSelectedItem(equipo.getCategoria());
        cbCategoria.setEnabled(false);
        info.add(cbCategoria);

        // Club
        info.add(createLabel("Club:"));
        txtClub = createField(equipo.getClub());
        txtClub.setEditable(false);
        info.add(txtClub);

        // Ciudad
        info.add(createLabel("Ciudad:"));
        txtCiudad = createField(equipo.getCiudad());
        txtCiudad.setEditable(false);
        info.add(txtCiudad);

        // País
        info.add(createLabel("País:"));
        txtPais = createField(equipo.getPais());
        txtPais.setEditable(false);
        info.add(txtPais);

        mainPanel.add(info, BorderLayout.NORTH);

        // Listas: disponibles y convocados (igual que antes)
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

        // --- Pie: botones ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);

        boolean coach = usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR;
        if (coach) {
            btnEditSave = new JButton("Editar datos");
            btnEditSave.addActionListener(e -> toggleEdit());
            footer.add(btnEditSave);

            JButton btnEliminar = new JButton("Eliminar equipo");
            btnEliminar.addActionListener(e -> {
                int r = JOptionPane.showConfirmDialog(this,
                        "¿Eliminar equipo \""+equipo.getNombre()+"\"?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    // borra todas las relaciones antes
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

    private void toggleEdit() {
        editing = !editing;
        // habilita/deshabilita edición
        txtNombre.setEditable(editing);
        txtLiga.setEditable(editing);
        txtClub.setEditable(editing);
        txtCiudad.setEditable(editing);
        txtPais.setEditable(editing);
        cbCategoria.setEnabled(editing);

        btnEditSave.setText(editing ? "Guardar cambios" : "Editar datos");

        if (!editing) {
            // al guardar, vuelca valores y persiste
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

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JTextField createField(String text) {
        JTextField f = new JTextField(text);
        f.setForeground(FG);
        f.setBackground(BG);
        f.setBorder(BorderFactory.createLineBorder(ACCENT));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    private void styleLabel(JLabel lbl) {
        lbl.setForeground(FG);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

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

    private ImageIcon loadIcon(String name, int w, int h) {
        URL res = getClass().getClassLoader().getResource("assets/"+name);
        Image img = (res != null)
                ? new ImageIcon(res).getImage()
                : new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }
}