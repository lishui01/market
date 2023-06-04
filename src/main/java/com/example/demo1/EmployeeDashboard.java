package com.example.demo1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//结账
public class EmployeeDashboard extends JFrame {
    private PointOfSale pointOfSale; // 收银管理类
    private DefaultListModel<Product> productListModel;
    private JList<Product> productList;
    private JButton addToCartButton;
    private JButton clearCartButton; // 清空购物车按钮
    private List<Product> availableProducts;
    private DefaultListModel<Product> cartListModel;
    private JList<Product> cartList;

    public EmployeeDashboard() {
        super("Employee Dashboard");

        pointOfSale = new PointOfSale();

        // 创建普通员工仪表盘的组件和布局
        JLabel welcomeLabel = new JLabel("Welcome, Employee!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(welcomeLabel, BorderLayout.NORTH);

        productListModel = new DefaultListModel<>();
        productList = new JList<>(productListModel);
        JScrollPane scrollPane = new JScrollPane(productList);
        panel.add(scrollPane, BorderLayout.CENTER);

        addToCartButton = new JButton("Add to Cart");
        panel.add(addToCartButton, BorderLayout.SOUTH);

        // 添加购物车部分的组件和布局
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BorderLayout());

        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cartLabel.setHorizontalAlignment(SwingConstants.CENTER);

        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        JScrollPane cartScrollPane = new JScrollPane(cartList);
        cartPanel.add(cartLabel, BorderLayout.NORTH);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        clearCartButton = new JButton("Clear Cart");
        cartPanel.add(clearCartButton, BorderLayout.SOUTH);

        // 使用网格布局将两个部分的面板放在一个面板中
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(panel);
        mainPanel.add(cartPanel);

        // 添加面板到窗口
        add(mainPanel);

        // 设置窗口的大小、位置和关闭操作
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = productList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Product selectedProduct = availableProducts.get(selectedIndex);
                    double price = selectedProduct.getPrice();
                    pointOfSale.addToCart(selectedProduct);
                    updateCartList();
                    double totalPrice = pointOfSale.calculateTotalPrice(); // 计算总价
                    JOptionPane.showMessageDialog(EmployeeDashboard.this, "Product added to cart\nTotal Price: $" + totalPrice);
                }
            }
        });

        clearCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pointOfSale.clearCart();
                updateCartList();
                double totalPrice = pointOfSale.calculateTotalPrice(); // 计算总价
                JOptionPane.showMessageDialog(EmployeeDashboard.this, "Cart cleared\nTotal Price: $" + totalPrice);
            }
        });

        // 从数据库读取商品数据并显示在界
        // 从数据库读取商品数据并显示在界面上
        fetchAvailableProducts();
        displayAvailableProducts();
        updateCartList();
    }

    private void fetchAvailableProducts() {
        try {
            String url = "jdbc:mysql://localhost:3306/store";
            String username = "yxn";
            String password = "yue002266";
            Connection connection = DriverManager.getConnection(url, username, password);

            String query = "SELECT * FROM products";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            availableProducts = new ArrayList<>();

            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                Product product = new Product(productName, price);
                availableProducts.add(product);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理数据库连接或查询错误
        }
    }

    private void displayAvailableProducts() {
        for (Product product : availableProducts) {
            productListModel.addElement(product);
        }
    }

    private void updateCartList() {
        cartListModel.clear();
        Map<String, Integer> itemCounts = new HashMap<>();

        for (Product product : pointOfSale.getCartItems()) {
            String productName = product.getName();
            if (itemCounts.containsKey(productName)) {
                int count = itemCounts.get(productName);
                itemCounts.put(productName, count + 1);
            } else {
                itemCounts.put(productName, 1);
            }
        }

        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            String productName = entry.getKey();
            int itemCount = entry.getValue();
            Product cartProduct = new Product(productName, 0);
            cartProduct.setCount(itemCount);
            cartListModel.addElement(cartProduct);
        }
    }

    public static void main(String[] args) {
        // 设置数据库驱动程序
        String driver = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EmployeeDashboard employeeDashboard = new EmployeeDashboard();
                employeeDashboard.setVisible(true);
            }
        });
    }
}

class Product {
    private String name;
    private double price;
    private int count;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
        this.count = 0;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        if (count >= 1) {
            return name + " x" + count + "  ";
        } else {
            return name + "  " + price + "￥";
        }
    }
}

class PointOfSale {
    private List<Product> cartItems;

    public PointOfSale() {
        cartItems = new ArrayList<>();
    }

    public void addToCart(Product product) {
        cartItems.add(product);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (Product item : cartItems) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    public List<Product> getCartItems() {
        return cartItems;
    }
}
