--查询当前schema
select current schema from sysibm.sysdummy1 
select current schema from sysibm.dual

--切换当前模式为toms
set current schema toms

--查询已有的schema
select SCHEMANAME,owner,CREATE_TIME from syscat.schemata


