package main.view;

import main.dao.EquipoJugadorDAO;
import main.dao.EquipoDAO;
import main.dao.PartidoDAO;
import main.model.Equipo;
import main.model.EquipoJugador;
import main.model.Partido;
import main.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Ventana que muestra la ficha detallada de un partido, permitiendo ver y editar
 * sus datos básicos, gestionar alineación de jugadores y eliminar el partido si
 * el usuario tiene los permisos adecuados.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class FichaPartidoVentana extends JFrame {
    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,109,233);

    private final Usuario usuarioLogado;
    private final Partido partido;
    private final PartidoDAO partidoDao       = new PartidoDAO();
    private final EquipoDAO equipoDao         = new EquipoDAO();
    private final EquipoJugadorDAO ejDao      = new EquipoJugadorDAO();

    private JComboBox<Equipo>               cbEquipo;
    private JTextField                      txtRival;
    private JTextField                      txtLugar;
    private JTextField                      txtFechaHora;
    private JTextField                      txtHoraQuedada;
    private JComboBox<Partido.TipoPartido>  cbTipo;
    private JTextArea                       txtInfo;

    private JButton    btnEditSave;
    private boolean    editing = false;

    private final SimpleDateFormat dfDateTime = new SimpleDateFormat("d/MM/yyyy HH:mm");
    private final SimpleDateFormat dfTime     = new SimpleDateFormat("HH:mm");

    /**
     * Construye la ventana de ficha de partido para el usuario y partido indicados.
     *
     * @param usuarioLogado usuario autenticado que accede a la ficha
     * @param partido       partido cuyos datos se mostrarán
     */
    public FichaPartidoVentana(Usuario usuarioLogado, Partido partido) {
        super("Ficha de Partido");
        this.usuarioLogado = usuarioLogado;
        this.partido       = partido;
        initUI();
    }

    /**
     * Inicializa la interfaz de usuario: icono, tamaño, disposición de componentes
     * para datos del partido, alineación de jugadores y botones de acción.
     */
    private void initUI() {
        Image appIcon = loadIcon("logoSinFondo.png",32,32).getImage();
        setIconImage(appIcon);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(10,20,10,20));
        JLabel lblTitle = new JLabel("Ficha de Partido", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(FG);
        header.add(lblTitle, BorderLayout.CENTER);
        getContentPane().add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8,8,8,8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx=0; gbc.gridy=0;
        center.add(createLabel("Equipo:"), gbc);
        gbc.gridx=1;
        List<Equipo> equipos = equipoDao.listarTodos();
        cbEquipo = new JComboBox<>(equipos.toArray(new Equipo[0]));
        cbEquipo.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list,value,idx,sel,foc);
                if (value instanceof Equipo) setText(((Equipo)value).getNombre());
                return this;
            }
        });
        for (int i=0; i<equipos.size(); i++) {
            if (equipos.get(i).getId() == partido.getEquipo().getId()) {
                cbEquipo.setSelectedIndex(i);
                break;
            }
        }
        center.add(cbEquipo, gbc);

        gbc.gridx=0; gbc.gridy=1;
        center.add(createLabel("Rival:"), gbc);
        gbc.gridx=1;
        txtRival = new JTextField(partido.getRival());
        center.add(txtRival, gbc);

        gbc.gridx=0; gbc.gridy=2;
        center.add(createLabel("Lugar:"), gbc);
        gbc.gridx=1;
        txtLugar = new JTextField(partido.getLugar());
        center.add(txtLugar, gbc);

        gbc.gridx=0; gbc.gridy=3;
        center.add(createLabel("Fecha / Hora:"), gbc);
        gbc.gridx=1;
        txtFechaHora = new JTextField(dfDateTime.format(partido.getFecha()));
        center.add(txtFechaHora, gbc);

        gbc.gridx=0; gbc.gridy=4;
        center.add(createLabel("Hora de quedada:"), gbc);
        gbc.gridx=1;
        txtHoraQuedada = new JTextField(
                partido.getHoraQuedada()!=null
                        ? dfTime.format(partido.getHoraQuedada())
                        : ""
        );
        center.add(txtHoraQuedada, gbc);

        gbc.gridx=0; gbc.gridy=5;
        center.add(createLabel("Categoría:"), gbc);
        gbc.gridx=1;
        cbTipo = new JComboBox<>(Partido.TipoPartido.values());
        cbTipo.setSelectedItem(partido.getTipoPartido());
        center.add(cbTipo, gbc);

        gbc.gridx=0; gbc.gridy=6; gbc.gridwidth=2;
        center.add(createLabel("Info de interés:"), gbc);
        gbc.gridy=7; gbc.fill=GridBagConstraints.BOTH; gbc.weighty=1.0;
        txtInfo = new JTextArea(partido.getInfo());
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(BG);
        txtInfo.setForeground(FG);
        txtInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane spInfo = new JScrollPane(txtInfo);
        spInfo.setBorder(BorderFactory.createLineBorder(ACCENT));
        center.add(spInfo, gbc);

        getContentPane().add(center, BorderLayout.CENTER);

        JPanel alineacion = new JPanel();
        alineacion.setBackground(BG);
        alineacion.setLayout(new BoxLayout(alineacion, BoxLayout.Y_AXIS));
        alineacion.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Jugadores alineados",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                FG
        ));
        List<EquipoJugador> lista = ejDao.listarPorEquipo(partido.getEquipo().getId());
        for (EquipoJugador ej : lista) {
            JLabel lbl = new JLabel(ej.getUsuario().getNombre() + " " + ej.getUsuario().getApellidos());
            lbl.setForeground(FG);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            alineacion.add(lbl);
        }
        JScrollPane spAline = new JScrollPane(alineacion);
        spAline.setPreferredSize(new Dimension(250,0));
        spAline.setBorder(null);
        getContentPane().add(spAline, BorderLayout.EAST);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);

        btnEditSave = new JButton("Editar datos");
        styleButton(btnEditSave);
        boolean canEdit = usuarioLogado.getRol()==Usuario.Rol.ENTRENADOR
                || usuarioLogado.getRol()==Usuario.Rol.DELEGADO;
        btnEditSave.setEnabled(canEdit);
        btnEditSave.addActionListener(e -> toggleEditSave());
        footer.add(btnEditSave);

        JButton btnEliminar = new JButton("Eliminar partido");
        styleButton(btnEliminar);
        btnEliminar.setEnabled(canEdit);
        btnEliminar.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "¿Seguro que deseas eliminar este partido?",
                    "Confirmar", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                partidoDao.eliminarPartido(partido);
                dispose();
            }
        });
        footer.add(btnEliminar);

        getContentPane().add(footer, BorderLayout.SOUTH);

        setFieldsEditable(false);
        setVisible(true);
    }

    /**
     * Alterna entre modo edición y guardado de datos. Si se sale de edición,
     * valida y persiste los cambios en el objeto Partido.
     */
    private void toggleEditSave() {
        editing = !editing;
        setFieldsEditable(editing);
        btnEditSave.setText(editing ? "Guardar cambios" : "Editar datos");
        if (!editing) {
            try {
                Date dt = dfDateTime.parse(txtFechaHora.getText().trim());
                partido.setFecha(dt);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Formato de fecha/hora inválido",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Date hq = dfTime.parse(txtHoraQuedada.getText().trim());
                partido.setHoraQuedada(hq);
            } catch (ParseException ex) {
                partido.setHoraQuedada(null);
            }
            partido.setEquipo((Equipo)cbEquipo.getSelectedItem());
            partido.setRival(txtRival.getText().trim());
            partido.setLugar(txtLugar.getText().trim());
            partido.setTipoPartido((Partido.TipoPartido)cbTipo.getSelectedItem());
            partido.setInfo(txtInfo.getText().trim());

            partidoDao.actualizarPartido(partido);
            JOptionPane.showMessageDialog(this,
                    "Cambios guardados", "OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Habilita o deshabilita los campos de edición según el parámetro.
     *
     * @param e true para permitir edición, false para solo lectura
     */
    private void setFieldsEditable(boolean e) {
        cbEquipo.setEnabled(e);
        txtRival.setEditable(e);
        txtLugar.setEditable(e);
        txtFechaHora.setEditable(e);
        txtHoraQuedada.setEditable(e);
        cbTipo.setEnabled(e);
        txtInfo.setEditable(e);
    }

    /**
     * Crea un JLabel con estilo estándar para etiquetas.
     *
     * @param text texto a mostrar
     * @return JLabel configurado con color y fuente
     */
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    /**
     * Aplica estilo coherente a un JButton.
     *
     * @param b botón a estilizar
     */
    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(FG);
        b.setBackground(ACCENT);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
    }

    /**
     * Carga un recurso de imagen y lo escala al tamaño dado.
     *
     * @param name nombre del recurso en assets
     * @param w    ancho en píxeles
     * @param h    alto en píxeles
     * @return ImageIcon escalado o transparente si no existe
     */
    private ImageIcon loadIcon(String name, int w, int h){
        URL u = getClass().getClassLoader().getResource("assets/"+name);
        Image img = (u!=null)
                ? new ImageIcon(u).getImage()
                : new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img.getScaledInstance(
                w,h,Image.SCALE_SMOOTH
        ));
    }
}
