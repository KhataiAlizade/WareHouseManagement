package com.example.demo.ui;

import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.service.ProductService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final User currentUser;
    private final Runnable onLogout;
    private final ApplicationContext context;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final ProductService productService;

    public DashboardPanel(User currentUser, ApplicationContext context, Runnable onLogout) {
        this.currentUser = currentUser;
        this.context = context;
        this.onLogout = onLogout;
        this.productService = context.getBean(ProductService.class);
        setLayout(new BorderLayout());
        initUI();
        checkLowStockAlerts();
    }

    private void initUI() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navBar.setBackground(new Color(139, 0, 0));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        addMenuButton(navBar, "Products", "ProductsPanel");
        addMenuButton(navBar, "New Transaction", "TransactionPanel");

        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.WAREHOUSE_MANAGER) {
            addMenuButton(navBar, "Suppliers", "SuppliersPanel");
            addMenuButton(navBar, "Dealers", "DealersPanel");
            addMenuButton(navBar, "View", "ViewPanel");
            addMenuButton(navBar, "Reports", "ReportsPanel");
        }

        if (currentUser.getRole() == UserRole.ADMIN) {
            addMenuButton(navBar, "Add Sales Manager", "AddManagerPanel");
            addMenuButton(navBar, "Add Warehouse Manager", "AddWarehousePanel");
        }

        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.addActionListener(e -> onLogout.run());
        navBar.add(logoutBtn);

        contentPanel.add(new ProductsPanel(productService), "ProductsPanel");
        contentPanel.add(new TransactionPanel(context, currentUser), "TransactionPanel");
        contentPanel.add(new SuppliersPanel(context), "SuppliersPanel");
        contentPanel.add(new DealersPanel(context), "DealersPanel");
        contentPanel.add(new ViewPanel(context), "ViewPanel");
        contentPanel.add(new ReportsPanel(context), "ReportsPanel");
        
        if (currentUser.getRole() == UserRole.ADMIN) {
            contentPanel.add(new AddUserPanel(context, UserRole.SALES_MANAGER), "AddManagerPanel");
            contentPanel.add(new AddUserPanel(context, UserRole.WAREHOUSE_MANAGER), "AddWarehousePanel");
        }
        
        contentPanel.add(new JLabel("Welcome, " + currentUser.getFullName() + "!", SwingConstants.CENTER), "WelcomePanel");

        add(navBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "WelcomePanel");
    }

    private void addMenuButton(JPanel navBar, String title, String cardName) {
        JButton btn = createStyledButton(title);
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        navBar.add(btn);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton("  " + text + "  ");
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 0, 0));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(178, 34, 34));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(139, 0, 0));
            }
        });
        return button;
    }

    private void checkLowStockAlerts() {
        List<Product> lowStockProducts = productService.getLowStockProducts();
        
        if (!lowStockProducts.isEmpty()) {
            StringBuilder alertMessage = new StringBuilder("WARNING: The following products are low on stock:\n\n");
            
            for (Product p : lowStockProducts) {
                alertMessage.append("• ").append(p.getName())
                            .append(" (Current Stock: ").append(p.getQuantityInStock())
                            .append(", Minimum: ").append(p.getMinStockLevel()).append(")\n");
            }
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, alertMessage.toString(), "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
            });
        }
    }
}