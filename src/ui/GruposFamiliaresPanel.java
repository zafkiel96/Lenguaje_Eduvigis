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

public class GruposFamiliaresPanel extends JPanel {
    private JTable tableGruposFamiliares;
    private DefaultTableModel modelGruposFamiliares;

    public GruposFamiliaresPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Gestión de Grupos Familiares", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        modelGruposFamiliares = new DefaultTableModel(new String[]{"ID", "Nombre de Familia", "Status"}, 0);
        tableGruposFamiliares = createStyledTable(modelGruposFamiliares);

        tableGruposFamiliares.getColumnModel().getColumn(0).setMinWidth(0);
        tableGruposFamiliares.getColumnModel().getColumn(0).setMaxWidth(0);
        tableGruposFamiliares.getColumnModel().getColumn(0).setPreferredWidth(0);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        tablesPanel.setOpaque(false);
        tablesPanel.add(new JScrollPane(tableGruposFamiliares));
        add(tablesPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Agregar Grupo Familiar");
        JButton btnEdit = createStyledButton("Editar Grupo Familiar");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddGrupoFamiliarForm();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditGrupoFamiliarForm();
            }
        });

        loadGruposFamiliaresFromDatabase();
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

    private void loadGruposFamiliaresFromDatabase() {
        modelGruposFamiliares.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM grupos_familiares";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id_grupo_flia");
                String nombreFamilia = rs.getString("nombre_familia");
                String status = rs.getString("status");
                modelGruposFamiliares.addRow(new Object[]{id, nombreFamilia, status});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar los grupos familiares: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddGrupoFamiliarForm() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Agregar Grupo Familiar", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblNombreFamilia = new JLabel("Nombre de Familia:");
        formPanel.add(lblNombreFamilia, gbc);

        JTextField txtNombreFamilia = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtNombreFamilia, gbc);

        JLabel lblStatus = new JLabel("Estado:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblStatus, gbc);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        gbc.gridx = 1;
        formPanel.add(comboStatus, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnSave = createStyledButton("Guardar");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "INSERT INTO grupos_familiares (nombre_familia, status) VALUES (?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, txtNombreFamilia.getText());
                    stmt.setString(2, comboStatus.getSelectedItem().toString());

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(dialog, "Grupo familiar registrado exitosamente.");
                        loadGruposFamiliaresFromDatabase();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Error al registrar el grupo familiar.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al registrar el grupo familiar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnCancel = createStyledButton("Cancelar");
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

    private void showEditGrupoFamiliarForm() {
        int selectedRow = tableGruposFamiliares.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un grupo familiar para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idGrupoFamiliar = (int) modelGruposFamiliares.getValueAt(selectedRow, 0);
        String currentNombreFamilia = modelGruposFamiliares.getValueAt(selectedRow, 1).toString();
        String currentStatus = modelGruposFamiliares.getValueAt(selectedRow, 2).toString();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Actualizar Grupo Familiar", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblNombreFamilia = new JLabel("Nombre de Familia:");
        formPanel.add(lblNombreFamilia, gbc);

        JTextField txtNombreFamilia = new JTextField(20);
        txtNombreFamilia.setText(currentNombreFamilia);
        gbc.gridx = 1;
        formPanel.add(txtNombreFamilia, gbc);

        JLabel lblStatus = new JLabel("Estado:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblStatus, gbc);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        comboStatus.setSelectedItem(currentStatus);
        gbc.gridx = 1;
        formPanel.add(comboStatus, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnSave = createStyledButton("Actualizar");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "UPDATE grupos_familiares SET nombre_familia = ?, status = ? WHERE id_grupo_flia = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, txtNombreFamilia.getText());
                    stmt.setString(2, comboStatus.getSelectedItem().toString());
                    stmt.setInt(3, idGrupoFamiliar);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(dialog, "Grupo familiar actualizado exitosamente.");
                        loadGruposFamiliaresFromDatabase();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Error al actualizar el grupo familiar.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar el grupo familiar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnCancel = createStyledButton("Cancelar");
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
