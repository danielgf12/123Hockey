package main.view;

import main.dao.JugadorInfoDAO;
import main.dao.UsuarioDAO;
import main.model.JugadorInfo;
import main.model.Usuario;
import main.util.HashUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Diálogo para la creación de un nuevo usuario, incluyendo su información de jugador si corresponde.
 * Permite ingresar datos personales, credenciales y opcionalmente una foto de perfil.
 * Valida y persiste los datos en la base de datos, mostrando mensajes de error o éxito.
 * Al finalizar correctamente, invoca un callback para actualizar la vista principal.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class CrearUsuarioVentana extends JDialog {
    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,141,225);
    private static final int   MAX_PHOTO_BYTES = 1 * 1024 * 1024;

    private final Runnable onSuccess;
    private final Usuario newUser = new Usuario();
    private final JugadorInfo newInfo = new JugadorInfo();

    private JLabel lblFoto;
    private JTextField txtNombre, txtApellidos, txtEmail;
    private JComboBox<String> cbCategoria;
    private JTextField txtNacimiento, txtTelefono, txtTelefonoPadre;
    private JComboBox<Usuario.Rol> cbRol;
    private JComboBox<JugadorInfo.Posicion> cbPosicion;
    private JTextArea txtDescripcion;
    private JTextField txtUsername;
    private JPasswordField pwdPass, pwdConfirm;

    private final SimpleDateFormat dfNacimiento = new SimpleDateFormat("d/M/yyyy");

    /**
     * Construye el diálogo para crear un nuevo usuario.
     *
     * @param parent    ventana padre sobre la que se mostrará el diálogo
     * @param onSuccess acción a ejecutar tras la creación exitosa del usuario
     */
    public CrearUsuarioVentana(Frame parent, Runnable onSuccess) {
        super(parent, "Crear nuevo usuario", true);
        this.onSuccess = onSuccess;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());
        initUI();
    }

    /**
     * Inicializa y dispone todos los componentes gráficos del formulario,
     * incluyendo el header con foto, el cuerpo con campos de texto y credenciales,
     * y el footer con botones de acción.
     */
    private void initUI() {
        lblFoto = new JLabel(cargarAvatarCircular(null, 80, 80));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFoto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblFoto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter(
                    "Imágenes (jpg, png, gif)", "jpg","jpeg","png","gif"
                ));
                if (chooser.showOpenDialog(CrearUsuarioVentana.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = chooser.getSelectedFile();
                        byte[] img = Files.readAllBytes(f.toPath());
                        if (img.length > MAX_PHOTO_BYTES) {
                            JOptionPane.showMessageDialog(
                                CrearUsuarioVentana.this,
                                "La imagen es demasiado grande (" + (img.length/1024) + " KB).\n" +
                                "Tamaño máximo permitido: 1 MB.",
                                "Error al cargar imagen", JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }
                        newUser.setFotoUsuario(img);
                        lblFoto.setIcon(cargarAvatarCircular(newUser, 80, 80));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                            CrearUsuarioVentana.this,
                            "No se pudo cargar la imagen:\n" + ex.getMessage(),
                            "Error al cargar imagen", JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        JPanel header = new JPanel();
        header.setBackground(BG);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(20,20,20,20));
        header.add(lblFoto);
        header.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblTitle = new JLabel("Crear nuevo usuario", SwingConstants.CENTER);
        lblTitle.setForeground(FG);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(lblTitle);
        getContentPane().add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8,8,8,8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        int row = 0;

        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Nombre:"), gbc);
        txtNombre = createField(""); gbc.gridx = 1; content.add(txtNombre, gbc);
        gbc.gridx = 2; content.add(createLabel("Apellidos:"), gbc);
        txtApellidos = createField(""); gbc.gridx = 3; content.add(txtApellidos, gbc);

        row++; gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Email:"), gbc);
        txtEmail = createField(""); gbc.gridx = 1; content.add(txtEmail, gbc);
        gbc.gridx = 2; content.add(createLabel("Rol:"), gbc);
        cbRol = new JComboBox<>(Usuario.Rol.values()); gbc.gridx = 3; content.add(cbRol, gbc);

        row++; gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 4;
        JPanel datosPanel = new JPanel(new GridBagLayout());
        datosPanel.setBackground(BG);
        datosPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT),
            "Datos básicos", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16), FG
        ));
        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.insets  = new Insets(6,6,6,6);
        dgbc.anchor  = GridBagConstraints.WEST;
        dgbc.fill    = GridBagConstraints.HORIZONTAL;
        dgbc.weightx = 1.0;
        int dr = 0;

        dgbc.gridy = dr; dgbc.gridx = 0;
        datosPanel.add(createLabel("Categoría:"), dgbc);
        cbCategoria = new JComboBox<>(new String[]{
            "Prebenjamin","Benjamin","Alevin",
            "Infantil","Juvenil","Junior","Senior"
        });
        dgbc.gridx = 1; datosPanel.add(cbCategoria, dgbc);
        dgbc.gridx = 2; datosPanel.add(createLabel("Posición:"), dgbc);
        cbPosicion = new JComboBox<>(JugadorInfo.Posicion.values());
        dgbc.gridx = 3; datosPanel.add(cbPosicion, dgbc);

        dr++; dgbc.gridy = dr; dgbc.gridx = 0;
        datosPanel.add(createLabel("Nacimiento (d/M/yyyy):"), dgbc);
        txtNacimiento = createField(""); dgbc.gridx = 1; datosPanel.add(txtNacimiento, dgbc);
        dgbc.gridx = 2; datosPanel.add(createLabel("Teléfono:"), dgbc);
        txtTelefono = createField(""); dgbc.gridx = 3; datosPanel.add(txtTelefono, dgbc);
        dr++; dgbc.gridy = dr; dgbc.gridx = 0;
        datosPanel.add(createLabel("Teléfono padres:"), dgbc);
        txtTelefonoPadre = createField(""); dgbc.gridx = 1; datosPanel.add(txtTelefonoPadre, dgbc);

        content.add(datosPanel, gbc);

        row++; gbc.gridy = row; gbc.gridwidth = 4;
        content.add(createLabel("Descripción / notas:"), gbc);
        row++; gbc.gridy = row; gbc.weighty = 0.2; gbc.fill = GridBagConstraints.BOTH;
        txtDescripcion = new JTextArea();
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBackground(BG);
        txtDescripcion.setForeground(FG);
        txtDescripcion.setBorder(new EmptyBorder(5,5,5,5));
        content.add(new JScrollPane(txtDescripcion), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0; gbc.gridwidth = 1;

        row++; gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Usuario:"), gbc);
        txtUsername = createField(""); gbc.gridx = 1; content.add(txtUsername, gbc);
        gbc.gridx = 2; content.add(createLabel("Contraseña:"), gbc);
        pwdPass = new JPasswordField(); styleField(pwdPass);
        gbc.gridx = 3; content.add(pwdPass, gbc);
        row++; gbc.gridy = row; gbc.gridx = 2;
        content.add(createLabel("Confirmar pwd:"), gbc);
        pwdConfirm = new JPasswordField(); styleField(pwdConfirm);
        gbc.gridx = 3; content.add(pwdConfirm, gbc);

        getContentPane().add(new JScrollPane(content), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        footer.add(btnCancelar);
        JButton btnCrear = new JButton("Crear usuario");
        btnCrear.addActionListener(e -> crearUsuario());
        footer.add(btnCrear);
        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    /**
     * Valida los campos del formulario, crea y persiste el usuario y su información de jugador si aplica.
     * Muestra mensajes de error detallados o de éxito según corresponda.
     */
    private void crearUsuario() {
        if (txtNombre.getText().trim().isEmpty()
            || txtApellidos.getText().trim().isEmpty()
            || txtEmail.getText().trim().isEmpty()
            || txtUsername.getText().trim().isEmpty()
            || pwdPass.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this,
                "Rellena todos los campos obligatorios:\n" +
                "Nombre, apellidos, email, usuario y contraseña.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String p1 = new String(pwdPass.getPassword());
        String p2 = new String(pwdConfirm.getPassword());
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this,
                "Las contraseñas no coinciden.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UsuarioDAO udao = new UsuarioDAO();
        String username = txtUsername.getText().trim();
        if (udao.buscarPorNombreUsuario(username) != null) {
            JOptionPane.showMessageDialog(this,
                "Ese nombre de usuario ya está en uso.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        newUser.setNombre(txtNombre.getText().trim());
        newUser.setApellidos(txtApellidos.getText().trim());
        newUser.setEmail(txtEmail.getText().trim());
        newUser.setTelefono(txtTelefono.getText().trim());
        newUser.setUsuario(username);
        newUser.setContrasena(HashUtil.hashSHA256(p1));
        newUser.setRol((Usuario.Rol)cbRol.getSelectedItem());
        try {
            udao.guardarUsuario(newUser);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Ese email ya está en uso.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newUser.getRol() == Usuario.Rol.JUGADOR) {
            newInfo.setUsuario(newUser);
            newInfo.setCategoria((String)cbCategoria.getSelectedItem());
            String fechaTxt = txtNacimiento.getText().trim();
            if (!fechaTxt.isEmpty()) {
                try {
                    newInfo.setFechaNacimiento(dfNacimiento.parse(fechaTxt));
                } catch (ParseException pe) {
                    JOptionPane.showMessageDialog(this,
                        "Formato de fecha inválido. Usa d/M/yyyy",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            newInfo.setTelefonoPadres(txtTelefonoPadre.getText().trim());
            newInfo.setPosicion((JugadorInfo.Posicion)cbPosicion.getSelectedItem());
            newInfo.setDescripcion(txtDescripcion.getText().trim());
            newInfo.setAsistenciaObligatoria(0);
            newInfo.setAsistenciaVoluntaria(0);
            newInfo.setPartidosJugados(0);
            try {
                new JugadorInfoDAO().guardarJugadorInfo(newInfo);
            } catch (Exception ex) {
                Throwable root = ex;
                while (root.getCause() != null) root = root.getCause();
                JOptionPane.showMessageDialog(this,
                    root.getMessage(),
                    "Error al guardar info de jugador", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(this,
            "Usuario creado correctamente.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        onSuccess.run();
    }

    /**
     * Crea un JLabel con estilo predeterminado para etiquetas de formulario.
     *
     * @param text texto a mostrar en la etiqueta
     * @return JLabel configurado con fuente y color estándar
     */
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    /**
     * Crea un JTextField con texto inicial y aplica estilo estándar.
     *
     * @param txt texto inicial del campo
     * @return JTextField configurado
     */
    private JTextField createField(String txt) {
        JTextField f = new JTextField(txt);
        styleField(f);
        return f;
    }

    /**
     * Aplica estilo de fondo, fuente y borde a un componente de texto.
     *
     * @param f componente JTextComponent a estilizar
     */
    private void styleField(JTextComponent f) {
        f.setBackground(BG);
        f.setForeground(FG);
        f.setBorder(BorderFactory.createLineBorder(ACCENT));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    /**
     * Carga una imagen de usuario o predeterminada y la recorta en forma circular.
     *
     * @param u usuario cuyo avatar cargar, o null para icono por defecto
     * @param w ancho deseado de la imagen en píxeles
     * @param h alto deseado de la imagen en píxeles
     * @return ImageIcon con el avatar circular
     */
    private ImageIcon cargarAvatarCircular(Usuario u, int w, int h) {
        try {
            byte[] img = (u != null ? u.getFotoUsuario() : null);
            if (img != null && img.length > 0) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(img));
                BufferedImage dst = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = dst.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new Ellipse2D.Float(0,0,w,h));
                g2.drawImage(src,0,0,w,h,null);
                g2.dispose();
                return new ImageIcon(dst);
            }
        } catch (Exception ignored) {}
        return loadIcon("user_default.png", w, h);
    }

    /**
     * Carga un icono desde recursos empaquetados y lo escala al tamaño indicado.
     *
     * @param name nombre del recurso dentro de assets (p. ej. "user_default.png")
     * @param w    ancho deseado en píxeles
     * @param h    alto deseado en píxeles
     * @return ImageIcon escalado, o imagen vacía si no se encuentra recurso
     */
    private ImageIcon loadIcon(String name, int w, int h) {
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u != null)
            ? new ImageIcon(u).getImage()
            : new ImageIcon("src/assets/" + name).getImage();
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }
}
