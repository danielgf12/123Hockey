package main.view;

import main.dao.*;
import main.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.List;

public class FichaEntrenamientoVentana extends JFrame {
    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,109,233);

    private final Usuario usuarioLogado;
    private final Entrenamiento entrenamiento;
    private final EntrenamientoDAO entDao = new EntrenamientoDAO();
    private final AsistenciaDAO    asiDao = new AsistenciaDAO();
    private final EquipoJugadorDAO ejDao  = new EquipoJugadorDAO();
    private final EquipoDAO        eqDao  = new EquipoDAO();
    private final JugadorInfoDAO infoDao = new JugadorInfoDAO();


    private JComboBox<Equipo>                          cbEquipo;
    private JTextField                                 txtFecha;
    private JTextField                                 txtHora;
    private JTextField                                 txtUbicacion;
    private JComboBox<Entrenamiento.Repetir>           cbRepetir;
    private JComboBox<Entrenamiento.TipoEntrenamiento> cbTipo;
    private JTextArea                                  txtObservaciones;

    private JButton btnEditSave;
    private boolean editing = false;

    private final SimpleDateFormat dfDate = new SimpleDateFormat("d/MM/yyyy");
    private final SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");

    public FichaEntrenamientoVentana(Usuario usuarioLogado, Entrenamiento entrenamiento) {
        super("Ficha Entrenamiento");
        this.usuarioLogado = usuarioLogado;
        this.entrenamiento = entrenamiento;
        initUI();
    }

    private void initUI() {
        // Ventana
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(10,20,10,20));
        JLabel lblTitle = new JLabel("Ficha Entrenamiento", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(FG);
        header.add(lblTitle, BorderLayout.CENTER);
        getContentPane().add(header, BorderLayout.NORTH);

        // Centro: formulario
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8,8,8,8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Equipo
        gbc.gridx = 0; gbc.gridy = 0;
        center.add(createLabel("Equipo:"), gbc);
        gbc.gridx = 1;
        List<Equipo> equipos = eqDao.listarTodos();
        cbEquipo = new JComboBox<>(equipos.toArray(new Equipo[0]));
        cbEquipo.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                if (value instanceof Equipo) setText(((Equipo)value).getNombre());
                return this;
            }
        });
        // Preseleccionar por ID
        int idActual = entrenamiento.getEquipo().getId();
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).getId() == idActual) {
                cbEquipo.setSelectedIndex(i);
                break;
            }
        }
        center.add(cbEquipo, gbc);

        // Fecha
        gbc.gridx = 0; gbc.gridy = 1;
        center.add(createLabel("Fecha (d/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        txtFecha = new JTextField(dfDate.format(entrenamiento.getFecha()));
        center.add(txtFecha, gbc);

        // Hora
        gbc.gridx = 0; gbc.gridy = 2;
        center.add(createLabel("Hora (HH:mm):"), gbc);
        gbc.gridx = 1;
        txtHora = new JTextField(dfTime.format(entrenamiento.getFecha()));
        center.add(txtHora, gbc);

        // Ubicación
        gbc.gridx = 0; gbc.gridy = 3;
        center.add(createLabel("Ubicación:"), gbc);
        gbc.gridx = 1;
        txtUbicacion = new JTextField(entrenamiento.getUbicacion());
        center.add(txtUbicacion, gbc);

        // Repetir
        gbc.gridx = 0; gbc.gridy = 4;
        center.add(createLabel("Repetir:"), gbc);
        gbc.gridx = 1;
        cbRepetir = new JComboBox<>(Entrenamiento.Repetir.values());
        cbRepetir.setSelectedItem(entrenamiento.getRepetir());
        center.add(cbRepetir, gbc);

        // Tipo
        gbc.gridx = 0; gbc.gridy = 5;
        center.add(createLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cbTipo = new JComboBox<>(Entrenamiento.TipoEntrenamiento.values());
        cbTipo.setSelectedItem(entrenamiento.getTipoEntrenamiento());
        center.add(cbTipo, gbc);

        // Observaciones
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        center.add(createLabel("Observaciones:"), gbc);
        gbc.gridy = 7; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        txtObservaciones = new JTextArea(entrenamiento.getObservaciones());
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBackground(BG);
        txtObservaciones.setForeground(FG);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane spObs = new JScrollPane(txtObservaciones);
        spObs.setBorder(BorderFactory.createLineBorder(ACCENT));
        center.add(spObs, gbc);

        getContentPane().add(center, BorderLayout.CENTER);

        // Derecha: lista de asistencia
        java.util.List<Asistencia> listaAsist = asiDao.listarPorEntrenamiento(entrenamiento.getId());
        Map<Integer,Asistencia> mapaAsist = new HashMap<>();
        for (Asistencia a : listaAsist) {
            mapaAsist.put(a.getUsuario().getId(), a);
        }

        JPanel asistenciaPanel = new JPanel();
        asistenciaPanel.setBackground(BG);
        asistenciaPanel.setLayout(new BoxLayout(asistenciaPanel, BoxLayout.Y_AXIS));
        asistenciaPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Asistencia Obligatoria",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                FG
        ));

        List<EquipoJugador> convocados = ejDao.listarPorEquipo(entrenamiento.getEquipo().getId());
        for (EquipoJugador ej : convocados) {
            Usuario jug = ej.getUsuario();
            Asistencia a = mapaAsist.get(jug.getId());
            final Asistencia[] reference = new Asistencia[]{ a };
            boolean asistio = (a != null && a.isAsistencia());

            JCheckBox cb = new JCheckBox(jug.getNombre()+" "+jug.getApellidos(), asistio);
            cb.setBackground(BG);
            cb.setForeground(FG);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cb.setEnabled(usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR);

            cb.addActionListener(evt -> {
                boolean sel = cb.isSelected();
                if (reference[0] == null) {
                    reference[0] = new Asistencia(entrenamiento, jug, sel);
                    asiDao.guardarAsistencia(reference[0]);
                } else {
                    reference[0].setAsistencia(sel);
                    asiDao.actualizarAsistencia(reference[0]);
                }
                // ——> Aquí actualizamos jugador_info:
                if (sel) {
                    JugadorInfo info = infoDao.buscarPorUsuarioId(jug.getId());
                    if (info != null) {
                        info.setAsistenciaObligatoria(info.getAsistenciaObligatoria() + 1);
                        infoDao.actualizarJugadorInfo(info);
                    }
                } else {
                    JugadorInfo info = infoDao.buscarPorUsuarioId(jug.getId());
                    if (info != null && info.getAsistenciaObligatoria() > 0) {
                    info.setAsistenciaObligatoria(info.getAsistenciaObligatoria() - 1);
                    infoDao.actualizarJugadorInfo(info);
                    }
                }
            });


            asistenciaPanel.add(cb);
        }

        JScrollPane scrollAs = new JScrollPane(asistenciaPanel);
        scrollAs.setPreferredSize(new Dimension(300,0));
        scrollAs.setBorder(null);
        getContentPane().add(scrollAs, BorderLayout.EAST);

        // Footer: editar/guardar + eliminar o cerrar
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);

        btnEditSave = new JButton("Editar datos");
        styleButton(btnEditSave);
        btnEditSave.setEnabled(usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR);
        btnEditSave.addActionListener(e -> toggleEditSave());
        footer.add(btnEditSave);

        if (usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR) {
            JButton btnEliminar = new JButton("Eliminar entrenamiento");
            styleButton(btnEliminar);
            btnEliminar.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this,
                        "¿Seguro que deseas eliminar este entrenamiento?",
                        "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    entDao.eliminarEntrenamiento(entrenamiento);
                    dispose();
                }
            });
            footer.add(btnEliminar);
        } else {
            JButton btnCerrar = new JButton("Cerrar");
            styleButton(btnCerrar);
            btnCerrar.addActionListener(e -> dispose());
            footer.add(btnCerrar);
        }

        getContentPane().add(footer, BorderLayout.SOUTH);

        // Inicialmente deshabilitados
        setFieldsEditable(false);
        setVisible(true);
    }

    private void toggleEditSave() {
        editing = !editing;
        setFieldsEditable(editing);
        btnEditSave.setText(editing ? "Guardar cambios" : "Editar datos");
        if (!editing) {
            // Guardar cambios
            try {
                Date d = dfDate.parse(txtFecha.getText().trim());
                Date t = dfTime.parse(txtHora.getText().trim());
                Date combinado = new Date(
                        d.getYear(), d.getMonth(), d.getDate(),
                        t.getHours(), t.getMinutes()
                );
                entrenamiento.setFecha(combinado);
            } catch(ParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Formato de fecha u hora inválido",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            entrenamiento.setEquipo((Equipo)cbEquipo.getSelectedItem());
            entrenamiento.setUbicacion(txtUbicacion.getText().trim());
            entrenamiento.setRepetir((Entrenamiento.Repetir)cbRepetir.getSelectedItem());
            entrenamiento.setTipoEntrenamiento((Entrenamiento.TipoEntrenamiento)cbTipo.getSelectedItem());
            entrenamiento.setObservaciones(txtObservaciones.getText().trim());

            entDao.actualizarEntrenamiento(entrenamiento);
            JOptionPane.showMessageDialog(this,
                    "Cambios guardados", "OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setFieldsEditable(boolean e) {
        cbEquipo.setEnabled(e);
        txtFecha.setEditable(e);
        txtHora.setEditable(e);
        txtUbicacion.setEditable(e);
        cbRepetir.setEnabled(e);
        cbTipo.setEnabled(e);
        txtObservaciones.setEditable(e);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(FG);
        b.setBackground(ACCENT);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
    }

    private ImageIcon loadIcon(String name,int w,int h){
        URL u = getClass().getClassLoader().getResource("assets/"+name);
        Image img = (u!=null)
                ? new ImageIcon(u).getImage()
                : new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }
}
