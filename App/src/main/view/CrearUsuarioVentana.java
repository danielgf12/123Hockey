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

public class CrearUsuarioVentana extends JDialog {
    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,141,225);

    private final Runnable onSuccess;
    private final Usuario newUser = new Usuario();
    private final JugadorInfo newInfo = new JugadorInfo();

    // UI components
    private JLabel lblFoto;
    private JTextField txtNombre, txtApellidos, txtEmail;
    private JComboBox<String> cbCategoria;
    private JTextField txtNacimiento, txtTelefono, txtTelefonoPadre;
    private JComboBox<Usuario.Rol> cbRol;
    private JComboBox<JugadorInfo.Posicion> cbPosicion;
    private JTextArea txtDescripcion;
    private JTextField txtUsername;
    private JPasswordField pwdPass, pwdConfirm;

    public CrearUsuarioVentana(Frame parent, Runnable onSuccess) {
        super(parent, "Crear nuevo usuario", true);
        this.onSuccess = onSuccess;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());

        // --- Header: avatar + title ---
        lblFoto = new JLabel(cargarAvatarCircular(null, 80, 80));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFoto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblFoto.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter(
                        "Imágenes (jpg, png, gif)", "jpg","jpeg","png","gif"
                ));
                if (chooser.showOpenDialog(CrearUsuarioVentana.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = chooser.getSelectedFile();
                        byte[] img = Files.readAllBytes(f.toPath());
                        newUser.setFotoUsuario(img);
                        lblFoto.setIcon(cargarAvatarCircular(newUser, 120, 120));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                CrearUsuarioVentana.this,
                                "Error al cargar imagen:\n" + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE
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

        // --- Form body ---
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8,8,8,8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Nombre / Apellidos
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Nombre:"), gbc);
        txtNombre = createField("");
        gbc.gridx = 1; content.add(txtNombre, gbc);

        gbc.gridx = 2; content.add(createLabel("Apellidos:"), gbc);
        txtApellidos = createField("");
        gbc.gridx = 3; content.add(txtApellidos, gbc);

        // Email / Rol
        row++;
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Email:"), gbc);
        txtEmail = createField("");
        gbc.gridx = 1; content.add(txtEmail, gbc);

        gbc.gridx = 2; content.add(createLabel("Rol:"), gbc);
        cbRol = new JComboBox<>(Usuario.Rol.values());
        gbc.gridx = 3; content.add(cbRol, gbc);

        // Datos básicos panel
        row++;
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 4;
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
        // Categoría / Posición
        dgbc.gridy = dr; dgbc.gridx = 0;
        datosPanel.add(createLabel("Categoría:"), dgbc);
        cbCategoria = new JComboBox<>(new String[]{
                "Prebenjamin","Benjamin","Alevin",
                "Infantil","Juvenil","Junior","Senior"
        });
        dgbc.gridx = 1; datosPanel.add(cbCategoria, dgbc);

        dgbc.gridx = 2;
        datosPanel.add(createLabel("Posición:"), dgbc);
        cbPosicion = new JComboBox<>(JugadorInfo.Posicion.values());
        dgbc.gridx = 3; datosPanel.add(cbPosicion, dgbc);

        // Nacimiento / Teléfono
        dr++;
        dgbc.gridy = dr; dgbc.gridx = 0;
        datosPanel.add(createLabel("Nacimiento (d/M/yyyy):"), dgbc);
        txtNacimiento = createField("");
        dgbc.gridx = 1; datosPanel.add(txtNacimiento, dgbc);

        dgbc.gridx = 2;
        datosPanel.add(createLabel("Teléfono:"), dgbc);
        txtTelefono = createField("");
        dgbc.gridx = 3; datosPanel.add(txtTelefono, dgbc);

        // Teléfono padres
        dr++;
        dgbc.gridy = dr; dgbc.gridx = 0;
        datosPanel.add(createLabel("Teléfono padres:"), dgbc);
        txtTelefonoPadre = createField("");
        dgbc.gridx = 1; datosPanel.add(txtTelefonoPadre, dgbc);

        gbc.gridwidth = 4;
        content.add(datosPanel, gbc);

        // Descripción
        row++;
        gbc.gridy = row; gbc.gridwidth = 4;
        content.add(createLabel("Descripción / notas:"), gbc);

        row++;
        gbc.gridy = row; gbc.weighty = 0.2; gbc.fill = GridBagConstraints.BOTH;
        txtDescripcion = new JTextArea();
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBackground(BG);
        txtDescripcion.setForeground(FG);
        txtDescripcion.setBorder(new EmptyBorder(5,5,5,5));
        content.add(new JScrollPane(txtDescripcion), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0; gbc.gridwidth = 1;

        // Credenciales
        row++;
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Usuario:"), gbc);
        txtUsername = createField("");
        gbc.gridx = 1; content.add(txtUsername, gbc);

        gbc.gridx = 2; content.add(createLabel("Contraseña:"), gbc);
        pwdPass = new JPasswordField();
        styleField(pwdPass);
        gbc.gridx = 3; content.add(pwdPass, gbc);

        row++;
        gbc.gridy = row; gbc.gridx = 2;
        content.add(createLabel("Confirmar pwd:"), gbc);
        pwdConfirm = new JPasswordField();
        styleField(pwdConfirm);
        gbc.gridx = 3; content.add(pwdConfirm, gbc);

        getContentPane().add(new JScrollPane(content), BorderLayout.CENTER);

        // Footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        footer.add(btnCancelar);

        JButton btnCrear = new JButton("Crear usuario");
        btnCrear.addActionListener(e -> crearUsuario());
        footer.add(btnCrear);

        getContentPane().add(footer, BorderLayout.SOUTH);

        // NO setVisible(true) aquí, lo hacemos desde quien lo invoque
    }

    private void crearUsuario() {
        // 1) Validación de campos obligatorios
        if (txtNombre.getText().trim().isEmpty()
                || txtApellidos.getText().trim().isEmpty()
                || txtEmail.getText().trim().isEmpty()
                || txtUsername.getText().trim().isEmpty()
                || pwdPass.getPassword().length == 0
        ) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, rellena todos los campos obligatorios:\n" +
                            "Nombre, apellidos, email, usuario y contraseña.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2) Validación de contraseña
        String p1 = new String(pwdPass.getPassword()),
                p2 = new String(pwdConfirm.getPassword());
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this,
                    "Las contraseñas no coinciden.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3) Validación de usuario único
        String username = txtUsername.getText().trim();
        UsuarioDAO udao = new UsuarioDAO();
        if (udao.buscarPorNombreUsuario(username) != null) {
            JOptionPane.showMessageDialog(this,
                    "Ese nombre de usuario ya está en uso. Elige otro.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4) Asignamos datos al nuevo usuario
        newUser.setNombre(txtNombre.getText().trim());
        newUser.setApellidos(txtApellidos.getText().trim());
        newUser.setEmail(txtEmail.getText().trim());
        newUser.setTelefono(txtTelefono.getText().trim());
        newUser.setUsuario(username);
        newUser.setContrasena(HashUtil.hashSHA256(p1));
        newUser.setRol((Usuario.Rol)cbRol.getSelectedItem());

        // 5) Guardamos en la BD con manejo de excepciones
        try {
            udao.guardarUsuario(newUser);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar usuario:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 6) Si es jugador, guardamos su info también
        if (newUser.getRol() == Usuario.Rol.JUGADOR) {
            newInfo.setUsuario(newUser);
            newInfo.setCategoria((String)cbCategoria.getSelectedItem());
            // parseamos fecha de nacimiento
            String fechaTxt = txtNacimiento.getText().trim();
            if (!fechaTxt.isEmpty()) {
                try {
                    Date d = new SimpleDateFormat("d/M/yyyy").parse(fechaTxt);
                    newInfo.setFechaNacimiento(d);
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
                JOptionPane.showMessageDialog(this,
                        "Error al guardar info de jugador:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 7) Éxito
        JOptionPane.showMessageDialog(this,
                "Usuario creado correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        onSuccess.run();
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JTextField createField(String txt) {
        JTextField f = new JTextField(txt);
        styleField(f);
        return f;
    }

    private void styleField(JTextComponent f) {
        f.setBackground(BG);
        f.setForeground(FG);
        f.setBorder(BorderFactory.createLineBorder(ACCENT));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private ImageIcon cargarAvatarCircular(Usuario u, int w, int h) {
        try {
            byte[] f = (u != null ? u.getFotoUsuario() : null);
            if (f != null && f.length > 0) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(f));
                BufferedImage dst = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = dst.createGraphics();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setClip(new Ellipse2D.Float(0,0,w,h));
                g2.drawImage(src,0,0,w,h,null);
                g2.dispose();
                return new ImageIcon(dst);
            }
        } catch(Exception ignored){}
        return loadIcon("user_default.png", w, h);
    }

    private ImageIcon loadIcon(String name, int w, int h) {
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u != null)
                ? new ImageIcon(u).getImage()
                : new ImageIcon("src/assets/" + name).getImage();
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }
}
