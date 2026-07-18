package com.example.demo;

import com.example.demo.service.UserService;
import com.example.demo.ui.DashboardPanel;
import com.example.demo.ui.LoginPanel;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(DemoApplication.class)
                .headless(false)
                .run(args);

        UserService userService = context.getBean(UserService.class);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Warehouse Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            LoginPanel loginPanel = new LoginPanel(userService, (loggedInUser) -> {
                DashboardPanel dashboardPanel = new DashboardPanel(loggedInUser, context, () -> {
                    cardLayout.show(mainPanel, "Login");
                });
                
                mainPanel.add(dashboardPanel, "Dashboard");
                cardLayout.show(mainPanel, "Dashboard");
            });

            mainPanel.add(loginPanel, "Login");

            frame.add(mainPanel);
            frame.setVisible(true);
            
            cardLayout.show(mainPanel, "Login");
        });
    }
}