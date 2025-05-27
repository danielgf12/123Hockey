package main.view;

import main.controller.RegistroController;
import main.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * Ventana de registro para entrenadores. Permite introducir datos personales y
 * de acceso, y redirige a la ventana correspondiente según el rol creado.
 * Utiliza componentes personalizados con placeholders y estilos redondeados.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class RegistroVentana extends JFrame {

    /**
     * Constructor que inicializa y muestra la interfaz gráfica de registro.
     * Incluye campos personalizados, lógica de validación y navegación entre ventanas.
     */
    public RegistroVentana() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int mX     = (int)(screen.width  * 0.04);
        int headerH = (int)(screen.height * 0.07);
        int iconW  = headerH - 16;
        int iconH  = headerH - 16;

        Image appIcon = loadIcon("logoSinFondo.png", 32, 32).getImage();
        setIconImage(appIcon);

        int arcField      = 15;
        int bordField     = 1;
        Color fieldBg     = new Color(18, 18, 18);
        Color fieldBorder = new Color(49, 109, 233);

        int arcBtn   = 45 / 3;
        int bordBtn  = 2;
        Color normalBg     = new Color(49, 109, 233);
        Color hoverBg      = new Color(52, 115, 255);
        Color pressBg      = new Color(142, 173, 233);

        setTitle("123Hockey - Registro");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(18, 18, 18));
        setLayout(null);

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

        JLabel subtitulo = new JLabel("Regístrate aquí");
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        subtitulo.setForeground(new Color(195,195,195));
        subtitulo.setBounds(800, 90, 300, 40);
        add(subtitulo);

        JTextField nombre      = createField("Nombre",        700, 150, 380, arcField, bordField, fieldBg, fieldBorder);
        JTextField apellido1   = createField("Apellido 1",    700, 210, 180, arcField, bordField, fieldBg, fieldBorder);
        JTextField apellido2   = createField("Apellido 2",    900,210, 180, arcField, bordField, fieldBg, fieldBorder);
        JTextField email       = createField("Email",         700, 270, 380, arcField, bordField, fieldBg, fieldBorder);
        JTextField telefono    = createField("Teléfono",      700, 330, 380, arcField, bordField, fieldBg, fieldBorder);
        JPasswordField pwd     = createPasswordField("Contraseña",700, 390, 180, arcField, bordField, fieldBg, fieldBorder);
        JPasswordField confirm = createPasswordField("Confirmar", 900,390, 180, arcField, bordField, fieldBg, fieldBorder);

        add(nombre);
        add(apellido1);
        add(apellido2);
        add(email);
        add(telefono);
        add(pwd);
        add(confirm);

        JButton btnRegistrar = new JButton("Registrarse") {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = getModel();
                Color bg = m.isPressed() ? pressBg :
                        m.isRollover()? hoverBg : normalBg;

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arcBtn, arcBtn);

                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(bg);
                g2.drawRoundRect(bordBtn/2, bordBtn/2, w-bordBtn, h-bordBtn, arcBtn, arcBtn);

                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnRegistrar.setOpaque(false);
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBounds(700, 450, 380, 45);
        add(btnRegistrar);

        btnRegistrar.addActionListener(e -> {
            RegistroController rc = new RegistroController();
            Usuario u = rc.registrarEntrenador(
                    nombre.getText(), apellido1.getText(), apellido2.getText(),
                    email.getText(), telefono.getText(),
                    new String(pwd.getPassword()), new String(confirm.getPassword())
            );
            if (u != null) {
                dispose();
                switch (u.getRol()) {
                    case ENTRENADOR:
                        new InicioEntrenadorVentana(u);
                        break;
                    case DELEGADO:
                        new InicioDelegadoVentana(u);
                        break;
                    case JUGADOR:
                        new InicioJugadorVentana(u);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error en el registro. Revisa los datos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JLabel loginLink = new JLabel(
                "<html>"
                        + "<span style='color:#999999;'>¿Ya tienes cuenta? </span>"
                        + "<span style='color:#318DE1; text-decoration:underline; cursor:hand;'>Pulsa aquí para iniciar sesión</span>"
                        + "</html>"
        );
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.setBounds(700, 510, 450, 30);
        add(loginLink);

        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginVentana();
            }
        });

        setVisible(true);
    }

    /**
     * Carga un icono desde la carpeta assets con el tamaño especificado.
     *
     * @param name Nombre del archivo de imagen
     * @param w    Ancho deseado
     * @param h    Alto deseado
     * @return ImageIcon redimensionado
     */
    private ImageIcon loadIcon(String name, int w, int h) {
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u != null)
                ? new ImageIcon(u).getImage()
                : new ImageIcon("src/assets/" + name).getImage();
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    /**
     * Crea un campo de texto con estilo redondeado y placeholder.
     *
     * @param placeholder Texto por defecto
     * @param x           Posición x
     * @param y           Posición y
     * @param w           Ancho
     * @param arc         Radio de redondeo
     * @param bord        Grosor del borde
     * @param bg          Color de fondo
     * @param border      Color del borde
     * @return JTextField personalizado
     */
    private JTextField createField(String placeholder, int x, int y, int w,
                                   int arc, int bord,
                                   Color bg, Color border) {
        JTextField f = new JTextField(placeholder) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),arc,arc));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(bord));
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Float(bord/2f,bord/2f,
                        getWidth()-bord, getHeight()-bord, arc, arc));
                g2.dispose();
            }
        };

        f.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        f.setForeground(Color.GRAY);
        f.setBackground(bg);
        f.setOpaque(false);
        f.setBorder(BorderFactory.createEmptyBorder(0,12,0,12));
        f.setBounds(x, y, w, 45);
        f.setFocusable(false);

        f.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                f.setFocusable(true);
                f.requestFocusInWindow();
            }
        });
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(Color.WHITE);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(Color.GRAY);
                }
            }
        });

        return f;
    }

    /**
     * Crea un campo de contraseña personalizado con placeholder y estilo redondeado.
     *
     * @param placeholder Texto por defecto
     * @param x           Posición x
     * @param y           Posición y
     * @param w           Ancho
     * @param arc         Radio de redondeo
     * @param bord        Grosor del borde
     * @param bg          Color de fondo
     * @param border      Color del borde
     * @return JPasswordField personalizado
     */
    private JPasswordField createPasswordField(String placeholder, int x, int y, int w,
                                               int arc, int bord,
                                               Color bg, Color border) {
        JPasswordField p = new JPasswordField(placeholder) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),arc,arc));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(bord));
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Float(bord/2f,bord/2f,
                        getWidth()-bord,getHeight()-bord,arc,arc));
                g2.dispose();
            }
        };

        p.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        p.setForeground(Color.GRAY);
        p.setEchoChar((char)0);
        p.setBackground(bg);
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0,12,0,12));
        p.setBounds(x, y, w, 45);
        p.setFocusable(false);

        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                p.setFocusable(true);
                p.requestFocusInWindow();
            }
        });
        p.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (String.valueOf(p.getPassword()).equals(placeholder)) {
                    p.setText("");
                    p.setEchoChar('•');
                    p.setForeground(Color.WHITE);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (String.valueOf(p.getPassword()).isEmpty()) {
                    p.setText(placeholder);
                    p.setEchoChar((char)0);
                    p.setForeground(Color.GRAY);
                }
            }
        });

        return p;
    }
}
