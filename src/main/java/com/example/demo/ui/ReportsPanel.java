package com.example.demo.ui;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionType;
import com.example.demo.service.TransactionService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsPanel extends JPanel {

    private final TransactionService transactionService;
    private JComboBox<Integer> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalInLabel;
    private JLabel totalOutLabel;

    public ReportsPanel(ApplicationContext context) {
        this.transactionService = context.getBean(TransactionService.class);
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Monthly Sales & Billing Report"));

        monthCombo = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            monthCombo.addItem(i);
        }
        monthCombo.setSelectedItem(LocalDate.now().getMonthValue());

        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearCombo.addItem(i);
        }
        yearCombo.setSelectedItem(currentYear);

        JButton generateBtn = new JButton("Generate Report");

        topPanel.add(new JLabel("Month:"));
        topPanel.add(monthCombo);
        topPanel.add(new JLabel("Year:"));
        topPanel.add(yearCombo);
        topPanel.add(generateBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Date", "Type", "Product", "Qty", "Price/Unit", "Total", "Party"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalInLabel = new JLabel("Total Stock Value IN: $0.00");
        totalOutLabel = new JLabel("Total Revenue OUT: $0.00");
        
        totalInLabel.setForeground(new Color(0, 100, 0));
        totalOutLabel.setForeground(new Color(139, 0, 0));
        Font boldFont = new Font("SansSerif", Font.BOLD, 14);
        totalInLabel.setFont(boldFont);
        totalOutLabel.setFont(boldFont);

        bottomPanel.add(totalInLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(totalOutLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        generateBtn.addActionListener(e -> generateReport());
    }

    private void generateReport() {
        int month = (Integer) monthCombo.getSelectedItem();
        int year = (Integer) yearCombo.getSelectedItem();

        List<Transaction> transactions = transactionService.getTransactionsForMonth(year, month);
        tableModel.setRowCount(0);

        BigDecimal totalIn = BigDecimal.ZERO;
        BigDecimal totalOut = BigDecimal.ZERO;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Transaction t : transactions) {
            String party = "";
            if (t.getTransactionType() == TransactionType.IN) {
                if (t.getSupplier() != null) party = t.getSupplier().getName();
                totalIn = totalIn.add(t.getTotalPrice());
            } else {
                if (t.getDealer() != null) party = t.getDealer().getName();
                totalOut = totalOut.add(t.getTotalPrice());
            }

            tableModel.addRow(new Object[]{
                    t.getTransactionDate() != null ? t.getTransactionDate().format(formatter) : "",
                    t.getTransactionType(),
                    t.getProduct().getName(),
                    t.getQuantity(),
                    t.getPricePerUnit(),
                    t.getTotalPrice(),
                    party
            });
        }

        totalInLabel.setText("Total Stock Value IN: $" + totalIn.toString());
        totalOutLabel.setText("Total Revenue OUT: $" + totalOut.toString());
    }
}