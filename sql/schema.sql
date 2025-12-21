CREATE DATABASE IF NOT EXISTS `library_management` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `library_management`;

CREATE TABLE IF NOT EXISTS `admins` (
  `username` VARCHAR(64) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `books` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `category` VARCHAR(100) NOT NULL,
  `total_copies` INT NOT NULL,
  `available_copies` INT NOT NULL,
  `remarks` VARCHAR(500),
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_books_category` (`category`),
  CONSTRAINT `chk_available_copies_valid` CHECK (`available_copies` <= `total_copies` AND `available_copies` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `admins` (`username`, `password`) VALUES ('admin', 'admin123');
INSERT INTO `books` (`title`, `category`, `total_copies`, `available_copies`, `remarks`) VALUES ('示例图书', '小说', 10, 10, '初始化样例数据');

CREATE TABLE IF NOT EXISTS `readers` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(30),
  `max_borrow` INT NOT NULL DEFAULT 5,
  `status` VARCHAR(20) NOT NULL DEFAULT 'active',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_readers_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `readers` (`name`, `code`, `phone`, `max_borrow`, `status`) VALUES
('张三', 'S20250001', '13800000001', 5, 'active'),
('李四', 'S20250002', '13800000002', 5, 'active');

CREATE TABLE IF NOT EXISTS `borrow_records` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `reader_id` INT NOT NULL,
  `book_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `borrow_date` TIMESTAMP NULL DEFAULT NULL,
  `return_date` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_br_reader` (`reader_id`),
  INDEX `idx_br_book` (`book_id`),
  CONSTRAINT `fk_br_reader` FOREIGN KEY (`reader_id`) REFERENCES `readers`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_br_book` FOREIGN KEY (`book_id`) REFERENCES `books`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
