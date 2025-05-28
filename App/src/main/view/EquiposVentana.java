package main.view;

import main.dao.EquipoDAO;
import main.dao.EquipoJugadorDAO;
import main.model.Equipo;
import main.model.EquipoJugador;
import main.model.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ventana que muestra el listado de equipos disponibles. Permite buscar,
 * filtrar por categoría, ordenar por liga o categoría y, para entrenadores,
 * crear un nuevo equipo. Cada fila permite acceder al detalle del equipo.
 * 
 * @author Daniel García
 * @version 1.0
 */
public class EquiposVentana extends JFrame {

    private int W, H, mX, headerH, sepY, ctrlY, ctrlH;
    private List<Equipo> todos;
    private JPanel inner;
    private JTextField txtBuscar;
    private Set<String> selectedCategories;
    private final Usuario usuarioLogado;
    private boolean sortByCategoria = false;
    private boolean sortByLiga = false;
    private JPopupMenu ordenarMenu;

    /**
     * Construye la ventana de equipos para el usuario especificado.
     *
     * @param usuario usuario logueado que utiliza la ventana
     */
    public EquiposVentana(Usuario usuario) {
        super("123Hockey – Equipos");
        this.usuarioLogado = usuario;

        Image appIcon = loadIcon("logoSinFondo.png", 32, 32).getImage();
        setIconImage(appIcon);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        W = screen.width;
        H = screen.height;
        mX = (int) (W * 0.04);
        headerH = (int) (H * 0.07);
        sepY = mX + headerH;
        ctrlY = mX + headerH + 80;
        ctrlH = (int) (headerH / 1.5);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(18, 18, 18));
        setLayout(null);

