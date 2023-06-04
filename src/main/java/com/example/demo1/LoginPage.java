package com.example.demo1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
//登录
public class LoginPage extends JFrame {
    private JLabel usernameLabel, passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JPanel panel;
    private Connection connection;

    public LoginPage() {
        super("Login Page");

        // Create components
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        panel = new JPanel(new GridLayout(4, 2));

        // Add components to the panel
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        // Set the event listener for the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate login
                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Login successful");
                    // Add logic for successful login here
                    // For example, open the dashboard based on the user's role
                    String role = getUserRole(username);
                    if (role.equals("admin")) {
                        AdminDashboard adminDashboard = new AdminDashboard();
                        adminDashboard.setVisible(true);
                    } else if (role.equals("employee")) {
                        EmployeeDashboard employeeDashboard = new EmployeeDashboard();
                        employeeDashboard.setVisible(true);
                    }
                    dispose(); // Close the login window
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Invalid username or password");
                }
            }
        });

        // Set the event listener for the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open registration page
                RegisterPage registerPage = new RegisterPage(connection);
                registerPage.setVisible(true);
            }
        });

        // Add the panel to the window
        add(panel, BorderLayout.CENTER);

        // Set the size, position, and close operation of the window
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Connect to the database
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/store";
            String dbUsername = "yxn";
            String dbPassword = "yue002266";
            connection = DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database connection errors
        }
    }

    private boolean validateLogin(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isValidLogin = resultSet.next();
            resultSet.close();
            preparedStatement.close();
            return isValidLogin;
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database query errors
        }
        return false;
    }

    private String getUserRole(String username) {
        try {
            String query = "SELECT role FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("role");
                resultSet.close();
                preparedStatement.close();
                return role;
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database query errors
        }
        return null;
    }

    public static void main(String[] args) {
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true);
    }
}



