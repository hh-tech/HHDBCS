--查看表结构
DESCRIBE tb_emp1;
des tb_emp1;
--查看表的详细信息
SHOW CREATE TABLE tb_emp1;

--数据库表添加字段ALTER TABLE <表名> ADD <新字段名><数据类型>[约束条件];
ALTER TABLE student ADD age INT(4);

--删除表
DROP TABLE tb_emp1;

--新建表
create table t_bookType(
	id int primary key auto_increment,
	bookTypeName varchar(20) not null,
	bookTypeDesc varchar(200) not null
);
