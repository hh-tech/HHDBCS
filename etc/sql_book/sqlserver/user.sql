--创建登录名
--Windows Authentication
create login [ReportLogin] from windows with default_database = [master],default_language = [us_english]
--SQL Server Authentication
create login [ReportLogin] with password = N'123456',default_database = [master],default_language = [us_english]
--创建用户
create user [ReportUser] for login [ReportLogin] with default_schema = dbo;
--将test加入 db_owner角色，授予dbowner角色权限
exec sp_addrolemember 'db_owner', 'ReportUser'  


--查看数据库用户
select * from sys.database_principals where type_desc='SQL_USER';

--查询可登录用户
SELECT dp.name as UserName, dp.type_desc as UserType, sp.name as LoginName, sp.type_desc as LoginType FROM sys.database_principals dp JOIN sys.server_principals sp ON dp.principal_id = sp.principal_id
order by UserType;

--查询当前数据库等数据库用户名对应的登录名
SELECT DP.name as[user_name],SP.name as [logion_name] 
FROM sys.database_principals DP ,sys.server_principals SP 
WHERE SP.sid = DP.sid 


--查询权限
SELECT
grantor.name as GrantorName, dp.state_desc as StateDesc, dp.class_desc as ClassDesc, dp.permission_name as PermissionName ,
OBJECT_NAME(major_id) as ObjectName, GranteeName = grantee.name
FROM sys.database_permissions dp
JOIN sys.database_principals grantee on dp.grantee_principal_id = grantee.principal_id
JOIN sys.database_principals grantor on dp.grantor_principal_id = grantor.principal_id