        initUI();
    }

    /**
     * Inicializa los componentes de la interfaz: cabecera, controles de búsqueda
     * y ordenación, panel de resultados y comportamiento de eventos.
     */
    private void initUI() {
        JLabel back = new JLabel(loadIcon("arrow_back.png", headerH - 16, headerH - 16));
        back.setBounds(mX, mX + 4, headerH - 16, headerH - 16);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                switch (usuarioLogado.getRol()) {
                    case ENTRENADOR:
                        new InicioEntrenadorVentana(usuarioLogado).setVisible(true);
                        break;
                    case DELEGADO:
                        new InicioDelegadoVentana(usuarioLogado).setVisible(true);
                        break;
                    case JUGADOR:
                        new InicioJugadorVentana(usuarioLogado).setVisible(true);
                        break;
                }
                dispose();
            }
        });
        add(back);

        JLabel title = new JLabel("123HOCKEY!");
        title.setFont(new Font("Arial Black", Font.BOLD, headerH / 2 + 4));
        title.setForeground(new Color(49, 109, 233));
        title.setBounds(mX + headerH, mX - 6, (int) (W * 0.3), headerH);
        add(title);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(49, 109, 233));
        sep.setBounds(mX, sepY, W - 2 * mX, 2);
        add(sep);

        int avatarSize = headerH - 16;
        int avatarX = W - mX - avatarSize;
        JLabel avatar = new JLabel(cargarAvatar(usuarioLogado, avatarSize, avatarSize));
        avatar.setBounds(avatarX, mX, avatarSize, avatarSize);
        add(avatar);

        JLabel hola = new JLabel("Hola, " + usuarioLogado.getNombre(), SwingConstants.RIGHT);
        hola.setFont(new Font("Segoe UI", Font.BOLD, headerH / 2 - 2));
        hola.setForeground(new Color(49, 109, 233));
        hola.setBounds(mX, mX, avatarX - 2 * mX + 30, avatarSize);
        add(hola);

        JLabel lbl = new JLabel("Equipos");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, headerH));
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(mX, mX + headerH + 17, 350, headerH + 10);
        add(lbl);

        int arcField = 15, bordField = 1;
        Color fieldBg = new Color(18, 18, 18);
        Color fieldBorder = new Color(49, 109, 233);
        txtBuscar = new JTextField("Buscar por nombre…") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fieldBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcField, arcField);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(bordField));
                g2.setColor(fieldBorder);
                double off = bordField / 2.0;
                g2.drawRoundRect((int) off, (int) off, getWidth() - bordField, getHeight() - bordField, arcField, arcField);
                g2.dispose();
            }
        };
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtBuscar.setForeground(Color.GRAY);
        txtBuscar.setOpaque(false);
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        txtBuscar.setBounds(mX, ctrlY + 5, (int) (W * 0.3), ctrlH);
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

        int arcBtn = ctrlH / 3, bordBtn = 1;
        Color normalBg = new Color(18, 18, 18);
        Color normalBorder = new Color(49, 109, 233);
        Color hoverBg = new Color(49, 109, 233);
        Color hoverBorder = new Color(49, 109, 233);
        Color pressBg = new Color(142, 173, 233);
        Color pressBorder = new Color(142, 173, 233);

        JButton btnFiltrar = new JButton("Filtrar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = getModel();
                Color bg = m.isPressed() ? pressBg : m.isRollover() ? hoverBg : normalBg;
                Color br = m.isPressed() ? pressBorder : m.isRollover() ? hoverBorder : normalBorder;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcBtn, arcBtn);
                g2.setStroke(new BasicStroke(bordBtn));
                double off = bordBtn / 2.0;
                g2.setColor(br);
                g2.drawRoundRect((int) off, (int) off, getWidth() - bordBtn, getHeight() - bordBtn, arcBtn, arcBtn);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnFiltrar.setOpaque(false);
        btnFiltrar.setContentAreaFilled(false);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setBorderPainted(false);
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnFiltrar.setBounds(mX + (int) (W * 0.3) + 20, ctrlY + 5, 100, ctrlH);
        add(btnFiltrar);

        JButton btnOrdenar = new JButton("Ordenar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = getModel();
                Color bg = m.isPressed() ? pressBg : m.isRollover() ? hoverBg : normalBg;
                Color br = m.isPressed() ? pressBorder : m.isRollover() ? hoverBorder : normalBorder;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcBtn, arcBtn);
                g2.setStroke(new BasicStroke(bordBtn));
                double off = bordBtn / 2.0;
                g2.setColor(br);
                g2.drawRoundRect((int) off, (int) off, getWidth() - bordBtn, getHeight() - bordBtn, arcBtn, arcBtn);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnOrdenar.setOpaque(false);
        btnOrdenar.setContentAreaFilled(false);
        btnOrdenar.setFocusPainted(false);
        btnOrdenar.setBorderPainted(false);
        btnOrdenar.setForeground(Color.WHITE);
        btnOrdenar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnOrdenar.setBounds(mX + (int) (W * 0.3) + 140, ctrlY + 5, 120, ctrlH);
        add(btnOrdenar);

        if (usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR) {
            JButton btnNuevo = new JButton("Nuevo equipo") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    ButtonModel m = getModel();
                    Color bg = m.isPressed() ? pressBg : m.isRollover() ? hoverBg : normalBg;
                    Color br = m.isPressed() ? pressBorder : m.isRollover() ? hoverBorder : normalBorder;
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcBtn, arcBtn);
                    g2.setStroke(new BasicStroke(bordBtn));
                    double off = bordBtn / 2.0;
                    g2.setColor(br);
                    g2.drawRoundRect((int) off, (int) off, getWidth() - bordBtn, getHeight() - bordBtn, arcBtn, arcBtn);
                    g2.dispose();
                    super.paintComponent(g);
                }
                @Override protected void paintBorder(Graphics g) {}
            };
            btnNuevo.setOpaque(false);
            btnNuevo.setContentAreaFilled(false);
            btnNuevo.setFocusPainted(false);
            btnNuevo.setBorderPainted(false);
            btnNuevo.setForeground(Color.WHITE);
            btnNuevo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btnNuevo.setBounds(mX + (int) (W * 0.3) + 280, ctrlY + 5, 160, ctrlH);
            btnNuevo.addActionListener(e -> {
                new CrearEquipoVentana(this, () -> filtrar(txtBuscar.getText())).setVisible(true);
            });
            add(btnNuevo);
        }

        cargarDatos();
        buildCategoryMenu(btnFiltrar);
        buildOrderMenu(btnOrdenar);

        inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(new Color(18, 18, 18));

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBounds(mX, mX + headerH + 120, W, H - (mX + headerH + 200));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll);

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(txtBuscar.getText()); }
            public void removeUpdate(DocumentEvent e) { filtrar(txtBuscar.getText()); }
            public void changedUpdate(DocumentEvent e) { filtrar(txtBuscar.getText()); }
        });

        filtrar("");
        setVisible(true);
    }

    /**
     * Carga los datos de los equipos según el rol del usuario:
     * entrenadores y delegados ven todos, jugadores solo los de sus equipos.
     */
    private void cargarDatos() {
        EquipoDAO edao = new EquipoDAO();
        if (usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR
                || usuarioLogado.getRol() == Usuario.Rol.DELEGADO) {
            todos = edao.listarTodos();
        } else {
            EquipoJugadorDAO ejdao = new EquipoJugadorDAO();
            todos = ejdao.listarPorJugador(usuarioLogado.getId())
                    .stream()
                    .map(EquipoJugador::getEquipo)
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    /**
     * Construye el menú de categorías para filtrado.
     *
     * @param btnFiltrar botón que mostrará el menú
     */
    private void buildCategoryMenu(JButton btnFiltrar) {
        Set<String> cats = todos.stream()
                .map(Equipo::getCategoria)
                .filter(Objects::nonNull)
                .filter(c -> !c.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new));
        cats.add("Otros");
        selectedCategories = new HashSet<>(cats);
        JPopupMenu catMenu = new JPopupMenu();
        for (String c : cats) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(c, true);
            catMenu.add(item);
            item.addActionListener(e -> {
                if (item.isSelected()) selectedCategories.add(c);
                else selectedCategories.remove(c);
                filtrar(txtBuscar.getText());
            });
        }
        btnFiltrar.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                catMenu.show(btnFiltrar, 0, btnFiltrar.getHeight());
            }
        });
    }

    /**
     * Construye el menú de ordenación por categoría o liga.
     *
     * @param btnOrdenar botón que mostrará el menú
     */
    private void buildOrderMenu(JButton btnOrdenar) {
        ordenarMenu = new JPopupMenu();
        JCheckBoxMenuItem byCat = new JCheckBoxMenuItem("Ordenar por categoría", sortByCategoria);
        JCheckBoxMenuItem byLiga = new JCheckBoxMenuItem("Ordenar por liga", sortByLiga);
        ordenarMenu.add(byCat);
        ordenarMenu.add(byLiga);
        byCat.addItemListener(e -> {
            sortByCategoria = byCat.isSelected();
            filtrar(txtBuscar.getText());
        });
        byLiga.addItemListener(e -> {
            sortByLiga = byLiga.isSelected();
            filtrar(txtBuscar.getText());
        });
        btnOrdenar.addActionListener(e -> ordenarMenu.show(btnOrdenar, 0, btnOrdenar.getHeight()));
    }

    /**
     * Filtra y muestra la lista de equipos según el texto de búsqueda,
     * categorías seleccionadas y criterio de orden.
     *
     * @param texto texto ingresado en el campo de búsqueda
     */
    private void filtrar(String texto) {
        if ("Buscar por nombre…".equals(texto)) texto = "";
        String t = texto == null ? "" : texto.toLowerCase().trim();
        inner.removeAll();
        List<Equipo> lista = todos.stream()
                .filter(eq -> eq.getNombre().toLowerCase().contains(t))
                .filter(eq -> {
                    String cat = (eq.getCategoria() != null && !eq.getCategoria().isEmpty())
                            ? eq.getCategoria() : "Otros";
                    return selectedCategories.contains(cat);
                })
                .collect(Collectors.toList());
        if (sortByCategoria || sortByLiga) {
            Comparator<Equipo> comp = Comparator.comparing(e -> "");
            if (sortByCategoria) {
                comp = Comparator.comparing(Equipo::getCategoria, Comparator.nullsFirst(String::compareTo));
            }
            if (sortByLiga) {
                Comparator<Equipo> ligaComp = Comparator.comparing(Equipo::getLiga, Comparator.nullsFirst(String::compareTo));
                comp = sortByCategoria ? comp.thenComparing(ligaComp) : ligaComp;
            }
            lista.sort(comp);
        }
        lista.forEach(eq -> inner.add(createRow(eq)));
        inner.revalidate();
        inner.repaint();
    }

    /**
     * Crea el panel que representa una fila con los datos de un equipo.
     *
     * @param eq equipo a mostrar
     * @return panel con la fila del equipo
     */
    private JPanel createRow(Equipo eq) {
        int rowH = 80;
        JPanel p = new JPanel(null);
        p.setPreferredSize(new Dimension(0, rowH));
        p.setBackground(new Color(18, 18, 18));

        int iconSize = rowH - 20;
        JLabel img = new JLabel(cargarFotoEquipo(eq, iconSize, iconSize));
        img.setBounds(10, 10, iconSize, iconSize);
        p.add(img);

        JLabel nombre = new JLabel(eq.getNombre());
        nombre.setForeground(Color.WHITE);
        nombre.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        nombre.setBounds(30 + iconSize, 0, 300, rowH);
        p.add(nombre);

        JLabel categoria = new JLabel(eq.getCategoria() != null ? eq.getCategoria() : "–");
        categoria.setForeground(Color.WHITE);
        categoria.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        categoria.setBounds(440, 0, 200, rowH);
        p.add(categoria);

        JLabel liga = new JLabel(eq.getLiga() != null ? eq.getLiga() : "–");
        liga.setForeground(Color.WHITE);
        liga.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        liga.setBounds(740, 0, 200, rowH);
        p.add(liga);

        JLabel ver = new JLabel(
            "<html><span style='color:#318DE1; text-decoration:underline; cursor:hand;'>Ver equipo...</span></html>"
        );
        ver.setBounds(1100, 0, 120, rowH);
        ver.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                new FichaEquipoVentana(usuarioLogado, eq).setVisible(true);
            }
        });
        p.add(ver);

        return p;
    }

    /**
     * Carga y recorta la foto de un equipo en forma circular.
     *
     * @param eq equipo cuyo logotipo se muestra
     * @param w  ancho deseado en píxeles
     * @param h  alto deseado en píxeles
     * @return ImageIcon con la imagen recortada, o icono por defecto
     */
    private ImageIcon cargarFotoEquipo(Equipo eq, int w, int h) {
        try {
            byte[] data = eq.getFotoEquipo();
            if (data != null && data.length > 0) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(data));
                BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = dst.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new Ellipse2D.Float(0, 0, w, h));
                g2.drawImage(src, 0, 0, w, h, null);
                g2.dispose();
                return new ImageIcon(dst);
            }
        } catch (Exception ignored) {}
        return loadIcon("user_default.png", w, h);
    }

    /**
     * Carga y recorta el avatar del usuario en forma circular.
     *
     * @param u usuario cuyo avatar se mostrará
     * @param w ancho deseado en píxeles
     * @param h alto deseado en píxeles
     * @return ImageIcon con el avatar recortado, o icono por defecto
     */
    private ImageIcon cargarAvatar(Usuario u, int w, int h) {
        try {
            byte[] f = u.getFotoUsuario();
            if (f != null && f.length > 0) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(f));
                BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = dst.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new Ellipse2D.Float(0, 0, w, h));
                g2.drawImage(src, 0, 0, w, h, null);
                g2.dispose();
                return new ImageIcon(dst);
            }
        } catch (Exception ignored) {}
        return loadIcon("user_default.png", w, h);
    }

    /**
     * Carga un icono desde recursos empaquetados y lo escala al tamaño indicado.
     *
     * @param name nombre del recurso dentro de assets
     * @param w    ancho deseado en píxeles
     * @param h    alto deseado en píxeles
     * @return ImageIcon escalado o imagen transparente si no se encuentra recurso
     */
    private ImageIcon loadIcon(String name, int w, int h) {
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u != null)
            ? new ImageIcon(u).getImage()
            : new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
}
