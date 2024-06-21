package ui;

import model.Usuario;
import model.DatabaseConnection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class JefeManzanaPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ArrayList<Usuario> usuarios;
    private final Font mainFont = new Font("Arial", Font.PLAIN, 14);
    private final Font boldFont = new Font("Arial", Font.BOLD, 14);
    private final Color mainColor = new Color(60, 63, 65);
    private final Color backgroundColor = new Color(242, 242, 242);
    private final Color buttonColor = new Color(28, 115, 196);

    public JefeManzanaPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));
      
        JLabel titleLabel = new JLabel("Panel de Jefe de Manzana", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Cédula", "Usuario", "Nombre", "Correo", "Status"}, 0);
        table = new JTable(tableModel);
        table.getTableHeader().setFont(boldFont);
        table.setFont(mainFont);
        table.setRowHeight(25);
        table.setBackground(Color.WHITE);
        table.setForeground(mainColor);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        JButton btnAdd = createStyledButton("Agregar");
        JButton btnEdit = createStyledButton("Editar");
        JButton btnDelete = createStyledButton("Eliminar");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        add(buttonPanel, BorderLayout.SOUTH);

        usuarios = new ArrayList<>();
        cargarUsuarios();

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUsuarioForm(null);
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    Usuario usuario = usuarios.get(selectedRow);
                    showUsuarioForm(usuario);
                } else {
                    JOptionPane.showMessageDialog(JefeManzanaPanel.this, "Seleccione un usuario para editar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    Usuario usuario = usuarios.get(selectedRow);
                    int confirm = showConfirmDialog(JefeManzanaPanel.this, "¿Está seguro de que desea eliminar este usuario?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        updateUsuarioStatus(usuario.getCedula(), "Inactivo");
                    }
                } else {
                    JOptionPane.showMessageDialog(JefeManzanaPanel.this, "Seleccione un usuario para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) {
        Object[] options = {"Sí", "No"};
        return JOptionPane.showOptionDialog(parentComponent, message, title, optionType, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(boldFont);
        button.setForeground(Color.BLACK);  // Cambiar color del texto a negro
        button.setBackground(buttonColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor, 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return button;
    }

    private void cargarUsuarios() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT u.id_usuario, u.cedula, u.usuario, u.contraseña, u.correo, u.status, p.primer_nombre, p.primer_apellido " +
                           "FROM usuarios u " +
                           "JOIN personas p ON u.cedula = p.cedula " +
                           "WHERE u.status = 'Activo'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            usuarios.clear();
            tableModel.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                String cedula = rs.getString("cedula");
                String usuario = rs.getString("usuario");
                String contraseña = rs.getString("contraseña");
                String correo = rs.getString("correo");
                String status = rs.getString("status");
                String nombre = rs.getString("primer_nombre") + " " + rs.getString("primer_apellido");

                Usuario user = new Usuario(id, cedula, usuario, contraseña, correo, status);
                usuarios.add(user);
                tableModel.addRow(new Object[]{cedula, usuario, nombre, correo, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUsuarioForm(Usuario usuario) {
        JDialog dialog = new JDialog((Frame) null, "Formulario de Usuario", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JTextField txtCedula = new JTextField();
        txtCedula.setFont(mainFont);
        txtCedula.setForeground(mainColor);
        ((AbstractDocument) txtCedula.getDocument()).setDocumentFilter(new DocumentFilter() {
            private final Pattern regEx = Pattern.compile("\\d*");

            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (regEx.matcher(string).matches() && (fb.getDocument().getLength() + string.length() <= 8)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                if (regEx.matcher(text).matches() && (fb.getDocument().getLength() - length + text.length() <= 8)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JTextField txtUsuario = new JTextField();
        txtUsuario.setFont(mainFont);
        txtUsuario.setForeground(mainColor);
        JPasswordField txtContraseña = new JPasswordField();
        txtContraseña.setFont(mainFont);
        txtContraseña.setForeground(mainColor);
        JTextField txtCorreo = new JTextField();
        txtCorreo.setFont(mainFont);
        txtCorreo.setForeground(mainColor);
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Activo"});
        cbStatus.setFont(mainFont);
        cbStatus.setForeground(mainColor);

        boolean isEditMode = (usuario != null);
        if (isEditMode) {
            txtCedula.setText(usuario.getCedula());
            txtCedula.setEnabled(false);
            txtUsuario.setText(usuario.getUsuario());
            txtCorreo.setText(usuario.getCorreo());
            cbStatus.setSelectedItem(usuario.getStatus());
        } else {
            cbStatus.setSelectedIndex(0);
        }

        formPanel.add(createStyledLabel("Cédula:"));
        formPanel.add(txtCedula);
        formPanel.add(createStyledLabel("Usuario:"));
        formPanel.add(txtUsuario);
        formPanel.add(createStyledLabel("Contraseña:"));
        formPanel.add(txtContraseña);
        formPanel.add(createStyledLabel("Correo:"));
        formPanel.add(txtCorreo);
        formPanel.add(createStyledLabel("Status:"));
        formPanel.add(cbStatus);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnSave = createStyledButton("Guardar");
        JButton btnCancel = createStyledButton("Cancelar");

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cedula = txtCedula.getText();
                String usuario = txtUsuario.getText();
                String contraseña = new String(txtContraseña.getPassword());
                String correo = txtCorreo.getText();
                String status = cbStatus.getSelectedItem().toString();

                if (isEditMode) {
                    updateUsuario(cedula, usuario, contraseña, correo, status);
                } else {
                    addUsuario(cedula, usuario, contraseña, correo, status);
                }
                dialog.dispose();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(mainFont);
        label.setForeground(mainColor);
        return label;
    }

    private void addUsuario(String cedula, String usuario, String contraseña, String correo, String status) {
        if (isCedulaOrUsuarioDuplicate(cedula, usuario)) {
            JOptionPane.showMessageDialog(this, "No se pueden registrar dos usuarios con la misma cédula o nombre de usuario.", "Error de registro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isCedulaInPersonas(cedula)) {
            JOptionPane.showMessageDialog(this, "La cédula no coincide con ninguna cédula de la tabla personas.", "Error de registro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO usuarios (cedula, usuario, contraseña, correo, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, cedula);
            stmt.setString(2, usuario);
            stmt.setString(3, contraseña);
            stmt.setString(4, correo);
            stmt.setString(5, status);

            stmt.executeUpdate();
            cargarUsuarios();
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUsuario(String cedula, String usuario, String contraseña, String correo, String status) {
        if (isUsuarioDuplicate(cedula, usuario)) {
            JOptionPane.showMessageDialog(this, "No se pueden registrar dos usuarios con el mismo nombre de usuario.", "Error de registro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE usuarios SET usuario = ?, contraseña = ?, correo = ?, status = ? WHERE cedula = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario);
            stmt.setString(2, contraseña);
            stmt.setString(3, correo);
            stmt.setString(4, status);
            stmt.setString(5, cedula);

            stmt.executeUpdate();
            cargarUsuarios();
            JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente.", "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isCedulaOrUsuarioDuplicate(String cedula, String usuario) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM usuarios WHERE cedula = ? OR usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, cedula);
            stmt.setString(2, usuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isUsuarioDuplicate(String cedula, String usuario) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM usuarios WHERE usuario = ? AND cedula != ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario);
            stmt.setString(2, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isCedulaInPersonas(String cedula) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM personas WHERE cedula = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateUsuarioStatus(String cedula, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE usuarios SET status = ? WHERE cedula = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, cedula);

            stmt.executeUpdate();
            cargarUsuarios();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado del usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
