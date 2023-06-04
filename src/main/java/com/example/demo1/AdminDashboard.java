package com.example.demo1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
//库管
public class AdminDashboard extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;

    public AdminDashboard() {
        super("Admin Dashboard");

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Stock");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        addButton = new JButton("Add Product");
        deleteButton = new JButton("Delete Product");
        updateButton = new JButton("Update Price");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(AdminDashboard.this, "Enter product name:");
                if (name != null && !name.isEmpty()) {
                    double price = Double.parseDouble(JOptionPane.showInputDialog(AdminDashboard.this, "Enter product price:"));
                    int stock = Integer.parseInt(JOptionPane.showInputDialog(AdminDashboard.this, "Enter product stock:"));
                    int id = Integer.parseInt(JOptionPane.showInputDialog(AdminDashboard.this, "Enter product id:"));
                    insertProduct(id,name, price, stock);
                    loadProducts();
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Product added successfully");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int id = (int) table.getValueAt(selectedRow, 0);
                    deleteProduct(id);
                    loadProducts();
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Product deleted successfully");
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Please select a product to delete");
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int id = (int) table.getValueAt(selectedRow, 0);
                    double newPrice = Double.parseDouble(JOptionPane.showInputDialog(AdminDashboard.this, "Enter new price:"));
                    updateProductPrice(id, newPrice);
                    loadProducts();
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Product price updated successfully");
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Please select a product to update");
                }
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        tableModel.setRowCount(0);

        try {
            String url = "jdbc:mysql://localhost:3306/store";
            String username = "yxn";
            String password = "yue002266";
            Connection connection = DriverManager.getConnection(url, username, password);

            String query = "SELECT * FROM products";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");
                Vector<Object> row = new Vector<>();
                row.add(id);
                row.add(name);
                row.add(price);
                row.add(stock);

                tableModel.addRow(row);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理数据库连接或查询错误
        }
    }

    private void insertProduct(int id, String name, double price, int stock) {
        try {
            String url = "jdbc:mysql://localhost:3306/store";
            String username = "yxn";
            String password = "yue002266";
            Connection connection = DriverManager.getConnection(url, username, password);

            String query = "INSERT INTO products (id, name, price, stock) VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, stock);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理数据库连接或查询错误
        }
    }



    private void deleteProduct(int id) {
        try {
            String url = "jdbc:mysql://localhost:3306/store";
            String username = "yxn";
            String password = "yue002266";
            Connection connection = DriverManager.getConnection(url, username, password);

            String query = "DELETE FROM products WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理数据库连接或查询错误
        }
    }

    private void updateProductPrice(int id, double newPrice) {
        try {
            String url = "jdbc:mysql://localhost:3306/store";
            String username = "yxn";
            String password = "yue002266";
            Connection connection = DriverManager.getConnection(url, username, password);

            String query = "UPDATE products SET price = ? WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, newPrice);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理数据库连接或查询错误
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AdminDashboard adminDashboard = new AdminDashboard();
                adminDashboard.setVisible(true);
            }
        });
    }
}
