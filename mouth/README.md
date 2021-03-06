# start mysql

`docker run --name erdon-mysql -p 3307:3306 -e MYSQL_ROOT_PASSWORD=123123 -d mysql`

# init db

```sql
drop database if exists erdon;

create database erdon;


GRANT ALL PRIVILEGES ON *.* TO 'erdon'@'localhost' IDENTIFIED BY 'erdon' WITH GRANT OPTION;

GRANT ALL PRIVILEGES ON *.* TO 'erdon'@'%' IDENTIFIED BY 'erdon' WITH GRANT OPTION;

FLUSH PRIVILEGES;
```

# create voice table

```sql
DROP TABLE IF EXISTS `person`;

CREATE TABLE `person` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(255),
  `sex` varchar(32)
);


DROP TABLE IF EXISTS `fragment`;
DROP TABLE IF EXISTS `clip`;
DROP TABLE IF EXISTS `word`;

CREATE TABLE `fragment` (
  `id` int auto_increment primary key,
  `clip_id` int not null,
  `person_id` int not null,
  `word_id` varchar(255) null,
  `wc` FLOAT,
  `wp` varchar(255) null,
  `data` MEDIUMBLOB,
  INDEX FK_PERSON_ID (person_id),
  INDEX FK_WORD_ID (word_id),
  INDEX FK_CLIP_ID (clip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `word` (
  `id` int auto_increment primary key,
  `name` varchar(255),
  `pronunciation` varchar(255),
  INDEX PRONUNCIATION (pronunciation),
  INDEX NAME (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `clip` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `format` varchar(255),
  `name` varchar(255),
  `data` LONGBLOB
);

```