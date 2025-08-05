--创建模式
create schema myschema;
--删除一个为空的模式（其中的所有对象已经被删除）
DROP SCHEMA myschema;
--删除一个模式以及其中包含的所有对象
DROP SCHEMA myschema CASCADE;
--切换模式到myschema
SET search_path TO myschema;
