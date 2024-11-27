SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8;
USE `mydb`;

-- Table: category
CREATE TABLE IF NOT EXISTS `category` (
    `cate_id` INT NOT NULL,
    `name` VARCHAR(45) NOT NULL,
    `description` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`cate_id`),
    UNIQUE INDEX `cate_id_UNIQUE` (`cate_id` ASC)
) ENGINE = InnoDB;

-- Table: book
CREATE TABLE IF NOT EXISTS `book` (
    `book_id` INT NOT NULL,
    `title` VARCHAR(45) NOT NULL,
    `author` VARCHAR(45) NOT NULL,
    `isbn` VARCHAR(45) NOT NULL,
    `price` FLOAT NOT NULL,
    `category_cate_id` INT NOT NULL,
    PRIMARY KEY (`book_id`),
    UNIQUE INDEX `book_id_UNIQUE` (`book_id` ASC),
    INDEX `fk_Book_category1_idx` (`category_cate_id` ASC),
    CONSTRAINT `fk_Book_category1`
        FOREIGN KEY (`category_cate_id`)
        REFERENCES `category` (`cate_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: customer
CREATE TABLE IF NOT EXISTS `customer` (
    `customer_id` INT NOT NULL,
    `Fname` VARCHAR(45) NOT NULL,
    `Lname` VARCHAR(45),
    `address` VARCHAR(45) NOT NULL,
    `email` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`customer_id`),
    UNIQUE INDEX `customer_id_UNIQUE` (`customer_id` ASC)
) ENGINE = InnoDB;

-- Table: order
CREATE TABLE IF NOT EXISTS `order` (
    `order_id` INT NOT NULL,
    `order_date` DATE NOT NULL,
    `total_price` FLOAT NOT NULL,
    `customer_id` INT NOT NULL,
    PRIMARY KEY (`order_id`),
    INDEX `fk_Order_Customer1_idx` (`customer_id` ASC),
    UNIQUE INDEX `order_id_UNIQUE` (`order_id` ASC),
    CONSTRAINT `fk_Order_Customer1`
        FOREIGN KEY (`customer_id`)
        REFERENCES `customer` (`customer_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: order_has_book
CREATE TABLE IF NOT EXISTS `order_has_book` (
    `order_id` INT NOT NULL,
    `book_id` INT NOT NULL,
    `quantity` INT NOT NULL CHECK (`quantity` > 0),
    PRIMARY KEY (`order_id`, `book_id`),
    INDEX `fk_Order_has_Book_Order2_idx` (`order_id` ASC),
    INDEX `fk_Order_has_Book_Book2_idx` (`book_id` ASC),
    CONSTRAINT `fk_Order_has_Book_Order2`
        FOREIGN KEY (`order_id`)
        REFERENCES `order` (`order_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_Order_has_Book_Book2`
        FOREIGN KEY (`book_id`)
        REFERENCES `book` (`book_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Table: paymentinformation
CREATE TABLE IF NOT EXISTS `paymentinformation` (
    `payment_id` INT NOT NULL,
    `card_number` VARCHAR(16) NOT NULL,
    `expiry_date` DATE NOT NULL,
    `payment_method` VARCHAR(45) NOT NULL,
    `order_id` INT NOT NULL,
    PRIMARY KEY (`order_id`, `payment_id`),
    UNIQUE INDEX `card_number_UNIQUE` (`card_number` ASC),
    CONSTRAINT `fk_Payment_Order`
        FOREIGN KEY (`order_id`)
        REFERENCES `order` (`order_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

select * from book;
