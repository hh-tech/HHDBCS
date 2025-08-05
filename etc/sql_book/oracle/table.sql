--新建表
create table classinfo(
   classid number(2) primary key,
   classname varchar(10) not null       
)

--查询表信息
select * from user_tab_columns where Table_Name='TEST' order by column_name
--当前用户的表 
select table_name from user_tables
--所有用户的表 
select table_name from all_tables
--包括系统表
select table_name from dba_tables
--重命名表中的一列
ALTER TABLE xxx RENAME COLUMN old_name TO new_name
--清空表
TRUNCATE TABLE xxx
--删除表
drop table tablename cascade constraints
drop table tablename
--查询表列信息
select * from user_tab_columns where Table_Name='TEST' order by column_name


