--创建用户指定密码
create user user1 identified by password1 default tablespace users;
--给用户指定配额空间
alter user user1 QUOTA UNLIMITED on users;
--授予登录和资源权限
grant connect,resource to user1;
--删除用户和关联对象
drop user user1 cascade;
