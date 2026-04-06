-- Xóa và tạo lại database

CREATE DATABASE kid_fashion_shop
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE kid_fashion_shop;

-- =========================
-- 1. Bảng users
-- =========================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =========================
-- 2. Bảng categories
-- =========================
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_id BIGINT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id) REFERENCES categories(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- =========================
-- 3. Bảng products
-- Thông tin chung của sản phẩm
-- =========================
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(12,2) NOT NULL DEFAULT 0,
    stock_quantity INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    color VARCHAR(50),
    brand VARCHAR(100),
    age_group VARCHAR(50),
    gender ENUM('BOY', 'GIRL', 'UNISEX') NOT NULL DEFAULT 'UNISEX',
    is_new BOOLEAN NOT NULL DEFAULT TRUE,
    sold_quantity INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- =========================
-- 4. Bảng product_sizes
-- Mỗi sản phẩm có nhiều size
-- =========================
CREATE TABLE product_sizes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    size VARCHAR(20) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_sizes_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT uk_product_size UNIQUE (product_id, size)
);

-- =========================
-- 5. Bảng coupons
-- =========================
CREATE TABLE coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type ENUM('PERCENT', 'FIXED') NOT NULL,
    discount_value DECIMAL(12,2) NOT NULL DEFAULT 0,
    min_order_value DECIMAL(12,2) NOT NULL DEFAULT 0,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVE', 'INACTIVE', 'EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 6. Bảng carts
