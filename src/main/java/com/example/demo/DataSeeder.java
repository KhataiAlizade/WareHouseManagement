package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final DealerRepository dealerRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("🌱 Seeding test data...");

            User admin = new User(null, "admin", "admin123", UserRole.ADMIN, "Khatai Admin", "admin@warehouse.com", null);
            User sales = new User(null, "sales1", "sales123", UserRole.SALES_MANAGER, "Sales Manager", "sales@warehouse.com", null);
            User warehouse = new User(null, "ware1", "ware123", UserRole.WAREHOUSE_MANAGER, "Warehouse Manager", "warehouse@warehouse.com", null);
            
            userRepository.saveAll(List.of(admin, sales, warehouse));

            Product p1 = new Product(null, "Logitech Wireless Mouse", "M-101", "Standard office mouse", 50, 10, new BigDecimal("25.50"), null);
            Product p2 = new Product(null, "Mechanical Keyboard", "K-202", "RGB mechanical keyboard", 5, 10, new BigDecimal("85.00"), null); 
            Product p3 = new Product(null, "27-inch Monitor", "MON-27", "1080p 60hz monitor", 120, 20, new BigDecimal("150.00"), null);
            
            productRepository.saveAll(List.of(p1, p2, p3));

            Supplier supplier = new Supplier(null, "Tech Distributors Ltd.", "tech@distributors.com", "123-456-789", "987-654-321", "Warsaw, Poland", null);
            supplierRepository.save(supplier);

            Dealer dealer = new Dealer(null, "Retail Store A", "contact@retaila.com", "111-222-333", null, "Krakow, Poland", null);
            dealerRepository.save(dealer);

            System.out.println("✅ Test data successfully loaded into PostgreSQL!");
        } else {
            System.out.println("ℹ️ Database already contains data. Skipping seeding.");
        }
    }
}