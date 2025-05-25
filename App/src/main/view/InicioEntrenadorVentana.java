package main.view;

import main.dao.*;
import main.model.Entrenamiento;
import main.model.Equipo;
import main.model.Partido;
import main.model.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InicioEntrenadorVentana extends JFrame {

    public InicioEntrenadorVentana(Usuario entrenador) {
        // Ventana principal
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
        int btnY      = sepY + (int)(H * 0.04);
        int btnW      = (int)(W * 0.28);
        int btnH      = (int)(H * 0.12);
        int gapX      = mX;
        int panelY    = btnY + btnH + (int)(H * 0.04);
        int panelGapX = mX;
        int panelW    = (W - 2*mX - panelGapX) / 2;
        int panelH    = (int)(H * 0.30);
        int innerMX   = (int)(panelW * 0.08);

        // ----------------------------------------------------
        // HEADER
        // ----------------------------------------------------
        // Icono
        JLabel menu = new JLabel(loadIcon("logoSinFondo.png", headerH-16, headerH-16));
        menu.setBounds(mX, mX, headerH-16, headerH-16);
        add(menu);
        // Título
        JLabel title = new JLabel("123HOCKEY!");
        title.setFont(new Font("Arial Black", Font.BOLD, headerH/2 + 4));
        title.setForeground(new Color(49,109,233));
        title.setBounds(mX + headerH, mX-6, (int)(W*0.3), headerH);
        add(title);
        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(49,109,233));
        sep.setBounds(mX, sepY, W - 2*mX, 2);
        add(sep);
        // Avatar
        int avatarSize = headerH - 16;
        int avatarX    = W - mX - avatarSize;
        JLabel avatar  = new JLabel(cargarAvatar(entrenador, avatarSize, avatarSize));
        avatar.setBounds(avatarX, mX, avatarSize, avatarSize);
        add(avatar);
        // Saludo
        int greetW = avatarX - 2*mX + 30;
        JLabel hola = new JLabel("Hola, " + entrenador.getNombre(), SwingConstants.RIGHT);
        hola.setFont(new Font("Segoe UI", Font.BOLD, headerH/2 - 2));
        hola.setForeground(new Color(49,109,233));
        hola.setBounds(mX, mX, greetW, avatarSize);
        add(hola);

        // ----------------------------------------------------
        // BOTONES DE MENÚ
        // ----------------------------------------------------
        int arcBtn   = btnH / 3;
        int bordBtn  = 2;
        Color normalBg     = new Color(18, 18, 18);
        Color normalBorder = new Color(49, 109, 233);
        Color hoverBg      = new Color(49, 109, 233);
        Color hoverBorder  = new Color(49, 109, 233);
        Color pressBg      = new Color(142, 173, 233);
        Color pressBorder  = new Color(142, 173, 233);

        JButton btnPlantilla = crearBotonRedondeado("Plantilla", arcBtn, bordBtn,
                normalBg, normalBorder, hoverBg, hoverBorder, pressBg, pressBorder,
                mX, btnY+20, btnW, btnH);
        btnPlantilla.addActionListener(e -> { new PlantillaVentana(entrenador); dispose(); });
        add(btnPlantilla);

        JButton btnEquipos = crearBotonRedondeado("Equipos", arcBtn, bordBtn,
                normalBg, normalBorder, hoverBg, hoverBorder, pressBg, pressBorder,
                mX+btnW+gapX, btnY+20, btnW, btnH);
        btnEquipos.addActionListener(e -> { new EquiposVentana(entrenador); dispose(); });
        add(btnEquipos);

        JButton btnCalendario = crearBotonRedondeado("Calendario", arcBtn, bordBtn,
                normalBg, normalBorder, hoverBg, hoverBorder, pressBg, pressBorder,
                mX+2*(btnW+gapX), btnY+20, btnW, btnH);
        btnCalendario.addActionListener(e -> { new CalendarioVentana(entrenador); dispose(); });
        add(btnCalendario);

        // ----------------------------------------------------
        // PANEL “PRÓXIMOS EVENTOS”
        // ----------------------------------------------------
        JPanel pE = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight(), arc = h/10;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // fondo redondeado
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,w,h,arc,arc);
                // borde
                g2.setStroke(new BasicStroke(3));
                g2.setColor(new Color(29,29,29));
                double off = 3/2.0;
                g2.drawRoundRect((int)off,(int)off,w-3,h-3,arc,arc);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pE.setOpaque(false);
        pE.setBackground(new Color(18,18,18));
        pE.setBounds(mX, panelY+35, panelW, panelH+100);
        add(pE);

        JLabel tE = new JLabel("Próximos eventos", SwingConstants.CENTER);
        tE.setFont(new Font("Segoe UI", Font.BOLD, (int)((panelH+100)*0.08)));
        tE.setForeground(Color.WHITE);
        int titleHt = tE.getPreferredSize().height;
        int titleTop = (int)((panelH+100)*0.05);
        tE.setBounds(innerMX, titleTop, panelW-2*innerMX, titleHt);
        pE.add(tE);

        int iconE = (int)((panelH+100)*0.16);
        int titleBot = titleTop + titleHt;
        int bottomM = (int)((panelH+100)*0.05);
        int availE = (panelH+100) - titleBot - bottomM;
        int rowsE = 2;
        int gapE = (availE - rowsE*iconE)/(rowsE+1);

        // Cálculo de próximos partido y entrenamiento (global)
        Date now = new Date();
        PartidoDAO partidoDAO = new PartidoDAO();
        EntrenamientoDAO entDAO  = new EntrenamientoDAO();

        Partido proximoPartido = partidoDAO.listarTodos().stream()
                .filter(p -> p.getFecha().after(now))
                .min(Comparator.comparing(Partido::getFecha))
                .orElse(null);

        Entrenamiento proximoEntreno = entDAO.listarTodos().stream()
                .filter(e -> e.getFecha().after(now))
                .min(Comparator.comparing(Entrenamiento::getFecha))
                .orElse(null);

        SimpleDateFormat dfF = new SimpleDateFormat("dd/MM");
        SimpleDateFormat dfH = new SimpleDateFormat("HH:mm");

        String textoPartido = proximoPartido!=null
                ? "Próximo partido: " + dfF.format(proximoPartido.getFecha())
                + " vs " + proximoPartido.getRival()
                : "Próximo partido: -";

        String textoEntreno = proximoEntreno!=null
                ? "Próximo entrenamiento: " + dfF.format(proximoEntreno.getFecha())
                + " " + dfH.format(proximoEntreno.getFecha())
                : "Próximo entrenamiento: -";

        String[] textosE = { textoPartido, textoEntreno };
        String[] iconosE = { "hockey_icon.png", "board_icon.png" };

        for(int i=0; i<rowsE; i++){
            int y = titleBot + gapE*(i+1) + iconE*i;
            addIconAndText(pE, iconosE[i], textosE[i],
                    innerMX, y, iconE,
                    panelW - innerMX - iconE - innerMX,
                    panelH+100);
        }

        // ----------------------------------------------------
        // PANEL “RESUMEN GENERAL”
        // ----------------------------------------------------
        JPanel pR = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight(), arc = h/10;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,w,h,arc,arc);
                g2.setStroke(new BasicStroke(3));
                g2.setColor(new Color(29,29,29));
                double off = 3/2.0;
                g2.drawRoundRect((int)off,(int)off,w-3,h-3,arc,arc);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pR.setOpaque(false);
        pR.setBackground(new Color(18,18,18));
        pR.setBounds(mX+panelW+panelGapX, panelY+35, panelW, panelH+100);
        add(pR);

        JLabel tR = new JLabel("Resumen general", SwingConstants.CENTER);
        tR.setFont(new Font("Segoe UI", Font.BOLD, (int)((panelH+100)*0.08)));
        tR.setForeground(Color.WHITE);
        tR.setBounds(innerMX, titleTop, panelW-2*innerMX, titleHt);
        pR.add(tR);

        int iconR = (int)((panelH+100)*0.13);
        int titleBotR = titleTop + titleHt;
        int availR = (panelH+100) - titleBotR - bottomM;
        int rowsR = 3;
        int gapR = (availR - rowsR*iconR)/(rowsR+1);

        // Datos dinámicos
        EquipoDAO equipoDAO       = new EquipoDAO();
        List<Equipo> equipos      = equipoDAO.listarTodos();
        int numEquipos            = equipos.size();

        JugadorInfoDAO jugadorDAO = new JugadorInfoDAO();
        int numJugadores          = jugadorDAO.listarTodos().size();

        // Media de asistencia solo sobre entrenamientos pasados:
        AsistenciaDAO asisDAO           = new AsistenciaDAO();
        EntrenamientoDAO entDAO2        = new EntrenamientoDAO();
        EquipoJugadorDAO ejDAO2         = new EquipoJugadorDAO();
        double sumaPorcentajes          = 0;
        int equiposContados             = 0;

        for (Equipo e : equipos) {
            // entrenamientos pasados de este equipo
            List<Entrenamiento> pasados = entDAO2.listarPorEquipo(e.getId())
                    .stream()
                    .filter(en -> en.getFecha().before(now))
                    .collect(Collectors.toList());
            if (pasados.isEmpty()) continue;

            // número de jugadores del equipo
            int numJugEquipo = ejDAO2.listarPorEquipo(e.getId()).size();

            int totalInvit = pasados.size() * numJugEquipo;
            long asistidos = pasados.stream()
                    .flatMap(en -> asisDAO.listarPorEntrenamiento(en.getId()).stream())
                    .filter(a -> a.isAsistencia())
                    .count();

            double pct = totalInvit>0
                    ? (double)asistidos/totalInvit*100
                    : 0;
            sumaPorcentajes += pct;
            equiposContados++;
        }
        double mediaAsistencia = equiposContados>0
                ? sumaPorcentajes / equiposContados
                : 0;

        String[] textosR = {
                "Equipos: " + numEquipos,
                "Jugadores totales: " + numJugadores,
                String.format("Media asistencia: %.1f %%", mediaAsistencia)
        };
        String[] iconosR = { "team_icon.png", "list_icon.png", "graph_icon.png" };

        for(int i=0; i<rowsR; i++){
            int y = titleBotR + gapR*(i+1) + iconR*i;
            addIconAndText(pR, iconosR[i], textosR[i],
                    innerMX, y, iconR,
                    panelW - innerMX - iconR - innerMX,
                    panelH+100);
        }

        setVisible(true);
    }

    // ---------------------------------------
    // Métodos auxiliares
    // ---------------------------------------
    private JButton crearBotonRedondeado(String text,
                                         int arc, int bord,
                                         Color bg, Color br,
                                         Color hb, Color hbr,
                                         Color pb, Color pbr,
                                         int x, int y, int w, int h) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                int w_ = getWidth(), h_ = getHeight(), arc_ = arc;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = getModel();
                Color fill, border;
                if (m.isPressed())      { fill = pb;  border = pbr; }
                else if (m.isRollover()) { fill = hb;  border = hbr; }
                else                     { fill = bg;  border = br; }
                g2.setColor(fill);
                g2.fillRoundRect(0,0,w_,h_,arc_,arc_);
                g2.setStroke(new BasicStroke(bord));
                g2.setColor(border);
                double off = bord/2.0;
                g2.drawRoundRect((int)off,(int)off,w_-bord,h_-bord,arc_,arc_);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) { /* no border */ }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, (int)(h*0.35)));
        b.setBounds(x,y,w,h);
        return b;
    }

    private ImageIcon cargarAvatar(Usuario u, int w, int h) {
        try {
            byte[] f = u.getFotoUsuario();
            if (f!=null && f.length>0) {
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
        } catch(Exception ignored) {}
        return loadIcon("user_default.png", w, h);
    }

    private ImageIcon loadIcon(String name, int w, int h) {
        URL u = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (u!=null)
                ? new ImageIcon(u).getImage()
                : new ImageIcon("src/assets/"+name).getImage();
        return new ImageIcon(img.getScaledInstance(w,h,Image.SCALE_SMOOTH));
    }

    private void addIconAndText(JPanel panel,
                                String iconFile, String text,
                                int x, int y, int iconSize,
                                int textWidth, int panelH) {
        ImageIcon icon = loadIcon(iconFile, iconSize, iconSize);
        JLabel lIcon = new JLabel(icon);
        lIcon.setBounds(x,y,iconSize,iconSize);
        panel.add(lIcon);

        JLabel lText = new JLabel(text);
        lText.setForeground(Color.WHITE);
        lText.setFont(new Font("Segoe UI", Font.PLAIN, (int)(panelH*0.065)));
        int th = lText.getPreferredSize().height;
        int ty = y + (iconSize - th)/2;
        lText.setBounds(x + iconSize + innerMX(panel), ty, textWidth, th);
        panel.add(lText);
    }

    private int innerMX(JPanel panel) {
        return (int)(panel.getWidth()*0.08);
    }
}