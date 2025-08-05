--创建一个序列
CREATE SEQUENCE xxx_no_seql INCREMENT BY 1 maxvalue 99999999 START 10000000;
--设置序列从20000001开始 修改 两种方法都可
SELECT setval('xxx_no_seql',20000001);
alter sequence xxx_no_seql restart with 20000001
--使用
SELECT nextval('xxx_no_seql');
--查看 r =普通表， i =索引，S =序列，v =视图，m =物化视图， c =复合类型，t = TOAST表，f =外部表
select * from pg_class where relkind='S';
--删除
drop sequence xxx_no_seql;


--函数					描述
--currval(regclass)	返回最近一次用 nextval 获取的指定序列的数值
--nextval(regclass)	递增序列并返回新值
--setval(regclass, bigint)	设置序列的当前数值
--setval(regclass, bigint, boolean)	设置序列的当前数值以及 is_called 标志
