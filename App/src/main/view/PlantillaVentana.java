package main.view;

import main.dao.JugadorInfoDAO;
import main.dao.UsuarioDAO;
import main.model.JugadorInfo;
import main.model.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class PlantillaVentana extends JFrame {

    // Variables de pantalla
    private int W, H, mX, headerH, sepY, btnY, btnW, btnH, gapX, panelY, panelGapX, panelW, panelH, innerMX;

    public PlantillaVentana(Usuario entrenador) {
        super("123Hockey - Plantilla");

        // --- Calculamos anchos/altos basados en pantalla ---
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        W       = screen.width;
        H       = screen.height;
        mX      = (int)(W * 0.04);
        headerH = (int)(H * 0.07);
        sepY    = mX + headerH;
        btnY    = sepY + (int)(H * 0.04);
        btnW    = (int)(W * 0.28);
        btnH    = (int)(H * 0.12);
        gapX    = mX;
        panelY  = btnY + btnH + (int)(H * 0.04);
        panelGapX = mX;
        panelW  = (W - 2*mX - panelGapX) / 2;
        panelH  = (int)(H * 0.30);
        innerMX = (int)(panelW * 0.08);

        // --- Ventana principal ---
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(18, 18, 18));
        setLayout(null);

        // --- Icono de la app ---
        Image appIcon = loadIcon("logoSinFondo.png", 32, 32).getImage();
        setIconImage(appIcon);

        // --- Menú (logo) ---
        JLabel menu = new JLabel(loadIcon("logoSinFondo.png", headerH - 16, headerH - 16));
        menu.setBounds(mX, mX, headerH - 16, headerH - 16);
        add(menu);

        // --- Título “123HOCKEY!” ---
        JLabel title = new JLabel("123HOCKEY!");
        title.setFont(new Font("Arial Black", Font.BOLD, headerH/2 + 4));
        title.setForeground(new Color(49,109,233));
        title.setBounds(mX + headerH, mX - 6, (int)(W*0.3), headerH);
        add(title);

        // --- Separador bajo cabecera ---
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(49,109,233));
        sep.setBounds(mX, sepY, W - 2*mX, 2);
        add(sep);

        // --- Avatar del entrenador ---
        int avatarSize = headerH - 16;
        int avatarX    = W - mX - avatarSize;
        ImageIcon avatarIcon = cargarAvatar(entrenador, avatarSize, avatarSize);
        JLabel avatar = new JLabel(avatarIcon);
        avatar.setBounds(avatarX, mX, avatarSize, avatarSize);
        add(avatar);

        // --- Saludo “Hola, <Nombre>” ---
        int greetW = avatarX - 2*mX + 30;
        JLabel hola = new JLabel("Hola, " + entrenador.getNombre(), SwingConstants.RIGHT);
        hola.setFont(new Font("Segoe UI", Font.BOLD, headerH/2 - 2));
        hola.setForeground(new Color(49,109,233));
        hola.setBounds(mX, mX, greetW, avatarSize);
        add(hola);

        // --- Título de sección y controles de búsqueda ---
        JLabel lblPlantilla = new JLabel("Plantilla");
        lblPlantilla.setFont(new Font("Segoe UI", Font.BOLD, headerH));
        lblPlantilla.setForeground(Color.WHITE);
        lblPlantilla.setBounds(mX, mX + headerH + 20, 350, headerH);
        add(lblPlantilla);

        int ctrlY = mX + headerH + 80;
        int ctrlH = (int)(headerH / 1.5);

        // ---------------------------------------
        // Campo de búsqueda con estilo igual al login
        // ---------------------------------------
        int arcField   = 15;             // radio de las esquinas
        int bordField  = 1;              // grosor del borde
        Color fieldBg     = new Color(18, 18, 18);
        Color fieldBorder = new Color(49, 109, 233);

        JTextField txtBuscar = new JTextField("Buscar por nombre…") {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo redondeado
                g2.setColor(fieldBg);
                g2.fillRoundRect(0, 0, w, h, arcField, arcField);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // Borde redondeado
                g2.setStroke(new BasicStroke(bordField));
                g2.setColor(fieldBorder);
                double off = bordField / 2.0;
                g2.drawRoundRect((int)off, (int)off,
                        w - bordField, h - bordField,
                        arcField, arcField);
                g2.dispose();
            }
        };
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtBuscar.setForeground(Color.GRAY);
        txtBuscar.setOpaque(false);
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        txtBuscar.setBounds(mX, ctrlY, (int)(W*0.3), ctrlH);
        txtBuscar.setFocusable(false);
        txtBuscar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txtBuscar.setFocusable(true);
                txtBuscar.requestFocusInWindow();
            }
        });
        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtBuscar.getText().equals("Buscar por nombre…")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtBuscar.getText().isEmpty()) {
                    txtBuscar.setText("Buscar por nombre…");
                    txtBuscar.setForeground(Color.GRAY);
                }
            }
        });
        add(txtBuscar);

        // ---------------------------------------
        // Botones redondeados al estilo login
        // ---------------------------------------
        int arcBtn        = ctrlH / 3;
        int bordBtn       = 1;
        Color normalBg     = new Color(18, 18, 18);
        Color normalBorder = new Color(49, 109, 233);
        Color hoverBg      = new Color(49, 109, 233);
        Color hoverBorder  = new Color(49, 109, 233);
        Color pressBg      = new Color(142, 173, 233);
        Color pressBorder  = new Color(142, 173, 233);

        JButton btnFiltrar = new JButton("Filtrar") {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight(), arc = arcBtn;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel m = getModel();
                Color bg, br;
                if (m.isPressed()) {
                    bg = pressBg; br = pressBorder;
                } else if (m.isRollover()) {
                    bg = hoverBg; br = hoverBorder;
                } else {
                    bg = normalBg; br = normalBorder;
                }

                // Fondo redondeado
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // Borde redondeado
                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(br);
                double off = bordBtn / 2.0;
                g2.drawRoundRect((int)off, (int)off, w - bordBtn, h - bordBtn, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) { /* nada */ }
        };
        btnFiltrar.setOpaque(false);
        btnFiltrar.setContentAreaFilled(false);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setBorderPainted(false);
        btnFiltrar.setRolloverEnabled(true);
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnFiltrar.setBounds(mX + (int)(W*0.3) + 20, ctrlY, 100, ctrlH);
        add(btnFiltrar);

        JButton btnOrdenar = new JButton("Ordenar") {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight(), arc = arcBtn;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel m = getModel();
                Color bg, br;
                if (m.isPressed()) {
                    bg = pressBg; br = pressBorder;
                } else if (m.isRollover()) {
                    bg = hoverBg; br = hoverBorder;
                } else {
                    bg = normalBg; br = normalBorder;
                }

                // Fondo redondeado
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // Borde redondeado
                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(br);
                double off = bordBtn / 2.0;
                g2.drawRoundRect((int)off, (int)off, w - bordBtn, h - bordBtn, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) { /* nada */ }
        };
        btnOrdenar.setOpaque(false);
        btnOrdenar.setContentAreaFilled(false);
        btnOrdenar.setFocusPainted(false);
        btnOrdenar.setBorderPainted(false);
        btnOrdenar.setRolloverEnabled(true);
        btnOrdenar.setForeground(Color.WHITE);
        btnOrdenar.setFont(new Font("Segoe UI", Font.BOLD, 18));
