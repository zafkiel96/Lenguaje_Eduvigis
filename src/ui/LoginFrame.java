package ui;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton changePasswordButton;

    public LoginFrame() {
        setTitle("Login");
        setSize(400, 270);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createMainPanel(), BorderLayout.CENTER);

        customizeAppearance();
    }

    private void customizeAppearance() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Inicio de Sesión");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        usernamePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        usernamePanel.setBackground(Color.WHITE);
        JLabel usernameLabel = new JLabel("Usuario:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernamePanel.add(usernameLabel);
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, usernameField.getPreferredSize().height));
        usernamePanel.add(usernameField);
        mainPanel.add(usernamePanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        passwordPanel.setBackground(Color.WHITE);
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordPanel.add(passwordLabel);
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordField.getPreferredSize().height));
        passwordPanel.add(passwordField);
        mainPanel.add(passwordPanel);

        loginButton = new JButton("Ingresar");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, loginButton.getPreferredSize().height));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Por favor, complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User user = User.getUserByUsername(username);
                if (user == null) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Usuario no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!user.validateCredentials(username, password)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Contraseña incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Ingreso exitoso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    DashboardFrame dashboardFrame = new DashboardFrame(user);
                    dashboardFrame.setVisible(true);
                    dispose();
                }
            }
        });
        mainPanel.add(loginButton);

        changePasswordButton = new JButton("Cambiar Contraseña");
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 14));
        changePasswordButton.setBackground(new Color(220, 53, 69));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePasswordButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, changePasswordButton.getPreferredSize().height));
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangePasswordFrame changePasswordFrame = new ChangePasswordFrame();
                changePasswordFrame.setVisible(true);
            }
        });
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(changePasswordButton);

        return mainPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
