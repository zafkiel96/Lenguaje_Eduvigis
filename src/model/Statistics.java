package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Statistics {

    public static int getNumberOfInhabitants() {
        String query = "SELECT COUNT(*) FROM personas WHERE status='Activo'";
        return executeQuery(query);
    }

    public static int getNumberOfHouses() {
        String query = "SELECT COUNT(*) FROM casas WHERE status='Activo'";
        return executeQuery(query);
    }

    public static int getNumberOfFamilies() {
        String query = "SELECT COUNT(*) FROM grupos_familiares WHERE status='Activo'";
        return executeQuery(query);
    }

    public static int getNumberOfBenefits() {
        String query = "SELECT COUNT(*) FROM beneficios WHERE status='Activo'";
        return executeQuery(query);
    }

    public static List<Personas> getRecentInhabitants() {
        List<Personas> recentInhabitants = new ArrayList<>();
        String query = "SELECT primer_nombre, primer_apellido, cedula, status " +
                       "FROM personas ORDER BY fecha_registro DESC LIMIT 10";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String primerNombre = resultSet.getString("primer_nombre");
                String primerApellido = resultSet.getString("primer_apellido");
                int cedula = resultSet.getInt("cedula");
                String status = resultSet.getString("status");

                Personas inhabitant = new Personas(primerNombre, primerApellido, cedula, status);
                recentInhabitants.add(inhabitant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recentInhabitants;
    }

    public static List<Benefit> getRecentBenefits() {
        List<Benefit> recentBenefits = new ArrayList<>();
        String query = "SELECT nombre_beneficio, fecha_entregado, status " +
                       "FROM beneficios ORDER BY fecha_entregado DESC LIMIT 5";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String nombreBeneficio = resultSet.getString("nombre_beneficio");
                String fechaEntregado = resultSet.getString("fecha_entregado");
                String status = resultSet.getString("status");

                Benefit benefit = new Benefit(nombreBeneficio, fechaEntregado, status);
                recentBenefits.add(benefit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recentBenefits;
    }

    private static int executeQuery(String query) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
