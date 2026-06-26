-- TideTempo Database Schema
-- Generated based on application source code

CREATE DATABASE IF NOT EXISTS `tidetempo`;
USE `tidetempo`;

-- -----------------------------------------------------
-- Table `gender`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gender` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL
);

-- -----------------------------------------------------
-- Table `status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `status` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL
);

-- -----------------------------------------------------
-- Table `priority`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `priority` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL
);

-- -----------------------------------------------------
-- Table `project_template`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `project_template` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL
);

-- -----------------------------------------------------
-- Table `payment_schedule`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `payment_schedule` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL
);

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `f_name` VARCHAR(50) NOT NULL,
    `l_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `mobile` VARCHAR(20),
    `gender_id` INT,
    `hourly_rate` DECIMAL(10, 2),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`gender_id`) REFERENCES `gender`(`id`) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- Table `client`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `client` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `company` VARCHAR(100),
    `email` VARCHAR(100),
    `phone` VARCHAR(20),
    `user_id` INT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Table `project`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `project` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(150) NOT NULL,
    `description` TEXT,
    `priority_id` INT,
    `user_id` INT,
    `client_id` INT,
    `status_id` INT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`priority_id`) REFERENCES `priority`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`client_id`) REFERENCES `client`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`status_id`) REFERENCES `status`(`id`) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- Table `contract`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `contract` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `date` DATE,
    `project_template_id` INT,
    `client_id` INT,
    `project_id` INT,
    `payment_schedule_id` INT,
    `total_amount` DECIMAL(12, 2),
    `hourly_rate` DECIMAL(10, 2),
    `estimated_hours` DECIMAL(10, 2),
    `number_of_revisions` INT,
    `cancellation_policy` TEXT,
    `intellectual_policy` TEXT,
    `status_id` INT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`project_template_id`) REFERENCES `project_template`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`client_id`) REFERENCES `client`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`payment_schedule_id`) REFERENCES `payment_schedule`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`status_id`) REFERENCES `status`(`id`) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- Table `task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(150) NOT NULL,
    `start_time` DATETIME,
    `end_time` DATETIME,
    `project_id` INT,
    `status_id` INT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`status_id`) REFERENCES `status`(`id`) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- Table `time_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `time_log` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT,
    `task_id` INT,
    `minutes` INT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`task_id`) REFERENCES `task`(`id`) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Insert Default Master Data
-- -----------------------------------------------------
INSERT INTO `gender` (`name`) VALUES ('Male'), ('Female'), ('Other');
INSERT INTO `status` (`name`) VALUES ('Pending'), ('In Progress'), ('Completed'), ('Cancelled');
INSERT INTO `priority` (`name`) VALUES ('Low'), ('Medium'), ('High'), ('Urgent');
INSERT INTO `project_template` (`name`) VALUES ('Web Development'), ('Mobile App'), ('SEO Optimization'), ('UI/UX Design');
INSERT INTO `payment_schedule` (`name`) VALUES ('Hourly'), ('Fixed Price'), ('Milestone Based');
