--列出表空间
SELECT spcname AS 名称,
				  pg_catalog.pg_get_userbyid(spcowner) AS 拥有者,
				  pg_catalog.pg_tablespace_location(oid) AS 所在地
			FROM pg_catalog.pg_tablespace
			ORDER BY 1;
			
			
--创建表空间
create tablespace tablespacename location '/data/t1';
--创建数据库时可以指定默认表空间也可以修改默认表空间
create database db1 tablespace tablespacename;
--创建数据库时可以指定默认表空间也可以修改默认表空间
alter database db1 set tablespace tablespacename;
--表空间下不单有表还有索引约束等，在创建这类对象时同样可以指定表空间
create table tb1(id int) tablespace tablespacename;
create index idx on tb1(id) tablespace tablespacename;