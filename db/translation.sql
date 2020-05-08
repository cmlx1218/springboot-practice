# 2020-5-8 学生表
DROP TABLE IF EXISTS `transaction_student`;
CREATE TABLE transaction_student
(
    id       BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    userName VARCHAR(20) NOT NULL COMMENT '用户名',
    sex      TINYINT(1) NOT NULL COMMENT '性别',
    hobby    VARCHAR(20) NOT NULL COMMENT '性别',
    address  VARCHAR(100) NOT NULL COMMENT '住址',
    grade    INT(5) NOT NULL COMMENT '年级',
    clazz    INT(5) NOT NULL COMMENT '班级',
    PRIMARY KEY (`id`),
    KEY `grade` (`grade`),
    KEY `clazz` (`clazz`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生表';