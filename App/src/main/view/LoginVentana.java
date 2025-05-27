package main.view;

import main.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Ventana de inicio de sesión para la aplicación 123Hockey.
 * Permite introducir nombre de usuario y contraseña, e iniciar sesión según el rol.
 * También proporciona un acceso a la ventana de registro de entrenadores.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class LoginVentana extends JFrame {

    /**
     * Constructor de la ventana de login.
     * Configura y muestra los elementos visuales necesarios para iniciar sesión.
     */
    public LoginVentana() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int mX     = (int)(screen.width  * 0.04);
        int headerH = (int)(screen.height * 0.07);
        int iconW  = headerH - 16;
        int iconH  = headerH - 16;

        Image appIcon = loadIcon("logoSinFondo.png", 32, 32).getImage();
        setIconImage(appIcon);

        int arcBtn   = 45 / 3;
        int bordBtn  = 2;
        Color normalBg     = new Color(49, 109, 233);
        Color normalBorder = new Color(49, 109, 233);
        Color hoverBg      = new Color(52, 115, 255);
        Color hoverBorder  = new Color(52, 115, 255);
        Color pressBg      = new Color(142, 173, 233);
        Color pressBorder  = new Color(142, 173, 233);

        setTitle("123Hockey - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(18, 18, 18));

        ImageIcon icono = new ImageIcon("src/assets/logoSinFondo.png");
        Image imagenEscalada = icono.getImage().getScaledInstance(420, 420, Image.SCALE_SMOOTH);
        JLabel imagen = new JLabel(new ImageIcon(imagenEscalada));
        imagen.setBounds(175, 170, 420, 420);
        add(imagen);

        JLabel titulo = new JLabel("123HOCKEY!");
        titulo.setFont(new Font("Arial Black", Font.BOLD, 52));
        titulo.setForeground(new Color(49, 109, 233));
        titulo.setBounds(185, 90, 500, 60);
        add(titulo);

        JLabel loginLabel = new JLabel("Inicia sesión");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        loginLabel.setForeground(new Color(195, 195, 195));
        loginLabel.setBounds(825, 170, 300, 40);
        add(loginLabel);

        int arcField = 15;
        int bordField = 1;
        Color fieldBg     = new Color(18, 18, 18);
        Color fieldBorder = new Color(49, 109, 233);

        JTextField campoUsuario = new JTextField("Nombre de usuario") {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fieldBg);
                g2.fillRoundRect(0, 0, w, h, arcField, arcField);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(bordField));
                g2.setColor(fieldBorder);
                double off = bordField / 2.0;
                g2.drawRoundRect((int)off, (int)off, w - bordField, h - bordField, arcField, arcField);
                g2.dispose();
            }
        };
        campoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campoUsuario.setForeground(Color.GRAY);
        campoUsuario.setBackground(fieldBg);
        campoUsuario.setOpaque(false);
        campoUsuario.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 10));
        campoUsuario.setBounds(700, 230, 400, 45);
        add(campoUsuario);
        campoUsuario.setFocusable(false);
        campoUsuario.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                campoUsuario.setFocusable(true);
                campoUsuario.requestFocusInWindow();
            }
        });
        campoUsuario.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (campoUsuario.getText().equals("Nombre de usuario")) {
                    campoUsuario.setText("");
                    campoUsuario.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (campoUsuario.getText().isEmpty()) {
                    campoUsuario.setText("Nombre de usuario");
                    campoUsuario.setForeground(Color.GRAY);
                }
            }
        });

        JPasswordField campoContrasena = new JPasswordField("Contraseña") {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fieldBg);
                g2.fillRoundRect(0, 0, w, h, arcField, arcField);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(bordField));
                g2.setColor(fieldBorder);
                double off = bordField / 2.0;
                g2.drawRoundRect((int)off, (int)off, w - bordField, h - bordField, arcField, arcField);
                g2.dispose();
            }
        };
        campoContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campoContrasena.setForeground(Color.GRAY);
        campoContrasena.setBackground(fieldBg);
        campoContrasena.setEchoChar((char)0);
        campoContrasena.setOpaque(false);
        campoContrasena.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        campoContrasena.setBounds(700, 290, 400, 45);
        add(campoContrasena);
        campoContrasena.setFocusable(false);
        campoContrasena.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                campoContrasena.setFocusable(true);
                campoContrasena.requestFocusInWindow();
            }
        });
        campoContrasena.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(campoContrasena.getPassword()).equals("Contraseña")) {
                    campoContrasena.setText("");
                    campoContrasena.setEchoChar('•');
                    campoContrasena.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (String.valueOf(campoContrasena.getPassword()).isEmpty()) {
                    campoContrasena.setText("Contraseña");
                    campoContrasena.setEchoChar((char)0);
                    campoContrasena.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnEntrar = new JButton("Entrar") {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight(), arc = arcBtn;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = getModel();
                Color bg, br;
                if (m.isPressed()) {
                    bg = pressBg; br = pressBorder;
                } else if (m.isRollover()) {
                    bg = hoverBg; br = hoverBorder;
                } else {
                    bg = normalBg; br = normalBorder;
                }
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(br);
                double off = bordBtn / 2.0;
                g2.drawRoundRect((int)off, (int)off, w - bordBtn, h - bordBtn, arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnEntrar.setOpaque(false);
        btnEntrar.setContentAreaFilled(false);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setRolloverEnabled(true);
        btnEntrar.setBackground(normalBg);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, (int)(45 * 0.35)));
        btnEntrar.setBounds(700, 350, 400, 45);
        add(btnEntrar);
        btnEntrar.addActionListener(e -> {
            String usuario = campoUsuario.getText();
            String contrasena = new String(campoContrasena.getPassword());
            LoginController login = new LoginController();
            String resultado = login.iniciarSesion(usuario, contrasena, this);
        });

        JLabel linkRegistro = new JLabel(
                "<html><span style='color:#999999;'>Si quieres crear una cuenta de entrenador, </span>" +
                        "<span style='color:#318DE1; text-decoration:underline; cursor:hand;'>pulsa aquí</span></html>");
        linkRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        linkRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkRegistro.setBounds(700, 400, 450, 30);
        add(linkRegistro);

        linkRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new RegistroVentana();
            }
        });

        setVisible(true);
    }

    /**
     * Carga un icono desde el sistema de archivos o recursos del proyecto.
     *
     * @param name nombre del archivo de imagen
     * @param w    ancho deseado del icono
     * @param h    alto deseado del icono
     * @return     el icono redimensionado
     */
    private ImageIcon loadIcon(String name, int w, int h) {
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u != null)
                ? new ImageIcon(u).getImage()
                : new ImageIcon("src/assets/" + name).getImage();
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
}
