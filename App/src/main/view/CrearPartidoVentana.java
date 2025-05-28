package main.view;

import main.dao.PartidoDAO;
import main.dao.EquipoDAO;
import main.model.Partido;
import main.model.Equipo;
import main.model.Usuario;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Diálogo para crear un nuevo partido, proporcionando campos para equipo,
 * rival, lugar, fecha, horas de partido y quedada, tipo de partido e información adicional.
 * Persiste el partido en la base de datos y notifica mediante un callback.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class CrearPartidoVentana extends JDialog {

    private static final Color BG     = new Color(18, 18, 18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49, 109, 233);

    private final Runnable onCreate;
    private final Usuario usuarioLogado;

    private JComboBox<Equipo>              cbEquipo;
    private JTextField                     txtRival;
    private JTextField                     txtLugar;
    private JTextField                     txtFecha;
    private JTextField                     txtHoraPartido;
    private JTextField                     txtHoraQuedada;
    private JComboBox<Partido.TipoPartido> cbTipo;
    private JTextArea                      txtInfo;
    private JButton                        btnGuardar;
    private JButton                        btnCancelar;

    private final PartidoDAO parDao = new PartidoDAO();
    private final EquipoDAO   eqDao  = new EquipoDAO();

    private final SimpleDateFormat dfFecha = new SimpleDateFormat("d/MM/yyyy");
    private final SimpleDateFormat dfHora  = new SimpleDateFormat("HH:mm");

    /**
     * Construye el diálogo para crear un partido.
     *
     * @param parent       ventana padre sobre la que se mostrará el diálogo
     * @param usuarioLogado usuario autenticado que crea el partido
     * @param onCreate     acción a ejecutar tras guardar el partido (por ejemplo, recargar vista)
     */
    public CrearPartidoVentana(Frame parent, Usuario usuarioLogado, Runnable onCreate) {
        super(parent, "Crear Partido", true);
        this.usuarioLogado = usuarioLogado;
        this.onCreate      = onCreate;
        initUI();
        setVisible(true);
    }

    /**
     * Inicializa la interfaz de usuario: icono, tamaño, formulario de datos
     * y botones de acción para guardar o cancelar.
     */
    private void initUI() {
        Image appIcon = loadIcon("logoSinFondo.png", 32, 32).getImage();
        setIconImage(appIcon);

        setSize(500, 500);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(BG);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Datos del partido",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                FG
        ));

        form.add(label("Equipo:"));
        List<Equipo> equipos = eqDao.listarTodos();
        cbEquipo = new JComboBox<>(equipos.toArray(new Equipo[0]));
        cbEquipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                if (value instanceof Equipo) {
                    setText(((Equipo) value).getNombre());
                }
                return this;
            }
        });
        form.add(cbEquipo);

        form.add(label("Rival:"));
        txtRival = createField("");
        form.add(txtRival);

        form.add(label("Lugar:"));
        txtLugar = createField("");
        form.add(txtLugar);

        form.add(label("Fecha (d/MM/yyyy):"));
        txtFecha = createField(dfFecha.format(new Date()));
        form.add(txtFecha);

        form.add(label("Hora partido (HH:mm):"));
        txtHoraPartido = createField(dfHora.format(new Date()));
        form.add(txtHoraPartido);

        form.add(label("Hora quedada (HH:mm):"));
        txtHoraQuedada = createField(dfHora.format(new Date()));
        form.add(txtHoraQuedada);

        form.add(label("Tipo de partido:"));
        cbTipo = new JComboBox<>(Partido.TipoPartido.values());
        form.add(cbTipo);

        form.add(label("Info adicional:"));
        txtInfo = new JTextArea(3, 20);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBackground(BG);
        txtInfo.setForeground(FG);
        txtInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane spInfo = new JScrollPane(txtInfo);
        spInfo.setBorder(BorderFactory.createLineBorder(ACCENT));
        form.add(spInfo);

        getContentPane().add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(BG);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setForeground(FG);
        btnGuardar.setBackground(ACCENT);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnGuardar.addActionListener(e -> onSave());
        footer.add(btnGuardar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setForeground(FG);
        btnCancelar.setBackground(BG);
        btnCancelar.setOpaque(true);
        btnCancelar.setBorder(BorderFactory.createLineBorder(ACCENT));
        btnCancelar.addActionListener(e -> dispose());
        footer.add(btnCancelar);

        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    /**
     * Lee los valores del formulario, valida formatos de fecha y hora,
     * crea un objeto Partido y lo guarda en la base de datos. Luego ejecuta el callback.
     */
    private void onSave() {
        Equipo eq       = (Equipo) cbEquipo.getSelectedItem();
        String rival    = txtRival.getText().trim();
        String lugar    = txtLugar.getText().trim();
        String sFecha   = txtFecha.getText().trim();
        String sHora    = txtHoraPartido.getText().trim();
        String sQuedada = txtHoraQuedada.getText().trim();
        Partido.TipoPartido tipo = (Partido.TipoPartido) cbTipo.getSelectedItem();
        String info     = txtInfo.getText().trim();

        Date fechaTime;
        Date horaQuedada;
        try {
            Date df = dfFecha.parse(sFecha);
            Date hp = dfHora.parse(sHora);
            fechaTime = new Date(
                    df.getYear(), df.getMonth(), df.getDate(),
                    hp.getHours(), hp.getMinutes()
            );
            Date hq = dfHora.parse(sQuedada);
            horaQuedada = new Date(
                    df.getYear(), df.getMonth(), df.getDate(),
                    hq.getHours(), hq.getMinutes()
            );
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Formato de fecha u hora inválido",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Partido p = new Partido();
        p.setEquipo(eq);
        p.setRival(rival);
        p.setLugar(lugar);
        p.setFecha(fechaTime);
        p.setTipoPartido(tipo);
        p.setInfo(info);
        p.setHoraQuedada(horaQuedada);

        parDao.guardarPartido(p);

        onCreate.run();
        dispose();
    }

    /**
     * Crea un JLabel con el texto especificado y estilo predeterminado.
     *
     * @param text texto a mostrar en la etiqueta
     * @return JLabel configurado con color y fuente estándar
     */
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    /**
     * Crea un JTextField con texto inicial y estilo predeterminado.
     *
     * @param text texto inicial a mostrar en el campo
     * @return JTextField configurado con color de fondo, borde y fuente estándar
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
     * Carga un icono desde los recursos empaquetados y lo escala al tamaño indicado.
     *
     * @param name nombre del archivo dentro de assets (por ejemplo "logoSinFondo.png")
     * @param w    ancho deseado del icono en píxeles
     * @param h    alto deseado del icono en píxeles
     * @return ImageIcon escalado, o un icono transparente si no se encuentra el recurso
     */
    private ImageIcon loadIcon(String name, int w, int h) {
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u != null)
                ? new ImageIcon(u).getImage()
                : new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
}
