--查询postgre数据库大小
SELECT pg_size_pretty(pg_database_size('postgre')) AS 大小;
--列出所有数据库
SELECT * from pg_database;		
--查询数据库版本
select version();
--查看数据库连接
SELECT pg_stat_get_backend_pid(s.backendid) AS procpid,
       pg_stat_get_backend_activity(s.backendid) AS current_query
    FROM (SELECT pg_stat_get_backend_idset() AS backendid) AS s;
    
    
    