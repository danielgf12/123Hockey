package main.view;

import main.dao.*;
import main.model.Entrenamiento;
import main.model.Equipo;
import main.model.Partido;
import main.model.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;

public class EquiposVentana extends JFrame {

    public EquiposVentana(Usuario entrenador) {
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
        ImageIcon avatarIcon = cargarAvatar(entrenador, avatarSize, avatarSize);
        JLabel avatar = new JLabel(avatarIcon);
        avatar.setBounds(avatarX, mX, avatarSize, avatarSize);
        add(avatar);

        // SALUDO (un poco más abajo y más grande)
        int greetW = avatarX - 2*mX + 30;
        JLabel hola = new JLabel("Hola, " + entrenador.getNombre(), SwingConstants.RIGHT);
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

        setVisible(true);
    }

    private ImageIcon cargarAvatar(Usuario u, int w, int h) {
        byte[] foto = u.getFotoUsuario();  // o getFotoBytes()
        BufferedImage srcImg = null;
        try {
            if (foto != null && foto.length > 0) {
                srcImg = ImageIO.read(new ByteArrayInputStream(foto));
            }
        } catch (Exception ignored) { }

        if (srcImg != null) {
            // reescalado de alta calidad
            BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buf.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(srcImg, 0, 0, w, h, null);
            g2.dispose();
            return new ImageIcon(buf);
        }

        // fallback al default usando el mismo loadIcon que empleas para los demás
        return loadIcon("user_default.png", w, h);
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


    /** Extrae innerMX para positioning de texto (8% de ancho del panel) */
    private int innerMX(JPanel panel) {
        return (int)(panel.getWidth() * 0.08);
    }

}
