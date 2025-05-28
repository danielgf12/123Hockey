package main.view;

import main.dao.EntrenamientoDAO;
import main.dao.EquipoDAO;
import main.model.Entrenamiento;
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
 * Diálogo para crear un nuevo entrenamiento y guardarlo en la base de datos.
 * Ofrece campos para seleccionar equipo, fecha, hora, ubicación, repetición, tipo y observaciones.
 * Al guardar, ejecuta la acción de recarga proporcionada.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class CrearEntrenamientoVentana extends JDialog {

    private static final Color BG     = new Color(18, 18, 18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49, 109, 233);

    private final Runnable onCreate;
    private final Usuario usuarioLogado;

    private JComboBox<Equipo>                          cbEquipo;
    private JTextField                                 txtFecha;
    private JTextField                                 txtHora;
    private JTextField                                 txtUbicacion;
    private JComboBox<Entrenamiento.Repetir>           cbRepetir;
    private JComboBox<Entrenamiento.TipoEntrenamiento> cbTipo;
    private JTextArea                                  txtObservaciones;
    private JButton                                    btnGuardar;
    private JButton                                    btnCancelar;

    private final EntrenamientoDAO entDao = new EntrenamientoDAO();
    private final EquipoDAO        eqDao  = new EquipoDAO();

    private final SimpleDateFormat dfFecha = new SimpleDateFormat("d/MM/yyyy");
    private final SimpleDateFormat dfHora  = new SimpleDateFormat("HH:mm");

    /**
     * Construye el diálogo para crear un entrenamiento.
     *
     * @param parent ventana padre sobre la que se mostrará el diálogo
     * @param usuarioLogado usuario actualmente autenticado
     * @param onCreate acción a ejecutar tras guardar el entrenamiento (para recargar la vista)
     */
    public CrearEntrenamientoVentana(Frame parent, Usuario usuarioLogado, Runnable onCreate) {
        super(parent, "Crear Entrenamiento", true);
        this.usuarioLogado = usuarioLogado;
        this.onCreate      = onCreate;
        initUI();
    }

    /**
     * Inicializa la interfaz de usuario, creando el formulario de datos
     * y los botones de guardar o cancelar.
     */
    private void initUI() {
        Image appIcon = loadIcon("logoSinFondo.png",32,32).getImage();
        setIconImage(appIcon);

        setSize(450, 450);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setBackground(BG);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Datos del entrenamiento",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                FG
        ));

        form.add(label("Equipo:"));
        List<Equipo> equipos = eqDao.listarTodos();
        cbEquipo = new JComboBox<>(equipos.toArray(new Equipo[0]));
        cbEquipo.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                if(value instanceof Equipo) {
                    setText(((Equipo)value).getNombre());
                }
                return this;
            }
        });
        form.add(cbEquipo);

        form.add(label("Fecha (d/MM/yyyy):"));
        txtFecha = createField(dfFecha.format(new Date()));
        form.add(txtFecha);

        form.add(label("Hora (HH:mm):"));
        txtHora = createField(dfHora.format(new Date()));
        form.add(txtHora);

        form.add(label("Ubicación:"));
        txtUbicacion = createField("");
        form.add(txtUbicacion);

        form.add(label("Repetir:"));
        cbRepetir = new JComboBox<>(Entrenamiento.Repetir.values());
        form.add(cbRepetir);

        form.add(label("Tipo:"));
        cbTipo = new JComboBox<>(Entrenamiento.TipoEntrenamiento.values());
        form.add(cbTipo);

        form.add(label("Observaciones:"));
        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBackground(BG);
        txtObservaciones.setForeground(FG);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane spObs = new JScrollPane(txtObservaciones);
        spObs.setBorder(BorderFactory.createLineBorder(ACCENT));
        form.add(spObs);

        getContentPane().add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setForeground(FG);
        btnGuardar.setBackground(ACCENT);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
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
     * Maneja la acción de guardar el nuevo entrenamiento.
     * Extrae valores del formulario, valida formato de fecha y hora,
     * crea el objeto Entrenamiento y lo persiste. Luego ejecuta onCreate.
     */
    private void onSave() {
        Equipo eq      = (Equipo) cbEquipo.getSelectedItem();
        String sFecha  = txtFecha.getText().trim();
        String sHora   = txtHora.getText().trim();
        String ubic    = txtUbicacion.getText().trim();
        Entrenamiento.Repetir rep = (Entrenamiento.Repetir) cbRepetir.getSelectedItem();
        Entrenamiento.TipoEntrenamiento tipo = (Entrenamiento.TipoEntrenamiento) cbTipo.getSelectedItem();
        String obs     = txtObservaciones.getText().trim();

        Date fechaTime;
        try {
            Date f = dfFecha.parse(sFecha);
            Date h = dfHora.parse(sHora);
            fechaTime = new Date(
                    f.getYear(), f.getMonth(), f.getDate(),
                    h.getHours(), h.getMinutes()
            );
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha o hora inválido",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Entrenamiento ent = new Entrenamiento();
        ent.setEquipo(eq);
        ent.setFecha(fechaTime);
        ent.setUbicacion(ubic);
        ent.setRepetir(rep);
        ent.setTipoEntrenamiento(tipo);
        ent.setObservaciones(obs);

        entDao.guardarEntrenamiento(ent);
        onCreate.run();
        dispose();
    }

    /**
     * Crea un JLabel con texto y estilo por defecto.
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
     * Crea un JTextField con texto inicial y estilo por defecto.
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
     * Carga un icono desde recursos empaquetados y lo escala.
     *
     * @param name nombre del recurso dentro de assets (p. ej. "logoSinFondo.png")
     * @param w ancho deseado del icono en píxeles
     * @param h alto deseado del icono en píxeles
     * @return ImageIcon escalado o un icono vacío si no se encuentra el recurso
     */
    private ImageIcon loadIcon(String name, int w, int h){
        URL u = getClass().getClassLoader().getResource("assets/"+name);
        Image img = (u!=null)
                ? new ImageIcon(u).getImage()
                : new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }
}
