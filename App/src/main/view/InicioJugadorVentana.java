package main.view;

import main.dao.*;
import main.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class InicioJugadorVentana extends JFrame {

    public InicioJugadorVentana(Usuario jugador) {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(18, 18, 18));
        setLayout(null);

        Image appIcon = loadIcon("logoSinFondo.png", 32, 32).getImage();
        setIconImage(appIcon);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int W = screen.width, H = screen.height;

        // Márgenes y proporciones
        int mX        = (int)(W * 0.04);
        int headerH   = (int)(H * 0.07);
        int sepY      = mX + headerH;
        int btnY      = sepY + (int)(H * 0.04);            // +4%
        int btnW      = (int)(W * 0.28);
        int btnH      = (int)(H * 0.12);
        int gapX      = mX;
        int panelY    = btnY + btnH + (int)(H * 0.04);     // +4%
        int panelGapX = mX;
        int panelW    = (W - 2*mX - panelGapX) / 2;
        int panelH    = (int)(H * 0.30);
        int innerMX   = (int)(panelW * 0.08);              // 8% margen interior

        // ICONO menú
        JLabel menu = new JLabel(loadIcon("logoSinFondo.png", headerH-16, headerH-16));
        menu.setBounds(mX, mX, headerH-16, headerH-16);
        add(menu);

        // TÍTULO
        JLabel title = new JLabel("123HOCKEY!");
        title.setFont(new Font("Arial Black", Font.BOLD, headerH/2 + 4));
        title.setForeground(new Color(49,109,233));
        title.setBounds(mX + headerH, mX-6, (int)(W*0.3), headerH);
        add(title);

        // SEPARADOR
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(49,109,233));
        sep.setBounds(mX, sepY, W - 2*mX, 2);
        add(sep);

        // AVATAR
        int avatarSize = headerH - 16;
        int avatarX    = W - mX - avatarSize;
        ImageIcon avatarIcon = cargarAvatar(jugador, avatarSize, avatarSize);
        JLabel avatar = new JLabel(avatarIcon);
        avatar.setBounds(avatarX, mX, avatarSize, avatarSize);
        add(avatar);

        // SALUDO (un poco más abajo y más grande)
        int greetW = avatarX - 2*mX + 30;
        JLabel hola = new JLabel("Hola, " + jugador.getNombre(), SwingConstants.RIGHT);
        hola.setFont(new Font("Segoe UI", Font.BOLD, headerH/2 - 2));
        hola.setForeground(new Color(49,109,233));
        hola.setBounds(mX, mX, greetW, avatarSize);
        add(hola);

        // BOTONES (mantengo +20 para estética)
        // ===== BOTONES REDONDEADOS INLINE CON HOVER Y PRESSED =====
        int arcBtn   = btnH / 3;            // radio de las esquinas (~33% de la altura)
        int bordBtn  = 2;                   // grosor del borde
        Color normalBg     = new Color(18, 18, 18);
        Color normalBorder = new Color(49, 109, 233);
        Color hoverBg      = new Color(49, 109, 233);
        Color hoverBorder  = new Color(49, 109, 233);
        Color pressBg      = new Color(142, 173, 233);
        Color pressBorder  = new Color(142, 173, 233);

