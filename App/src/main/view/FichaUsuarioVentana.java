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
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FichaUsuarioVentana extends JFrame {

    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,141,225);

    private final Usuario     loggedUser;
    private final Usuario     targetUser;
    private final JugadorInfo info;

    private JLabel            lblFoto;
    private JComboBox<String> cbCategoria;
    private JTextField        txtNacimiento, txtTelefono, txtTelefonoPadre, txtUsername, txtPassword;
    private JComboBox<Usuario.Rol>           cbRol;
    private JComboBox<JugadorInfo.Posicion>  cbPosicion;
    private JTextArea        txtDescripcion;
    private JButton           btnChangePass, btnEditSave, btnDelete;
    private boolean           editing = false;

    public FichaUsuarioVentana(Usuario loggedUser, Usuario targetUser) {
        super("Ficha de " + targetUser.getNombre() + " " + targetUser.getApellidos());
        this.loggedUser = loggedUser;
        this.targetUser = targetUser;
        this.info       = new JugadorInfoDAO().buscarPorUsuarioId(targetUser.getId());

        Image appIcon = loadIcon("logoSinFondo.png",32,32).getImage();
        setIconImage(appIcon);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600,650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());

        // Header: avatar + name
        lblFoto = new JLabel(cargarAvatarCircular(targetUser,120,120));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFoto.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (!editing) return;
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter(
                        "Imágenes (jpg, png, gif)", "jpg","jpeg","png","gif"
                ));
                if (chooser.showOpenDialog(FichaUsuarioVentana.this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        targetUser.setFotoUsuario(bytes);
                        new UsuarioDAO().actualizarUsuario(targetUser);
                        lblFoto.setIcon(cargarAvatarCircular(targetUser,120,120));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(FichaUsuarioVentana.this,
                                "Error al cargar o guardar imagen: "+ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JPanel header = new JPanel();
        header.setBackground(BG);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(20,20,20,20));
        header.add(lblFoto);
        header.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel lblNombre = new JLabel(
                targetUser.getNombre()+" "+targetUser.getApellidos(),
                SwingConstants.CENTER
        );
        lblNombre.setForeground(FG);
        lblNombre.setFont(new Font("Segoe UI",Font.BOLD,24));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(lblNombre);
        getContentPane().add(header, BorderLayout.NORTH);

        // Center panel
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8,8,8,8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Datos básicos panel
        JPanel datosPanel = new JPanel(new GridBagLayout());
        datosPanel.setBackground(BG);
        datosPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Datos básicos",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI",Font.BOLD,16), FG
        ));
        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.insets  = new Insets(6,6,6,6);
        dgbc.anchor  = GridBagConstraints.WEST;
        dgbc.fill    = GridBagConstraints.HORIZONTAL;
        dgbc.weightx = 1.0;

        // Categoría
        dgbc.gridx=0; dgbc.gridy=0;
        datosPanel.add(createLabel("Categoría:"), dgbc);
        cbCategoria = new JComboBox<>(new String[]{
                "Prebenjamin","Benjamin","Alevin",
                "Infantil","Juvenil","Junior","Senior"
        });
        cbCategoria.setSelectedItem(info!=null?info.getCategoria():null);
        cbCategoria.setEnabled(false);
        dgbc.gridx=1; datosPanel.add(cbCategoria, dgbc);

        // Nacimiento
        dgbc.gridx=2;
        datosPanel.add(createLabel("Nacimiento:"), dgbc);
        txtNacimiento = createField(formatearFecha(info!=null?info.getFechaNacimiento():null));
        dgbc.gridx=3; datosPanel.add(txtNacimiento, dgbc);

        // Teléfono
        dgbc.gridy=1; dgbc.gridx=0;
        datosPanel.add(createLabel("Teléfono:"), dgbc);
        txtTelefono = createField(targetUser.getTelefono());
        dgbc.gridx=1; datosPanel.add(txtTelefono, dgbc);

        // Teléfonos padres
        dgbc.gridx=2;
        datosPanel.add(createLabel("Teléfono padres:"), dgbc);
        txtTelefonoPadre = createField(info!=null?info.getTelefonoPadres():null);
        dgbc.gridx=3; datosPanel.add(txtTelefonoPadre, dgbc);

        // Rol
        dgbc.gridy=2; dgbc.gridx=0;
        datosPanel.add(createLabel("Rol:"), dgbc);
        cbRol = new JComboBox<>(Usuario.Rol.values());
        cbRol.setSelectedItem(targetUser.getRol());
        cbRol.setEnabled(false);
        dgbc.gridx=1; datosPanel.add(cbRol, dgbc);

        // Posición
        dgbc.gridx=2;
        datosPanel.add(createLabel("Posición:"), dgbc);
        cbPosicion = new JComboBox<>(JugadorInfo.Posicion.values());
        if (info!=null && info.getPosicion()!=null) {
            cbPosicion.setSelectedItem(info.getPosicion());
        }
        cbPosicion.setEnabled(false);
        dgbc.gridx=3; datosPanel.add(cbPosicion, dgbc);

        // Asistencia obligatoria
        dgbc.gridy=3; dgbc.gridx=0;
        datosPanel.add(createLabel("Asist. obligatoria:"), dgbc);
        JTextField txtAsistOblig = createField(
                String.valueOf(info!=null?info.getAsistenciaObligatoria():0)
        );
        txtAsistOblig.setEditable(false);
        dgbc.gridx=1; datosPanel.add(txtAsistOblig, dgbc);

        // Partidos jugados
        dgbc.gridx=2;
        datosPanel.add(createLabel("Partidos jugados:"), dgbc);
        JTextField txtPartidos = createField(
                String.valueOf(info!=null?info.getPartidosJugados():0)
        );
        txtPartidos.setEditable(false);
        dgbc.gridx=3; datosPanel.add(txtPartidos, dgbc);

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        content.add(datosPanel, gbc);

        int nextRow = 1;

        // Descripción / notas (sólo ENTRENADOR o DELEGADO)
        if (loggedUser.getRol() != Usuario.Rol.JUGADOR) {
            gbc.gridy=nextRow; gbc.gridwidth=2;
            JLabel lblDesc = new JLabel("Descripción / notas:");
            lblDesc.setForeground(FG);
            lblDesc.setFont(new Font("Segoe UI",Font.BOLD,16));
            content.add(lblDesc, gbc);

            txtDescripcion = new JTextArea(info!=null?info.getDescripcion():"");
            txtDescripcion.setLineWrap(true);
            txtDescripcion.setWrapStyleWord(true);
            txtDescripcion.setBackground(BG);
            txtDescripcion.setForeground(FG);
            txtDescripcion.setFont(new Font("Segoe UI",Font.PLAIN,14));
            txtDescripcion.setBorder(new EmptyBorder(5,5,5,5));
            txtDescripcion.setEditable(false);

            gbc.gridy=++nextRow;
            gbc.weighty=0.2;
            gbc.fill=GridBagConstraints.BOTH;
            JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
            scrollDesc.setBorder(BorderFactory.createLineBorder(ACCENT));
            scrollDesc.getViewport().setBackground(BG);
            content.add(scrollDesc, gbc);

            nextRow++;
        }

        // Acceso
        gbc.gridy=nextRow;
        gbc.weighty=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        JPanel accPanel = new JPanel(new GridBagLayout());
        accPanel.setBackground(BG);
        accPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Acceso", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI",Font.BOLD,16), FG
        ));
        GridBagConstraints agb = new GridBagConstraints();
        agb.insets  = new Insets(6,6,6,6);
        agb.anchor  = GridBagConstraints.WEST;
        agb.fill    = GridBagConstraints.HORIZONTAL;
        agb.weightx = 1.0;

        agb.gridx=0; agb.gridy=0;
        accPanel.add(createLabel("Usuario:"), agb);
        txtUsername = createField(targetUser.getUsuario());
        agb.gridx=1; accPanel.add(txtUsername, agb);

        agb.gridx=2;
        accPanel.add(createLabel("Contraseña:"), agb);
        JPanel pwdPanel = new JPanel(new BorderLayout(4,0));
        pwdPanel.setBackground(BG);
        txtPassword = createField("••••••••");
        txtPassword.setEditable(false);
        pwdPanel.add(txtPassword, BorderLayout.CENTER);
        btnChangePass = new JButton("Cambiar...");
        btnChangePass.setFocusable(false);
        btnChangePass.addActionListener(e -> openChangePasswordDialog());
        pwdPanel.add(btnChangePass, BorderLayout.EAST);
        agb.gridx=3; accPanel.add(pwdPanel, agb);

        gbc.gridwidth=2;
        content.add(accPanel, gbc);

        // Scroll central
        JScrollPane centerScroll = new JScrollPane(content);
        centerScroll.setBorder(null);
        centerScroll.getViewport().setBackground(BG);
        getContentPane().add(centerScroll, BorderLayout.CENTER);

        // Footer: botones
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);
        boolean canEdit = loggedUser.getRol()==Usuario.Rol.DELEGADO
                || loggedUser.getRol()==Usuario.Rol.ENTRENADOR;
        btnEditSave = new JButton("Editar datos");
        btnEditSave.setEnabled(canEdit);
        btnEditSave.addActionListener(e -> toggleEdit());
        footer.add(btnEditSave);
        btnDelete = new JButton("Eliminar usuario");
        btnDelete.setEnabled(canEdit);
        btnDelete.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "¿Confirma eliminar a "+targetUser.getNombre()+"?",
                    "Atención",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                new JugadorInfoDAO().eliminarPorUsuarioId(targetUser.getId());
                new UsuarioDAO().eliminarUsuario(targetUser);
                dispose();
            }
        });
        footer.add(btnDelete);
        getContentPane().add(footer, BorderLayout.SOUTH);

        setFieldsEditable(false);
        setVisible(true);
    }

    private void toggleEdit() {
        editing = !editing;
        setFieldsEditable(editing);
        btnEditSave.setText(editing ? "Guardar cambios" : "Editar datos");
        if (!editing) {
            info.setCategoria((String)cbCategoria.getSelectedItem());
            info.setFechaNacimiento(parseFecha(txtNacimiento.getText()));
            targetUser.setTelefono(txtTelefono.getText());
            targetUser.setUsuario(txtUsername.getText());
            info.setTelefonoPadres(txtTelefonoPadre.getText());
            if (loggedUser.getRol()==Usuario.Rol.ENTRENADOR) {
                targetUser.setRol((Usuario.Rol)cbRol.getSelectedItem());
            }
            info.setPosicion((JugadorInfo.Posicion)cbPosicion.getSelectedItem());
            if (loggedUser.getRol()!=Usuario.Rol.JUGADOR) {
                info.setDescripcion(txtDescripcion.getText());
            }
            new JugadorInfoDAO().actualizarJugadorInfo(info);
            new UsuarioDAO().actualizarUsuario(targetUser);
        }
    }

    private void setFieldsEditable(boolean e) {
        cbCategoria.setEnabled(e);
        txtNacimiento.setEditable(e);
        txtTelefono.setEditable(e);
        txtTelefonoPadre.setEditable(e);
        txtUsername.setEditable(e);
        cbRol.setEnabled(e && loggedUser.getRol()==Usuario.Rol.ENTRENADOR);
        cbPosicion.setEnabled(e);
        if (loggedUser.getRol()!=Usuario.Rol.JUGADOR) {
            txtDescripcion.setEditable(e);
        }
    }

    private void openChangePasswordDialog() {
        JPasswordField pass1 = new JPasswordField(15);
        JPasswordField pass2 = new JPasswordField(15);
        Object[] msg = {"Nueva contraseña:", pass1, "Repetir contraseña:", pass2};
        int option = JOptionPane.showConfirmDialog(this,msg,
                "Cambiar contraseña",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if (option==JOptionPane.OK_OPTION) {
            String p1=new String(pass1.getPassword()),
                    p2=new String(pass2.getPassword());
            if (p1.isEmpty()) {
                JOptionPane.showMessageDialog(this,"La contraseña no puede estar vacía",
                        "Error",JOptionPane.ERROR_MESSAGE);
            } else if (!p1.equals(p2)) {
                JOptionPane.showMessageDialog(this,"Las contraseñas no coinciden",
                        "Error",JOptionPane.ERROR_MESSAGE);
            } else {
                targetUser.setContrasena(HashUtil.hashSHA256(p1));
                new UsuarioDAO().actualizarUsuario(targetUser);
                JOptionPane.showMessageDialog(this,
                        "Contraseña actualizada correctamente",
                        "Hecho",JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI",Font.PLAIN,14));
        return l;
    }

    private JTextField createField(String text) {
        JTextField f = new JTextField(text);
        f.setForeground(FG);
        f.setBackground(BG);
        f.setBorder(BorderFactory.createLineBorder(ACCENT));
        f.setFont(new Font("Segoe UI",Font.PLAIN,14));
        return f;
    }

    private String formatearFecha(Date d) {
        if (d==null) return "–";
        return new SimpleDateFormat("d/MM/yyyy").format(d);
    }

    private Date parseFecha(String s) {
        try {
            return new SimpleDateFormat("d/MM/yyyy").parse(s);
        } catch(Exception ex) {
            return null;
        }
    }

    private ImageIcon cargarAvatarCircular(Usuario u,int w,int h) {
        try {
            byte[] f = u.getFotoUsuario();
            if (f!=null && f.length>0){
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
        return loadIcon("user_default.png",w,h);
    }

    private ImageIcon loadIcon(String name,int w,int h) {
        URL u = getClass().getClassLoader().getResource("assets/"+name);
        Image img = (u!=null
                ? new ImageIcon(u).getImage()
                : new ImageIcon("src/assets/"+name).getImage());
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }
}
