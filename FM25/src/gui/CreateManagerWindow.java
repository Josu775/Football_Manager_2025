package gui;

import javax.swing.*;
import java.awt.*;

public class CreateManagerWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public CreateManagerWindow(JFrame parent, java.util.function.BiConsumer<String, String> callback) {
        super("Nuevo Manager");

        setSize(420, 260);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(15,15));
        panel.setBackground(new Color(5, 10, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        add(panel);

        // — TITULO —
        JLabel title = new JLabel("Introduce tu nombre:", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // — CAMPO DE TEXTO —
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(5, 10, 30));

        JTextField txtName = new JTextField();
        txtName.setPreferredSize(new Dimension(230, 32));  
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtName.setHorizontalAlignment(SwingConstants.CENTER);
        txtName.setBackground(new Color(15, 25, 55));
        txtName.setForeground(Color.WHITE);
        txtName.setCaretColor(Color.WHITE);

        // límite 12 chars
        txtName.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str != null && (getLength() + str.length()) <= 12) {
                    super.insertString(offs, str, a);
                }
            }
        });

        centerPanel.add(txtName);
        panel.add(centerPanel, BorderLayout.CENTER);

        // — BOTÓN CONTINUAR (ESTÁNDAR, SIN COLORES) —
        JButton btn = new JButton("Continuar");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        // ❌ Sin setBackground
        // ❌ Sin setForeground
        // → Botón normal del sistema

        JPanel south = new JPanel();
        south.setBackground(new Color(5, 10, 30));
        south.add(btn);
        panel.add(south, BorderLayout.SOUTH);

        btn.addActionListener(e -> {
            String name = txtName.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduce un nombre.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.length() > 12) {
                JOptionPane.showMessageDialog(this, "Máximo 12 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            new AppearanceWindow(this, name, callback);
            dispose();
        });

        setVisible(true);
    }
}
