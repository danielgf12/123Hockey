package main.view;

import main.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginVentana extends JFrame {

    public LoginVentana() {
        setTitle("123Hockey - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(18, 18, 18)); // fondo negro

        // ---------- Imagen izquierda escalada
        ImageIcon icono = new ImageIcon("src/assets/logoSinFondo.png");
        Image imagenEscalada = icono.getImage().getScaledInstance(420, 420, Image.SCALE_SMOOTH);
        JLabel imagen = new JLabel(new ImageIcon(imagenEscalada));
        imagen.setBounds(200, 180, 420, 420);
        add(imagen);

        // ---------- Título encima de la imagen
        JLabel titulo = new JLabel("123HOCKEY!");
        titulo.setFont(new Font("Arial Black", Font.BOLD, 52)); // Horizon simulada
        titulo.setForeground(new Color(49, 109, 233));
        titulo.setBounds(230, 100, 500, 60); // centrado arriba de la imagen
        add(titulo);

        // ---------- "Inicia sesión"
        JLabel loginLabel = new JLabel("Inicia sesión");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Poppins simulada
        loginLabel.setForeground(new Color(49, 109, 233));
        loginLabel.setBounds(825, 170, 300, 40);
        add(loginLabel);

        // ---------- Campo de usuario
        JTextField campoUsuario = new JTextField("Nombre de usuario");
        campoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campoUsuario.setForeground(Color.GRAY);
        campoUsuario.setBackground(new Color(18, 18, 18));
        campoUsuario.setBounds(700, 230, 400, 45);
        campoUsuario.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        add(campoUsuario);

        campoUsuario.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (campoUsuario.getText().equals("Nombre de usuario")) {
                    campoUsuario.setText("");
                    campoUsuario.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (campoUsuario.getText().isEmpty()) {
                    campoUsuario.setText("Nombre de usuario");
                    campoUsuario.setForeground(Color.GRAY);
                }
            }
        });

        // ---------- Campo de contraseña
        JPasswordField campoContrasena = new JPasswordField("Contraseña");
        campoContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campoContrasena.setForeground(Color.GRAY);
        campoContrasena.setBackground(new Color(18, 18, 18));
        campoContrasena.setEchoChar((char) 0);
        campoContrasena.setBounds(700, 290, 400, 45);
        campoContrasena.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        add(campoContrasena);

        campoContrasena.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(campoContrasena.getPassword()).equals("Contraseña")) {
                    campoContrasena.setText("");
                    campoContrasena.setEchoChar('•');
                    campoContrasena.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (String.valueOf(campoContrasena.getPassword()).isEmpty()) {
                    campoContrasena.setText("Contraseña");
                    campoContrasena.setEchoChar((char) 0);
                    campoContrasena.setForeground(Color.GRAY);
                }
            }
        });

        // ---------- Botón Entrar
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(700, 350, 400, 45);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnEntrar.setBackground(new Color(49, 109, 233));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        add(btnEntrar);

        // ---------- Texto inferior con enlace
        JLabel linkRegistro = new JLabel("<html><u>Si quieres crear una cuenta de entrenador, pulsa aquí</u></html>");
        linkRegistro.setForeground(new Color(49, 109, 233));
        linkRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        linkRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkRegistro.setBounds(700, 410, 450, 30);
        add(linkRegistro);

        // ---------- Acción del botón "Entrar"
        btnEntrar.addActionListener(e -> {
            String usuario = campoUsuario.getText();
            String contrasena = new String(campoContrasena.getPassword());

            LoginController login = new LoginController();
            String resultado = login.iniciarSesion(usuario, contrasena);

            JOptionPane.showMessageDialog(this, resultado);
        });

        // ---------- Acción del enlace
        linkRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose(); // cerrar ventana actual
                new RegistroVentana(); // abrir la de registro
            }
        });

        setVisible(true);
    }
}
