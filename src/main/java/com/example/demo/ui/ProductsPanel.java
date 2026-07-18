package com.example.demo.ui;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductsPanel extends JPanel {

    private final ProductService productService;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField idField, nameField, skuField, qtyField, minStockField, priceField;
    private JTextArea descArea;

    public ProductsPanel(ProductService productService) {
        this.productService = productService;
        setLayout(new BorderLayout());
        initUI();
        loadProducts();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadProducts();
            }
        });
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Products"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(15);
        idField.setEditable(false);
        nameField = new JTextField(15);
        skuField = new JTextField(15);
        qtyField = new JTextField(15);
        minStockField = new JTextField(15);
        priceField = new JTextField(15);
        descArea = new JTextArea(3, 15);

        int row = 0;
        addFormField(formPanel, "ID:", idField, gbc, row++);
        addFormField(formPanel, "Name:", nameField, gbc, row++);
        addFormField(formPanel, "SKU:", skuField, gbc, row++);
        addFormField(formPanel, "Quantity:", qtyField, gbc, row++);
        addFormField(formPanel, "Min Stock:", minStockField, gbc, row++);
        addFormField(formPanel, "Price:", priceField, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(descArea), gbc);
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

        String[] columns = {"ID", "Name", "SKU", "Qty", "Min Stock", "Price"};
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
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                skuField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                qtyField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                minStockField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                priceField.setText(tableModel.getValueAt(selectedRow, 5).toString());
            }
        });

        addButton.addActionListener(e -> saveOrUpdateProduct(null));
        updateButton.addActionListener(e -> {
            if (!idField.getText().isEmpty()) {
                saveOrUpdateProduct(Long.parseLong(idField.getText()));
            } else {
                JOptionPane.showMessageDialog(this, "Select a product to update.");
            }
        });
        deleteButton.addActionListener(e -> {
            if (!idField.getText().isEmpty()) {
                productService.deleteProduct(Long.parseLong(idField.getText()));
                clearFields();
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Select a product to delete.");
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

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productService.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getSku(), p.getQuantityInStock(), p.getMinStockLevel(), p.getPrice()
            });
        }
    }

    private void saveOrUpdateProduct(Long id) {
        try {
            Product product = new Product();
            product.setId(id);
            product.setName(nameField.getText());
            product.setSku(skuField.getText());
            product.setDescription(descArea.getText());
            product.setQuantityInStock(Integer.parseInt(qtyField.getText()));
            product.setMinStockLevel(Integer.parseInt(minStockField.getText()));
            product.setPrice(new BigDecimal(priceField.getText()));

            productService.saveProduct(product);
            clearFields();
            loadProducts();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your fields.");
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        skuField.setText("");
        qtyField.setText("");
        minStockField.setText("");
        priceField.setText("");
        descArea.setText("");
        table.clearSelection();
    }
}