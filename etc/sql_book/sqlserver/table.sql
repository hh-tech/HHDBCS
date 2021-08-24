--创建表
create table [db_local_table]  
(  
  id  int,  
  name varchar(50),  
  age int,  
  area int  
)
--删除表
drop table db_local_table;



--创建索引
create index idx_name on db_local_table(name);
--删除索引
drop index idxname;