--创建用户
CREATE USER 'test1'@'localhost' IDENTIFIED BY '123456';
--刷新权限
FLUSH PRIVILEGES;
--重命名用户RENAME USER <旧用户> TO <新用户>
RENAME USER 'test1'@'localhost' TO 'testUser1'@'localhost';
--删除用户
DROP USER 'test1'@'localhost';
--查看用户信息基本权限
SELECT * FROM mysql.user;
--用户授权
GRANT SELECT,INSERT ON *.* TO 'testUser'@'localhost' IDENTIFIED BY 'testPwd' WITH GRANT OPTION;
--取消用户授权
REVOKE INSERT ON *.* FROM 'testUser'@'localhost';

--修改用户密码
set password for username @localhost = password(newpwd);