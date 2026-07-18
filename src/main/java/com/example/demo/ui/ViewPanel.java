package com.example.demo.ui;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionType;
import com.example.demo.service.TransactionService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewPanel extends JPanel {

    private final TransactionService transactionService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewPanel(ApplicationContext context) {
        this.transactionService = context.getBean(TransactionService.class);
        setLayout(new BorderLayout());
        initUI();
        loadTransactions();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadTransactions();
            }
        });
    }

    private void initUI() {
        String[] columns = {"Tx ID", "Date", "Type", "Product", "Qty", "Price/Unit", "Total", "Party", "User"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("All System Transactions"));
        
        JButton billButton = new JButton("Generate Bill");
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(billButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        billButton.addActionListener(e -> generateBill());
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionService.getAllTransactions();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Transaction t : transactions) {
            String party = "";
            if (t.getTransactionType() == TransactionType.IN && t.getSupplier() != null) {
                party = t.getSupplier().getName();
            } else if (t.getTransactionType() == TransactionType.OUT && t.getDealer() != null) {
                party = t.getDealer().getName();
            }

            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getTransactionDate() != null ? t.getTransactionDate().format(formatter) : "",
                    t.getTransactionType(),
                    t.getProduct().getName(),
                    t.getQuantity(),
                    t.getPricePerUnit(),
                    t.getTotalPrice(),
                    party,
                    t.getCreatedBy().getUsername()
            });
        }
    }

    private void generateBill() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction from the table to generate a bill.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();
        String date = tableModel.getValueAt(selectedRow, 1).toString();
        String type = tableModel.getValueAt(selectedRow, 2).toString();
        String product = tableModel.getValueAt(selectedRow, 3).toString();
        String qty = tableModel.getValueAt(selectedRow, 4).toString();
        String price = tableModel.getValueAt(selectedRow, 5).toString();
        String total = tableModel.getValueAt(selectedRow, 6).toString();
        String party = tableModel.getValueAt(selectedRow, 7).toString();
        String user = tableModel.getValueAt(selectedRow, 8).toString();

        StringBuilder bill = new StringBuilder();
        bill.append("=========================================\n");
        bill.append("         WAREHOUSE OFFICIAL BILL         \n");
        bill.append("=========================================\n");
        bill.append("Transaction ID : ").append(id).append("\n");
        bill.append("Date           : ").append(date).append("\n");
        bill.append("Type           : ").append(type).append("\n");
        bill.append("Processed By   : ").append(user).append("\n");
        bill.append("-----------------------------------------\n");
        
        if (type.equals("IN")) {
            bill.append("Supplier       : ").append(party).append("\n");
        } else {
            bill.append("Dealer         : ").append(party).append("\n");
        }
        
        bill.append("-----------------------------------------\n");
        bill.append("Product        : ").append(product).append("\n");
        bill.append("Quantity       : ").append(qty).append("\n");
        bill.append("Price per Unit : $").append(price).append("\n");
        bill.append("-----------------------------------------\n");
        bill.append("TOTAL AMOUNT   : $").append(total).append("\n");
        bill.append("=========================================\n");

        JTextArea textArea = new JTextArea(bill.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        textArea.setBackground(new Color(245, 245, 245));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Transaction Bill", JOptionPane.PLAIN_MESSAGE);
    }
}