-- Mỗi user có 1 giỏ hàng
-- =========================
CREATE TABLE carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_carts_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =========================
-- 7. Bảng cart_items
-- Giỏ hàng lưu theo product_size
-- =========================
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_size_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,

    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id) REFERENCES carts(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_cart_items_product_size
        FOREIGN KEY (product_size_id) REFERENCES product_sizes(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT uk_cart_product_size UNIQUE (cart_id, product_size_id)
);

-- =========================
-- 8. Bảng orders
-- =========================
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NULL,
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    receiver_name VARCHAR(100) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    final_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    payment_method ENUM('COD', 'BANK_TRANSFER') NOT NULL DEFAULT 'COD',
    order_status ENUM('PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    note TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_orders_coupon
        FOREIGN KEY (coupon_id) REFERENCES coupons(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- =========================
-- 9. Bảng order_items
-- Lưu snapshot tại thời điểm mua
-- =========================
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_size_id BIGINT NULL,
    product_name VARCHAR(150) NOT NULL,
    size VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_order_items_product_size
        FOREIGN KEY (product_size_id) REFERENCES product_sizes(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- =========================
-- INDEX để tối ưu tìm kiếm
-- =========================
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_created_at ON products(created_at);
CREATE INDEX idx_products_sold_quantity ON products(sold_quantity);
CREATE INDEX idx_products_status ON products(status);

CREATE INDEX idx_product_sizes_product_id ON product_sizes(product_id);
CREATE INDEX idx_product_sizes_size ON product_sizes(size);

CREATE INDEX idx_coupons_code ON coupons(code);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_orders_status ON orders(order_status);

CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_size_id ON cart_items(product_size_id);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_order_items_product_size_id ON order_items(product_size_id);


DESCRIBE categories;
SHOW CREATE TABLE categories;

USE kid_fashion_shop;

ALTER TABLE users
MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE categories
MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE products
MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE product_sizes
MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE carts
MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE orders
MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

SELECT * FROM carts;
SELECT * FROM cart_items;
SELECT * FROM orders;
SELECT * FROM order_items;

SHOW CREATE TABLE orders;

ALTER TABLE orders
MODIFY order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

SELECT * FROM users;
SELECT * FROM carts;
SELECT * FROM cart_items;
SELECT * FROM orders ORDER BY id DESC;
SELECT * FROM order_items ORDER BY id DESC;
SELECT * FROM cart_items;

USE kid_fashion_shop;
SELECT id, username, role, status FROM users WHERE username = 'admin';
DELETE FROM users WHERE username = 'admin';

INSERT INTO users (full_name, username, email, password, phone, address, role, status)
VALUES (
    'Administrator',
    'admin',
    'admin@gmail.com',
    '$2a$10$aEIKxtbtx2CMXkSTUR7iA.qMj/.wWQVOt6PzVFmk2S.BlixVNo4sS',
    '0123456789',
    'Admin Address',
    'ADMIN',
    'ACTIVE'
);

-- set danh mục cha con --
INSERT INTO categories (id, name, description, parent_id, status)
VALUES
(1, 'Áo', 'Danh mục áo trẻ em', NULL, 'ACTIVE'),
(2, 'Áo khoác', 'Danh mục áo khoác trẻ em', 1, 'ACTIVE'),
(3, 'Áo thun', 'Danh mục áo thun trẻ em', 1, 'ACTIVE'),

(4, 'Quần & Váy', 'Danh mục quần và váy trẻ em', NULL, 'ACTIVE'),
(5, 'Quần', 'Danh mục quần trẻ em', 4, 'ACTIVE'),
(6, 'Đầm & Chân váy', 'Danh mục đầm và chân váy trẻ em', 4, 'ACTIVE'),

(7, 'Đồ mặc nhà', 'Danh mục đồ mặc nhà trẻ em', NULL, 'ACTIVE'),
(8, 'Đồ ngủ', 'Danh mục đồ ngủ trẻ em', 7, 'ACTIVE'),

(9, 'Đồ vận động', 'Danh mục đồ vận động trẻ em', NULL, 'ACTIVE'),
(10, 'Đồ thể thao', 'Danh mục đồ thể thao trẻ em', 9, 'ACTIVE'),

(11, 'Phụ kiện', 'Danh mục phụ kiện trẻ em', NULL, 'ACTIVE');

-- thêm sản phẩm --
INSERT INTO products
(category_id, name, description, price, stock_quantity, image_url, color, brand, age_group, gender, is_new, sold_quantity, status)
VALUES
(2, 'Áo Hoodie Trẻ Em Old Navy', 'Hoodie cotton mềm, có mũ, giữ ấm tốt, phù hợp đi học & đi chơi', 699000, 20, '/images/ao-hoodie-tre-em-old-navy.jpg', 'Xanh', 'Old Navy', '4-6 tuổi', 'UNISEX', TRUE, 5, 'ACTIVE'),
(2, 'Áo khoác cotton dày trẻ em', 'Chất liệu cotton dày, ấm áp, an toàn cho da trẻ', 289000, 18, '/images/ao-khoac-cotton-day-tre-em.jpg', 'Kem', 'shopQHC', '4-6 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(2, 'Áo khoác hoodie có nón', 'Thiết kế năng động, có mũ trùm, phù hợp thời tiết se lạnh', 149000, 25, '/images/ao-khoac-hoodie-co-non.jpg', 'Xám', 'shopQHC', '4-7 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(2, 'Áo khoác bé gái hồng lấp lánh', 'Thiết kế dễ thương, màu nổi bật, phù hợp bé gái đi chơi', 659000, 15, '/images/ao-khoac-be-gai-hong-lap-lanh.jpg', 'Hồng', 'shopQHC', '4-6 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(2, 'Áo khoác chống thấm Quechua', 'Chống nước, chống gió, phù hợp hoạt động ngoài trời', 459000, 16, '/images/ao-khoac-chong-tham-quechua.jpg', 'Xanh navy', 'Quechua', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(2, 'Áo khoác gió trẻ em', 'Nhẹ, chống gió, tiện mang khi đi học hoặc du lịch', 499000, 20, '/images/ao-khoac-gio-tre-em.jpg', 'Xanh lá', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(2, 'Áo khoác kaki lót bông', 'Vải kaki chắc chắn, bên trong lót bông giữ ấm', 375000, 14, '/images/ao-khoac-kaki-lot-bong.jpg', 'Nâu', 'shopQHC', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(2, 'Áo khoác chống nước CANIFA', 'Chống thấm nhẹ, thiết kế hiện đại, phù hợp mùa mưa', 454000, 17, '/images/ao-khoac-chong-nuoc-canifa.jpg', 'Đen', 'CANIFA', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(2, 'Áo khoác parka trẻ em', 'Dáng dài, giữ ấm tốt, phong cách Hàn Quốc', 284000, 19, '/images/ao-khoac-parka-tre-em.jpg', 'Rêu', 'shopQHC', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(2, 'Áo khoác mỏng nhẹ bé trai', 'Nhẹ, thoáng, phù hợp mặc hàng ngày', 149000, 22, '/images/ao-khoac-mong-nhe-be-trai.jpg', 'Xanh dương', 'shopQHC', '4-6 tuổi', 'BOY', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun cotton basic cổ tròn', 'Vải cotton mềm, thoáng mát, phù hợp mặc hàng ngày', 149000, 25, '/images/ao-thun-cotton-basic-co-tron.jpg', 'Trắng', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun in hình hoạt hình', 'In hình dễ thương, màu sắc tươi sáng, phù hợp bé trai & bé gái', 129000, 25, '/images/ao-thun-in-hinh-hoat-hinh.jpg', 'Vàng', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun cổ tròn 100% cotton cao cấp', 'Cotton cao cấp, thấm hút mồ hôi tốt, an toàn cho da nhạy cảm', 199000, 20, '/images/ao-thun-co-tron-100-cotton-cao-cap.jpg', 'Kem', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun bé trai form rộng thể thao', 'Form rộng thoải mái, phù hợp vận động, chơi thể thao', 159000, 18, '/images/ao-thun-be-trai-form-rong-the-thao.jpg', 'Xanh navy', 'shopQHC', '5-8 tuổi', 'BOY', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun bé gái tay ngắn họa tiết hoa', 'Thiết kế nữ tính, nhẹ nhàng, phù hợp đi chơi', 139000, 18, '/images/ao-thun-be-gai-tay-ngan-hoa-tiet-hoa.jpg', 'Hồng', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun cotton organic cho bé', '100% cotton hữu cơ, không gây kích ứng, thoáng khí tốt', 115000, 20, '/images/ao-thun-cotton-organic-cho-be.jpg', 'Xanh mint', 'shopQHC', '3-7 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun cổ bẻ (polo) trẻ em', 'Phong cách lịch sự, phù hợp đi học hoặc đi chơi', 249000, 16, '/images/ao-thun-co-be-polo-tre-em.jpg', 'Trắng', 'shopQHC', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun dài tay trẻ em', 'Giữ ấm nhẹ, phù hợp thời tiết se lạnh', 179000, 20, '/images/ao-thun-dai-tay-tre-em.jpg', 'Xám', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun unisex cotton co giãn', 'Cotton pha spandex, co giãn tốt, vận động thoải mái', 169000, 20, '/images/ao-thun-unisex-cotton-co-gian.jpg', 'Be', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(3, 'Áo thun trơn nhiều màu (set 3 áo)', 'Set nhiều màu, dễ phối đồ, tiết kiệm chi phí', 399000, 14, '/images/ao-thun-tron-nhieu-mau-set-3-ao.jpg', 'Nhiều màu', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần short cotton basic trẻ em', 'Vải cotton mềm, thoáng mát, co giãn tốt, phù hợp mặc mùa hè', 149000, 24, '/images/quan-short-cotton-basic-tre-em.jpg', 'Be', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần short nỉ thể thao trẻ em', 'Form rộng, năng động, thích hợp vận động ngoài trời', 249000, 18, '/images/quan-short-ni-the-thao-tre-em.jpg', 'Đen', 'shopQHC', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần dài cotton trẻ em', 'Cotton co giãn 4 chiều, mềm mại, phù hợp mặc ở nhà và đi học', 99000, 28, '/images/quan-dai-cotton-tre-em.jpg', 'Xám', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần jogger trẻ em', 'Bo ống, phong cách thể thao, dễ phối với áo thun', 179000, 22, '/images/quan-jogger-tre-em.jpg', 'Xanh rêu', 'shopQHC', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần jean trẻ em', 'Chất liệu denim cotton mềm, bền, phù hợp đi chơi', 149000, 20, '/images/quan-jean-tre-em.jpg', 'Xanh đậm', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần legging bé gái', 'Co giãn tốt, ôm nhẹ, phù hợp mặc với váy hoặc áo dài', 129000, 22, '/images/quan-legging-be-gai.jpg', 'Đen', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(5, 'Quần kaki trẻ em', 'Vải kaki chắc chắn, lịch sự, phù hợp đi học', 199000, 18, '/images/quan-kaki-tre-em.jpg', 'Nâu nhạt', 'shopQHC', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần yếm trẻ em (overall)', 'Cotton hữu cơ an toàn, thiết kế dễ thương, thoáng khí', 202000, 16, '/images/quan-yem-tre-em-overall.jpg', 'Xanh denim', 'shopQHC', '3-7 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần thể thao trẻ em (co giãn)', 'Vải co giãn, thấm hút mồ hôi, phù hợp hoạt động nhiều', 159000, 20, '/images/quan-the-thao-tre-em-co-gian.jpg', 'Xám đậm', 'shopQHC', '5-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(5, 'Quần nỉ dài giữ ấm', 'Chất liệu nỉ mềm, giữ ấm tốt, phù hợp mùa lạnh', 189000, 18, '/images/quan-ni-dai-giu-am.jpg', 'Ghi', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(6, 'Váy cotton jersey in hình', 'Vải cotton mềm, in hình dễ thương, thoáng mát cho mùa hè', 179000, 18, '/images/vay-cotton-jersey-in-hinh.jpg', 'Hồng pastel', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Váy denim có cổ', 'Chất liệu jean mềm, form đứng dáng, phù hợp đi chơi', 699000, 12, '/images/vay-denim-co-co.jpg', 'Xanh denim', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Váy thun cá sấu (polo dress)', 'Co giãn tốt, phong cách năng động, mặc đi học', 299000, 16, '/images/vay-thun-ca-sau-polo-dress.jpg', 'Đỏ đô', 'shopQHC', '5-8 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Váy vải tuyn công chúa', 'Chân váy xòe nhiều lớp, phù hợp đi tiệc hoặc sinh nhật', 449000, 14, '/images/vay-vai-tuyn-cong-chua.jpg', 'Hồng', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Váy cotton muslin cao cấp', 'Chất vải nhẹ, thoáng khí, an toàn cho da trẻ', 499000, 14, '/images/vay-cotton-muslin-cao-cap.jpg', 'Kem', 'shopQHC', '3-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Váy hoa bé gái dáng xòe', 'Họa tiết hoa dễ thương, phong cách nữ tính', 249000, 18, '/images/vay-hoa-be-gai-dang-xoe.jpg', 'Hồng nhạt', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Chân váy chữ A trẻ em', 'Dáng chữ A basic, dễ phối áo thun hoặc sơ mi', 199000, 18, '/images/chan-vay-chu-a-tre-em.jpg', 'Be', 'shopQHC', '5-8 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Chân váy jean trẻ em', 'Denim bền, phong cách cá tính, phù hợp đi chơi', 299000, 15, '/images/chan-vay-jean-tre-em.jpg', 'Xanh denim', 'shopQHC', '5-8 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Chân váy xòe vải tuyn', 'Nhẹ, bồng bềnh, phù hợp phong cách công chúa', 229000, 16, '/images/chan-vay-xoe-vai-tuyn.jpg', 'Tím pastel', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(6, 'Váy nỉ dài tay giữ ấm', 'Chất liệu nỉ mềm, giữ ấm tốt, phù hợp mùa lạnh', 449000, 12, '/images/vay-ni-dai-tay-giu-am.jpg', 'Đỏ rượu', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(8, 'Bộ pijama trẻ em tay dài chất lụa', 'Vải lụa mềm, mịn, thoáng mát, phù hợp ngủ điều hòa', 129000, 20, '/images/bo-pijama-tre-em-tay-dai-chat-lua.jpg', 'Hồng nhạt', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(8, 'Bộ pijama cotton cộc tay Embes', 'Cotton mềm, thấm hút mồ hôi, phù hợp mùa hè', 202000, 18, '/images/bo-pijama-cotton-coc-tay-embes.jpg', 'Xanh nhạt', 'Embes', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(8, 'Pijama Paw Patrol cotton', 'In hình hoạt hình, chất cotton dễ chịu, bé rất thích', 153000, 18, '/images/pijama-paw-patrol-cotton.jpg', 'Xanh dương', 'shopQHC', '4-7 tuổi', 'BOY', TRUE, 0, 'ACTIVE'),
(8, 'Set pijama dài tay vải đũi', 'Vải đũi nhẹ, thoáng, phù hợp ngủ điều hòa', 195000, 16, '/images/set-pijama-dai-tay-vai-dui.jpg', 'Kem', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(8, 'Bộ pijama Nous dài tay', 'Thun lạnh mỏng, mềm, thích hợp thời tiết nóng', 110000, 20, '/images/bo-pijama-nous-dai-tay.jpg', 'Trắng', 'Nous', '3-7 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(8, 'Bộ đồ ngủ cotton hình thú', 'Cotton co giãn, họa tiết dễ thương, mặc ở nhà', 120000, 20, '/images/bo-do-ngu-cotton-hinh-thu.jpg', 'Vàng', 'shopQHC', '3-7 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(8, 'Bộ pijama dài tay mùa đông', 'Vải nỉ mềm, giữ ấm tốt, phù hợp thời tiết lạnh', 189000, 16, '/images/bo-pijama-dai-tay-mua-dong.jpg', 'Xám', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(8, 'Bộ đồ ngủ ba lỗ mùa hè', 'Thiết kế thoáng, mát, phù hợp bé vận động nhiều', 99000, 22, '/images/bo-do-ngu-ba-lo-mua-he.jpg', 'Xanh mint', 'shopQHC', '3-7 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(8, 'Bộ pijama cotton cổ bèo bé gái', 'Thiết kế nữ tính, nhẹ nhàng, thoải mái khi ngủ', 139000, 18, '/images/bo-pijama-cotton-co-beo-be-gai.jpg', 'Hồng', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(8, 'Bộ đồ ngủ liền thân (romper ngủ)', 'Thiết kế liền thân, tiện lợi cho trẻ nhỏ, giữ ấm tốt', 179000, 15, '/images/bo-do-ngu-lien-than-romper-ngu.jpg', 'Kem', 'shopQHC', '2-5 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Bộ thể thao cotton basic trẻ em', 'Cotton mềm, thoáng mát, phù hợp vận động nhẹ & mặc ở nhà', 39000, 30, '/images/bo-the-thao-cotton-basic-tre-em.jpg', 'Xám', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Set thể thao bé gái áo + quần short', 'Thiết kế dễ thương, co giãn tốt, phù hợp mùa hè', 99000, 24, '/images/set-the-thao-be-gai-ao-quan-short.jpg', 'Hồng', 'shopQHC', '4-7 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(10, 'Bộ đồ bóng đá trẻ em CLB (Real/Barcelona…)', 'Vải mè thoáng khí, phù hợp đá bóng ngoài trời', 78000, 28, '/images/bo-do-bong-da-tre-em-clb-real-barcelona.jpg', 'Trắng', 'shopQHC', '5-9 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Bộ đồ bóng đá trẻ em Chelsea', 'Chất liệu thấm hút mồ hôi, thiết kế giống cầu thủ', 115000, 22, '/images/bo-do-bong-da-tre-em-chelsea.jpg', 'Xanh dương', 'Chelsea', '5-9 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Bộ thể thao adidas trẻ em', 'Thương hiệu cao cấp, form chuẩn, chất liệu dệt kim bền', 1300000, 10, '/images/bo-the-thao-adidas-tre-em.jpg', 'Đen', 'Adidas', '5-9 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Set thể thao áo thun + quần jogger', 'Phong cách thể thao năng động, phù hợp đi học', 200000, 18, '/images/set-the-thao-ao-thun-quan-jogger.jpg', 'Xanh navy', 'shopQHC', '5-9 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Bộ thể thao mùa hè (áo + quần short)', 'Nhẹ, thoáng, phù hợp thời tiết nóng', 150000, 20, '/images/bo-the-thao-mua-he-ao-quan-short.jpg', 'Cam', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Bộ thể thao dài tay mùa đông', 'Vải nỉ giữ ấm, phù hợp thời tiết lạnh', 250000, 16, '/images/bo-the-thao-dai-tay-mua-dong.jpg', 'Xám đậm', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Set thể thao unisex co giãn', 'Vải co giãn 4 chiều, vận động thoải mái', 180000, 18, '/images/set-the-thao-unisex-co-gian.jpg', 'Đen', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(10, 'Bộ thể thao phối lưới thoáng khí', 'Thiết kế thể thao chuyên dụng, phù hợp chạy nhảy nhiều', 220000, 16, '/images/bo-the-thao-phoi-luoi-thoang-khi.jpg', 'Xanh neon', 'shopQHC', '5-9 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(11, 'Mũ lưỡi trai trẻ em', 'Chống nắng, nhẹ, phù hợp đi học & đi chơi', 79000, 30, '/images/mu-luoi-trai-tre-em.jpg', 'Xanh navy', 'shopQHC', '3-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(11, 'Mũ beanie trẻ em phong cách Hàn', 'Giữ ấm, co giãn tốt, phù hợp mùa lạnh', 54000, 24, '/images/mu-beanie-tre-em-phong-cach-han.jpg', 'Be', 'shopQHC', '3-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(11, 'Găng tay len trẻ em Spider-Man', 'Giữ ấm tay, thiết kế hoạt hình, bé thích', 34000, 28, '/images/gang-tay-len-tre-em-spider-man.jpg', 'Đỏ', 'Marvel', '3-8 tuổi', 'BOY', TRUE, 0, 'ACTIVE'),
(11, 'Tất trẻ em Uniqlo set 3 đôi', 'Cotton mềm, thấm hút tốt, dùng hàng ngày', 146000, 30, '/images/tat-tre-em-uniqlo-set-3-doi.jpg', 'Nhiều màu', 'Uniqlo', '3-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(11, 'Set mũ + găng tay cho bé sơ sinh', 'Giữ ấm toàn diện, phù hợp trẻ nhỏ', 82000, 20, '/images/set-mu-gang-tay-cho-be-so-sinh.jpg', 'Kem', 'shopQHC', '0-2 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(11, 'Khăn quàng cổ trẻ em', 'Giữ ấm cổ, chất len mềm, dễ phối đồ', 99000, 22, '/images/khan-quang-co-tre-em.jpg', 'Nâu', 'shopQHC', '3-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(11, 'Balo mini trẻ em', 'Nhỏ gọn, dễ thương, phù hợp đi học hoặc đi chơi', 199000, 18, '/images/balo-mini-tre-em.jpg', 'Vàng', 'shopQHC', '3-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(11, 'Túi đeo chéo trẻ em', 'Thiết kế tiện lợi, đựng đồ cá nhân nhỏ', 149000, 18, '/images/tui-deo-cheo-tre-em.jpg', 'Hồng pastel', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE'),
(11, 'Kẹp tóc / băng đô bé gái', 'Trang trí tóc, nhiều kiểu dáng dễ thương', 39000, 30, '/images/kep-toc-bang-do-be-gai.jpg', 'Hồng', 'shopQHC', '3-8 tuổi', 'GIRL', TRUE, 0, 'ACTIVE'),
(11, 'Kính mát trẻ em', 'Chống nắng, bảo vệ mắt, thời trang', 89000, 20, '/images/kinh-mat-tre-em.jpg', 'Đen', 'shopQHC', '4-8 tuổi', 'UNISEX', TRUE, 0, 'ACTIVE');
-- kiểm tra bảng --
SELECT id, name, category_id FROM products ORDER BY id;
SELECT id, name, parent_id FROM categories ORDER BY id;

-- thêm lại size --
INSERT INTO product_sizes (product_id, size, stock_quantity, price)
VALUES
-- 1-10: Áo khoác
(1, 'S', 6, 699000), (1, 'M', 7, 699000), (1, 'L', 7, 699000),
(2, 'S', 6, 289000), (2, 'M', 6, 289000), (2, 'L', 6, 289000),
(3, 'S', 8, 149000), (3, 'M', 8, 149000), (3, 'L', 9, 149000),
(4, 'S', 5, 659000), (4, 'M', 5, 659000), (4, 'L', 5, 659000),
(5, 'S', 5, 459000), (5, 'M', 5, 459000), (5, 'L', 6, 459000),
(6, 'S', 6, 499000), (6, 'M', 7, 499000), (6, 'L', 7, 499000),
(7, 'S', 4, 375000), (7, 'M', 5, 375000), (7, 'L', 5, 375000),
(8, 'S', 5, 454000), (8, 'M', 6, 454000), (8, 'L', 6, 454000),
(9, 'S', 6, 284000), (9, 'M', 6, 284000), (9, 'L', 7, 284000),
(10, 'S', 7, 149000), (10, 'M', 7, 149000), (10, 'L', 8, 149000),

-- 11-20: Áo thun
(11, 'S', 8, 149000), (11, 'M', 8, 149000), (11, 'L', 9, 149000),
(12, 'S', 8, 129000), (12, 'M', 8, 129000), (12, 'L', 9, 129000),
(13, 'S', 6, 199000), (13, 'M', 7, 199000), (13, 'L', 7, 199000),
(14, 'S', 6, 159000), (14, 'M', 6, 159000), (14, 'L', 6, 159000),
(15, 'S', 6, 139000), (15, 'M', 6, 139000), (15, 'L', 6, 139000),
(16, 'S', 6, 115000), (16, 'M', 7, 115000), (16, 'L', 7, 115000),
(17, 'S', 5, 249000), (17, 'M', 5, 249000), (17, 'L', 6, 249000),
(18, 'S', 6, 179000), (18, 'M', 7, 179000), (18, 'L', 7, 179000),
(19, 'S', 6, 169000), (19, 'M', 7, 169000), (19, 'L', 7, 169000),
(20, 'S', 4, 399000), (20, 'M', 5, 399000), (20, 'L', 5, 399000),

-- 21-30: Quần
(21, 'S', 7, 149000), (21, 'M', 8, 149000), (21, 'L', 9, 149000),
(22, 'S', 5, 249000), (22, 'M', 6, 249000), (22, 'L', 7, 249000),
(23, 'S', 9, 99000),  (23, 'M', 9, 99000),  (23, 'L', 10, 99000),
(24, 'S', 7, 179000), (24, 'M', 7, 179000), (24, 'L', 8, 179000),
(25, 'S', 6, 149000), (25, 'M', 7, 149000), (25, 'L', 7, 149000),
(26, 'S', 7, 129000), (26, 'M', 7, 129000), (26, 'L', 8, 129000),
(27, 'S', 5, 199000), (27, 'M', 6, 199000), (27, 'L', 7, 199000),
(28, 'S', 5, 202000), (28, 'M', 5, 202000), (28, 'L', 6, 202000),
(29, 'S', 6, 159000), (29, 'M', 7, 159000), (29, 'L', 7, 159000),
(30, 'S', 5, 189000), (30, 'M', 6, 189000), (30, 'L', 7, 189000),

-- 31-40: Váy / chân váy
(31, 'S', 6, 179000), (31, 'M', 6, 179000), (31, 'L', 6, 179000),
(32, 'S', 4, 699000), (32, 'M', 4, 699000), (32, 'L', 4, 699000),
(33, 'S', 5, 299000), (33, 'M', 5, 299000), (33, 'L', 6, 299000),
(34, 'S', 4, 449000), (34, 'M', 5, 449000), (34, 'L', 5, 449000),
(35, 'S', 4, 499000), (35, 'M', 5, 499000), (35, 'L', 5, 499000),
(36, 'S', 6, 249000), (36, 'M', 6, 249000), (36, 'L', 6, 249000),
(37, 'S', 6, 199000), (37, 'M', 6, 199000), (37, 'L', 6, 199000),
(38, 'S', 5, 299000), (38, 'M', 5, 299000), (38, 'L', 5, 299000),
(39, 'S', 5, 229000), (39, 'M', 5, 229000), (39, 'L', 6, 229000),
(40, 'S', 4, 449000), (40, 'M', 4, 449000), (40, 'L', 4, 449000),

-- 41-50: Đồ ngủ
(41, 'S', 6, 129000), (41, 'M', 7, 129000), (41, 'L', 7, 129000),
(42, 'S', 5, 202000), (42, 'M', 6, 202000), (42, 'L', 7, 202000),
(43, 'S', 6, 153000), (43, 'M', 6, 153000), (43, 'L', 6, 153000),
(44, 'S', 5, 195000), (44, 'M', 5, 195000), (44, 'L', 6, 195000),
(45, 'S', 6, 110000), (45, 'M', 7, 110000), (45, 'L', 7, 110000),
(46, 'S', 6, 120000), (46, 'M', 7, 120000), (46, 'L', 7, 120000),
(47, 'S', 5, 189000), (47, 'M', 5, 189000), (47, 'L', 6, 189000),
(48, 'S', 7, 99000),  (48, 'M', 7, 99000),  (48, 'L', 8, 99000),
(49, 'S', 6, 139000), (49, 'M', 6, 139000), (49, 'L', 6, 139000),
(50, 'S', 4, 179000), (50, 'M', 5, 179000), (50, 'L', 6, 179000),

-- 51-60: Đồ thể thao
(51, 'S', 9, 39000),  (51, 'M', 10, 39000), (51, 'L', 11, 39000),
(52, 'S', 7, 99000),  (52, 'M', 8, 99000),  (52, 'L', 9, 99000),
(53, 'S', 8, 78000),  (53, 'M', 9, 78000),  (53, 'L', 10, 78000),
(54, 'S', 7, 115000), (54, 'M', 7, 115000), (54, 'L', 8, 115000),
(55, 'S', 3, 1300000), (55, 'M', 3, 1300000), (55, 'L', 4, 1300000),
(56, 'S', 5, 200000), (56, 'M', 6, 200000), (56, 'L', 7, 200000),
(57, 'S', 6, 150000), (57, 'M', 7, 150000), (57, 'L', 7, 150000),
(58, 'S', 5, 250000), (58, 'M', 5, 250000), (58, 'L', 6, 250000),
(59, 'S', 6, 180000), (59, 'M', 6, 180000), (59, 'L', 6, 180000),
(60, 'S', 5, 220000), (60, 'M', 5, 220000), (60, 'L', 6, 220000),

-- 61-70: Phụ kiện
(61, 'Free Size', 30, 79000),
(62, 'Free Size', 24, 54000),
(63, 'Free Size', 28, 34000),
(64, 'Free Size', 30, 146000),
(65, 'Free Size', 20, 82000),
(66, 'Free Size', 22, 99000),
(67, 'Free Size', 18, 199000),
(68, 'Free Size', 18, 149000),
(69, 'Free Size', 30, 39000),
(70, 'Free Size', 20, 89000);



