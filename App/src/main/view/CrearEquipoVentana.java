package main.view;

import main.dao.EquipoDAO;
import main.model.Equipo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;

public class CrearEquipoVentana extends JDialog {
    private static final Color BG     = new Color(18,18,18);
    private static final Color FG     = Color.WHITE;
    private static final Color ACCENT = new Color(49,141,225);
    private static final int   MAX_PHOTO_BYTES = 1 * 1024 * 1024; // 1 MB


    private final Runnable onSuccess;
    private final Equipo newEquipo = new Equipo();

    // Componentes de UI
    private JLabel lblFoto;
    private JTextField txtNombre, txtLiga, txtClub, txtCiudad, txtPais;
    private JComboBox<String> cbCategoria;

    public CrearEquipoVentana(Frame parent, Runnable onSuccess) {
        super(parent, "Crear nuevo equipo", true);
        this.onSuccess = onSuccess;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG);
        getContentPane().setLayout(new BorderLayout());

        // --- Encabezado: título arriba, foto abajo ---
        lblFoto = new JLabel(cargarAvatarCircular(null, 100, 100));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFoto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblFoto.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter(
                        "Imágenes (jpg, png, gif)", "jpg","jpeg","png","gif"
                ));
                if (chooser.showOpenDialog(CrearEquipoVentana.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = chooser.getSelectedFile();
                        byte[] img = Files.readAllBytes(f.toPath());
                        if (img.length > MAX_PHOTO_BYTES) {
                            JOptionPane.showMessageDialog(
                                    CrearEquipoVentana.this,
                                    "La imagen es demasiado grande (" + (img.length/1024) + " KB).\n" +
                                            "Tamaño máximo permitido: 1 MB.",
                                    "Error al cargar imagen", JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }
                        newEquipo.setFotoEquipo(img);
                        lblFoto.setIcon(cargarAvatarCircular(newEquipo, 80, 80));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                CrearEquipoVentana.this,
                                "No se pudo cargar la imagen:\n" + ex.getMessage(),
                                "Error al cargar imagen", JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        JPanel header = new JPanel();
        header.setBackground(BG);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(20,20,10,20));

        JLabel lblTitle = new JLabel("Crear nuevo equipo", SwingConstants.CENTER);
        lblTitle.setForeground(FG);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(lblTitle);

        header.add(Box.createRigidArea(new Dimension(0, 15)));
        header.add(lblFoto);

        getContentPane().add(header, BorderLayout.NORTH);

        // --- Cuerpo del formulario (2 columnas) ---
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8,8,8,8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Nombre
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Nombre:"), gbc);
        txtNombre = createField("");
        gbc.gridx = 1;
        content.add(txtNombre, gbc);

        // Liga
        row++;
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Liga:"), gbc);
        txtLiga = createField("");
        gbc.gridx = 1;
        content.add(txtLiga, gbc);

        // Categoría (ahora JComboBox)
        row++;
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Categoría:"), gbc);
        cbCategoria = new JComboBox<>(new String[]{
                "Prebenjamin","Benjamin","Alevin",
                "Infantil","Juvenil","Junior","Senior"
        });
        cbCategoria.setBackground(BG);
        cbCategoria.setForeground(FG);
        cbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbCategoria.setBorder(BorderFactory.createLineBorder(ACCENT));
        gbc.gridx = 1;
        content.add(cbCategoria, gbc);

        // Club
        row++;
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Club:"), gbc);
        txtClub = createField("");
        gbc.gridx = 1;
        content.add(txtClub, gbc);

        // Ciudad
        row++;
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("Ciudad:"), gbc);
        txtCiudad = createField("");
        gbc.gridx = 1;
        content.add(txtCiudad, gbc);

        // País
        row++;
        gbc.gridy = row; gbc.gridx = 0;
        content.add(createLabel("País:"), gbc);
        txtPais = createField("");
        gbc.gridx = 1;
        content.add(txtPais, gbc);

        getContentPane().add(content, BorderLayout.CENTER);

        // --- Botones de pie ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        footer.setBackground(BG);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        footer.add(btnCancelar);

        JButton btnCrear = new JButton("Crear equipo");
        btnCrear.addActionListener(e -> crearEquipo());
        footer.add(btnCrear);

        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    private void crearEquipo() {
        // Validar obligatorios
        if (txtNombre.getText().trim().isEmpty()
                || txtLiga.getText().trim().isEmpty()
                || cbCategoria.getSelectedItem() == null
                || txtClub.getText().trim().isEmpty()
                || txtCiudad.getText().trim().isEmpty()
                || txtPais.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Rellena todos los campos obligatorios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Asignar datos
        newEquipo.setNombre(txtNombre.getText().trim());
        newEquipo.setLiga(txtLiga.getText().trim());
        newEquipo.setCategoria((String)cbCategoria.getSelectedItem());
        newEquipo.setClub(txtClub.getText().trim());
        newEquipo.setCiudad(txtCiudad.getText().trim());
        newEquipo.setPais(txtPais.getText().trim());

        // Guardar en BD
        try {
            new EquipoDAO().guardarEquipo(newEquipo);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar equipo:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Equipo creado correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        onSuccess.run();
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JTextField createField(String txt) {
        JTextField f = new JTextField(txt);
        styleField(f);
        return f;
    }

    private void styleField(JTextComponent f) {
        f.setBackground(BG);
        f.setForeground(FG);
        f.setBorder(BorderFactory.createLineBorder(ACCENT));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private ImageIcon cargarAvatarCircular(Equipo e, int w, int h) {
        try {
            byte[] foto = (e != null ? e.getFotoEquipo() : null);
            if (foto != null && foto.length > 0) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(foto));
                BufferedImage dst = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = dst.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new Ellipse2D.Float(0,0,w,h));
                g2.drawImage(src,0,0,w,h,null);
                g2.dispose();
                return new ImageIcon(dst);
            }
        } catch (Exception ignored) {}

        // Icono por defecto de equipo
        return loadIcon("user_default.png", w, h);
    }

    private ImageIcon loadIcon(String name, int w, int h) {
        URL res = getClass().getClassLoader().getResource("assets/" + name);
        Image img = (res != null)
                ? new ImageIcon(res).getImage()
                : new ImageIcon("src/assets/" + name).getImage();
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
}
