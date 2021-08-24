--切换到数据库
use databasename;
--查看数据库
SHOW DATABASES;
--创建数据库
CREATE DATABASE test_db;
CREATE DATABASE IF NOT EXISTS test_db;
--查看创建定义
 SHOW CREATE DATABASE test_db;
--修改数据库字符集
 ALTER DATABASE test_db DEFAULT CHARACTER SET gb2312 DEFAULT COLLATE gb2312_chinese_ci;
--删除数据库‘
DROP DATABASE test_db;