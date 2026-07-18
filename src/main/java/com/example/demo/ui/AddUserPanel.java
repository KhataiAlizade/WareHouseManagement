package com.example.demo.ui;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.service.UserService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AddUserPanel extends JPanel {

    private final UserService userService;
    private final UserRole roleToManage;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField userField, passField, nameField, emailField;

    public AddUserPanel(ApplicationContext context, UserRole roleToManage) {
        this.userService = context.getBean(UserService.class);
        this.roleToManage = roleToManage;
        setLayout(new BorderLayout());
        initUI();
        loadUsers();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadUsers();
            }
        });
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Register New " + roleToManage.name()));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        userField = new JTextField(15);
        passField = new JTextField(15);
        nameField = new JTextField(15);
        emailField = new JTextField(15);

        int row = 0;
        addFormField(formPanel, "Username:", userField, gbc, row++);
        addFormField(formPanel, "Password:", passField, gbc, row++);
        addFormField(formPanel, "Full Name:", nameField, gbc, row++);
        addFormField(formPanel, "Email:", emailField, gbc, row++);

        JButton addButton = new JButton("Add User");
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);

        String[] columns = {"ID", "Username", "Full Name", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, new JScrollPane(table));
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> saveUser());
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User u : users) {
            if (u.getRole() == roleToManage) {
                tableModel.addRow(new Object[]{
                        u.getId(), u.getUsername(), u.getFullName(), u.getEmail()
                });
            }
        }
    }

    private void saveUser() {
        try {
            User user = new User();
            user.setUsername(userField.getText());
            user.setPassword(passField.getText());
            user.setFullName(nameField.getText());
            user.setEmail(emailField.getText());
            user.setRole(roleToManage);

            userService.registerUser(user);
            
            userField.setText("");
            passField.setText("");
            nameField.setText("");
            emailField.setText("");
            loadUsers();
            JOptionPane.showMessageDialog(this, "User added successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}