// Plantilla
        JButton btnPlantilla = new JButton("Plantilla") {
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
        btnPlantilla.setOpaque(false);
        btnPlantilla.setContentAreaFilled(false);
        btnPlantilla.setFocusPainted(false);
        btnPlantilla.setBorderPainted(false);
        btnPlantilla.setRolloverEnabled(true);
        btnPlantilla.setBackground(normalBg);
        btnPlantilla.setForeground(Color.WHITE);
        btnPlantilla.setFont(new Font("Segoe UI", Font.BOLD, (int)(btnH * 0.35)));
        btnPlantilla.setBounds(mX, btnY + 20, btnW, btnH);
        btnPlantilla.addActionListener(e -> {
            // 'entrenador' es el Usuario que recibiste en el constructor de esta clase
            new PlantillaVentana(jugador);
            dispose();
        });
        add(btnPlantilla);

// Equipos
        JButton btnEquipos = new JButton("Equipos") {
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

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(br);
                double off = bordBtn / 2.0;
                g2.drawRoundRect((int)off, (int)off, w - bordBtn, h - bordBtn, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) { /* nada */ }
        };
        btnEquipos.setOpaque(false);
        btnEquipos.setContentAreaFilled(false);
        btnEquipos.setFocusPainted(false);
        btnEquipos.setBorderPainted(false);
        btnEquipos.setRolloverEnabled(true);
        btnEquipos.setBackground(normalBg);
        btnEquipos.setForeground(Color.WHITE);
        btnEquipos.setFont(new Font("Segoe UI", Font.BOLD, (int)(btnH * 0.35)));
        btnEquipos.setBounds(mX + btnW + gapX, btnY + 20, btnW, btnH);
        btnEquipos.addActionListener(e -> {
            // 'entrenador' es el Usuario que recibiste en el constructor de esta clase
            new EquiposVentana(jugador);
            dispose();
        });
        add(btnEquipos);

// Calendario
        JButton btnCalendario = new JButton("Calendario") {
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

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                g2.setStroke(new BasicStroke(bordBtn));
                g2.setColor(br);
                double off = bordBtn / 2.0;
                g2.drawRoundRect((int)off, (int)off, w - bordBtn, h - bordBtn, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) { /* nada */ }
        };
        btnCalendario.setOpaque(false);
        btnCalendario.setContentAreaFilled(false);
        btnCalendario.setFocusPainted(false);
        btnCalendario.setBorderPainted(false);
        btnCalendario.setRolloverEnabled(true);
        btnCalendario.setBackground(normalBg);
        btnCalendario.setForeground(Color.WHITE);
        btnCalendario.setFont(new Font("Segoe UI", Font.BOLD, (int)(btnH * 0.35)));
        btnCalendario.setBounds(mX + 2*(btnW + gapX), btnY + 20, btnW, btnH);
        btnCalendario.addActionListener(e -> {
            // 'entrenador' es el Usuario que recibiste en el constructor de esta clase
            new CalendarioVentana(jugador);
            dispose();
        });
        add(btnCalendario);




        // PANEL “PRÓXIMOS EVENTOS” (mantengo +35 y +100)
        // Panel “Próximos eventos” con esquinas y borde redondeados inline
        JPanel pE = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                // arco = 20% de la altura (ajusta divisor para más o menos curvatura)
                int arc = h / 10;

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // 1) Fondo redondeado
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // 2) Borde redondeado (5px de grosor)
                g2.setStroke(new BasicStroke(3));
                g2.setColor(new Color(29, 29, 29));
                double off = 5 / 2.0;
                g2.drawRoundRect((int) off,
                        (int) off,
                        w - 5,
                        h - 5,
                        arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        pE.setOpaque(false);
        pE.setBackground(new Color(18, 18, 18));
// Ya no necesitamos setBorder(...) porque lo pintamos manualmente
        pE.setBounds(mX, panelY + 35, panelW, panelH + 100);
        add(pE);


        // TÍTULO CENTRALIZADO
        JLabel tE = new JLabel("Próximos eventos", SwingConstants.CENTER);
        tE.setFont(new Font("Segoe UI", Font.BOLD, (int)((panelH+100) * 0.08)));
        tE.setForeground(Color.WHITE);
        int titleHt = tE.getPreferredSize().height;
        int titleTop = (int)((panelH+100) * 0.05);
        tE.setBounds(innerMX, titleTop, panelW - 2*innerMX, titleHt);
        pE.add(tE);

        // Distribución vertical de 2 filas
        int iconE    = (int)((panelH+100) * 0.16);
        int titleBot = titleTop + titleHt;
        int bottomM  = (int)((panelH+100) * 0.05);
        int availE   = (panelH+100) - titleBot - bottomM;
        int rowsE    = 2;
        int gapE     = (availE - rowsE * iconE) / (rowsE + 1);

        PartidoDAO        partidoDAO       = new PartidoDAO();
        EntrenamientoDAO  entDAO           = new EntrenamientoDAO();

// Sólo los próximos del jugador
        Partido           proximoPartido       = partidoDAO.obtenerProximoPartidoPorJugador(jugador.getId());
        Entrenamiento     proximoEntrenamiento = entDAO.obtenerProximoEntrenamientoPorJugador(jugador.getId());

        SimpleDateFormat  dfFecha = new SimpleDateFormat("dd/MM");
        SimpleDateFormat  dfHora  = new SimpleDateFormat("HH:mm");

        String textoPartido = (proximoPartido != null)
                ? String.format("Próximo partido: %s vs %s",
                dfFecha.format(proximoPartido.getFecha()),
                proximoPartido.getRival())
                : "Próximo partido: -";

        String textoEntreno = (proximoEntrenamiento != null)
                ? String.format("Próximo entrenamiento: %s %s",
                dfFecha.format(proximoEntrenamiento.getFecha()),
                dfHora .format(proximoEntrenamiento.getFecha()))
                : "Próximo entrenamiento: -";

        String[] textosE = { textoPartido, textoEntreno };
        String[] iconosE = { "hockey_icon.png", "board_icon.png" };

        for (int i = 0; i < textosE.length; i++) {
            int y = titleBot + gapE * (i + 1) + iconE * i;
            addIconAndText(
                    pE,
                    iconosE[i],
                    textosE[i],
                    innerMX(pE),
                    y,
                    iconE,
                    panelW - 2 * innerMX(pE) - iconE,
                    panelH + 100
            );
        }


        // PANEL “RESUMEN GENERAL”
        JPanel pR = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                // calcula un arco “grande” según el alto
                int arc = h / 10; // 20% de la altura, prueba a ajustar este factor

                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // 1) rellena el fondo con esquinas redondeadas
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // 2) dibuja el borde con ese mismo arco
                g2.setStroke(new BasicStroke(3));                  // grosor 5px
                g2.setColor(new Color(29, 29, 29));                  // tu color de borde
                // desplaza medio grosor para que el borde quede bien dentro
                double off = 5 / 2.0;
                g2.drawRoundRect((int)off,
                        (int)off,
                        w - 5,
                        h - 5,
                        arc, arc);

                g2.dispose();
                // luego pinta el contenido (labels, iconos…)
                super.paintComponent(g);
            }
        };
        pR.setOpaque(false);  // muy importante
        pR.setBackground(new Color(18,18,18));
