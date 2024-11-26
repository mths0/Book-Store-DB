-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';



-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Category` (
  `cate_id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`cate_id`),
  UNIQUE INDEX `cate_id_UNIQUE` (`cate_id` ASC) VISIBLE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Book` (
  `book_id` INT NOT NULL,
  `title` VARCHAR(45) NOT NULL,
  `author` VARCHAR(45) NOT NULL,
  `isbn` VARCHAR(45) NOT NULL,
  `price` FLOAT NOT NULL,
  PRIMARY KEY (`book_id`),
  UNIQUE INDEX `book_id_UNIQUE` (`book_id` ASC) VISIBLE)
ENGINE = InnoDB;



-- -----------------------------------------------------
-- Table `mydb`.`Customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Customer` (
  `customer_id` INT NOT NULL COMMENT '\n',
  `Fname` VARCHAR(45) NOT NULL,
  `Lname` VARCHAR(45) NULL,
  `address` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE INDEX `customer_id_UNIQUE` (`customer_id` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Order` (
  `order_id` INT NOT NULL,
  `order_date` DATE NOT NULL,
  `total_price` FLOAT NULL,
  `Customer_id` INT NOT NULL,
  PRIMARY KEY (`order_id`),
  INDEX `fk_Order_Customer1_idx` (`Customer_id` ASC) VISIBLE,
  UNIQUE INDEX `order_id_UNIQUE` (`order_id` ASC) VISIBLE,
  CONSTRAINT `fk_Order_Customer1`
    FOREIGN KEY (`Customer_id`)
    REFERENCES `mydb`.`Customer` (`customer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Customer_profile`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Customer_profile` (
  `history` VARCHAR(45) NULL,
  `cart` VARCHAR(45) NULL,
  `DateOfBirth` DATE NOT NULL,
  `Preferences` VARCHAR(45) NULL,
  `Customer_id` INT NOT NULL,
  INDEX `fk_Customer_profile_Customer_idx` (`Customer_id` ASC) VISIBLE,
  PRIMARY KEY (`Customer_id`),
  CONSTRAINT `fk_Customer_profile_Customer`
    FOREIGN KEY (`Customer_id`)
    REFERENCES `mydb`.`Customer` (`customer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;





-- -----------------------------------------------------
-- Table `mydb`.`Category_has_Book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Category_has_Book` (
  `Category_id1` INT NOT NULL,
  `Book_id1` INT NOT NULL,
  INDEX `fk_Category_has_Book_Category2_idx` (`Category_id1` ASC) VISIBLE,
  INDEX `fk_Category_has_Book_Book2_idx` (`Book_id1` ASC) VISIBLE,
  CONSTRAINT `fk_Category_has_Book_Category2`
    FOREIGN KEY (`Category_id1`)
    REFERENCES `mydb`.`Category` (`cate_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Category_has_Book_Book2`
    FOREIGN KEY (`Book_id1`)
    REFERENCES `mydb`.`Book` (`book_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Order_has_Book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Order_has_Book` (
  `Book_id1` INT NOT NULL,
  `Order_id1` INT NOT NULL,
  INDEX `fk_Order_has_Book_Book2_idx` (`Book_id1` ASC) VISIBLE,
  INDEX `fk_Order_has_Book_Order2_idx` (`Order_id1` ASC) VISIBLE,
  CONSTRAINT `fk_Order_has_Book_Book2`
    FOREIGN KEY (`Book_id1`)
    REFERENCES `mydb`.`Book` (`book_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Order_has_Book_Order2`
    FOREIGN KEY (`Order_id1`)
    REFERENCES `mydb`.`Order` (`order_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
