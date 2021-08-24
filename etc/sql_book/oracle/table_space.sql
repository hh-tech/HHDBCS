--列出所有表空间
SELECT tablespace_name, file_id, file_name, round(bytes / (1024 * 1024), 0) total_space FROM dba_data_files ORDER BY tablespace_name;
--查看用户对应的表空间
select username,default_tablespace from dba_users;
--查看SYSTEM表空间下的所有表
SELECT TABLE_NAME,TABLESPACE_NAME from dba_tables where TABLESPACE_NAME='SYSTEM';
--查询表空间信息
select
a.a1 表空间名称,
c.c2 类型,
c.c3 区管理,
b.b2/1024/1024 表空间大小M,
(b.b2-a.a2)/1024/1024 已使用M,
(a.a2)/1024/1024 未使用M,
substr((b.b2-a.a2)/b.b2*100,1,5) 利用率
from
(select tablespace_name a1, sum(nvl(bytes,0)) a2 from dba_free_space group by tablespace_name) a,
(select tablespace_name b1,sum(bytes) b2 from dba_data_files group by tablespace_name) b,
(select tablespace_name c1,contents c2,extent_management c3 from dba_tablespaces) c
where a.a1=b.b1 and c.c1=b.b1;
--查询表空间文件
SELECT * FROM DBA_DATA_FILES;
--创建表空间
CREATE TABLESPACE ODI  DATAFILE 'D:\ORACLE\PRODUCT\10.2.0\ORADATA\ORCL\ODI.DBF' SIZE 50M AUTOEXTEND ON NEXT 10M PERMANENT EXTENT MANAGEMENT LOCAL;
--向表空间里增加数据文件
ALTER TABLESPACE tablespace_name ADD DATAFILE 'filename' SIZE size;