// Ya no necesitas setBorder(...) porque lo pintamos nosotros
        pR.setBounds(mX + panelW + panelGapX,
                panelY + 35,
                panelW,
                panelH + 100);
        add(pR);


        JLabel tR = new JLabel("Resumen general", SwingConstants.CENTER);
        tR.setFont(new Font("Segoe UI", Font.BOLD, (int)((panelH+100) * 0.08)));
        tR.setForeground(Color.WHITE);
        tR.setBounds(innerMX, titleTop, panelW - 2*innerMX, titleHt);
        pR.add(tR);

        // Distribución vertical de 3 filas
        int iconR    = (int)((panelH+100) * 0.13);
        int titleBotR= titleTop + titleHt;
        int availR   = (panelH+100) - titleBotR - bottomM;
        int rowsR    = 3;
        int gapR     = (availR - rowsR * iconR) / (rowsR + 1);


        // ————— Datos dinámicos para el jugador —————
        int idJugador = jugador.getId();  // o como tengas el ID

// 1) Total de partidos jugados por este jugador
        JugadorInfoDAO jugadorInfoDAO = new JugadorInfoDAO();
        jugadorInfoDAO.actualizarPartidosJugados(jugador.getId());

        // Ahora recuperamos JugadorInfo para mostrar sus datos:
        JugadorInfo ji = jugadorInfoDAO.buscarPorUsuario(jugador.getId());
        int totalPartidos = ji != null ? ji.getPartidosJugados() : 0;

// 2) Total de entrenamientos a los que se le invitó (o existen en su equipo)
        AsistenciaDAO asisDAO = new AsistenciaDAO();
        List<Asistencia> asistencias = asisDAO.listarPorJugador(idJugador);
        int totalEntrenamientos = asistencias.size();

// 3) % de asistencia real del jugador
        // 1) Recupera los IDs de los equipos a los que pertenece el jugador
        EquipoJugadorDAO ejDao = new EquipoJugadorDAO();
        List<EquipoJugador> listaEqJug = ejDao.listarPorJugador(idJugador);
        List<Integer> equiposIds = listaEqJug.stream()
                .map(ej -> ej.getEquipo().getId())
                .collect(Collectors.toList());

