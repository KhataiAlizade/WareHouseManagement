package com.example.demo.ui;

import com.example.demo.entity.*;
import com.example.demo.repository.DealerRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.TransactionService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class TransactionPanel extends JPanel {

    private final TransactionService transactionService;
    private final ProductService productService;
    private final SupplierRepository supplierRepository;
    private final DealerRepository dealerRepository;
    private final User currentUser;

    private JComboBox<TransactionType> typeCombo;
    private JComboBox<ProductComboItem> productCombo;
    private JTextField quantityField;
    private JTextField priceField;
    private JComboBox<SupplierComboItem> supplierCombo;
    private JComboBox<DealerComboItem> dealerCombo;

    public TransactionPanel(ApplicationContext context, User currentUser) {
        this.transactionService = context.getBean(TransactionService.class);
        this.productService = context.getBean(ProductService.class);
        this.supplierRepository = context.getBean(SupplierRepository.class);
        this.dealerRepository = context.getBean(DealerRepository.class);
        this.currentUser = currentUser;

        setLayout(new GridBagLayout());
        initUI();
        loadDropdowns();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadDropdowns();
            }
        });
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Process New Transaction"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        typeCombo = new JComboBox<>(TransactionType.values());
        productCombo = new JComboBox<>();
        quantityField = new JTextField(15);
        priceField = new JTextField(15);
        supplierCombo = new JComboBox<>();
        dealerCombo = new JComboBox<>();

        int row = 0;
        addFormField(formPanel, "Type:", typeCombo, gbc, row++);
        addFormField(formPanel, "Product:", productCombo, gbc, row++);
        addFormField(formPanel, "Quantity:", quantityField, gbc, row++);
        addFormField(formPanel, "Price per Unit:", priceField, gbc, row++);
        addFormField(formPanel, "Supplier (If IN):", supplierCombo, gbc, row++);
        addFormField(formPanel, "Dealer (If OUT):", dealerCombo, gbc, row++);

        JButton submitBtn = new JButton("Submit Transaction");
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(submitBtn, gbc);

        add(formPanel);

        typeCombo.addActionListener(e -> {
            TransactionType type = (TransactionType) typeCombo.getSelectedItem();
            if (type == TransactionType.IN) {
                supplierCombo.setEnabled(true);
                dealerCombo.setEnabled(false);
                dealerCombo.setSelectedIndex(-1);
            } else {
                supplierCombo.setEnabled(false);
                supplierCombo.setSelectedIndex(-1);
                dealerCombo.setEnabled(true);
            }
        });

        typeCombo.setSelectedItem(TransactionType.IN);
        dealerCombo.setEnabled(false);

        submitBtn.addActionListener(e -> processTransaction());
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadDropdowns() {
        productCombo.removeAllItems();
        List<Product> products = productService.getAllProducts();
        for (Product p : products) {
            productCombo.addItem(new ProductComboItem(p));
        }

        supplierCombo.removeAllItems();
        List<Supplier> suppliers = supplierRepository.findAll();
        for (Supplier s : suppliers) {
            supplierCombo.addItem(new SupplierComboItem(s));
        }

        dealerCombo.removeAllItems();
        List<Dealer> dealers = dealerRepository.findAll();
        for (Dealer d : dealers) {
            dealerCombo.addItem(new DealerComboItem(d));
        }
    }

    private void processTransaction() {
        try {
            TransactionType type = (TransactionType) typeCombo.getSelectedItem();
            ProductComboItem selectedProduct = (ProductComboItem) productCombo.getSelectedItem();
            int qty = Integer.parseInt(quantityField.getText());
            BigDecimal price = new BigDecimal(priceField.getText());

            Transaction transaction = new Transaction();
            transaction.setTransactionType(type);
            transaction.setProduct(selectedProduct.getProduct());
            transaction.setQuantity(qty);
            transaction.setPricePerUnit(price);
            transaction.setCreatedBy(currentUser);

            if (type == TransactionType.IN) {
                SupplierComboItem selectedSupplier = (SupplierComboItem) supplierCombo.getSelectedItem();
                if (selectedSupplier != null) {
                    transaction.setSupplier(selectedSupplier.getSupplier());
                }
            } else {
                DealerComboItem selectedDealer = (DealerComboItem) dealerCombo.getSelectedItem();
                if (selectedDealer != null) {
                    transaction.setDealer(selectedDealer.getDealer());
                }
            }

            transactionService.createTransaction(transaction);
            JOptionPane.showMessageDialog(this, "Transaction processed successfully!");
            quantityField.setText("");
            priceField.setText("");
            loadDropdowns();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Transaction Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ProductComboItem {
        private final Product product;
        public ProductComboItem(Product product) { this.product = product; }
        public Product getProduct() { return product; }
        @Override public String toString() { return product.getName() + " (Stock: " + product.getQuantityInStock() + ")"; }
    }

    private static class SupplierComboItem {
        private final Supplier supplier;
        public SupplierComboItem(Supplier supplier) { this.supplier = supplier; }
        public Supplier getSupplier() { return supplier; }
        @Override public String toString() { return supplier.getName(); }
    }

    private static class DealerComboItem {
        private final Dealer dealer;
        public DealerComboItem(Dealer dealer) { this.dealer = dealer; }
        public Dealer getDealer() { return dealer; }
        @Override public String toString() { return dealer.getName(); }
    }
}