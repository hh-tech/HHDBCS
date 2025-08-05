
--CREATE SEQUENCE sequence //创建序列名称
--[INCREMENT BY n] //递增的序列值是 n 如果 n 是正数就递增,如果是负数就递减 默认是 1
--[START WITH n] //开始的值,递增默认是 minvalue 递减是 maxvalue
--[{MAXVALUE n | NOMAXVALUE}] //最大值  
--[{MINVALUE n | NOMINVALUE}] //最小值
--[{CYCLE | NOCYCLE}] //循环/不循环
--[{CACHE n | NOCACHE}];//分配并存入到内存中


--创建序列
CREATE SEQUENCE SEQ_TEST;

--创建序列
CREATE SEQUENCE SEQ_TEST
minvalue 1
maxvalue 999999
start with 1
increment by 1
cache 20;

--序列调用 产生一个新的序列
select seq_test.nextval from dual
--查看当前序列的值
select seq_test.currval from dual
