CREATE SCHEMA `customer_schema` ;


CREATE TABLE `customer_schema`.`customer` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `customer_status` INT NULL,
  `customer_firstname` VARCHAR(100) NULL,
  `customer_lastname` VARCHAR(100) NULL,
  `created` DATETIME NULL,
  `customer_address` VARCHAR(200) NULL,
  `customer_phone` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC));


CREATE TABLE `customer_schema`.`customer_status` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `status_name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `customer_schema`.`customer_comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `customer_id` INT NULL,
  `comment_text` VARCHAR(3000) NULL,
  PRIMARY KEY (`id`));


ALTER TABLE `customer_schema`.`customer_comment` 
ADD COLUMN `created` DATETIME NULL AFTER `comment_text`;


ALTER TABLE `customer_schema`.`customer_comment` 
ADD INDEX `fk_customer_id_idx` (`customer_id` ASC);

ALTER TABLE `customer_schema`.`customer_comment` 
ADD CONSTRAINT `fk_customer_id`
  FOREIGN KEY (`customer_id`)
  REFERENCES `customer_schema`.`customer` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `customer_schema`.`customer` 
ADD INDEX `fk_customer_status_idx` (`customer_status` ASC);

ALTER TABLE `customer_schema`.`customer` 
ADD CONSTRAINT `fk_customer_status`
  FOREIGN KEY (`customer_status`)
  REFERENCES `customer_schema`.`customer_status` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;


DROP TRIGGER IF EXISTS `customer_schema`.`customer_BEFORE_INSERT`;

DELIMITER $$
USE `customer_schema`$$
CREATE DEFINER = CURRENT_USER TRIGGER `customer_schema`.`customer_BEFORE_INSERT` BEFORE INSERT ON `customer` FOR EACH ROW
BEGIN
	SET NEW.created = NOW();
END$$
DELIMITER ;


DROP TRIGGER IF EXISTS `customer_schema`.`customer_comment_BEFORE_INSERT`;

DELIMITER $$
USE `customer_schema`$$
CREATE DEFINER = CURRENT_USER TRIGGER `customer_schema`.`customer_comment_BEFORE_INSERT` BEFORE INSERT ON `customer_comment` FOR EACH ROW
BEGIN
	SET NEW.created = NOW();
END$$
DELIMITER ;


ALTER TABLE `customer_schema`.`customer_status` 
CHANGE COLUMN `id` `id` INT(11) GENERATED ALWAYS AS () VIRTUAL ,
ADD UNIQUE INDEX `id_UNIQUE` (`id` ASC);


INSERT INTO `customer_schema`.`customer_status` (`id`, `status_name`) VALUES ('1', 'prospective'); 
INSERT INTO `customer_schema`.`customer_status` (`id`, `status_name`) VALUES ('2', 'current'); 
INSERT INTO `customer_schema`.`customer_status` (`id`, `status_name`) VALUES ('3', 'non-active');
