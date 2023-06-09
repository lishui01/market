package com.example.demo1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeInfoWindow extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton;
    private JButton updateButton;
    private JLabel roleLabel;
    private JComboBox<String> roleComboBox;
    private Connection connection;

    public EmployeeInfoWindow(Connection connection) {
        super("员工信息");
        this.connection = connection;

        // 创建组件
        employeeTable = new JTable();
        deleteButton = new JButton("删除");
        updateButton = new JButton("更新");
        roleLabel = new JLabel("角色:");
        roleComboBox = new JComboBox<>(new String[]{"employee", "admin"});

        // 创建表格模型并设置到表格中
        tableModel = new DefaultTableModel();
        employeeTable.setModel(tableModel);

        // 创建面板并设置布局
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // 创建按钮面板并添加按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(roleLabel);
        buttonPanel.add(roleComboBox);

        // 将按钮面板添加到主面板中
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 从数据库加载员工数据
        loadEmployeeData();

        // 设置窗口的内容面板
        setContentPane(panel);

        // 设置窗口的大小、位置和关闭操作
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 为按钮设置动作监听器
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeRole();
            }
        });
    }

    private void loadEmployeeData() {
        try {
            String query = "SELECT * FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // 清空现有数据
            tableModel.setColumnCount(0);
            tableModel.setRowCount(0);

            // 获取列名的元数据
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(resultSet.getMetaData().getColumnName(i));
            }

            // 添加数据行
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载员工数据时出错");
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            int employeeId = (int) tableModel.getValueAt(selectedRow, 0);

            try {
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, employeeId);

                int rowsDeleted = preparedStatement.executeUpdate();
                preparedStatement.close();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "员工删除成功");
                    loadEmployeeData(); // 删除后重新加载员工数据
                } else {
                    JOptionPane.showMessageDialog(this, "删除员工失败");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "删除员工时出错");
            }
        } else {
            JOptionPane.showMessageDialog(this, "未选中员工");
        }
    }

    private void updateEmployeeRole() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
            String newRole = (String) roleComboBox.getSelectedItem();

            try {
                String query = "UPDATE users SET role = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newRole);
                preparedStatement.setInt(2, employeeId);

                int rowsUpdated = preparedStatement.executeUpdate();
                preparedStatement.close();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "员工角色更新成功");
                    loadEmployeeData(); // 更新后重新加载员工数据
                } else {
                    JOptionPane.showMessageDialog(this, "更新员工角色失败");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "更新员工角色时出错");
            }
        } else {
            JOptionPane.showMessageDialog(this, "未选中员工");
        }
    }

    public static void main(String[] args) {
        // 连接数据库
        Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost:3306/store";
            String dbUsername = "yxn";
            String dbPassword = "yue002266";
            connection = DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理数据库连接错误
        }

        Connection finalConnection = connection;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EmployeeInfoWindow employeeInfoWindow = new EmployeeInfoWindow(finalConnection);
                employeeInfoWindow.setVisible(true);
            }
        });
    }
}
