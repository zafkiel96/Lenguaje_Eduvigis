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

public class CasasPanel extends JPanel {
    private JTable tableCasas;
    private DefaultTableModel modelCasas;

    public CasasPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Gestión de Casas", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        modelCasas = new DefaultTableModel(new String[]{"ID Casa", "Número Manzana", "Nombre Familia", "Número Casa", "Anexo Casa", "Tenencia", "Estado"}, 0);
        tableCasas = createStyledTable(modelCasas);

        tableCasas.getColumnModel().getColumn(0).setMinWidth(0);
        tableCasas.getColumnModel().getColumn(0).setMaxWidth(0);
        tableCasas.getColumnModel().getColumn(0).setPreferredWidth(0);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        tablesPanel.setOpaque(false);
        tablesPanel.add(new JScrollPane(tableCasas));
        add(tablesPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton btnAddCasa = createStyledButton("Registrar Casa");
        JButton btnUpdateCasa = createStyledButton("Actualizar Casa");

        buttonPanel.add(btnAddCasa);
        buttonPanel.add(btnUpdateCasa);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAddCasa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCasaForm();
            }
        });

        btnUpdateCasa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateCasaForm();
            }
        });

        loadCasasFromDatabase();
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.BLACK);

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

    private void loadCasasFromDatabase() {
        modelCasas.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT c.id_casa, m.numero_manzana, g.nombre_familia, " +
                           "c.numero_casa, c.anexo_casa, c.ten_casa, c.status " +
                           "FROM casas c " +
                           "JOIN manzanas m ON c.id_manzana = m.id_manzana " +
                           "JOIN grupos_familiares g ON c.id_grupo_familiar = g.id_grupo_flia";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idCasa = rs.getInt("id_casa");
                int numeroManzana = rs.getInt("numero_manzana");
                String nombreFamilia = rs.getString("nombre_familia");
                int numeroCasa = rs.getInt("numero_casa");
                String anexoCasa = rs.getString("anexo_casa");
                String tenCasa = rs.getString("ten_casa");
                String status = rs.getString("status");

                modelCasas.addRow(new Object[]{idCasa, numeroManzana, nombreFamilia, numeroCasa, anexoCasa, tenCasa, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las casas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] getManzanas() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT numero_manzana FROM manzanas";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            java.util.List<String> manzanas = new java.util.ArrayList<>();
            while (rs.next()) {
                manzanas.add(rs.getString("numero_manzana"));
            }
            return manzanas.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar manzanas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private String[] getGruposFamiliares() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre_familia FROM grupos_familiares";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            java.util.List<String> grupos = new java.util.ArrayList<>();
            while (rs.next()) {
                grupos.add(rs.getString("nombre_familia"));
            }
            return grupos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar grupos familiares: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void showCasaForm() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Casa", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblNumeroCasa = new JLabel("Número de Casa:");
        formPanel.add(lblNumeroCasa, gbc);

        JTextField txtNumeroCasa = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtNumeroCasa, gbc);

        JLabel lblNumeroManzana = new JLabel("Número de Manzana:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblNumeroManzana, gbc);

        JComboBox<String> comboNumeroManzana = new JComboBox<>(getManzanas());
        gbc.gridx = 1;
        formPanel.add(comboNumeroManzana, gbc);

        JLabel lblNombreFamilia = new JLabel("Nombre de la Familia:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblNombreFamilia, gbc);

        JComboBox<String> comboNombreFamilia = new JComboBox<>(getGruposFamiliares());
        gbc.gridx = 1;
        formPanel.add(comboNombreFamilia, gbc);

        JLabel lblAnexoCasa = new JLabel("Anexo Casa:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblAnexoCasa, gbc);

        JTextField txtAnexoCasa = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtAnexoCasa, gbc);

        JLabel lblTenCasa = new JLabel("Tenencia:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(lblTenCasa, gbc);

        JComboBox<String> comboTenCasa = new JComboBox<>(new String[]{"Propia", "Alquilada"});
        gbc.gridx = 1;
        formPanel.add(comboTenCasa, gbc);

        JLabel lblStatus = new JLabel("Estado:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(lblStatus, gbc);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        gbc.gridx = 1;
        formPanel.add(comboStatus, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnSave = new JButton("Guardar");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String checkCasaQuery = "SELECT COUNT(*) FROM casas WHERE numero_casa = ?";
                    PreparedStatement checkCasaStmt = conn.prepareStatement(checkCasaQuery);
                    checkCasaStmt.setString(1, txtNumeroCasa.getText());
                    ResultSet rsCasa = checkCasaStmt.executeQuery();
                    rsCasa.next();
                    if (rsCasa.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "Ya existe una casa con ese número.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String checkFamiliaQuery = "SELECT COUNT(*) FROM casas WHERE id_grupo_familiar = (SELECT id_grupo_flia FROM grupos_familiares WHERE nombre_familia = ?)";
                    PreparedStatement checkFamiliaStmt = conn.prepareStatement(checkFamiliaQuery);
                    checkFamiliaStmt.setString(1, comboNombreFamilia.getSelectedItem().toString());
                    ResultSet rsFamilia = checkFamiliaStmt.executeQuery();
                    rsFamilia.next();
                    if (rsFamilia.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "La familia ya está registrada en otra casa.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String query = "INSERT INTO casas (id_manzana, id_grupo_familiar, numero_casa, anexo_casa, ten_casa, status) " +
                                   "VALUES ((SELECT id_manzana FROM manzanas WHERE numero_manzana = ?), " +
                                   "(SELECT id_grupo_flia FROM grupos_familiares WHERE nombre_familia = ?), ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, comboNumeroManzana.getSelectedItem().toString());
                    stmt.setString(2, comboNombreFamilia.getSelectedItem().toString());
                    stmt.setString(3, txtNumeroCasa.getText());
                    stmt.setString(4, txtAnexoCasa.getText());
                    stmt.setString(5, comboTenCasa.getSelectedItem().toString());
                    stmt.setString(6, comboStatus.getSelectedItem().toString());

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(dialog, "Casa registrada exitosamente.");
                        loadCasasFromDatabase();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Error al registrar la casa.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al registrar la casa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnCancel = new JButton("Cancelar");
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

    private void showUpdateCasaForm() {
        int selectedRow = tableCasas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una casa para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCasa = (int) modelCasas.getValueAt(selectedRow, 0);
        String currentNumeroCasa = modelCasas.getValueAt(selectedRow, 3).toString();
        String currentAnexoCasa = modelCasas.getValueAt(selectedRow, 4).toString();
        String currentTenCasa = modelCasas.getValueAt(selectedRow, 5).toString();
        String currentStatus = modelCasas.getValueAt(selectedRow, 6).toString();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Actualizar Casa", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblNumeroCasa = new JLabel("Número de Casa:");
        formPanel.add(lblNumeroCasa, gbc);

        JTextField txtNumeroCasa = new JTextField(20);
        txtNumeroCasa.setText(currentNumeroCasa);
        gbc.gridx = 1;
        formPanel.add(txtNumeroCasa, gbc);

        JLabel lblAnexoCasa = new JLabel("Anexo Casa:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblAnexoCasa, gbc);

        JTextField txtAnexoCasa = new JTextField(20);
        txtAnexoCasa.setText(currentAnexoCasa);
        gbc.gridx = 1;
        formPanel.add(txtAnexoCasa, gbc);

        JLabel lblTenCasa = new JLabel("Tenencia:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblTenCasa, gbc);

        JComboBox<String> comboTenCasa = new JComboBox<>(new String[]{"Propia", "Alquilada"});
        comboTenCasa.setSelectedItem(currentTenCasa);
        gbc.gridx = 1;
        formPanel.add(comboTenCasa, gbc);

        JLabel lblStatus = new JLabel("Estado:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblStatus, gbc);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        comboStatus.setSelectedItem(currentStatus);
        gbc.gridx = 1;
        formPanel.add(comboStatus, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnSave = new JButton("Guardar");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String checkCasaQuery = "SELECT COUNT(*) FROM casas WHERE numero_casa = ? AND id_casa <> ?";
                    PreparedStatement checkCasaStmt = conn.prepareStatement(checkCasaQuery);
                    checkCasaStmt.setString(1, txtNumeroCasa.getText());
                    checkCasaStmt.setInt(2, idCasa);
                    ResultSet rsCasa = checkCasaStmt.executeQuery();
                    rsCasa.next();
                    if (rsCasa.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "Ya existe una casa con ese número.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String checkFamiliaQuery = "SELECT COUNT(*) FROM casas WHERE id_grupo_familiar = (SELECT id_grupo_flia FROM grupos_familiares WHERE nombre_familia = ?) AND id_casa <> ?";
                    PreparedStatement checkFamiliaStmt = conn.prepareStatement(checkFamiliaQuery);
                    checkFamiliaStmt.setString(1, comboTenCasa.getSelectedItem().toString());
                    checkFamiliaStmt.setInt(2, idCasa);
                    ResultSet rsFamilia = checkFamiliaStmt.executeQuery();
                    rsFamilia.next();
                    if (rsFamilia.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "La familia ya está registrada en otra casa.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String query = "UPDATE casas SET numero_casa = ?, anexo_casa = ?, ten_casa = ?, status = ? WHERE id_casa = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, txtNumeroCasa.getText());
                    stmt.setString(2, txtAnexoCasa.getText());
                    stmt.setString(3, comboTenCasa.getSelectedItem().toString());
                    stmt.setString(4, comboStatus.getSelectedItem().toString());
                    stmt.setInt(5, idCasa);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(dialog, "Casa actualizada exitosamente.");
                        loadCasasFromDatabase();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Error al actualizar la casa.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar la casa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnCancel = new JButton("Cancelar");
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
}
