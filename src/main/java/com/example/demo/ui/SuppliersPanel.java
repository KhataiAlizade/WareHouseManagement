package com.example.demo.ui;

import com.example.demo.entity.Supplier;
import com.example.demo.repository.SupplierRepository;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SuppliersPanel extends JPanel {

    private final SupplierRepository supplierRepository;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField idField, nameField, emailField, phone1Field, phone2Field;
    private JTextArea addressArea;

    public SuppliersPanel(ApplicationContext context) {
        this.supplierRepository = context.getBean(SupplierRepository.class);
        setLayout(new BorderLayout());
        initUI();
        loadSuppliers();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Suppliers"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(15);
        idField.setEditable(false);
        nameField = new JTextField(15);
        emailField = new JTextField(15);
        phone1Field = new JTextField(15);
        phone2Field = new JTextField(15);
        addressArea = new JTextArea(3, 15);

        int row = 0;
        addFormField(formPanel, "ID:", idField, gbc, row++);
        addFormField(formPanel, "Name:", nameField, gbc, row++);
        addFormField(formPanel, "Email:", emailField, gbc, row++);
        addFormField(formPanel, "Phone 1:", phone1Field, gbc, row++);
        addFormField(formPanel, "Phone 2:", phone2Field, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(addressArea), gbc);
        row++;

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        String[] columns = {"ID", "Name", "Email", "Phone 1", "Phone 2", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, scrollPane);
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                nameField.setText(tableModel.getValueAt(selectedRow, 1) != null ? tableModel.getValueAt(selectedRow, 1).toString() : "");
                emailField.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");
                phone1Field.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
                phone2Field.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
                addressArea.setText(tableModel.getValueAt(selectedRow, 5) != null ? tableModel.getValueAt(selectedRow, 5).toString() : "");
            }
        });

        addButton.addActionListener(e -> saveOrUpdateSupplier(null));
        updateButton.addActionListener(e -> {
            if (!idField.getText().isEmpty()) {
                saveOrUpdateSupplier(Long.parseLong(idField.getText()));
            } else {
                JOptionPane.showMessageDialog(this, "Select a supplier to update.");
            }
        });
        deleteButton.addActionListener(e -> {
            if (!idField.getText().isEmpty()) {
                supplierRepository.deleteById(Long.parseLong(idField.getText()));
                clearFields();
                loadSuppliers();
            } else {
                JOptionPane.showMessageDialog(this, "Select a supplier to delete.");
            }
        });
        clearButton.addActionListener(e -> clearFields());
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = supplierRepository.findAll();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getEmail(), s.getPhone1(), s.getPhone2(), s.getAddress()
            });
        }
    }

    private void saveOrUpdateSupplier(Long id) {
        try {
            Supplier supplier = new Supplier();
            supplier.setId(id);
            supplier.setName(nameField.getText());
            supplier.setEmail(emailField.getText());
            supplier.setPhone1(phone1Field.getText());
            supplier.setPhone2(phone2Field.getText());
            supplier.setAddress(addressArea.getText());

            supplierRepository.save(supplier);
            clearFields();
            loadSuppliers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your fields.");
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phone1Field.setText("");
        phone2Field.setText("");
        addressArea.setText("");
        table.clearSelection();
    }
}