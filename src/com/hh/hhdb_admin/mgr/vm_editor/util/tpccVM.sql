##使用模版根据用户填写的仓库数量,模拟生成TPCC测试表以及数据
##请将该用例执行到文件使用！

#set($Dtype = "oracle") ##数据库类型（oracle,hhdb,postgresql,mysql,sqlserver）
#set($warehouse = 1)  	##仓库数量,请根据需要填写

##创建表格
create table bmsql_config (
  cfg_name    varchar(30) primary key,
  cfg_value   varchar(50)
);

create table bmsql_warehouse (
  w_id        integer   not null,
  w_ytd       decimal(12,2),
  w_tax       decimal(4,4),
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
  d_tax        decimal(4,4),
  d_next_o_id  integer,
  d_name       varchar(10),
  d_street_1   varchar(20),
  d_street_2   varchar(20),
  d_city       varchar(20),
  d_state      char(2),
  d_zip        char(9)
);

create table bmsql_customer (
  c_w_id         integer        not null,
  c_d_id         integer        not null,
  c_id           integer        not null,
  c_discount     decimal(4,4),
  c_credit       char(2),
  c_last         varchar(16),
  c_first        varchar(16),
  c_credit_lim   decimal(12,2),
  c_balance      decimal(12,2),
  c_ytd_payment  decimal(12,2),
  c_payment_cnt  integer,
  c_delivery_cnt integer,
  c_street_1     varchar(20),
  c_street_2     varchar(20),
  c_city         varchar(20),
  c_state        char(2),
  c_zip          char(9),
  c_phone        char(16),
  c_since        timestamp,
  c_middle       char(2),
  c_data         varchar(500)
);

create table bmsql_history (
  hist_id  integer,
  h_c_id   integer,
  h_c_d_id integer,
  h_c_w_id integer,
  h_d_id   integer,
  h_w_id   integer,
  h_date   timestamp,
  h_amount decimal(6,2),
  h_data   varchar(24)
);

create table bmsql_new_order (
  no_w_id  integer   not null,
  no_d_id  integer   not null,
  no_o_id  integer   not null
);

create table bmsql_oorder (
  o_w_id       integer      not null,
  o_d_id       integer      not null,
  o_id         integer      not null,
  o_c_id       integer,
  o_carrier_id integer,
  o_ol_cnt     integer,
  o_all_local  integer,
  o_entry_d    timestamp
);

create table bmsql_order_line (
  ol_w_id         integer   not null,
  ol_d_id         integer   not null,
  ol_o_id         integer   not null,
  ol_number       integer   not null,
  ol_i_id         integer   not null,
  ol_delivery_d   timestamp,
  ol_amount       decimal(6,2),
  ol_supply_w_id  integer,
  ol_quantity     integer,
  ol_dist_info    char(24)
);

create table bmsql_item (
  i_id     integer      not null,
  i_name   varchar(24),
  i_price  decimal(5,2),
  i_data   varchar(50),
  i_im_id  integer
);

create table bmsql_stock (
  s_w_id       integer       not null,
  s_i_id       integer       not null,
  s_quantity   integer,
  s_ytd        integer,
  s_order_cnt  integer,
  s_remote_cnt integer,
  s_data       varchar(50),
  s_dist_01    char(24),
  s_dist_02    char(24),
  s_dist_03    char(24),
  s_dist_04    char(24),
  s_dist_05    char(24),
  s_dist_06    char(24),
  s_dist_07    char(24),
  s_dist_08    char(24),
  s_dist_09    char(24),
  s_dist_10    char(24)
);

##生成配置表数据
INSERT INTO bmsql_config (cfg_name, cfg_value) VALUES ('warehouses', $warehouse);
INSERT INTO bmsql_config (cfg_name, cfg_value) VALUES ('nURandCLast', '169');
INSERT INTO bmsql_config (cfg_name, cfg_value) VALUES ('nURandCC_ID', '237');
INSERT INTO bmsql_config (cfg_name, cfg_value) VALUES ('nURandCI_ID', '6927');

##添加测试数据
#if( $Dtype == "oracle" )
	#set($tim = "to_timestamp(to_char(sysdate,'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')" )
#elseif( $Dtype == "hhdb" || $Dtype == "postgresql" || $Dtype == "mysql" )
	#set($tim = "now()" )
#elseif( $Dtype == "sqlserver" )
	#set($tim = "DEFAULT" )
#else
	#set($tim = "null" )
