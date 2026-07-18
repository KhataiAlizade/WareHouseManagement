package com.example.demo.ui;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

public class LoginPanel extends JPanel {

    private final UserService userService;
    private final Consumer<User> onSuccess;

    public LoginPanel(UserService userService, Consumer<User> onSuccess) {
        this.userService = userService;
        this.onSuccess = onSuccess;
        setLayout(new GridBagLayout());
        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("System Login"));

        JLabel roleLabel = new JLabel("Login Portal:");
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");

        formPanel.add(roleLabel);
        formPanel.add(roleCombo);
        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        formPanel.add(new JLabel(""));
        formPanel.add(loginButton);

        add(formPanel);

        Runnable performLogin = () -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            UserRole selectedRole = (UserRole) roleCombo.getSelectedItem();

            Optional<User> user = userService.login(username, password);

            if (user.isPresent()) {
                if (user.get().getRole() == selectedRole) {
                    onSuccess.accept(user.get());
                    userField.setText("");
                    passField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Access Denied. You are not a " + selectedRole.name() + ".", "Role Mismatch", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        loginButton.addActionListener(e -> performLogin.run());

        userField.addActionListener(e -> performLogin.run());
        passField.addActionListener(e -> performLogin.run());
    }
}