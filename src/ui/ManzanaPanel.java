package ui;

import model.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManzanaPanel extends JPanel {
    private JTable tableManzanas;
    private DefaultTableModel modelManzanas;

    public ManzanaPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Gestión de Manzanas", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        modelManzanas = new DefaultTableModel(new String[]{"ID Manzana", "Número Manzana", "Número de Casas", "Número de Grupos", "Jefe de Manzana"}, 0);
        tableManzanas = createStyledTable(modelManzanas);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        tablesPanel.setOpaque(false);
        tablesPanel.add(new JScrollPane(tableManzanas));
        add(tablesPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton btnAddManzana = createStyledButton("Registrar Manzana");
        JButton btnAssignJefe = createStyledButton("Asignar Jefe");

        buttonPanel.add(btnAddManzana);
        buttonPanel.add(btnAssignJefe);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAddManzana.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManzanaForm();
            }
        });

        btnAssignJefe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAssignJefeForm();
            }
        });

        loadManzanasFromDatabase();
    }

    private JTable createStyledTable(DefaultTableModel model) {
    JTable table = new JTable(model);
    table.setFillsViewportHeight(true);
    table.setRowHeight(30);
    table.setFont(new Font("Arial", Font.PLAIN, 14));
    table.setSelectionBackground(new Color(184, 207, 229));
    table.setSelectionForeground(Color.BLACK);

    table.getColumnModel().getColumn(0).setMinWidth(0);
    table.getColumnModel().getColumn(0).setMaxWidth(0);
    table.getColumnModel().getColumn(0).setWidth(0);
    table.getColumnModel().getColumn(0).setPreferredWidth(0);

    JTableHeader header = table.getTableHeader();
    header.setBackground(new Color(70, 130, 180));
    header.setForeground(Color.BLACK);
    header.setFont(new Font("Arial", Font.BOLD, 16));

    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    table.setDefaultRenderer(Object.class, centerRenderer);

    return table;
}

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void loadManzanasFromDatabase() {
        modelManzanas.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT m.id_manzana, m.numero_manzana, " +
                           "COUNT(c.id_casa) AS num_casas, " +
                           "COUNT(DISTINCT c.id_grupo_familiar) AS num_grupos, " +
                           "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS jefe_manzana " +
                           "FROM manzanas m " +
                           "LEFT JOIN casas c ON m.id_manzana = c.id_manzana " +
                           "LEFT JOIN usuarios u ON m.id_jefe_manzana = u.id_usuario " +
                           "LEFT JOIN personas p ON u.cedula = p.cedula " +
                           "GROUP BY m.id_manzana";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idManzana = rs.getInt("id_manzana");
                String numeroManzana = rs.getString("numero_manzana");
                int numCasas = rs.getInt("num_casas");
                int numGrupos = rs.getInt("num_grupos");
                String jefeManzana = rs.getString("jefe_manzana");
                modelManzanas.addRow(new Object[]{idManzana, numeroManzana, numCasas, numGrupos, jefeManzana});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las manzanas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showManzanaForm() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Manzana", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblNumeroManzana = new JLabel("Número de Manzana:");
        formPanel.add(lblNumeroManzana, gbc);

        JTextField txtNumeroManzana = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtNumeroManzana, gbc);

        JLabel lblDescripcion = new JLabel("Descripción:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblDescripcion, gbc);

        JTextField txtDescripcion = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtDescripcion, gbc);

        JLabel lblObservacion = new JLabel("Observación:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblObservacion, gbc);

        JTextField txtObservacion = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtObservacion, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton btnSave = createStyledButton("Guardar");
        JButton btnCancel = createStyledButton("Cancelar");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numeroManzana = txtNumeroManzana.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                String observacion = txtObservacion.getText().trim();

                if (numeroManzana.isEmpty() || descripcion.isEmpty() || observacion.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                registerManzana(numeroManzana, descripcion, observacion);
                loadManzanasFromDatabase();
                dialog.dispose();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void registerManzana(String numeroManzana, String descripcion, String observacion) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String checkQuery = "SELECT COUNT(*) FROM manzanas WHERE numero_manzana = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, numeroManzana);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(this, "Ya existe una manzana con ese número.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO manzanas (numero_manzana, descripcion, observacion) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, numeroManzana);
        stmt.setString(2, descripcion);
        stmt.setString(3, observacion);
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Manzana registrada exitosamente.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la manzana.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al registrar la manzana: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void showAssignJefeForm() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Asignar Jefe de Manzana", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblManzana = new JLabel("Seleccionar Manzana:");
        formPanel.add(lblManzana, gbc);

        JComboBox<String> cbManzanas = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cbManzanas, gbc);

        JLabel lblJefe = new JLabel("Seleccionar Jefe:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblJefe, gbc);

        JComboBox<String> cbJefes = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cbJefes, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton btnSave = createStyledButton("Guardar");
        JButton btnCancel = createStyledButton("Cancelar");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        loadManzanas(cbManzanas);
        loadJefes(cbJefes);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedManzana = (String) cbManzanas.getSelectedItem();
                String selectedJefe = (String) cbJefes.getSelectedItem();

                if (selectedManzana == null || selectedJefe == null) {
                    JOptionPane.showMessageDialog(dialog, "Debe seleccionar una manzana y un jefe.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int idManzana = Integer.parseInt(selectedManzana.split(" - ")[0]);
                int idJefe = Integer.parseInt(selectedJefe.split(" - ")[0]);

                assignJefe(idManzana, idJefe);
                loadManzanasFromDatabase();
                dialog.dispose();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadManzanas(JComboBox<String> cbManzanas) {
        cbManzanas.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id_manzana, numero_manzana FROM manzanas";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idManzana = rs.getInt("id_manzana");
                String numeroManzana = rs.getString("numero_manzana");
                cbManzanas.addItem(idManzana + " - " + numeroManzana);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las manzanas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadJefes(JComboBox<String> cbJefes) {
        cbJefes.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT u.id_usuario, CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nombre_completo " +
                           "FROM usuarios u " +
                           "JOIN personas p ON u.cedula = p.cedula " +
                           "JOIN tipos_usuarios tu ON u.id_usuario = tu.id_usuario " +
                           "WHERE tu.tipo = 'Jefe Manzana' AND tu.status = 'Activo' AND u.status = 'Activo'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idUsuario = rs.getInt("id_usuario");
                String nombreCompleto = rs.getString("nombre_completo");
                cbJefes.addItem(idUsuario + " - " + nombreCompleto);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los jefes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignJefe(int idManzana, int idJefe) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT COUNT(*) AS count FROM manzanas WHERE id_jefe_manzana = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, idJefe);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("count") < 2) {
                String updateQuery = "UPDATE manzanas SET id_jefe_manzana = ? WHERE id_manzana = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, idJefe);
                updateStmt.setInt(2, idManzana);
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Jefe asignado exitosamente.", "Asignación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo asignar el jefe.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No puede haber más de 2 jefes de manzana.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al asignar el jefe: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
