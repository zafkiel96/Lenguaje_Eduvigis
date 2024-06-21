package ui;

import model.User;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangePasswordFrame extends JFrame {
    private JTextField usernameField;
    private JTextField cedulaField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton confirmButton;
    private JButton backButton;

    public ChangePasswordFrame() {
        setTitle("Cambiar Contraseña");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel usernameLabel = new JLabel("Usuario:");
        usernameLabel.setBounds(20, 20, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 160, 25);
        add(usernameField);

        JLabel cedulaLabel = new JLabel("Cédula:");
        cedulaLabel.setBounds(20, 60, 80, 25);
        add(cedulaLabel);

        cedulaField = new JTextField(20);
        cedulaField.setBounds(100, 60, 160, 25);
        add(cedulaField);

        ((AbstractDocument) cedulaField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) {
                    return;
                }
                if (isNumeric(string) && (fb.getDocument().getLength() + string.length() <= 9)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) {
                    return;
                }
                if (isNumeric(string) && (fb.getDocument().getLength() + string.length() - length <= 9)) {  // Corrige el límite aquí
                    super.replace(fb, offset, length, string, attr);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
            }

            private boolean isNumeric(String str) {
                return str.chars().allMatch(Character::isDigit);
            }
        });

        JLabel newPasswordLabel = new JLabel("Nueva Contraseña:");
        newPasswordLabel.setBounds(20, 100, 120, 25);
        add(newPasswordLabel);

        newPasswordField = new JPasswordField(20);
        newPasswordField.setBounds(140, 100, 120, 25);
        add(newPasswordField);

        JLabel confirmPasswordLabel = new JLabel("Confirmar Contraseña:");
        confirmPasswordLabel.setBounds(20, 140, 140, 25);
        add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBounds(160, 140, 100, 25);
        add(confirmPasswordField);

        confirmButton = new JButton("Confirmar");
        confirmButton.setBounds(20, 180, 100, 25);
        add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String cedula = cedulaField.getText().trim();
                String newPassword = new String(newPasswordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

                if (username.isEmpty() || cedula.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Por favor, complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User user = User.getUserByUsername(username);
                if (user == null) {
                    JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Usuario incorrecto", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!user.getCedula().equals(cedula)) {
                    JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Cédula incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (user.changePassword(newPassword)) {
                    JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Contraseña actualizada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    LoginFrame login = new LoginFrame();
                    login.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Error al actualizar la contraseña", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton = new JButton("Volver");
        backButton.setBounds(140, 180, 100, 25);
        add(backButton);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChangePasswordFrame().setVisible(true);
            }
        });
    }
}