#end
#foreach( $item in [1..$warehouse] )
	##生成仓库数据
	#set($w_tax = $item / 10000.0)
	INSERT INTO bmsql_warehouse ( w_id, w_name, w_street_1, w_street_2, w_city, w_state, w_zip, w_tax, w_ytd)
	VALUES ($item, $item, $item, $item, $item, '1', $item,$w_tax , '300000.0');

	##生成地区数据
	#set($w_id = $item)
	#foreach( $item in [1..10] )
		#set($d_tax = $item / 10000.0)
		INSERT INTO bmsql_district (d_id, d_w_id, d_name, d_street_1, d_street_2,d_city, d_state, d_zip, d_tax, d_ytd, d_next_o_id)
		VALUES ($item, $w_id, $item, $item, $item, $item, '1', $item, $d_tax, '30000.0', '3001');

		#set($d_id = $item)
		#foreach( $item in [1..3000] )

			##生成用户数据
			#set($c_discount = $item / 10000.0)
			INSERT INTO bmsql_customer (
		  	c_id, c_d_id, c_w_id, c_first, c_middle, c_last,c_street_1, c_street_2, c_city, c_state, c_zip,
		 	c_phone, c_since, c_credit, c_credit_lim, c_discount,c_balance, c_ytd_payment, c_payment_cnt,c_delivery_cnt, c_data)
			VALUES ($item, $d_id, $w_id, $item, 'OE', $item, $item, $item, $item, '1', $item, $item, $tim, 'GC', '50000.00',$c_discount, '-10.00', '10', $item, $item, $item);

			##生成订单数据
			INSERT INTO bmsql_oorder (o_id, o_d_id, o_w_id, o_c_id, o_entry_d,o_carrier_id, o_ol_cnt, o_all_local)
			VALUES ($item, $d_id, $w_id, $item, $tim, $item, $item, 1);

			##生成订单状态数据
			INSERT INTO bmsql_order_line (ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id,ol_supply_w_id, ol_delivery_d, ol_quantity,ol_amount, ol_dist_info)
			VALUES ($item, $d_id, $w_id, $item, $item, $w_id, $tim, 5, 0.00, $item);

			##生成历史订单数据
			#set($hist_id = ($w_id - 1) * 30000 + ($d_id - 1) * 3000 + $item)
			INSERT INTO bmsql_history (hist_id, h_c_id, h_c_d_id, h_c_w_id, h_d_id, h_w_id,h_date, h_amount, h_data)
			VALUES ($hist_id, $item, $d_id, $w_id, $d_id, $w_id, $tim, 10.00, $item);

			##生成新订单数据
			#if( $item >= 2101 )
				INSERT INTO bmsql_new_order (no_o_id, no_d_id, no_w_id)
				VALUES ($item, $d_id, $w_id);
			#end
		#end
	#end

	##生成库存数据
	#foreach( $item in [1..100000] )
		#set($i_price = $item / 10000.0)
		INSERT INTO bmsql_stock (s_i_id, s_w_id, s_quantity, s_dist_01, s_dist_02,s_dist_03, s_dist_04, s_dist_05, s_dist_06,
		s_dist_07, s_dist_08, s_dist_09, s_dist_10,s_ytd, s_order_cnt, s_remote_cnt, s_data)
		VALUES ($item, $w_id, $item, $item, $item, $item, $item, $item, $item, $item, $item, $item, $item, 0, 0, 0, $item);
	#end
#end

##生成商品数据
#foreach( $item in [1..100000] )
	#set($i_price = $item / 10000.0)
	INSERT INTO bmsql_item (i_id, i_im_id, i_name, i_price, i_data)
	VALUES ($item, $item, $item, $i_price, $item);
#end

##创建索引
alter table bmsql_warehouse add constraint bmsql_warehouse_pkey
  primary key (w_id);

alter table bmsql_district add constraint bmsql_district_pkey
  primary key (d_w_id, d_id);

alter table bmsql_customer add constraint bmsql_customer_pkey
  primary key (c_w_id, c_d_id, c_id);

alter table bmsql_oorder add constraint bmsql_oorder_pkey
  primary key (o_w_id, o_d_id, o_id);

alter table bmsql_new_order add constraint bmsql_new_order_pkey
  primary key (no_w_id, no_d_id, no_o_id);

alter table bmsql_order_line add constraint bmsql_order_line_pkey
  primary key (ol_w_id, ol_d_id, ol_o_id, ol_number);

alter table bmsql_stock add constraint bmsql_stock_pkey
  primary key (s_w_id, s_i_id);

alter table bmsql_item add constraint bmsql_item_pkey
  primary key (i_id);

alter table bmsql_district add constraint d_warehouse_fkey
    foreign key (d_w_id)
    references bmsql_warehouse (w_id);

alter table bmsql_customer add constraint c_district_fkey
    foreign key (c_w_id, c_d_id)
    references bmsql_district (d_w_id, d_id);

alter table bmsql_history add constraint h_customer_fkey
    foreign key (h_c_w_id, h_c_d_id, h_c_id)
    references bmsql_customer (c_w_id, c_d_id, c_id);
alter table bmsql_history add constraint h_district_fkey
    foreign key (h_w_id, h_d_id)
    references bmsql_district (d_w_id, d_id);

alter table bmsql_new_order add constraint no_order_fkey
    foreign key (no_w_id, no_d_id, no_o_id)
    references bmsql_oorder (o_w_id, o_d_id, o_id);

alter table bmsql_oorder add constraint o_customer_fkey
    foreign key (o_w_id, o_d_id, o_c_id)
    references bmsql_customer (c_w_id, c_d_id, c_id);

alter table bmsql_order_line add constraint ol_order_fkey
    foreign key (ol_w_id, ol_d_id, ol_o_id)
    references bmsql_oorder (o_w_id, o_d_id, o_id);
alter table bmsql_order_line add constraint ol_stock_fkey
    foreign key (ol_supply_w_id, ol_i_id)
    references bmsql_stock (s_w_id, s_i_id);

alter table bmsql_stock add constraint s_warehouse_fkey
    foreign key (s_w_id)
    references bmsql_warehouse (w_id);
alter table bmsql_stock add constraint s_item_fkey
    foreign key (s_i_id)
    references bmsql_item (i_id);

