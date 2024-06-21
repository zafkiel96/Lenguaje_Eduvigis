package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int idUsuario;
    private String username;
    private String password;
    private String cedula;
    private String email;
    private String userType;
    private String status;

    public User(int idUsuario, String username, String password, String cedula, String email, String userType, String status) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.cedula = cedula;
        this.email = email;
        this.userType = userType;
        this.status = status;
    }

    public User(ResultSet rs) throws SQLException {
        this.idUsuario = rs.getInt("id_usuario");
        this.username = rs.getString("usuario");
        this.password = rs.getString("contraseña");
        this.cedula = rs.getString("cedula");
        this.email = rs.getString("correo");
        this.userType = rs.getString("tipo_usuario");
        this.status = rs.getString("status");
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static User getUserByUsername(String username) {
        User user = null;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM usuarios WHERE usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean validateCredentials(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public boolean changePassword(String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE usuarios SET contraseña = ? WHERE usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newPassword);
            stmt.setString(2, this.username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
