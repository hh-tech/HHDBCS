##使用模版根据用户填写的仓库数量和地区比例生成数据
##请将该用例执行到文件使用！

#set($warehouse = 1)  	##仓库数量
#set($ratio = 100000)  		##仓库数量与地区比例

##创建表格
create table bmsql_warehouse (
  w_id        integer   not null,
  w_ytd       decimal(12,2),
  w_name      varchar(10),
  w_street_1  varchar(20),
  w_street_2  varchar(20),
  w_city      varchar(20),
  w_state     char(2),
  w_zip       char(9)
);

create table bmsql_district (
  d_w_id       integer       not null,
  d_id         integer       not null,
  d_ytd        decimal(12,2),
  d_next_o_id  integer,
  d_name       varchar(10),
  d_street_1   varchar(20),
  d_street_2   varchar(20),
  d_city       varchar(20),
  d_state      char(2),
  d_zip        char(9)
);

##生成数据
#foreach( $item  in [1..$warehouse] )
	##生成仓库数据
	INSERT INTO bmsql_warehouse ( w_id, w_name, w_street_1, w_street_2, w_city, w_state, w_zip, w_ytd)
	VALUES ($item, $item, $item, $item, $item, '1', $item, '300000.0');

	##生成地区数据
	#set($w_id = $item)
	#foreach( $item in [1..$ratio] )
		INSERT INTO bmsql_district (d_id, d_w_id, d_name, d_street_1, d_street_2,d_city, d_state, d_zip, d_ytd, d_next_o_id)
		VALUES ($item, $w_id, $item, $item, $item, $item, '1', $item, '30000.0', '3001');
	#end
#end

##创建索引
alter table bmsql_warehouse add constraint bmsql_warehouse_pkey
  primary key (w_id);

alter table bmsql_district add constraint bmsql_district_pkey
  primary key (d_w_id, d_id);

alter table bmsql_district add constraint d_warehouse_fkey
    foreign key (d_w_id)
    references bmsql_warehouse (w_id);


