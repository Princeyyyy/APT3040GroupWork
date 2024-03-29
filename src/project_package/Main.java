/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project_package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        createDatabase();

        new loginPage().setVisible(true);
    }

    public static void createDatabase() {
        try {
            // Establish connection to MySQL database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

            if (con != null) {
                System.out.println("Connected to the database");

                // Create the database if it doesn't exist
                try (Statement stmt = con.createStatement()) {
                    String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS employees_time_off_registry";
                    stmt.executeUpdate(createDatabaseSQL);
                    System.out.println("Database created successfully");
                }

                // Connect to the created database
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employees_time_off_registry", "root", "");

                if (con != null) {
                    // Create tables in the database
                    try (Statement stmt = con.createStatement()) {
                        String createEmployeeDetailsTableSQL = "CREATE TABLE IF NOT EXISTS employee_details (\n"
                                + "    id INT AUTO_INCREMENT PRIMARY KEY,\n"
                                + "    employee_username VARCHAR(255) NOT NULL,\n"
                                + "    employee_password VARCHAR(255) NOT NULL\n"
                                + ");";

                        String createEmployeeOffTimeTableSQL = "CREATE TABLE IF NOT EXISTS employee_off_time (\n"
                                + "    id INT AUTO_INCREMENT PRIMARY KEY,\n"
                                + "    employee_id INT,\n"
                                + "    start_time VARCHAR(255) NOT NULL,\n"
                                + "    end_time VARCHAR(255) NOT NULL,\n"
                                + "    FOREIGN KEY (employee_id) REFERENCES employee_details(id)\n"
                                + ");";

                        // Execute the create table statements
                        stmt.executeUpdate(createEmployeeDetailsTableSQL);
                        stmt.executeUpdate(createEmployeeOffTimeTableSQL);

                        System.out.println("Tables created successfully");
                    }
                } else {
                    System.out.println("Failed to connect to the database");
                }

                // Close the connection
                con.close();
            } else {
                System.out.println("Failed to connect to MySQL server");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

}
