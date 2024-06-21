package ui;

import java.util.ArrayList;
import model.Benefit;
import model.Personas;
import model.Statistics;
import model.User;
import model.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private User user;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public DashboardFrame(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Añade paneles aquí
        mainContentPanel.add(createMainDashboardPanel(), "Inicio");
        mainContentPanel.add(new JefeManzanaPanel(), "JefeDeManzana");
        mainContentPanel.add(new ManzanaPanel(), "Manzana");
        mainContentPanel.add(new CasasPanel(), "Casas");
        mainContentPanel.add(new BeneficiosPanel(), "Beneficios");
        mainContentPanel.add(new HabitantesPanel(), "Habitantes");
        mainContentPanel.add(new GruposFamiliaresPanel(), "GruposFamiliares");

        add(createTopPanel(), BorderLayout.NORTH);
        add(createSidePanel(), BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        customizeAppearance();
    }

    private void customizeAppearance() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private JPanel createTopPanel() {
        JLabel welcomeLabel = new JLabel("Bienvenido, " + user.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.BLACK);

        JButton logoutButton = new JButton("Cerrar sesión");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBackground(new Color(70, 130, 180));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.addActionListener(e -> handleLogout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        return topPanel;
    }

    private void handleLogout() {
        Object[] options = {"Sí", "No"};
        int response = JOptionPane.showOptionDialog(
                DashboardFrame.this,
                "¿Está seguro que desea cerrar sesión?",
                "Confirmar cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (response == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    DashboardFrame.this,
                    "Sesión cerrada exitosamente",
                    "Cierre de sesión",
                    JOptionPane.INFORMATION_MESSAGE
            );

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel(new GridLayout(8, 1, 0, 10));

        sidePanel.add(createSideButton("Inicio", this::showInicio));
        sidePanel.add(createSideButton("Jefe de Manzana", this::showJefeManzana));
        sidePanel.add(createSideButton("Manzana", this::showManzana));
        sidePanel.add(createSideButton("Casas", this::showCasas));
        sidePanel.add(createSideButton("Beneficios", this::showBeneficios));
        sidePanel.add(createSideButton("Habitantes", this::showHabitantes));
        sidePanel.add(createSideButton("Grupos Familiares", this::showGruposFamiliares));

        sidePanel.setBackground(new Color(230, 230, 230));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return sidePanel;
    }

    private JButton createSideButton(String buttonText, Runnable action) {
        JButton button = new JButton(buttonText);
        button.addActionListener(e -> action.run());
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private JPanel createMainDashboardPanel() {
        JPanel mainDashboardPanel = new JPanel();
        mainDashboardPanel.setLayout(new BoxLayout(mainDashboardPanel, BoxLayout.Y_AXIS));

        mainDashboardPanel.add(createStatisticsPanel());
        mainDashboardPanel.add(createRecentInhabitantsPanel());
        mainDashboardPanel.add(createRecentBenefitsPanel());

        mainDashboardPanel.setBackground(Color.WHITE);
        mainDashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        return mainDashboardPanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));

        statsPanel.add(createStatPanel("Habitantes", Statistics.getNumberOfInhabitants()));
        statsPanel.add(createStatPanel("Casas", Statistics.getNumberOfHouses()));
        statsPanel.add(createStatPanel("Familias", Statistics.getNumberOfFamilies()));
        statsPanel.add(createStatPanel("Beneficios", Statistics.getNumberOfBenefits()));

        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return statsPanel;
    }

    private JPanel createStatPanel(String title, int value) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title + ": " + value, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        panel.add(label, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel createRecentInhabitantsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.9;

        JLabel label = new JLabel("Últimos 10 habitantes registrados", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.BLACK);
        panel.add(label, gbc);

        List<Personas> recentInhabitants = Statistics.getRecentInhabitants();

        String[] columnNames = {"Nombre", "Apellido", "Cédula", "Estado"};
        List<Object[]> data = new ArrayList<>();

        for (Personas inhabitant : recentInhabitants) {
            if ("activo".equals(inhabitant.getStatus())) {
                Object[] rowData = {inhabitant.getPrimerNombre(), inhabitant.getPrimerApellido(), inhabitant.getCedula(), inhabitant.getStatus()};
                data.add(rowData);
            }
        }

        Object[][] dataArray = new Object[data.size()][4];
        dataArray = data.toArray(dataArray);

        JTable table = createStyledTable(new JTable(dataArray, columnNames));

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createRecentBenefitsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Últimos 5 beneficios registrados", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.BLACK);

        panel.add(label, BorderLayout.NORTH);

        String[] columnNames = {"Nombre", "Fecha", "Estado"};
        List<Benefit> recentBenefits = Statistics.getRecentBenefits();
        List<Object[]> data = new ArrayList<>();

        for (Benefit benefit : recentBenefits) {
            if ("activo".equals(benefit.getStatus())) {
                Object[] rowData = {benefit.getName(), benefit.getDate(), benefit.getStatus()};
                data.add(rowData);
            }
        }

        Object[][] dataArray = new Object[data.size()][3];
        dataArray = data.toArray(dataArray);

        JTable table = createStyledTable(new JTable(dataArray, columnNames));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createStyledTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(Color.BLACK);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        return table;
    }

    private void showInicio() {
        cardLayout.show(mainContentPanel, "Inicio");
    }

    private void showJefeManzana() {
        cardLayout.show(mainContentPanel, "JefeDeManzana");
    }

    private void showManzana() {
        cardLayout.show(mainContentPanel, "Manzana");
    }

    private void showCasas() {
        cardLayout.show(mainContentPanel, "Casas");
    }

    private void showBeneficios() {
        cardLayout.show(mainContentPanel, "Beneficios");
    }

    private void showHabitantes() {
        cardLayout.show(mainContentPanel, "Habitantes");
    }

    private void showGruposFamiliares() {
        cardLayout.show(mainContentPanel, "GruposFamiliares");
    }
}
