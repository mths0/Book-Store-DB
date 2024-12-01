-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8mb3 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`category` (
  `cate_id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`cate_id`),
  UNIQUE INDEX `cate_id_UNIQUE` (`cate_id` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`book` (
  `book_id` INT NOT NULL,
  `title` VARCHAR(45) NOT NULL,
  `author` VARCHAR(45) NOT NULL,
  `isbn` VARCHAR(45) NOT NULL,
  `price` FLOAT NOT NULL,
  `category_cate_id` INT NOT NULL,
  PRIMARY KEY (`book_id`),
  UNIQUE INDEX `book_id_UNIQUE` (`book_id` ASC) VISIBLE,
  INDEX `fk_Book_category1_idx` (`category_cate_id` ASC) VISIBLE,
  CONSTRAINT `fk_Book_category1`
    FOREIGN KEY (`category_cate_id`)
    REFERENCES `mydb`.`category` (`cate_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`category_has_book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`category_has_book` (
  `Category_id1` INT NOT NULL,
  `Book_id1` INT NOT NULL,
  INDEX `fk_Category_has_Book_Category2_idx` (`Category_id1` ASC) VISIBLE,
  INDEX `fk_Category_has_Book_Book2_idx` (`Book_id1` ASC) VISIBLE,
  CONSTRAINT `fk_Category_has_Book_Book2`
    FOREIGN KEY (`Book_id1`)
    REFERENCES `mydb`.`book` (`book_id`),
  CONSTRAINT `fk_Category_has_Book_Category2`
    FOREIGN KEY (`Category_id1`)
    REFERENCES `mydb`.`category` (`cate_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`customer` (
  `customer_id` INT NOT NULL COMMENT '\\n',
  `Fname` VARCHAR(45) NOT NULL,
  `Lname` VARCHAR(45) NULL DEFAULT NULL,
  `address` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE INDEX `customer_id_UNIQUE` (`customer_id` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`orders` (
  `order_id` INT NOT NULL,
  `order_date` DATE NOT NULL,
  `total_price` FLOAT NULL DEFAULT NULL,
  `Customer_id` INT NOT NULL,
  PRIMARY KEY (`order_id`),
  UNIQUE INDEX `order_id_UNIQUE` (`order_id` ASC) VISIBLE,
  INDEX `fk_Order_Customer1_idx` (`Customer_id` ASC) VISIBLE,
  CONSTRAINT `fk_Order_Customer1`
    FOREIGN KEY (`Customer_id`)
    REFERENCES `mydb`.`customer` (`customer_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`order_has_book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`order_has_book` (
  `Book_id1` INT NOT NULL,
  `Order_id1` INT NOT NULL,
  `quantity` INT NOT NULL,
  INDEX `fk_Order_has_Book_Book2_idx` (`Book_id1` ASC) VISIBLE,
  INDEX `fk_Order_has_Book_Order2_idx` (`Order_id1` ASC) VISIBLE,
  CONSTRAINT `fk_Order_has_Book_Book2`
    FOREIGN KEY (`Book_id1`)
    REFERENCES `mydb`.`book` (`book_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_Order_has_Book_Orders2`
    FOREIGN KEY (`Order_id1`)
    REFERENCES `mydb`.`orders` (`order_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`paymentinformation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`paymentinformation` (
  `payment_id` INT NOT NULL,
  `card_number` VARCHAR(20) NOT NULL,
  `payment_method` VARCHAR(45) NOT NULL,
  `expiry_date` DATE NULL DEFAULT NULL,
  `order_id` INT NOT NULL,
  PRIMARY KEY (`order_id`, `payment_id`),
  UNIQUE INDEX `card_number_UNIQUE` (`card_number` ASC) VISIBLE,
  UNIQUE INDEX `order_id_UNIQUE` (`order_id` ASC) VISIBLE,
  CONSTRAINT `fk_Payment_Orders1`
    FOREIGN KEY (`order_id`)
    REFERENCES `mydb`.`orders` (`order_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
