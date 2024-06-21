package ui;

import model.DatabaseConnection;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.toedter.calendar.JCalendar;
import java.sql.*;

public class BeneficiosPanel extends JPanel {
    private JTable tableBeneficios;
    private JTable tableEntregados;
    private DefaultTableModel modelBeneficios;
    private DefaultTableModel modelEntregados;

    public BeneficiosPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Gestión de Beneficios", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        modelBeneficios = new DefaultTableModel(new String[]{"Nombre del Beneficio", "Status"}, 0);
        tableBeneficios = createStyledTable(modelBeneficios);

        modelEntregados = new DefaultTableModel(new String[]{"Nombre del Beneficio", "Manzana", "Grupo Familiar", "Casa", "Fecha Entrega", "Status"}, 0);
        tableEntregados = createStyledTable(modelEntregados);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablesPanel.setOpaque(false);
        tablesPanel.add(new JScrollPane(tableBeneficios));
        tablesPanel.add(new JScrollPane(tableEntregados));
        add(tablesPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton btnAddBeneficio = createStyledButton("Registrar Beneficio");
        JButton btnAddEntrega = createStyledButton("Registrar Entrega");
        JButton btnReporteSemanal = createStyledButton("Reportes de Entrega");
        buttonPanel.add(btnAddBeneficio);
        buttonPanel.add(btnAddEntrega);
        buttonPanel.add(btnReporteSemanal);
 
        add(buttonPanel, BorderLayout.SOUTH);

        btnAddBeneficio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBeneficioForm();
            }
        });

        btnAddEntrega.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEntregaForm();
            }
        });
