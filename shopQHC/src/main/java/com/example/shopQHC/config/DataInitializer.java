package com.example.shopQHC.config;

import com.example.shopQHC.entity.Category;
import com.example.shopQHC.entity.Product;
import com.example.shopQHC.entity.ProductSize;
import com.example.shopQHC.entity.User;
import com.example.shopQHC.repository.CategoryRepository;
import com.example.shopQHC.repository.ProductRepository;
import com.example.shopQHC.repository.ProductSizeRepository;
import com.example.shopQHC.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           ProductSizeRepository productSizeRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productSizeRepository = productSizeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createDefaultUsers();
        createSampleData();
    }

    private void createDefaultUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .fullName("Administrator")
                    .username("admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .phone("0123456789")
                    .address("Admin Address")
                    .role(User.Role.ADMIN)
                    .status(User.Status.ACTIVE)
                    .build();

            userRepository.save(admin);
        }

        if (userRepository.findByUsername("customer1").isEmpty()) {
            User customer = User.builder()
                    .fullName("Khách hàng 1")
                    .username("customer1")
                    .email("customer1@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .phone("0987654321")
                    .address("Hồ Chí Minh")
                    .role(User.Role.CUSTOMER)
                    .status(User.Status.ACTIVE)
                    .build();

            userRepository.save(customer);
        }
    }

    private void createSampleData() {
        if (categoryRepository.count() > 0 || productRepository.count() > 0 || productSizeRepository.count() > 0) {
            return;
        }

        Category aoBeTrai = Category.builder()
                .name("Áo bé trai")
                .description("Danh mục áo cho bé trai")
                .status(Category.Status.ACTIVE)
                .build();

        Category vayBeGai = Category.builder()
                .name("Váy bé gái")
                .description("Danh mục váy cho bé gái")
                .status(Category.Status.ACTIVE)
                .build();

        Category quanTreEm = Category.builder()
                .name("Quần trẻ em")
                .description("Danh mục quần cho trẻ em")
                .status(Category.Status.ACTIVE)
                .build();

        aoBeTrai = categoryRepository.save(aoBeTrai);
        vayBeGai = categoryRepository.save(vayBeGai);
        quanTreEm = categoryRepository.save(quanTreEm);

        Product p1 = Product.builder()
                .category(aoBeTrai)
                .name("Áo thun bé trai")
                .description("Áo thun cotton mềm mại cho bé trai")
                .price(new BigDecimal("120000"))
                .stockQuantity(50)
                .imageUrl("/images/ao-thun-be-trai.jpg")
                .color("Xanh")
                .brand("shopQHC")
                .ageGroup("3-5 tuổi")
                .gender(Product.Gender.BOY)
                .isNew(true)
                .soldQuantity(20)
                .status(Product.Status.ACTIVE)
                .build();

        Product p2 = Product.builder()
                .category(vayBeGai)
                .name("Váy bé gái")
                .description("Váy xinh xắn dành cho bé gái")
                .price(new BigDecimal("180000"))
                .stockQuantity(40)
                .imageUrl("/images/vay-be-gai.jpg")
                .color("Hồng")
                .brand("shopQHC")
                .ageGroup("4-6 tuổi")
                .gender(Product.Gender.GIRL)
                .isNew(true)
                .soldQuantity(15)
                .status(Product.Status.ACTIVE)
                .build();

        Product p3 = Product.builder()
                .category(quanTreEm)
                .name("Quần jean trẻ em")
                .description("Quần jean co giãn thoải mái")
                .price(new BigDecimal("150000"))
                .stockQuantity(35)
                .imageUrl("/images/quan-jean-tre-em.jpg")
                .color("Xanh đậm")
                .brand("shopQHC")
                .ageGroup("5-7 tuổi")
                .gender(Product.Gender.UNISEX)
                .isNew(false)
                .soldQuantity(30)
                .status(Product.Status.ACTIVE)
                .build();

        p1 = productRepository.save(p1);
        p2 = productRepository.save(p2);
        p3 = productRepository.save(p3);

        saveProductSize(p1, "S", 10, "120000");
        saveProductSize(p1, "M", 20, "125000");
        saveProductSize(p1, "L", 20, "130000");

        saveProductSize(p2, "S", 10, "180000");
        saveProductSize(p2, "M", 15, "185000");
        saveProductSize(p2, "L", 15, "190000");

        saveProductSize(p3, "S", 10, "150000");
        saveProductSize(p3, "M", 10, "155000");
        saveProductSize(p3, "L", 15, "160000");
    }

    private void saveProductSize(Product product, String size, Integer stockQuantity, String price) {
        ProductSize productSize = ProductSize.builder()
                .product(product)
                .size(size)
                .stockQuantity(stockQuantity)
                .price(new BigDecimal(price))
                .build();

        productSizeRepository.save(productSize);
    }
}