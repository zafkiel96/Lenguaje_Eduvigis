package ui;

import model.Persona;
import model.DatabaseConnection;
import util.LimitedDocument;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HabitantesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private ArrayList<Persona> personas;

    public HabitantesPanel() {
        personas = new ArrayList<>();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Gestión de Habitantes", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        searchField = new JTextField(15);
        JLabel searchLabel = new JLabel("Buscar: ");
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Cédula", "Primer Nombre", "Segundo Nombre", "Primer Apellido", "Segundo Apellido", "Correo", "Teléfono", "Género", "Número de Hijos", "Status"}, 0);
        table = createStyledTable(new JTable(tableModel));
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        add(new JScrollPane(table), BorderLayout.CENTER);
        loadPersonasFromDatabase();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnAdd = createStyledButton("Registrar");
        JButton btnEdit = createStyledButton("Editar");
        JButton btnDelete = createStyledButton("Eliminar");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPersonaForm(null);
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    Persona persona = personas.get(table.convertRowIndexToModel(selectedRow));
                    showPersonaForm(persona);
                } else {
                    JOptionPane.showMessageDialog(HabitantesPanel.this, "Seleccione una persona para editar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    Persona persona = personas.get(table.convertRowIndexToModel(selectedRow));
                    int confirm = JOptionPane.showConfirmDialog(HabitantesPanel.this, "¿Está seguro de que desea eliminar esta persona?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        updatePersonaStatus(persona.getCedula(), "Inactivo");
                    }
                } else {
                    JOptionPane.showMessageDialog(HabitantesPanel.this, "Seleccione una persona para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.BLACK); // Cambia el color del texto a negro
        button.setBackground(new Color(70, 130, 180));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private JTable createStyledTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(Color.BLACK); // Cambia el color del texto a negro
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.BLACK); // Cambia el color del texto de los encabezados a negro
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setForeground(Color.BLACK); // Cambia el color del texto de las celdas a negro
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        return table;
    }

    private void applyFilter() {
        String filterText = searchField.getText().trim();
        if (filterText.length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText));
        }
    }

    private void loadPersonasFromDatabase() {
        personas.clear();
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM personas WHERE status = 'Activo'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idPersona = rs.getInt("id_persona");
                String tipoCedula = rs.getString("tipo_cedula");
                int cedula = rs.getInt("cedula");
                String primerNombre = rs.getString("primer_nombre");
                String segundoNombre = rs.getString("segundo_nombre");
                String primerApellido = rs.getString("primer_apellido");
                String segundoApellido = rs.getString("segundo_apellido");
                String genero = rs.getString("genero");
                String telefono = rs.getString("telefono");
                String correo = rs.getString("correo");
                int numeroHijos = rs.getInt("numero_hijos");
                String fechaRegistro = rs.getString("fecha_registro");
                String status = rs.getString("status");

                Persona persona = new Persona(idPersona, tipoCedula, cedula, primerNombre, segundoNombre, primerApellido, segundoApellido, genero, telefono, correo, numeroHijos, fechaRegistro, status);
                personas.add(persona);

                tableModel.addRow(new Object[]{cedula, primerNombre, segundoNombre, primerApellido, segundoApellido, correo, telefono, genero, numeroHijos, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPersonaForm(Persona persona) {
        Frame parentFrame = null;
        if (SwingUtilities.getWindowAncestor(this) instanceof Frame) {
            parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        }

        JDialog dialog = new JDialog(parentFrame, persona == null ? "Registrar Persona" : "Editar Persona", true);
        dialog.setSize(400, 600);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblTipoCedula = new JLabel("Tipo de Cédula:");
        formPanel.add(lblTipoCedula, gbc);

        JComboBox<String> cbTipoCedula = new JComboBox<>(new String[]{"Venezolano", "Extranjero"});
        gbc.gridx = 1;
        formPanel.add(cbTipoCedula, gbc);

        JLabel lblCedula = new JLabel("Cédula:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblCedula, gbc);

        JTextField tfCedula = new JTextField(15);
        tfCedula.setDocument(new LimitedDocument(8));
        gbc.gridx = 1;
        formPanel.add(tfCedula, gbc);

        JLabel lblPrimerNombre = new JLabel("Primer Nombre:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblPrimerNombre, gbc);

        JTextField tfPrimerNombre = new JTextField(15);
        tfPrimerNombre.setDocument(new LimitedDocument(30));
        gbc.gridx = 1;
        formPanel.add(tfPrimerNombre, gbc);

        JLabel lblSegundoNombre = new JLabel("Segundo Nombre:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblSegundoNombre, gbc);

        JTextField tfSegundoNombre = new JTextField(15);
        tfSegundoNombre.setDocument(new LimitedDocument(30));
        gbc.gridx = 1;
        formPanel.add(tfSegundoNombre, gbc);

        JLabel lblPrimerApellido = new JLabel("Primer Apellido:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblPrimerApellido, gbc);

        JTextField tfPrimerApellido = new JTextField(15);
        tfPrimerApellido.setDocument(new LimitedDocument(30));
        gbc.gridx = 1;
        formPanel.add(tfPrimerApellido, gbc);

        JLabel lblSegundoApellido = new JLabel("Segundo Apellido:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblSegundoApellido, gbc);

        JTextField tfSegundoApellido = new JTextField(15);
        tfSegundoApellido.setDocument(new LimitedDocument(30));
        gbc.gridx = 1;
        formPanel.add(tfSegundoApellido, gbc);

        JLabel lblGenero = new JLabel("Género:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblGenero, gbc);

        JComboBox<String> cbGenero = new JComboBox<>(new String[]{"Hombre", "Mujer"});
        gbc.gridx = 1;
        formPanel.add(cbGenero, gbc);

        JLabel lblTelefono = new JLabel("Teléfono:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblTelefono, gbc);

        JTextField tfTelefono = new JTextField(15);
        tfTelefono.setDocument(new LimitedDocument(15));
        gbc.gridx = 1;
        formPanel.add(tfTelefono, gbc);

        JLabel lblCorreo = new JLabel("Correo:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblCorreo, gbc);

        JTextField tfCorreo = new JTextField(15);
        tfCorreo.setDocument(new LimitedDocument(50));
        gbc.gridx = 1;
        formPanel.add(tfCorreo, gbc);

        JLabel lblNumeroHijos = new JLabel("Número de Hijos:");
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(lblNumeroHijos, gbc);

        JTextField tfNumeroHijos = new JTextField(15);
        tfNumeroHijos.setDocument(new LimitedDocument(2));
        gbc.gridx = 1;
        formPanel.add(tfNumeroHijos, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JButton btnSave = createStyledButton("Guardar");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        if (persona != null) {
            cbTipoCedula.setSelectedItem(persona.getTipoCedula());
            tfCedula.setText(String.valueOf(persona.getCedula()));
            tfPrimerNombre.setText(persona.getPrimerNombre());
            tfSegundoNombre.setText(persona.getSegundoNombre());
            tfPrimerApellido.setText(persona.getPrimerApellido());
            tfSegundoApellido.setText(persona.getSegundoApellido());
            cbGenero.setSelectedItem(persona.getGenero());
            tfTelefono.setText(persona.getTelefono());
            tfCorreo.setText(persona.getCorreo());
            tfNumeroHijos.setText(String.valueOf(persona.getNumeroHijos()));
        }

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfCedula.getText().trim().isEmpty() || tfPrimerNombre.getText().trim().isEmpty() || tfPrimerApellido.getText().trim().isEmpty() || tfCorreo.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Por favor, complete los campos obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String tipoCedula = (String) cbTipoCedula.getSelectedItem();
                int cedula = Integer.parseInt(tfCedula.getText().trim());
                String primerNombre = tfPrimerNombre.getText().trim();
                String segundoNombre = tfSegundoNombre.getText().trim();
                String primerApellido = tfPrimerApellido.getText().trim();
                String segundoApellido = tfSegundoApellido.getText().trim();
                String genero = (String) cbGenero.getSelectedItem();
                String telefono = tfTelefono.getText().trim();
                String correo = tfCorreo.getText().trim();
                int numeroHijos = Integer.parseInt(tfNumeroHijos.getText().trim());

                if (persona == null) {
                    addPersonaToDatabase(tipoCedula, cedula, primerNombre, segundoNombre, primerApellido, segundoApellido, genero, telefono, correo, numeroHijos);
                } else {
                    updatePersonaInDatabase(persona.getIdPersona(), tipoCedula, cedula, primerNombre, segundoNombre, primerApellido, segundoApellido, genero, telefono, correo, numeroHijos);
                }

                dialog.dispose();
                loadPersonasFromDatabase();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addPersonaToDatabase(String tipoCedula, int cedula, String primerNombre, String segundoNombre, String primerApellido, String segundoApellido, String genero, String telefono, String correo, int numeroHijos) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO personas (tipo_cedula, cedula, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, genero, telefono, correo, numero_hijos, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, tipoCedula);
            stmt.setInt(2, cedula);
            stmt.setString(3, primerNombre);
            stmt.setString(4, segundoNombre);
            stmt.setString(5, primerApellido);
            stmt.setString(6, segundoApellido);
            stmt.setString(7, genero);
            stmt.setString(8, telefono);
            stmt.setString(9, correo);
            stmt.setInt(10, numeroHijos);
            stmt.setString(11, "Activo");
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la persona en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePersonaInDatabase(int idPersona, String tipoCedula, int cedula, String primerNombre, String segundoNombre, String primerApellido, String segundoApellido, String genero, String telefono, String correo, int numeroHijos) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE personas SET tipo_cedula = ?, cedula = ?, primer_nombre = ?, segundo_nombre = ?, primer_apellido = ?, segundo_apellido = ?, genero = ?, telefono = ?, correo = ?, numero_hijos = ? WHERE id_persona = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, tipoCedula);
            stmt.setInt(2, cedula);
            stmt.setString(3, primerNombre);
            stmt.setString(4, segundoNombre);
            stmt.setString(5, primerApellido);
            stmt.setString(6, segundoApellido);
            stmt.setString(7, genero);
            stmt.setString(8, telefono);
            stmt.setString(9, correo);
            stmt.setInt(10, numeroHijos);
            stmt.setInt(11, idPersona);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar la persona en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePersonaStatus(int cedula, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE personas SET status = ? WHERE cedula = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, cedula);
            stmt.executeUpdate();
            loadPersonasFromDatabase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado de la persona en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