// 2) Trae todos los entrenamientos de esos equipos y filtra sólo los que ya pasaron
        EntrenamientoDAO entDao = new EntrenamientoDAO();
        Date ahora = new Date();
        List<Entrenamiento> entrenosPasados = equiposIds.stream()
                .flatMap(eqId -> entDao.listarPorEquipo(eqId).stream())
                .filter(e -> e.getFecha().before(ahora))
                .distinct() // por si un jugador está en varios equipos y se repite el mismo entreno
                .collect(Collectors.toList());

// 3) Cuenta el total de entrenamientos pasados (ese será el 100%)
        int totalPasados = entrenosPasados.size();

// 4) Cuenta cuántos de esos tiene registro de asistencia=true
        AsistenciaDAO asisDao = new AsistenciaDAO();
        long asistidos = entrenosPasados.stream()
                .filter(e -> {
                    Asistencia a = asisDao.buscarPorEntrenamientoYJugador(e.getId(), idJugador);
                    return a != null && a.isAsistencia();
                })
                .count();

// 5) Calcula el porcentaje
        double mediaAsistencia = totalPasados > 0
                ? (double)asistidos / totalPasados * 100
                : 0;

// Ahora puedes mostrar:
// "Total entrenamientos pasados: " + totalPasados
// "Entrenamientos asistidos: "      + asistidos
// "Media asistencia: " + String.format("%.1f %%", mediaAsistencia)


// 4) Array de textos ya dinámico
        String[] textosR = {
                "Total partidos jugados: " + totalPartidos,
                "Total entrenamientos: "   + totalEntrenamientos,
                String.format("Media asistencia: %.1f %%", mediaAsistencia)
        };


        String[] iconosR = {"hockey_icon.png","board_icon.png","graph_icon.png"};

        for(int i=0; i<rowsR; i++){
            int y = titleBotR + gapR*(i+1) + iconR*i;
            addIconAndText(
                    pR,
                    iconosR[i],
                    textosR[i],
                    innerMX, y, iconR,
                    panelW - innerMX - iconR - innerMX,
                    panelH+100
            );
        }

        setVisible(true);
    }

    private ImageIcon cargarAvatar(Usuario u, int w, int h) {
        try{
            byte[] f=u.getFotoUsuario();
            if (f!=null && f.length>0){
                BufferedImage src=ImageIO.read(new ByteArrayInputStream(f));
                BufferedImage dst=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2=dst.createGraphics();
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


    /** Carga y escala icono desde classpath o src/assets con SCALE_SMOOTH */
    private ImageIcon loadIcon(String name, int w, int h) {
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u != null)
                ? new ImageIcon(u).getImage()
                : new ImageIcon("src/assets/" + name).getImage();
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    /** Añade un icono y un label centrado verticalmente */
    private void addIconAndText(JPanel panel, String iconFile, String text,
                                int x, int y, int iconSize, int textWidth, int panelH) {
        ImageIcon icon = loadIcon(iconFile, iconSize, iconSize);
        JLabel lIcon = new JLabel(icon);
        lIcon.setBounds(x, y, iconSize, iconSize);
        panel.add(lIcon);

        JLabel lText = new JLabel(text);
        lText.setForeground(Color.WHITE);
        lText.setFont(new Font("Segoe UI", Font.PLAIN, (int)(panelH*0.065)));
        int th = lText.getPreferredSize().height;
        int ty = y + (iconSize - th)/2;
        lText.setBounds(x + iconSize + innerMX(panel), ty, textWidth, th);
        panel.add(lText);
    }

    /** Extrae innerMX para positioning de texto (8% de ancho del panel) */
    private int innerMX(JPanel panel) {
        return (int)(panel.getWidth() * 0.08);
    }

    private JButton crearBoton(String txt, int x, int y, int w, int h) {
        JButton b = new JButton(txt);
        b.setBounds(x, y, w, h);
        b.setFont(new Font("Segoe UI", Font.BOLD, (int)(h * 0.35)));
        b.setBackground(new Color(18,18,18));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(49,109,233),2));
        return b;
    }
}
