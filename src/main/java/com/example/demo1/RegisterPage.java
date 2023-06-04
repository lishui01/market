package com.example.demo1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//注册
public class RegisterPage extends JFrame {
    private JLabel idLabel,usernameLabel, passwordLabel, roleLabel;
    private JTextField idField,usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JPanel panel;
    private Connection connection;

    public RegisterPage(Connection connection) {
        super("Register Page");
        this.connection = connection;

        // Create components
        idLabel = new JLabel("id:");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        roleLabel = new JLabel("Role:");
        idField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(new String[]{"admin", "employee"});
        registerButton = new JButton("Register");

        // Create panel and set layout
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Add components to the panel with specified constraints
        panel.add(idLabel, constraints);
        constraints.gridy = 1;
        panel.add(usernameLabel, constraints);
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);
        constraints.gridy = 3;
        panel.add(roleLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(idField, constraints);
        constraints.gridy = 1;
        panel.add(usernameField, constraints);
        constraints.gridy = 2;
        panel.add(passwordField, constraints);
        constraints.gridy = 3;
        panel.add(roleComboBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(registerButton, constraints);

        // Set the event listener for the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idField.getText());
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String role = (String) roleComboBox.getSelectedItem();

                // Register new user
                if (registerUser(id, username, password, role)) {
                    JOptionPane.showMessageDialog(RegisterPage.this, "Registration successful");
                    dispose(); // Close the registration window
                } else {
                    JOptionPane.showMessageDialog(RegisterPage.this, "Registration failed");
                }
            }
        });

        // Set the layout of the JFrame and add the panel
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        // Set the size, position, and close operation of the window
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private boolean registerUser(int id, String username, String password, String role) {
        try {
            String query = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, role);
            int rowsInserted = preparedStatement.executeUpdate();
            preparedStatement.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database query errors
        }
        return false;
    }
    public static void main(String[] args) {
        // Connect to the database
        Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost:3306/store";
            String dbUsername = "yxn";
            String dbPassword = "yue002266";
            connection = DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database connection errors
        }

        RegisterPage registerPage = new RegisterPage(connection);
        registerPage.setVisible(true);
    }
}


