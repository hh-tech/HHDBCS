--创建用户
create user db_user1 password '123'; 
 --创建角色，同上一句等价
create role db_user1 password '123' LOGIN; 
 --删除用户
drop user db_user1;  
--修改密码
alter user db_user1 password '123456'; 
--对用户授权
alter user db_user1 createdb createrole; 
--创建角色1
create role db_role1 createdb createrole; 
--给用户1,2赋予角色1,两个用户就拥有了创建数据库和创建角色的权限
grant db_role1 to db_user1,db_user2; 
--从用户1移除角色1，用户不在拥有角色1的权限
revoke db_role1 from db_user1; 

