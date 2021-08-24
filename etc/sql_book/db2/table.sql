--查看所有表信息
select * from sysibm.systables where type=’T’ and creator=’DB2ADMIN’ ORDER BY NAME
--创建表
create table T_flow_step_def(
Step_no int not null, --流程步骤ID 
Step_name varchar(30) not null, --流程步骤名称 
Step_des varchar(64) not null, --流程步骤描述
Limit_time int not null, --时限
URL varchar(64) not null
)

--删除表
drop table T_flow_step_def