// aquí cambiamos la anchura de 100 a 120
        btnOrdenar.setBounds(mX + (int)(W*0.3) + 140, ctrlY, 120, ctrlH);
        add(btnOrdenar);


        // --- Panel interno con BoxLayout para filas ---
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(new Color(18,18,18));

        // --- Sección Jugadores ---
        inner.add(createSectionHeader("Jugadores"));
        List<Usuario> todos = new UsuarioDAO().listarTodos();
        List<Usuario> jugadores = todos.stream()
                .filter(u -> u.getRol() == Usuario.Rol.JUGADOR)
                .collect(Collectors.toList());
        jugadores.forEach(u -> inner.add(createRow(u)));

        // --- Sección Cuerpo técnico (Delegados) ---
        inner.add(createSectionHeader("Cuerpo técnico"));
        List<Usuario> delegados = todos.stream()
                .filter(u -> u.getRol() == Usuario.Rol.DELEGADO)
                .collect(Collectors.toList());
        delegados.forEach(u -> inner.add(createRow(u)));

        // --- Scrollpane que ocupa el resto de la pantalla ---
        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBounds(
                mX,
                mX + headerH + 120,
                W,
                H - (mX + headerH + 200)
        );
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll);

        revalidate();
        repaint();
        setVisible(true);
    }

    /** Cabecera de sección (“Jugadores”, “Cuerpo técnico”) **/
    private JPanel createSectionHeader(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(18, 18, 18));
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    /** Fila para cada usuario (Jugador o Delegado) **/
    private JPanel createRow(Usuario u){
        int rowH = 80;
        JPanel p = new JPanel(null);
        p.setPreferredSize(new Dimension(0, rowH));
        p.setBackground(new Color(18,18,18));

        // Avatar
        int iconSize = rowH - 20;
        ImageIcon av = cargarAvatar(u, iconSize, iconSize);
        JLabel lblIcon = new JLabel(av);
        lblIcon.setBounds(10,10,iconSize,iconSize);
        p.add(lblIcon);

        // Nombre completo
        JLabel lblNombre = new JLabel(u.getNombre() + " " + u.getApellidos());
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblNombre.setBounds(30 + iconSize, 0, 300, rowH);
        p.add(lblNombre);

        // Categoría o Teléfono
        JLabel lblCat = new JLabel();
        lblCat.setForeground(Color.WHITE);
        lblCat.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblCat.setBounds(440, 0, 200, rowH);
        if (u.getRol() == Usuario.Rol.JUGADOR) {
            JugadorInfo info = new JugadorInfoDAO().buscarPorUsuarioId(u.getId());
            String cat = (info != null ? info.getCategoria() : "–");
            lblCat.setText(cat);
        } else {
            // delegado
            lblCat.setText(u.getTelefono());
        }
        p.add(lblCat);

        // Tipo / Posición
        JLabel lblTipo = new JLabel();
        lblTipo.setForeground(Color.WHITE);
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTipo.setBounds(740, 0, 150, rowH);
        if (u.getRol() == Usuario.Rol.JUGADOR) {
            JugadorInfo info = new JugadorInfoDAO().buscarPorUsuarioId(u.getId());
            String pos = (info != null && info.getPosicion()!=null)
                    ? info.getPosicion().name()
                    : "–";
            lblTipo.setText(pos);
        } else {
            lblTipo.setText("Delegado");
        }
        p.add(lblTipo);

        // “Ver ficha...” clicable
        JLabel lblFicha = new JLabel(
                "<html><span style='color:#318DE1; text-decoration:underline; cursor:hand;'>Ver ficha...</span></html>"
        );
        lblFicha.setBounds(1100, 0, 100, rowH);
        lblFicha.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO: abrir ventana de ficha de 'u'
            }
        });
        p.add(lblFicha);

        return p;
    }

    /** Carga avatar desde BBDD o recurso por defecto **/
    private ImageIcon cargarAvatar(Usuario u, int w, int h) {
        byte[] foto = u.getFotoUsuario();
        try {
            if (foto != null && foto.length > 0) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(foto));
                BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = buf.createGraphics();
                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC
                );
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.drawImage(src, 0, 0, w, h, null);
                g2.dispose();
                return new ImageIcon(buf);
            }
        } catch (Exception ignored) { }
        return loadIcon("user_default.png", w, h);
    }

    /** Carga iconos estáticos desde /assets **/
    private ImageIcon loadIcon(String name, int w, int h){
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u!=null)
                ? new ImageIcon(u).getImage()
                : new ImageIcon("src/assets/" + name).getImage();
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
}
