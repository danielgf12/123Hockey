package main.view;

import main.dao.EntrenamientoDAO;
import main.dao.EquipoDAO;
import main.dao.PartidoDAO;
import main.dao.EquipoJugadorDAO;
import main.model.Entrenamiento;
import main.model.Partido;
import main.model.EquipoJugador;
import main.model.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Ventana principal que muestra el calendario de entrenamientos y partidos para el usuario autenticado.
 *
 * Permite filtrar eventos por fecha y tipo, y crear nuevos eventos si el usuario tiene rol de entrenador.
 *
 * @author Daniel García
 * @version 1.0
 */
public class CalendarioVentana extends JFrame {
    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,109,233);

    private final Usuario usuarioLogado;
    private final List<Evento> eventos = new ArrayList<>();

    private JTextField txtFechaBuscar;
    private JButton    btnFiltrar, btnNuevoEntrenamiento, btnNuevoPartido;
    private JPanel     inner;

    private Set<String> selectedTipos;
    private JPopupMenu tipoMenu;

    private int W, H, mX, headerH, sepY, ctrlY, ctrlH;

    /**
     * Construye la ventana de calendario para el usuario especificado.
     *
     * @param usuario usuario autenticado para el cual se muestra el calendario
     */
    public CalendarioVentana(Usuario usuario) {
        super("123Hockey – Calendario");
        this.usuarioLogado = usuario;
        initUI();
    }

    /**
     * Configura los componentes de la interfaz de usuario, inicializa datos y hace visible la ventana.
     */
    private void initUI() {
        Image appIcon = loadIcon("logoSinFondo.png",32,32).getImage();
        setIconImage(appIcon);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        W       = screen.width;
        H       = screen.height;
        mX      = (int)(W * 0.04);
        headerH = (int)(H * 0.07);
        sepY    = mX + headerH;
        ctrlY   = mX + headerH + 80;
        ctrlH   = (int)(headerH / 1.5);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        setLayout(null);

        JLabel back = new JLabel(loadIcon("arrow_back.png", headerH-16, headerH-16));
        back.setBounds(mX, mX+4, headerH-16, headerH-16);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                switch(usuarioLogado.getRol()){
                    case ENTRENADOR:
                        new InicioEntrenadorVentana(usuarioLogado).setVisible(true); break;
                    case DELEGADO:
                        new InicioDelegadoVentana(usuarioLogado).setVisible(true);   break;
                    case JUGADOR:
                        new InicioJugadorVentana(usuarioLogado).setVisible(true);    break;
                }
                dispose();
            }
        });
        add(back);

        JLabel title = new JLabel("123HOCKEY!");
        title.setFont(new Font("Arial Black",Font.BOLD, headerH/2 + 4));
        title.setForeground(ACCENT);
        title.setBounds(mX + headerH, mX - 6, (int)(W*0.3), headerH);
        add(title);

        JSeparator sep = new JSeparator();
        sep.setForeground(ACCENT);
        sep.setBounds(mX, sepY, W - 2*mX, 2);
        add(sep);

        int avatarSize = headerH - 16;
        int avatarX    = W - mX - avatarSize;
        JLabel avatar  = new JLabel(cargarAvatar(usuarioLogado, avatarSize, avatarSize));
        avatar.setBounds(avatarX, mX, avatarSize, avatarSize);
        add(avatar);

        JLabel hola = new JLabel("Hola, " + usuarioLogado.getNombre(), SwingConstants.RIGHT);
        hola.setFont(new Font("Segoe UI",Font.BOLD, headerH/2 - 2));
        hola.setForeground(ACCENT);
        hola.setBounds(mX, mX, avatarX - 2*mX + 30, avatarSize);
        add(hola);

        JLabel lblSec = new JLabel("Calendario");
        lblSec.setFont(new Font("Segoe UI",Font.BOLD, headerH));
        lblSec.setForeground(FG);
        lblSec.setBounds(mX, mX + headerH + 17, 350, headerH+10);
        add(lblSec);

        int arcField   = 15, bordField = 1;
        Color fieldBg     = BG;
        Color fieldBorder = ACCENT;
        txtFechaBuscar = new JTextField("Buscar por fecha…") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fieldBg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),arcField,arcField);
                super.paintComponent(g2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(bordField));
                g2.setColor(fieldBorder);
                double off = bordField/2.0;
                g2.drawRoundRect((int)off,(int)off,getWidth()-bordField,getHeight()-bordField,arcField,arcField);
                g2.dispose();
            }
        };
        txtFechaBuscar.setFont(new Font("Segoe UI",Font.PLAIN,16));
        txtFechaBuscar.setForeground(Color.GRAY);
        txtFechaBuscar.setOpaque(false);
        txtFechaBuscar.setBorder(BorderFactory.createEmptyBorder(0,12,0,12));
        txtFechaBuscar.setBounds(mX, ctrlY+5, (int)(W*0.3), ctrlH);
        txtFechaBuscar.setFocusable(false);
        txtFechaBuscar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                txtFechaBuscar.setFocusable(true);
                txtFechaBuscar.requestFocusInWindow();
            }
        });
        txtFechaBuscar.addFocusListener(new FocusAdapter(){
            @Override public void focusGained(FocusEvent e){
                if(txtFechaBuscar.getText().equals("Buscar por fecha…")){
                    txtFechaBuscar.setText("");
                    txtFechaBuscar.setForeground(FG);
                }
            }
            @Override public void focusLost(FocusEvent e){
                if(txtFechaBuscar.getText().isEmpty()){
                    txtFechaBuscar.setText("Buscar por fecha…");
                    txtFechaBuscar.setForeground(Color.GRAY);
                }
            }
        });
        txtFechaBuscar.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ filtrar(); }
            public void removeUpdate(DocumentEvent e){ filtrar(); }
            public void changedUpdate(DocumentEvent e){ filtrar(); }
        });
        add(txtFechaBuscar);

        int btnX = mX + (int)(W*0.3) + 20;

        selectedTipos = new HashSet<>(Arrays.asList("Entrenamiento","Partido"));
        tipoMenu = new JPopupMenu();
        JCheckBoxMenuItem itmEnt = new JCheckBoxMenuItem("Entrenamiento", true);
        JCheckBoxMenuItem itmPar = new JCheckBoxMenuItem("Partido", true);
        tipoMenu.add(itmEnt);
        tipoMenu.add(itmPar);
        itmEnt.addActionListener(e -> {
            if (itmEnt.isSelected()) selectedTipos.add("Entrenamiento");
            else selectedTipos.remove("Entrenamiento");
            filtrar();
        });
        itmPar.addActionListener(e -> {
            if (itmPar.isSelected()) selectedTipos.add("Partido");
            else selectedTipos.remove("Partido");
            filtrar();
        });

        btnFiltrar = makeButton("Filtrar");
        btnFiltrar.setBounds(btnX, ctrlY+5, 100, ctrlH);
        btnFiltrar.addMouseListener(new MouseAdapter(){
            @Override public void mousePressed(MouseEvent e){
                tipoMenu.show(btnFiltrar, 0, btnFiltrar.getHeight());
            }
        });
        add(btnFiltrar);

        if(usuarioLogado.getRol() == Usuario.Rol.ENTRENADOR) {
            btnNuevoEntrenamiento = makeButton("Nuevo entrenamiento");
            btnNuevoEntrenamiento.setBounds(btnX + 120, ctrlY+5, 220, ctrlH);
            btnNuevoEntrenamiento.addActionListener(e -> {
                CrearEntrenamientoVentana dialog = new CrearEntrenamientoVentana(
                        this,
                        usuarioLogado,
                        () -> {
                            cargarDatos();
                            filtrar();
                        }
                );
                dialog.setVisible(true);
            });
            add(btnNuevoEntrenamiento);

            btnNuevoPartido = makeButton("Nuevo partido");
            btnNuevoPartido.setBounds(btnX + 360, ctrlY+5, 220, ctrlH);
            btnNuevoPartido.addActionListener(e -> {
                CrearPartidoVentana dialog = new CrearPartidoVentana(
                        CalendarioVentana.this,
                        usuarioLogado,
                        () -> {
                            cargarDatos();
                            filtrar();
                        }
                );
            });

            add(btnNuevoPartido);
        }

        inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(BG);

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBounds(mX, mX + headerH + 120, W, H - (mX + headerH + 200));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll);

        cargarDatos();
        filtrar();
        setVisible(true);
    }

    /**
     * Crea un botón con estilo personalizado redondeado y efectos de resaltado.
     *
     * @param text texto que se mostrará en el botón
     * @return JButton configurado con los estilos definidos
     */
    private JButton makeButton(String text){
        JButton btn = new JButton(text){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                ButtonModel m = getModel();
                Color bg = m.isPressed()
                        ? ACCENT.brighter()
                        : m.isRollover()
                        ? ACCENT
                        : BG;
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),ctrlH/2,ctrlH/2);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,ctrlH/2,ctrlH/2);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g){}
        };
        btn.setForeground(FG);
        btn.setFont(new Font("Segoe UI",Font.BOLD,16));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * Carga los eventos de entrenamientos y partidos desde la base de datos según el rol del usuario
     * y los ordena por fecha.
     */
    private void cargarDatos(){
        eventos.clear();
        EquipoDAO eqDao = new EquipoDAO();
        EntrenamientoDAO entDao = new EntrenamientoDAO();
        PartidoDAO parDao = new PartidoDAO();
        EquipoJugadorDAO ejDao = new EquipoJugadorDAO();
        entDao.generarRepeticionesPendientes();

        switch(usuarioLogado.getRol()){
            case ENTRENADOR:
                eqDao.listarTodos().forEach(eq->{
                    entDao.listarPorEquipo(eq.getId())
                            .forEach(e->eventos.add(
                                    new Evento(false,e.getFecha(),e,null)
                            ));
                    parDao.listarPorEquipo(eq.getId())
                            .forEach(p->eventos.add(
                                    new Evento(true,p.getFecha(),null,p)
                            ));
                });
                break;
            case DELEGADO:
                eqDao.listarTodos().forEach(eq->
                        parDao.listarPorEquipo(eq.getId())
                                .forEach(p->eventos.add(
                                        new Evento(true,p.getFecha(),null,p)
                                ))
                );
                break;
            case JUGADOR:
                Set<Integer> misEqs = new HashSet<>();
                ejDao.listarPorJugador(usuarioLogado.getId())
                        .forEach(r->misEqs.add(r.getEquipo().getId()));
                misEqs.forEach(id->{
                    entDao.listarPorEquipo(id)
                            .forEach(e->eventos.add(
                                    new Evento(false,e.getFecha(),e,null)
                            ));
                    parDao.listarPorEquipo(id)
                            .forEach(p->eventos.add(
                                    new Evento(true,p.getFecha(),null,p)
                            ));
                });
                break;
        }
        eventos.sort(Comparator.comparing(ev->ev.fecha));
    }

    /**
     * Filtra y muestra los eventos próximos y pasados en la interfaz según la fecha buscada
     * y los tipos de evento seleccionados.
     */
    private void filtrar() {
        inner.removeAll();
        Date now = new Date();
        SimpleDateFormat dfDate = new SimpleDateFormat("d/MM/yyyy");
        SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");

        String raw = txtFechaBuscar.getText().trim();
        String t   = raw.equalsIgnoreCase("Buscar por fecha…")
                ? "" : raw.toLowerCase();

        inner.add(sectionHeader("Próximos"));
        for (Evento ev : eventos) {
            if (ev.isPartido && !selectedTipos.contains("Partido")) continue;
            if (!ev.isPartido && !selectedTipos.contains("Entrenamiento")) continue;
            if (ev.fecha.before(now)) continue;
            if (!t.isEmpty()
                    && !dfDate.format(ev.fecha).toLowerCase().contains(t)) continue;
            inner.add(eventRow(ev, dfDate, dfTime));
        }

        inner.add(sectionHeader("Pasados"));
        for (Evento ev : eventos) {
            if (ev.isPartido && !selectedTipos.contains("Partido")) continue;
            if (!ev.isPartido && !selectedTipos.contains("Entrenamiento")) continue;
            if (!ev.fecha.before(now)) continue;
            if (!t.isEmpty()
                    && !dfDate.format(ev.fecha).toLowerCase().contains(t)) continue;
            inner.add(eventRow(ev, dfDate, dfTime));
        }

        inner.revalidate();
        inner.repaint();
    }

    /**
     * Crea un panel de encabezado de sección con un título dado.
     *
     * @param text texto del encabezado de sección
     * @return JPanel que contiene el encabezado formateado
     */
    private JPanel sectionHeader(String text){
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,24));
        lbl.setForeground(FG);
        lbl.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.add(lbl,BorderLayout.WEST);
        return p;
    }

    /**
     * Crea una fila de evento para mostrar en la lista, con icono, fecha, hora y enlace de detalle.
     *
     * @param ev evento a representar (entrenamiento o partido)
     * @param dfDate formateador para la fecha
     * @param dfTime formateador para la hora
     * @return JPanel con la fila del evento
     */
    private JPanel eventRow(Evento ev, SimpleDateFormat dfDate, SimpleDateFormat dfTime){
        JPanel p = new JPanel(null);
        p.setPreferredSize(new Dimension(0,80));
        p.setBackground(BG);

        String ico = ev.isPartido
                ? "hockey_icon.png"
                : "board_icon.png";
        JLabel icon = new JLabel(loadIcon(ico,36,36));
        icon.setBounds(10,22,36,36);
        p.add(icon);

        JLabel lblT = new JLabel(
                ev.isPartido ? "Partido" : "Entrenamiento"
        );
        lblT.setForeground(FG);
        lblT.setFont(new Font("Segoe UI",Font.PLAIN,18));
        lblT.setBounds(60,0,200,80);
        p.add(lblT);

        JLabel lblF = new JLabel(dfDate.format(ev.fecha));
        lblF.setForeground(FG);
        lblF.setFont(new Font("Segoe UI",Font.PLAIN,18));
        lblF.setBounds(440,0,200,80);
        p.add(lblF);

        JLabel lblH = new JLabel(dfTime.format(ev.fecha));
        lblH.setForeground(FG);
        lblH.setFont(new Font("Segoe UI",Font.PLAIN,18));
        lblH.setBounds(740,0,100,80);
        p.add(lblH);

        JLabel ver = new JLabel(
                "<html><span style='color:#318DE1;"
                        + "text-decoration:underline;cursor:hand;'>Ver...</span></html>"
        );
        ver.setBounds(1100,0,100,80);
        ver.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                if(ev.isPartido)
                    new FichaPartidoVentana(usuarioLogado, ev.partido)
                            .setVisible(true);
                else
                    new FichaEntrenamientoVentana(usuarioLogado, ev.entrenamiento)
                            .setVisible(true);
            }
        });
        p.add(ver);

        return p;
    }

    /**
     * Carga y recorta la imagen de avatar del usuario en forma circular.
     *
     * @param u usuario cuyo avatar se cargará
     * @param w ancho deseado de la imagen
     * @param h alto deseado de la imagen
     * @return ImageIcon con la imagen del avatar recortada, o un icono predeterminado si no existe foto
     */
    private ImageIcon cargarAvatar(Usuario u, int w, int h){
        try{
            byte[] f = u.getFotoUsuario();
            if(f!=null && f.length>0){
                BufferedImage src = javax.imageio.ImageIO
                        .read(new ByteArrayInputStream(f));
                BufferedImage dst = new BufferedImage(
                        w,h,BufferedImage.TYPE_INT_ARGB
                );
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
        return loadIcon("user_default.png",w,h);
    }

    /**
     * Carga un icono desde los recursos empaquetados y lo escala al tamaño indicado.
     *
     * @param name nombre del archivo de imagen dentro de la carpeta assets
     * @param w ancho deseado del icono
     * @param h alto deseado del icono
     * @return ImageIcon con la imagen escalada
     */
    private ImageIcon loadIcon(String name, int w, int h){
        URL u = getClass().getClassLoader()
                .getResource("assets/"+name);
        Image img = (u!=null)
                ? new ImageIcon(u).getImage()
                : new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img.getScaledInstance(
                w,h,Image.SCALE_SMOOTH
        ));
    }

    /**
     * Clase que representa un evento de calendario, ya sea entrenamiento o partido.
     */
    private static class Evento {
        final boolean       isPartido;
        final Date          fecha;
        final Entrenamiento entrenamiento;
        final Partido       partido;

        /**
         * Construye un nuevo Evento.
         *
         * @param isPartido     true si es un partido, false si es un entrenamiento
         * @param fecha         fecha y hora del evento
         * @param e             objeto Entrenamiento asociado, o null si es un partido
         * @param p             objeto Partido asociado, o null si es un entrenamiento
         */
        Evento(boolean isPartido, Date fecha,
               Entrenamiento e, Partido p){
            this.isPartido     = isPartido;
            this.fecha         = fecha;
            this.entrenamiento = e;
            this.partido       = p;
        }
    }
}
