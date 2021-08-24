--查询hhdb数据库大小
SELECT hh_size_pretty(hh_database_size('hhdb')) AS 大小;
--列出所有数据库
SELECT * from hh_database;		
--查询数据库版本
select version();
--查看数据库连接
SELECT hh_stat_get_backend_pid(s.backendid) AS procpid,
       hh_stat_get_backend_activity(s.backendid) AS current_query
    FROM (SELECT hh_stat_get_backend_idset() AS backendid) AS s;
    
    
    