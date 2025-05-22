package main.view;

import main.dao.JugadorInfoDAO;
import main.dao.UsuarioDAO;
import main.model.JugadorInfo;
import main.model.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlantillaVentana extends JFrame {

    // Variables de pantalla
    private int W, H, mX, headerH, sepY, btnY, btnW, btnH, gapX, panelY, panelGapX, panelW, panelH, innerMX;
    // Datos y panel de filas
    private List<Usuario> todos;
    private JPanel inner;
    private JTextField txtBuscar;
    private Set<String> selectedCategories;
    // Usuario logueado
    private Usuario usuarioLogado;

    // —— Añadido para ordenar ——
    private boolean sortByCategory = false;
    private boolean sortByPosition = false;
    private JPopupMenu ordenarMenu;
    // ——————————————————————

    public PlantillaVentana(Usuario usuario) {
        super("123Hockey - Plantilla");
        this.usuarioLogado = usuario;

        Image appIcon = loadIcon("logoSinFondo.png", 32, 32).getImage();
        setIconImage(appIcon);

        // --- Calculamos anchos/altos basados en pantalla ---
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        W = screen.width;
        H = screen.height;
        mX = (int)(W * 0.04);
        headerH = (int)(H * 0.07);
        sepY = mX + headerH;
        btnY = sepY + (int)(H * 0.04);
        btnW = (int)(W * 0.28);
        btnH = (int)(H * 0.12);
        gapX = mX;
        panelY = btnY + btnH + (int)(H * 0.04);
        panelGapX = mX;
        panelW = (W - 2*mX - panelGapX) / 2;
        panelH = (int)(H * 0.30);
        innerMX = (int)(panelW * 0.08);

        // --- Ventana principal ---
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(18, 18, 18));
        setLayout(null);

        // --- Botón “volver” ---
        JLabel back = new JLabel(loadIcon("arrow_back.png", headerH - 16, headerH - 16));
        back.setBounds(mX, mX+4, headerH - 16, headerH - 16);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (usuario.getRol()){
                    case ENTRENADOR:
                        new InicioEntrenadorVentana(usuario).setVisible(true);
                        break;
                    case DELEGADO:
                        new InicioDelegadoVentana(usuario).setVisible(true);
                        break;
                    case JUGADOR:
                        new InicioJugadorVentana(usuario).setVisible(true);
                        break;
                    default:
                        break;
                }
                dispose();
            }
        });
        add(back);

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

        // --- Avatar del usuario ---
        int avatarSize = headerH - 16;
        int avatarX    = W - mX - avatarSize;
        ImageIcon avatarIcon = cargarAvatar(usuario, avatarSize, avatarSize);
        JLabel avatar = new JLabel(avatarIcon);
        avatar.setBounds(avatarX, mX, avatarSize, avatarSize);
        add(avatar);

        // --- Saludo “Hola, <Nombre>” ---
        int greetW = avatarX - 2*mX + 30;
        JLabel hola = new JLabel("Hola, " + usuario.getNombre(), SwingConstants.RIGHT);
        hola.setFont(new Font("Segoe UI", Font.BOLD, headerH/2 - 2));
        hola.setForeground(new Color(49,109,233));
        hola.setBounds(mX, mX, greetW, avatarSize);
        add(hola);

        // --- Título de sección ---
        JLabel lblPlantilla = new JLabel("Plantilla");
        lblPlantilla.setFont(new Font("Segoe UI", Font.BOLD, headerH));
        lblPlantilla.setForeground(Color.WHITE);
        lblPlantilla.setBounds(mX, mX + headerH + 20, 350, headerH);
        add(lblPlantilla);

        int ctrlY = mX + headerH + 80;
        int ctrlH = (int)(headerH / 1.5);

        // ---------------------------------------
        // Campo de búsqueda con estilo login
        // ---------------------------------------
        int arcField   = 15, bordField  = 1;
        Color fieldBg     = new Color(18, 18, 18);
        Color fieldBorder = new Color(49, 109, 233);

        txtBuscar = new JTextField("Buscar por nombre…") {
            @Override protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fieldBg);
                g2.fillRoundRect(0, 0, w, h, arcField, arcField);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(bordField));
                g2.setColor(fieldBorder);
                double off = bordField / 2.0;
                g2.drawRoundRect((int)off, (int)off, w-bordField, h-bordField, arcField, arcField);
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
            @Override public void mouseClicked(MouseEvent e) {
                txtBuscar.setFocusable(true);
                txtBuscar.requestFocusInWindow();
            }
        });
        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtBuscar.getText().equals("Buscar por nombre…")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(Color.WHITE);
                }
            }
            @Override public void focusLost(FocusEvent e) {
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
        int arcBtn        = ctrlH / 3, bordBtn = 1;
        Color normalBg     = new Color(18, 18, 18);
        Color normalBorder = new Color(49, 109, 233);
        Color hoverBg      = new Color(49, 109, 233);
        Color hoverBorder  = new Color(49, 109, 233);
        Color pressBg      = new Color(142, 173, 233);
        Color pressBorder  = new Color(142, 173, 233);

        final JButton btnFiltrar = new JButton("Filtrar") {
            @Override protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight(), arc = arcBtn;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = getModel();
                Color bg = m.isPressed() ? pressBg : m.isRollover() ? hoverBg : normalBg;
                Color br = m.isPressed() ? pressBorder : m.isRollover() ? hoverBorder : normalBorder;
                g2.setColor(bg);
                g2.fillRoundRect(0,0,w,h,arc,arc);
                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(br);
                double off = bordBtn/2.0;
                g2.drawRoundRect((int)off,(int)off,w-bordBtn,h-bordBtn,arc,arc);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
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

        final JButton btnOrdenar = new JButton("Ordenar") {
            @Override protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight(), arc = arcBtn;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = getModel();
                Color bg = m.isPressed() ? pressBg : m.isRollover() ? hoverBg : normalBg;
                Color br = m.isPressed() ? pressBorder : m.isRollover() ? hoverBorder : normalBorder;
                g2.setColor(bg);
                g2.fillRoundRect(0,0,w,h,arc,arc);
                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(br);
                double off = bordBtn/2.0;
                g2.drawRoundRect((int)off,(int)off,w-bordBtn,h-bordBtn,arc,arc);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnOrdenar.setOpaque(false);
        btnOrdenar.setContentAreaFilled(false);
        btnOrdenar.setFocusPainted(false);
        btnOrdenar.setBorderPainted(false);
        btnOrdenar.setRolloverEnabled(true);
        btnOrdenar.setForeground(Color.WHITE);
        btnOrdenar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnOrdenar.setBounds(mX + (int)(W*0.3) + 140, ctrlY, 120, ctrlH);
        add(btnOrdenar);

        // ——— Botón “Nuevo usuario” ———
        JButton btnNuevo = new JButton("Nuevo usuario") {
            @Override protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight(), arc = arcBtn;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = getModel();
                Color bg = m.isPressed() ? pressBg : m.isRollover() ? hoverBg : normalBg;
                Color br = m.isPressed() ? pressBorder : m.isRollover() ? hoverBorder : normalBorder;
                g2.setColor(bg);
                g2.fillRoundRect(0,0,w,h,arc,arc);
                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(br);
                double off = bordBtn/2.0;
                g2.drawRoundRect((int)off,(int)off,w-bordBtn,h-bordBtn,arc,arc);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnNuevo.setOpaque(false);
        btnNuevo.setContentAreaFilled(false);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setBorderPainted(false);
        btnNuevo.setRolloverEnabled(true);
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnNuevo.setBounds(mX-21 + (int)(W*0.3) + 300, ctrlY, 180, ctrlH);
        btnNuevo.addActionListener(e -> {
            CrearUsuarioVentana dialog = new CrearUsuarioVentana(
                    PlantillaVentana.this,
                    () -> filtrar(txtBuscar.getText())
            );
            dialog.setVisible(true);
        });
        // Solo ENTRENADOR o DELEGADO ven este botón:
        if (usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR
                || usuarioLogado.getRol() == Usuario.Rol.DELEGADO) {
            add(btnNuevo);
        }

        // --- Datos y panel interno ---
        todos = new UsuarioDAO().listarTodos();
        inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(new Color(18,18,18));

        // --- Menú emergente de categorías ---
        List<String> categories = new JugadorInfoDAO().listarTodos().stream()
                .map(JugadorInfo::getCategoria)
                .filter(c -> c != null && !c.isEmpty())
                .distinct().sorted()
                .collect(Collectors.toList());
        categories.add("Otros");
        selectedCategories = new HashSet<>(categories);
        final JPopupMenu categoryMenu = new JPopupMenu();
        for (String cat : categories) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(cat, true);
            categoryMenu.add(item);
            item.addActionListener(e -> {
                if (item.isSelected()) selectedCategories.add(cat);
                else                    selectedCategories.remove(cat);
                filtrar(txtBuscar.getText());
            });
        }
        btnFiltrar.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                categoryMenu.show(btnFiltrar, 0, btnFiltrar.getHeight());
            }
        });

        // —— Añadido: menú emergente para “Ordenar” ——
        ordenarMenu = new JPopupMenu();
        JCheckBoxMenuItem orderCat = new JCheckBoxMenuItem("Ordenar por categoría", sortByCategory);
        JCheckBoxMenuItem orderPos = new JCheckBoxMenuItem("Ordenar por posición", sortByPosition);
        ordenarMenu.add(orderCat);
        ordenarMenu.add(orderPos);
        orderCat.addItemListener(e -> {
            sortByCategory = orderCat.isSelected();
            filtrar(txtBuscar.getText());
        });
        orderPos.addItemListener(e -> {
            sortByPosition = orderPos.isSelected();
            filtrar(txtBuscar.getText());
        });
        btnOrdenar.addActionListener(e ->
                ordenarMenu.show(btnOrdenar, 0, btnOrdenar.getHeight())
        );
        // ————————————————————————————————

        // --- Búsqueda en tiempo real ---
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrar(txtBuscar.getText()); }
            public void removeUpdate(DocumentEvent e)  { filtrar(txtBuscar.getText()); }
            public void changedUpdate(DocumentEvent e) { filtrar(txtBuscar.getText()); }
        });

        // --- Scrollpane que ocupa el resto de la pantalla ---
        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBounds(mX, mX + headerH + 120, W, H - (mX + headerH + 200));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll);

        // --- Poblado inicial sin filtro ---
        filtrar("");

        revalidate();
        repaint();
        setVisible(true);
    }

    /** Realiza el filtrado de la lista según el texto y las categorías y orden **/
    private void filtrar(String texto) {
        if ("Buscar por nombre…".equals(texto)) texto = "";
        String t = texto == null ? "" : texto.toLowerCase().trim();

        inner.removeAll();

        // Jugadores con filtro de nombre + categoría
        inner.add(createSectionHeader("Jugadores"));
        List<Usuario> jugadores = todos.stream()
                .filter(u -> u.getRol() == Usuario.Rol.JUGADOR)
                .filter(u -> (u.getNombre() + " " + u.getApellidos()).toLowerCase().contains(t))
                .filter(u -> {
                    JugadorInfo info = new JugadorInfoDAO().buscarPorUsuarioId(u.getId());
                    String cat = (info != null && info.getCategoria() != null && !info.getCategoria().isEmpty())
                            ? info.getCategoria()
                            : "Otros";
                    return selectedCategories.contains(cat);
                })
                .collect(Collectors.toList());

        // aplicar orden si está seleccionado
        if (sortByCategory || sortByPosition) {
            Comparator<Usuario> comp = (u1, u2) -> 0;
            if (sortByCategory) {
                comp = Comparator.comparing(u -> {
                    JugadorInfo info = new JugadorInfoDAO().buscarPorUsuarioId(u.getId());
                    return info != null ? info.getCategoria() : "";
                });
            }
            if (sortByPosition) {
                Comparator<Usuario> posComp = Comparator.comparing(u -> {
                    JugadorInfo info = new JugadorInfoDAO().buscarPorUsuarioId(u.getId());
                    return info != null && info.getPosicion() != null
                            ? info.getPosicion().name() : "";
                });
                comp = sortByCategory ? comp.thenComparing(posComp) : posComp;
            }
            jugadores.sort(comp);
        }
        jugadores.forEach(u -> inner.add(createRow(u)));

        // Cuerpo técnico (delegados + entrenadores)
        inner.add(createSectionHeader("Cuerpo técnico"));
        todos.stream()
                .filter(u -> u.getRol() == Usuario.Rol.DELEGADO || u.getRol() == Usuario.Rol.ENTRENADOR)
                .filter(u -> (u.getNombre() + " " + u.getApellidos()).toLowerCase().contains(t))
                .forEach(u -> inner.add(createRow(u)));

        inner.revalidate();
        inner.repaint();
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

    /** Fila para cada usuario (Jugador o Delegado/Entrenador) **/
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
            lblCat.setText(info != null ? info.getCategoria() : "–");
        } else {
            lblCat.setText(u.getTelefono());
        }
        p.add(lblCat);

        // Posición o Rol (Delegado/Entrenador)
        JLabel lblTipo = new JLabel();
        lblTipo.setForeground(Color.WHITE);
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTipo.setBounds(740, 0, 150, rowH);
        if (u.getRol() == Usuario.Rol.JUGADOR) {
            JugadorInfo info = new JugadorInfoDAO().buscarPorUsuarioId(u.getId());
            lblTipo.setText(info != null && info.getPosicion()!=null
                    ? info.getPosicion().name() : "–");
        } else {
            lblTipo.setText(u.getRol().toString());
        }
        p.add(lblTipo);

        // “Ver ficha...” clicable según permisos
        boolean canView = usuarioLogado.getRol() != Usuario.Rol.JUGADOR
                || usuarioLogado.getId() == u.getId();
        if (canView) {
            JLabel lblFicha = new JLabel(
                    "<html><span style='color:#318DE1; text-decoration:underline; cursor:hand;'>Ver ficha...</span></html>"
            );
            lblFicha.setBounds(1100, 0, 100, rowH);
            lblFicha.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    new FichaUsuarioVentana(usuarioLogado, u).setVisible(true);
                }
            });
            p.add(lblFicha);
        }

        return p;
    }

    /** Carga avatar desde BBDD o recurso por defecto **/
    private ImageIcon cargarAvatar(Usuario u, int w, int h) {
        try {
            byte[] f = u.getFotoUsuario();
            if (f != null && f.length > 0) {
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

    /** Carga iconos desde /assets **/
    private ImageIcon loadIcon(String name,int w,int h){
        URL u = getClass().getClassLoader().getResource("assets/"+name);
        Image img = (u!=null)
                ? new ImageIcon(u).getImage()
                : new ImageIcon("src/assets/" + name).getImage();
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }
}