btnReporteSemanal.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        showDateRangeForm("REPORTES");
    }
});
       

        loadBeneficiosFromDatabase();
        loadEntregadosFromDatabase();
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

    private void loadBeneficiosFromDatabase() {
        modelBeneficios.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre_beneficio, status FROM beneficios";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombre = rs.getString("nombre_beneficio");
                String status = rs.getString("status");
                modelBeneficios.addRow(new Object[]{nombre, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los beneficios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEntregadosFromDatabase() {
        modelEntregados.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.nombre_beneficio, m.numero_manzana, gf.nombre_familia, c.numero_casa, h.fecha, h.status " +
                           "FROM historicos_beneficios h " +
                           "JOIN beneficios b ON h.id_beneficio = b.id_beneficio " +
                           "JOIN casas c ON h.id_casa = c.id_casa " +
                           "JOIN manzanas m ON c.id_manzana = m.id_manzana " +
                           "JOIN grupos_familiares gf ON h.id_grupo_flia = gf.id_grupo_flia " +
                           "ORDER BY h.fecha DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombre = rs.getString("nombre_beneficio");
                String manzana = rs.getString("numero_manzana");
                String grupoFamiliar = rs.getString("nombre_familia");
                String casa = rs.getString("numero_casa");
                String fecha = rs.getString("fecha");
                String status = rs.getString("status");
                modelEntregados.addRow(new Object[]{nombre, manzana, grupoFamiliar, casa, fecha, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los beneficios entregados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBeneficioForm() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Beneficio", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblNombre = new JLabel("Nombre del Beneficio:");
        formPanel.add(lblNombre, gbc);

        JTextField txtNombre = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);

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
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "El nombre del beneficio es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isBeneficioDuplicated(nombre)) {
                    JOptionPane.showMessageDialog(dialog, "El beneficio ya existe.", "Error de Duplicidad", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                registerBeneficio(nombre);
                loadBeneficiosFromDatabase();
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

    private void registerBeneficio(String nombre) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO beneficios (nombre_beneficio, status, fecha_entregado) VALUES (?, 'Activo', NOW())";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Beneficio registrado exitosamente.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el beneficio.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el beneficio: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isBeneficioDuplicated(String nombre) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM beneficios WHERE nombre_beneficio = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar la duplicidad del beneficio: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void showEntregaForm() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Entrega de Beneficio", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblBeneficio = new JLabel("Beneficio:");
        formPanel.add(lblBeneficio, gbc);

        JComboBox<String> cbBeneficio = new JComboBox<>();
        loadBeneficioNames(cbBeneficio);
        gbc.gridx = 1;
        formPanel.add(cbBeneficio, gbc);

        JLabel lblGrupoFamiliar = new JLabel("Grupo Familiar:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblGrupoFamiliar, gbc);

        JComboBox<String> cbGrupoFamiliar = new JComboBox<>();
        loadGrupoFamiliarNames(cbGrupoFamiliar);
        gbc.gridx = 1;
        formPanel.add(cbGrupoFamiliar, gbc);

        JLabel lblDescripcion = new JLabel("Descripción:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblDescripcion, gbc);

        JTextField txtDescripcion = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtDescripcion, gbc);

        JLabel lblObservacion = new JLabel("Observación:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblObservacion, gbc);

        JTextField txtObservacion = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtObservacion, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnSave = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String beneficio = (String) cbBeneficio.getSelectedItem();
                String grupoFamiliar = (String) cbGrupoFamiliar.getSelectedItem();
                String descripcion = txtDescripcion.getText().trim();
                String observacion = txtObservacion.getText().trim();
                LocalDate fecha = LocalDate.now();

                if (beneficio == null || grupoFamiliar == null || descripcion.isEmpty() || observacion.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isEntregaDuplicated(grupoFamiliar, fecha)) {
                    JOptionPane.showMessageDialog(dialog, "El beneficio ya ha sido entregado al mismo grupo familiar en la misma fecha.", "Error de Duplicidad", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                registerEntrega(beneficio, grupoFamiliar, descripcion, observacion, fecha);
                loadEntregadosFromDatabase();
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

    private void loadBeneficioNames(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre_beneficio FROM beneficios";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboBox.addItem(rs.getString("nombre_beneficio"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los nombres de beneficios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGrupoFamiliarNames(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre_familia FROM grupos_familiares";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboBox.addItem(rs.getString("nombre_familia"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los nombres de grupos familiares: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isEntregaDuplicated(String grupoFamiliar, LocalDate fecha) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM historicos_beneficios hb " +
                           "JOIN grupos_familiares gf ON hb.id_grupo_flia = gf.id_grupo_flia " +
                           "WHERE gf.nombre_familia = ? AND hb.fecha = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, grupoFamiliar);
            stmt.setDate(2, Date.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar la duplicidad de la entrega: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void registerEntrega(String beneficio, String grupoFamiliar, String descripcion, String observacion, LocalDate fecha) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String queryBeneficio = "SELECT id_beneficio FROM beneficios WHERE nombre_beneficio = ?";
            PreparedStatement stmtBeneficio = conn.prepareStatement(queryBeneficio);
            stmtBeneficio.setString(1, beneficio);
            ResultSet rsBeneficio = stmtBeneficio.executeQuery();
            rsBeneficio.next();
            int idBeneficio = rsBeneficio.getInt("id_beneficio");

            String queryGrupoFamiliar = "SELECT id_grupo_flia FROM grupos_familiares WHERE nombre_familia = ?";
            PreparedStatement stmtGrupoFamiliar = conn.prepareStatement(queryGrupoFamiliar);
            stmtGrupoFamiliar.setString(1, grupoFamiliar);
            ResultSet rsGrupoFamiliar = stmtGrupoFamiliar.executeQuery();
            rsGrupoFamiliar.next();
            int idGrupoFamiliar = rsGrupoFamiliar.getInt("id_grupo_flia");

            String queryCasa = "SELECT id_casa, id_manzana FROM casas WHERE id_grupo_familiar = ?";
            PreparedStatement stmtCasa = conn.prepareStatement(queryCasa);
            stmtCasa.setInt(1, idGrupoFamiliar);
            ResultSet rsCasa = stmtCasa.executeQuery();
            rsCasa.next();
            int idCasa = rsCasa.getInt("id_casa");
            int idManzana = rsCasa.getInt("id_manzana");

            String query = "INSERT INTO historicos_beneficios (id_usuario, id_tipo_usuario, id_grupo_flia, id_manzana, id_casa, id_beneficio, fecha, descripcion, observacion, status) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Entregado')";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, 1);
            stmt.setInt(2, 1);
            stmt.setInt(3, idGrupoFamiliar);
            stmt.setInt(4, idManzana);
            stmt.setInt(5, idCasa);
            stmt.setInt(6, idBeneficio);
            stmt.setDate(7, Date.valueOf(fecha));
            stmt.setString(8, descripcion);
            stmt.setString(9, observacion);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Entrega registrada exitosamente.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la entrega.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar la entrega: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generarReporte(java.util.Date fechaInicio, java.util.Date fechaFin, String tipo) {
    JTextArea textArea = new JTextArea(20, 50);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    textArea.setEditable(false);
    textArea.setText("Reporte de Entregas " + tipo + "\n\n");

    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT b.nombre_beneficio, m.numero_manzana, gf.nombre_familia, c.numero_casa, h.fecha, h.status " +
                       "FROM historicos_beneficios h " +
                       "JOIN beneficios b ON h.id_beneficio = b.id_beneficio " +
                       "JOIN casas c ON h.id_casa = c.id_casa " +
                       "JOIN manzanas m ON c.id_manzana = m.id_manzana " +
                       "JOIN grupos_familiares gf ON h.id_grupo_flia = gf.id_grupo_flia " +
                       "WHERE h.fecha BETWEEN ? AND ? " +
                       "ORDER BY h.fecha DESC";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
        stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String nombre = rs.getString("nombre_beneficio");
            String manzana = rs.getString("numero_manzana");
            String grupoFamiliar = rs.getString("nombre_familia");
            String casa = rs.getString("numero_casa");
            Date fecha = rs.getDate("fecha");
            String status = rs.getString("status");

            textArea.append("Beneficio: " + nombre + "\n");
            textArea.append("Manzana: " + manzana + "\n");
            textArea.append("Grupo Familiar: " + grupoFamiliar + "\n");
            textArea.append("Casa: " + casa + "\n");
            textArea.append("Fecha Entrega: " + fecha + "\n");
            textArea.append("Status: " + status + "\n");
            textArea.append("\n");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    JScrollPane scrollPane = new JScrollPane(textArea);
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reporte de Entregas " + tipo, true);
    dialog.add(scrollPane);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}

    
   private void showDateRangeForm(String tipo) {
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Seleccionar Rango de Fechas", true);
    dialog.setSize(400, 500);
    dialog.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    JLabel lblFechaInicio = new JLabel("Fecha Inicio:");
    dialog.add(lblFechaInicio, gbc);

    JCalendar calendarInicio = new JCalendar();
    gbc.gridx = 1;
    dialog.add(calendarInicio, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    JLabel lblFechaFin = new JLabel("Fecha Fin:");
    dialog.add(lblFechaFin, gbc);

    JCalendar calendarFin = new JCalendar();
    gbc.gridx = 1;
    dialog.add(calendarFin, gbc);

    JPanel buttonPanel = new JPanel();
    JButton btnGenerar = createStyledButton("Generar Reporte");
    JButton btnCancelar = createStyledButton("Cancelar");

    buttonPanel.add(btnGenerar);
    buttonPanel.add(btnCancelar);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    dialog.add(buttonPanel, gbc);

    btnGenerar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            java.util.Date fechaInicio = calendarInicio.getDate();
            java.util.Date fechaFin = calendarFin.getDate();

            if (fechaInicio == null || fechaFin == null) {
                JOptionPane.showMessageDialog(dialog, "Ambas fechas son obligatorias.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fechaInicio.after(fechaFin)) {
                JOptionPane.showMessageDialog(dialog, "La fecha de inicio no puede ser después de la fecha de fin.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            generarReporte(fechaInicio, fechaFin, tipo);
            dialog.dispose();
        }
    });

    btnCancelar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    });

    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}

}
