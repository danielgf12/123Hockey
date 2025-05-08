package main.view;

import main.controller.RegistroController;
import main.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RegistroVentana extends JFrame {

    public RegistroVentana() {
        setTitle("123Hockey - Registro");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(18, 18, 18)); // Fondo oscuro

        // ---------- Imagen a la izquierda
        ImageIcon icono = new ImageIcon("src/assets/logoSinFondo.png");
        Image imagenEscalada = icono.getImage().getScaledInstance(420, 420, Image.SCALE_SMOOTH);
        JLabel imagen = new JLabel(new ImageIcon(imagenEscalada));
        imagen.setBounds(200, 180, 420, 420);
        add(imagen);

        // ---------- Título encima de la imagen
        JLabel titulo = new JLabel("123HOCKEY!");
        titulo.setFont(new Font("Arial Black", Font.BOLD, 52));
        titulo.setForeground(new Color(49, 109, 233));
        titulo.setBounds(230, 100, 500, 60);
        add(titulo);

        // ---------- Subtítulo
        JLabel subtitulo = new JLabel("Regístrate aquí");
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        subtitulo.setForeground(new Color(49, 109, 233));
        subtitulo.setBounds(880, 80, 300, 40);
        add(subtitulo);

        // ---------- Campos del formulario
        JTextField nombre = crearCampo("Nombre", 800, 140);
        JTextField apellido1 = crearCampoApellido("Apellido 1", 800, 200);
        JTextField apellido2 = crearCampoApellido("Apellido 2", 1005, 200);
        JTextField email = crearCampo("Email", 800, 260);
        JTextField telefono = crearCampo("Teléfono", 800, 320);
        JPasswordField contrasena = crearCampoPassword("Contraseña", 800, 380);
        JPasswordField confirmar = crearCampoPassword("Confirmar", 1005, 380);

        // Añadirlos
        add(nombre);
        add(apellido1);
        add(apellido2);
        add(email);
        add(telefono);
        add(contrasena);
        add(confirmar);

        // ---------- Botón registrar
        JButton btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setBounds(800, 440, 380, 45);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnRegistrar.setBackground(new Color(49, 109, 233));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        add(btnRegistrar);

        // ---------- Enlace inferior
        JLabel login = new JLabel("<html><u>¿Ya tienes cuenta? Pulsa aquí para iniciar sesión</u></html>");
        login.setForeground(new Color(49, 109, 233));
        login.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        login.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        login.setBounds(800, 500, 450, 30);
        add(login);

        // ---------- Lógica del botón
        btnRegistrar.addActionListener(e -> {
            RegistroController rc = new RegistroController();
            Usuario nuevoUsuario = rc.registrarEntrenador(
                    nombre.getText(),
                    apellido1.getText(),
                    apellido2.getText(),
                    email.getText(),
                    telefono.getText(),
                    new String(contrasena.getPassword()),
                    new String(confirmar.getPassword())
            );

            if (nuevoUsuario != null) {
                dispose(); // Cerrar ventana de registro
                switch (nuevoUsuario.getRol()) {
                    case ENTRENADOR:
                        new InicioEntrenadorVentana(nuevoUsuario);
                        break;
                    case DELEGADO:
                        // new InicioDelegadoVentana(nuevoUsuario);
                        break;
                    case JUGADOR:
                        // new InicioJugadorVentana(nuevoUsuario);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error en el registro. Revisa los datos.");
            }
        });


        // ---------- Volver al login
        login.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new LoginVentana();
            }
        });

        setVisible(true);
    }

    // Método para crear campos de texto con placeholder
    private JTextField crearCampoApellido(String placeholder, int x, int y) {
        JTextField campo = new JTextField(placeholder);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setForeground(Color.GRAY);
        campo.setBackground(new Color(18, 18, 18));
        campo.setBounds(x, y, 200, 45);
        campo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));

        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });

        return campo;
    }

    private JTextField crearCampo(String placeholder, int x, int y) {
        JTextField campo = new JTextField(placeholder);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setForeground(Color.GRAY);
        campo.setBackground(new Color(18, 18, 18));
        campo.setBounds(x, y, 380, 45);
        campo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));

        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });

        return campo;
    }


    // Método para campos de contraseña con placeholder
    private JPasswordField crearCampoPassword(String placeholder, int x, int y) {
        JPasswordField campo = new JPasswordField(placeholder);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setForeground(Color.GRAY);
        campo.setEchoChar((char) 0);
        campo.setBackground(new Color(18, 18, 18));
        campo.setBounds(x, y, 200, 45);
        campo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));

        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(campo.getPassword()).equals(placeholder)) {
                    campo.setText("");
                    campo.setEchoChar('•');
                    campo.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (String.valueOf(campo.getPassword()).isEmpty()) {
                    campo.setText(placeholder);
                    campo.setEchoChar((char) 0);
                    campo.setForeground(Color.GRAY);
                }
            }
        });

        return campo;
    }
}
