USE vueblog;
CREATE TABLE `User` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `username` varchar(64) DEFAULT NULL,
                          `avatar` varchar(255) DEFAULT NULL,
                          `email` varchar(64) DEFAULT NULL,
                          `password` varchar(64) DEFAULT NULL,
                          `status` int(5) NOT NULL,
                          `created` datetime DEFAULT NULL,
                          `last_login` datetime DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `UK_USERNAME` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `Blog` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `user_id` bigint(20) NOT NULL,
                          `title` varchar(255) NOT NULL,
                          `description` varchar(255) NOT NULL,
                          `content` longtext,
                          `created` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                          `status` tinyint(4) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;
INSERT INTO `User` (`id`, `username`, `avatar`, `email`, `password`, `status`, `created`, `last_login`) VALUES ('1', 'Beauuu', 'https://img2.baidu.com/it/u=4244269751,4000533845&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500.jpg', NULL, '81dc9bdb52d04dc20036dbd8313ed055', '0', '2022-08-24 16:00:00', NULL);
