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
    `status_id` INT DEFAULT 1,
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
    `estimated_hours` DOUBLE DEFAULT 0,
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

-- -----------------------------------------------------
-- Insert Sample Data
-- Demo login: kasun.perera@example.com / Test@123
-- (passwords are stored as plain text, matching the app's current login logic)
-- -----------------------------------------------------
INSERT INTO `user` (`id`, `f_name`, `l_name`, `email`, `password`, `mobile`, `gender_id`, `hourly_rate`, `created_at`) VALUES
(1, 'Kasun',  'Perera',     'kasun.perera@example.com',  'Test@123', '0771234567', 1, 2500.00, '2026-10-01 09:00:00'),
(2, 'Nimali', 'Fernando',   'nimali.fernando@example.com', 'Test@123', '0712345678', 2, 3000.00, '2026-10-02 10:30:00');

INSERT INTO `client` (`id`, `name`, `company`, `email`, `phone`, `user_id`, `status_id`, `created_at`) VALUES
(1, 'Saman Wijesinghe',  'Lanka Tech Solutions', 'saman@lankatech.lk',      '0112345678', 1, 1, '2026-10-03 11:00:00'),
(2, 'Dilani Jayasuriya', 'Ceylon Crafts',        'dilani@ceyloncrafts.lk',  '0763456789', 1, 1, '2026-10-05 14:15:00'),
(3, 'Ruwan Silva',       'Island Travels',       'ruwan@islandtravels.lk',  '0812234567', 1, 1, '2026-10-08 09:45:00'),
(4, 'Tharindu Bandara',  'Bandara Holdings',     'tharindu@bandara.lk',     '0705678901', 2, 1, '2026-10-10 16:00:00');

INSERT INTO `project` (`id`, `title`, `description`, `priority_id`, `user_id`, `client_id`, `status_id`, `estimated_hours`, `created_at`) VALUES
(1, 'Company Website Redesign', 'Redesign the corporate website with a responsive layout and a new CMS.', 3, 1, 1, 2, 80, '2026-10-04 10:00:00'),
(2, 'Online Handicraft Store',  'E-commerce store with product catalogue, cart and payment gateway.',    2, 1, 2, 2, 120, '2026-10-06 10:00:00'),
(3, 'Travel Booking Mobile App', 'Android and iOS app for tour package browsing and booking.',           4, 1, 3, 1, 200, '2026-10-09 10:00:00'),
(4, 'SEO Optimization Package', 'On-page and technical SEO audit with monthly reporting.',               1, 2, 4, 3, 40, '2026-10-11 10:00:00');

INSERT INTO `contract` (`id`, `date`, `project_template_id`, `client_id`, `project_id`, `payment_schedule_id`, `total_amount`, `hourly_rate`, `estimated_hours`, `number_of_revisions`, `cancellation_policy`, `intellectual_policy`, `status_id`, `created_at`) VALUES
(1, '2026-10-04', 1, 1, 1, 3, 200000.00, 2500.00, 80.00, 3,
 'Either party may terminate with 14 days written notice. Work completed to date is payable.',
 'All intellectual property transfers to the client upon full payment.', 2, '2026-10-04 12:00:00'),
(2, '2026-10-06', 1, 2, 2, 2, 300000.00, 2500.00, 120.00, 2,
 'Cancellation after kickoff forfeits the 30% advance payment.',
 'Source code ownership transfers to the client upon final payment.', 2, '2026-10-06 12:00:00'),
(3, '2026-10-11', 3, 4, 4, 1, 120000.00, 3000.00, 40.00, 1,
 'Either party may terminate with 7 days written notice.',
 'Reports and deliverables belong to the client after payment.', 3, '2026-10-11 12:00:00');

INSERT INTO `task` (`id`, `title`, `start_time`, `end_time`, `project_id`, `status_id`, `created_at`) VALUES
(1,  'Gather requirements',        '2026-10-05 09:00:00', '2026-10-05 12:00:00', 1, 3, '2026-10-05 08:30:00'),
(2,  'Design homepage mockups',    '2026-10-06 09:00:00', '2026-10-07 13:00:00', 1, 3, '2026-10-05 08:35:00'),
(3,  'Develop responsive layout',  '2026-11-02 09:00:00', NULL,                  1, 2, '2026-10-05 08:40:00'),
(4,  'Set up product catalogue',   '2026-10-12 09:00:00', '2026-10-14 17:00:00', 2, 3, '2026-10-12 08:30:00'),
(5,  'Integrate payment gateway',  '2026-11-03 09:00:00', NULL,                  2, 2, '2026-10-12 08:35:00'),
(6,  'Build shopping cart',        NULL,                  NULL,                  2, 1, '2026-10-12 08:40:00'),
(7,  'Create UI wireframes',       NULL,                  NULL,                  3, 1, '2026-10-15 08:30:00'),
(8,  'Plan app architecture',      NULL,                  NULL,                  3, 1, '2026-10-15 08:35:00'),
(9,  'Technical SEO audit',        '2026-10-13 09:00:00', '2026-10-15 15:00:00', 4, 3, '2026-10-12 08:30:00'),
(10, 'Keyword research report',    '2026-10-16 09:00:00', '2026-10-17 12:00:00', 4, 3, '2026-10-12 08:35:00');

INSERT INTO `time_log` (`user_id`, `task_id`, `minutes`, `created_at`) VALUES
(1, 1, 180, '2026-10-05 12:00:00'),
(1, 2, 300, '2026-10-06 17:00:00'),
(1, 2, 240, '2026-10-07 13:00:00'),
(1, 3, 210, '2026-11-02 13:30:00'),
(1, 3, 150, '2026-11-03 11:30:00'),
(1, 4, 480, '2026-10-13 17:00:00'),
(1, 4, 360, '2026-10-14 15:00:00'),
(1, 5, 120, '2026-11-03 11:00:00'),
(2, 9, 420, '2026-10-13 16:00:00'),
(2, 9, 300, '2026-10-14 14:00:00'),
(2, 10, 270, '2026-10-16 14:30:00');
