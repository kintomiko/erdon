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
DROP TABLE IF EXISTS `voice`;

CREATE TABLE `voice` (
  `id` int auto_increment primary key,
  `wordsName` varchar(255) null,
  `wc` FLOAT,
  `wp` varchar(255) null,
  `data` MEDIUMBLOB,
  `format` varchar(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```