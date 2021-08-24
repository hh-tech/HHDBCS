prompt PL/SQL Developer Export User Objects for user U1@ORCL
prompt Created by Tony on 2021年1月8日
set define off
spool oracle.log

prompt
prompt Creating table ALLTYPE
prompt ======================
prompt
create table ALLTYPE
(
  int_type               INTEGER,
  number_type            NUMBER(22),
  number_type1           NUMBER(6,-3) default 131313,
  number_type2           NUMBER(6,3) default 131.313131313,
  number_type3           NUMBER(3,6) default 0.000131,
  float_type             FLOAT default 1234567890123456789012345678901234567890,
  float_type1            FLOAT(12) default 141414.141414,
  binary_float_type      BINARY_FLOAT,
  binary_double_type     BINARY_DOUBLE,
  char_type              CHAR(10) default 'abc',
  varchar2_type          VARCHAR2(1000),
  nchar_type             NCHAR(20),
  nchar2_type            NVARCHAR2(20),
  long_type              LONG,
  raw_type               RAW(400),
  date_type              DATE,
  date_type1             DATE,
  date_type2             DATE,
  date_type3             DATE,
  timestampe_type        TIMESTAMP(6),
  timestampe0_type       TIMESTAMP(0),
  timestampe_zone_type   TIMESTAMP(6) WITH TIME ZONE,
  timestampe_zone_type1  TIMESTAMP(3) WITH LOCAL TIME ZONE,
  timestampe_nozone_type TIMESTAMP(6),
  interval_type          INTERVAL YEAR(9) TO MONTH
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table ALLTYPE
  is '全数据类型数据生成';
comment on column ALLTYPE.int_type
  is '测试整形数据类型';

prompt
prompt Creating table BMSQL_CONFIG
prompt ===========================
prompt
create table BMSQL_CONFIG
(
  cfg_name  VARCHAR2(30) not null,
  cfg_value VARCHAR2(50)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_CONFIG
  add constraint BMSQL_CONFIG_PKEY primary key (CFG_NAME)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table BMSQL_WAREHOUSE
prompt ==============================
prompt
create table BMSQL_WAREHOUSE
(
  w_id       INTEGER not null,
  w_ytd      NUMBER(12,2),
  w_tax      NUMBER(4,4),
  w_name     VARCHAR2(10),
  w_street_1 VARCHAR2(20),
  w_street_2 VARCHAR2(20),
  w_city     VARCHAR2(20),
  w_state    CHAR(2),
  w_zip      CHAR(9)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_WAREHOUSE
  add constraint BMSQL_WAREHOUSE_PKEY primary key (W_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table BMSQL_DISTRICT
prompt =============================
prompt
create table BMSQL_DISTRICT
(
  d_w_id      INTEGER not null,
  d_id        INTEGER not null,
  d_ytd       NUMBER(12,2),
  d_tax       NUMBER(4,4),
  d_next_o_id INTEGER,
  d_name      VARCHAR2(10),
  d_street_1  VARCHAR2(20),
  d_street_2  VARCHAR2(20),
  d_city      VARCHAR2(20),
  d_state     CHAR(2),
  d_zip       CHAR(9)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_DISTRICT
  add constraint BMSQL_DISTRICT_PKEY primary key (D_W_ID, D_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_DISTRICT
  add constraint D_WAREHOUSE_FKEY foreign key (D_W_ID)
  references BMSQL_WAREHOUSE (W_ID);

prompt
prompt Creating table BMSQL_CUSTOMER
prompt =============================
prompt
create table BMSQL_CUSTOMER
(
  c_w_id         INTEGER not null,
  c_d_id         INTEGER not null,
  c_id           INTEGER not null,
  c_discount     NUMBER(4,4),
  c_credit       CHAR(2),
  c_last         VARCHAR2(16),
  c_first        VARCHAR2(16),
  c_credit_lim   NUMBER(12,2),
  c_balance      NUMBER(12,2),
  c_ytd_payment  NUMBER(12,2),
  c_payment_cnt  INTEGER,
  c_delivery_cnt INTEGER,
  c_street_1     VARCHAR2(20),
  c_street_2     VARCHAR2(20),
  c_city         VARCHAR2(20),
  c_state        CHAR(2),
  c_zip          CHAR(9),
  c_phone        CHAR(16),
  c_since        TIMESTAMP(6),
  c_middle       CHAR(2),
  c_data         VARCHAR2(500)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create index BMSQL_CUSTOMER_IDX1 on BMSQL_CUSTOMER (C_W_ID, C_D_ID, C_LAST, C_FIRST)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_CUSTOMER
  add constraint BMSQL_CUSTOMER_PKEY primary key (C_W_ID, C_D_ID, C_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_CUSTOMER
  add constraint C_DISTRICT_FKEY foreign key (C_W_ID, C_D_ID)
  references BMSQL_DISTRICT (D_W_ID, D_ID);

prompt
prompt Creating table BMSQL_HISTORY
prompt ============================
prompt
create table BMSQL_HISTORY
(
  hist_id  INTEGER,
  h_c_id   INTEGER,
  h_c_d_id INTEGER,
  h_c_w_id INTEGER,
  h_d_id   INTEGER,
  h_w_id   INTEGER,
  h_date   TIMESTAMP(6),
  h_amount NUMBER(6,2),
  h_data   VARCHAR2(24)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_HISTORY
  add constraint H_CUSTOMER_FKEY foreign key (H_C_W_ID, H_C_D_ID, H_C_ID)
  references BMSQL_CUSTOMER (C_W_ID, C_D_ID, C_ID);
alter table BMSQL_HISTORY
  add constraint H_DISTRICT_FKEY foreign key (H_W_ID, H_D_ID)
  references BMSQL_DISTRICT (D_W_ID, D_ID);

prompt
prompt Creating table BMSQL_ITEM
prompt =========================
prompt
create table BMSQL_ITEM
(
  i_id    INTEGER not null,
  i_name  VARCHAR2(24),
  i_price NUMBER(5,2),
  i_data  VARCHAR2(50),
  i_im_id INTEGER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_ITEM
  add constraint BMSQL_ITEM_PKEY primary key (I_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table BMSQL_OORDER
prompt ===========================
prompt
create table BMSQL_OORDER
(
  o_w_id       INTEGER not null,
  o_d_id       INTEGER not null,
  o_id         INTEGER not null,
  o_c_id       INTEGER,
  o_carrier_id INTEGER,
  o_ol_cnt     INTEGER,
  o_all_local  INTEGER,
  o_entry_d    TIMESTAMP(6)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create unique index BMSQL_OORDER_IDX1 on BMSQL_OORDER (O_W_ID, O_D_ID, O_CARRIER_ID, O_ID)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_OORDER
  add constraint BMSQL_OORDER_PKEY primary key (O_W_ID, O_D_ID, O_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_OORDER
  add constraint O_CUSTOMER_FKEY foreign key (O_W_ID, O_D_ID, O_C_ID)
  references BMSQL_CUSTOMER (C_W_ID, C_D_ID, C_ID);

prompt
prompt Creating table BMSQL_NEW_ORDER
prompt ==============================
prompt
create table BMSQL_NEW_ORDER
(
  no_w_id INTEGER not null,
  no_d_id INTEGER not null,
  no_o_id INTEGER not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;
alter table BMSQL_NEW_ORDER
  add constraint BMSQL_NEW_ORDER_PKEY primary key (NO_W_ID, NO_D_ID, NO_O_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;
alter table BMSQL_NEW_ORDER
  add constraint NO_ORDER_FKEY foreign key (NO_W_ID, NO_D_ID, NO_O_ID)
  references BMSQL_OORDER (O_W_ID, O_D_ID, O_ID);

prompt
prompt Creating table BMSQL_STOCK
prompt ==========================
prompt
create table BMSQL_STOCK
(
  s_w_id       INTEGER not null,
  s_i_id       INTEGER not null,
  s_quantity   INTEGER,
  s_ytd        INTEGER,
  s_order_cnt  INTEGER,
  s_remote_cnt INTEGER,
  s_data       VARCHAR2(50),
  s_dist_01    CHAR(24),
  s_dist_02    CHAR(24),
  s_dist_03    CHAR(24),
  s_dist_04    CHAR(24),
  s_dist_05    CHAR(24),
  s_dist_06    CHAR(24),
  s_dist_07    CHAR(24),
  s_dist_08    CHAR(24),
  s_dist_09    CHAR(24),
  s_dist_10    CHAR(24)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_STOCK
  add constraint BMSQL_STOCK_PKEY primary key (S_W_ID, S_I_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_STOCK
  add constraint S_ITEM_FKEY foreign key (S_I_ID)
  references BMSQL_ITEM (I_ID);
alter table BMSQL_STOCK
  add constraint S_WAREHOUSE_FKEY foreign key (S_W_ID)
  references BMSQL_WAREHOUSE (W_ID);

prompt
prompt Creating table BMSQL_ORDER_LINE
prompt ===============================
prompt
create table BMSQL_ORDER_LINE
(
  ol_w_id        INTEGER not null,
  ol_d_id        INTEGER not null,
  ol_o_id        INTEGER not null,
  ol_number      INTEGER not null,
  ol_i_id        INTEGER,
  ol_delivery_d  TIMESTAMP(6),
  ol_amount      NUMBER(6,2),
  ol_supply_w_id INTEGER,
  ol_quantity    INTEGER,
  ol_dist_info   CHAR(24)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_ORDER_LINE
  add constraint BMSQL_ORDER_LINE_PKEY primary key (OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table BMSQL_ORDER_LINE
  add constraint OL_ORDER_FKEY foreign key (OL_W_ID, OL_D_ID, OL_O_ID)
  references BMSQL_OORDER (O_W_ID, O_D_ID, O_ID);
alter table BMSQL_ORDER_LINE
  add constraint OL_STOCK_FKEY foreign key (OL_SUPPLY_W_ID, OL_I_ID)
  references BMSQL_STOCK (S_W_ID, S_I_ID);

prompt
prompt Creating table DBMS_JOB_TABLE
prompt =============================
prompt
create table DBMS_JOB_TABLE
(
  a1 VARCHAR2(500)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table DEP_FOREIGN
prompt ==========================
prompt
create table DEP_FOREIGN
(
  dep_id   INTEGER,
  dep_name VARCHAR2(100) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table DEP_FOREIGN
  add primary key (DEP_ID, DEP_NAME)
  deferrable
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table DEP_FOREIGN
  add unique (DEP_NAME)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table DEP_FOREIGN
  add unique (DEP_ID)
  deferrable initially deferred
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table EMP_MAIN
prompt =======================
prompt
create table EMP_MAIN
(
  emp_id   INTEGER not null,
  depid    INTEGER,
  depname  VARCHAR2(100),
  emp_name VARCHAR2(100),
  salary   INTEGER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create index EMP_INDEX on EMP_MAIN (EMP_ID, EMP_NAME)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create bitmap index EMP_SALARY_INDEX on EMP_MAIN (SALARY)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;
alter table EMP_MAIN
  add primary key (EMP_ID);
alter table EMP_MAIN
  add constraint EMP_UNIQUE unique (EMP_NAME)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  )
  novalidate;
alter table EMP_MAIN
  add constraint INDEX_UNIQUE unique (EMP_ID, EMP_NAME);
alter table EMP_MAIN
  add unique (DEPNAME, EMP_NAME)
  disable
  novalidate;
alter table EMP_MAIN
  add foreign key (DEPNAME)
  references DEP_FOREIGN (DEP_NAME) on delete set null
  deferrable;
alter table EMP_MAIN
  add foreign key (DEPID, DEPNAME)
  references DEP_FOREIGN (DEP_ID, DEP_NAME) on delete cascade;
alter table EMP_MAIN
  add foreign key (DEPID)
  references DEP_FOREIGN (DEP_ID) on delete cascade
  deferrable initially deferred;
alter table EMP_MAIN
  add check (salary > 1000 and emp_id>0);

prompt
prompt Creating table INDEX_TABLE
prompt ==========================
prompt
create table INDEX_TABLE
(
  id        NUMBER(11),
  col_test1 VARCHAR2(50),
  col_test2 VARCHAR2(50)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;
  
prompt
prompt Creating function FUNCTION_TEST_FOR_INDEX
prompt =========================================
prompt
create or replace function function_test_for_index(n1 varchar2) return varchar2 deterministic  is
			BEGIN
			   return n1;
			END;
/

create index CUSTOM_FUN_INDEX_TEST on INDEX_TABLE ("FUNCTION_TEST_FOR_INDEX"(COL_TEST1) DESC, LOWER(COL_TEST2))
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 165;
create index SYS_FUN_INDEX_TEST on INDEX_TABLE (UPPER(COL_TEST1) DESC, LOWER(COL_TEST2))
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;

prompt
prompt Creating table JOB_TABLE
prompt ========================
prompt
create table JOB_TABLE
(
  date_time DATE,
  mark      VARCHAR2(200)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;

prompt
prompt Creating table LOB_EMPTY_TABLE
prompt ==============================
prompt
create table LOB_EMPTY_TABLE
(
  id       INTEGER not null,
  filename VARCHAR2(100),
  content1 CLOB not null,
  content2 BLOB not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table LOB_TABLE
prompt ========================
prompt
create table LOB_TABLE
(
  id               NUMBER(16),
  address_blob     BLOB,
  address_clob     CLOB,
  address_nclob    NCLOB,
  address_long_raw LONG RAW
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table MLOG$_MVIEW_LOG_TAB_1
prompt ====================================
prompt
create table MLOG$_MVIEW_LOG_TAB_1
(
  id              NUMBER,
  snaptime$$      DATE,
  dmltype$$       VARCHAR2(1),
  old_new$$       VARCHAR2(1),
  change_vector$$ RAW(255),
  xid$$           NUMBER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table MLOG$_MVIEW_LOG_TAB_1
  is 'snapshot log for master table U1.MVIEW_LOG_TAB_1';
create index I_MLOG$_MVIEW_LOG_TAB_1 on MLOG$_MVIEW_LOG_TAB_1 (XID$$)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table MLOG$_MVIEW_LOG_TAB_2
prompt ====================================
prompt
create table MLOG$_MVIEW_LOG_TAB_2
(
  m_row$$         VARCHAR2(255),
  snaptime$$      DATE,
  dmltype$$       VARCHAR2(1),
  old_new$$       VARCHAR2(1),
  change_vector$$ RAW(255),
  xid$$           NUMBER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table MLOG$_MVIEW_LOG_TAB_2
  is 'snapshot log for master table U1.MVIEW_LOG_TAB_2';
create index I_MLOG$_MVIEW_LOG_TAB_2 on MLOG$_MVIEW_LOG_TAB_2 (XID$$)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table MLOG$_MVIEW_LOG_TAB_3
prompt ====================================
prompt
create table MLOG$_MVIEW_LOG_TAB_3
(
  id              NUMBER,
  num             NUMBER,
  msg             VARCHAR2(50),
  sequence$$      NUMBER,
  snaptime$$      DATE,
  dmltype$$       VARCHAR2(1),
  old_new$$       VARCHAR2(1),
  change_vector$$ RAW(255),
  xid$$           NUMBER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table MLOG$_MVIEW_LOG_TAB_3
  is 'snapshot log for master table U1.MVIEW_LOG_TAB_3';
create index I_MLOG$_MVIEW_LOG_TAB_3 on MLOG$_MVIEW_LOG_TAB_3 (XID$$)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table MLOG$_MVIEW_LOG_TAB_4
prompt ====================================
prompt
create table MLOG$_MVIEW_LOG_TAB_4
(
  id              NUMBER,
  m_row$$         VARCHAR2(255),
  sequence$$      NUMBER,
  snaptime$$      DATE,
  dmltype$$       VARCHAR2(1),
  old_new$$       VARCHAR2(1),
  change_vector$$ RAW(255),
  xid$$           NUMBER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table MLOG$_MVIEW_LOG_TAB_4
  is 'snapshot log for master table U1.MVIEW_LOG_TAB_4';
create index I_MLOG$_MVIEW_LOG_TAB_4 on MLOG$_MVIEW_LOG_TAB_4 (XID$$)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table MLOG$_MVIEW_LOG_TAB_5
prompt ====================================
prompt
create table MLOG$_MVIEW_LOG_TAB_5
(
  sys_nc_oid$     RAW(16),
  snaptime$$      DATE,
  dmltype$$       VARCHAR2(1),
  old_new$$       VARCHAR2(1),
  change_vector$$ RAW(255),
  xid$$           NUMBER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table MLOG$_MVIEW_LOG_TAB_5
  is 'snapshot log for master table U1.MVIEW_LOG_TAB_5';
create index I_MLOG$_MVIEW_LOG_TAB_5 on MLOG$_MVIEW_LOG_TAB_5 (XID$$)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table MVIEW_LOG_TAB_1
prompt ==============================
prompt
create table MVIEW_LOG_TAB_1
(
  id          NUMBER not null,
  num         NUMBER not null,
  msg         VARCHAR2(50) not null,
  create_date DATE not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;
alter table MVIEW_LOG_TAB_1
  add primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;

prompt
prompt Creating table MVIEW_LOG_TAB_2
prompt ==============================
prompt
create table MVIEW_LOG_TAB_2
(
  id          NUMBER not null,
  num         NUMBER not null,
  msg         VARCHAR2(50) not null,
  create_date DATE not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;
alter table MVIEW_LOG_TAB_2
  add primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;

prompt
prompt Creating table MVIEW_LOG_TAB_3
prompt ==============================
prompt
create table MVIEW_LOG_TAB_3
(
  id          NUMBER not null,
  num         NUMBER not null,
  msg         VARCHAR2(50) not null,
  create_date DATE not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;
alter table MVIEW_LOG_TAB_3
  add primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;

prompt
prompt Creating table MVIEW_LOG_TAB_4
prompt ==============================
prompt
create table MVIEW_LOG_TAB_4
(
  id          NUMBER not null,
  num         NUMBER not null,
  msg         VARCHAR2(50) not null,
  create_date DATE not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;
alter table MVIEW_LOG_TAB_4
  add primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;

prompt
prompt Creating type MVIEW_LOG_TYPE_TEST
prompt =================================
prompt
CREATE OR REPLACE TYPE MVIEW_LOG_TYPE_TEST AS OBJECT (
            id   NUMBER(4),
            num   NUMBER,
            MSG   VARCHAR2(50),
            CREATE_DATE DATE
            );
/

prompt
prompt Creating table MVIEW_LOG_TAB_5
prompt ==============================
prompt
create table MVIEW_LOG_TAB_5
  of MVIEW_LOG_TYPE_TEST
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table MVIEW_TAB
prompt ========================
prompt
create table MVIEW_TAB
(
  tno       VARCHAR2(20) not null,
  tname     VARCHAR2(20) not null,
  tsex      VARCHAR2(10) not null,
  tbirthday DATE,
  prof      VARCHAR2(20),
  depart    VARCHAR2(20) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;

prompt
prompt Creating table MYTAB
prompt ====================
prompt
create table MYTAB
(
  name VARCHAR2(200)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;

prompt
prompt Creating table MiXCapTab
prompt ========================
prompt
create table "MiXCapTab"
(
  "MiXCapTab" NUMBER(10),
  "user"        NUMBER(10),
  "AA/BB"     NUMBER(10),
  "<mytab>"     NUMBER(10)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;

prompt
prompt Creating type OBJECT_TYPE_FOR_ARRAY
prompt ===================================
prompt
CREATE OR REPLACE TYPE OBJECT_TYPE_FOR_ARRAY AS OBJECT(ID NUMBER, NAME VARCHAR2(100));
/

prompt
prompt Creating type VARRAY_TYPE_IN_OBJECT_TYPE
prompt ========================================
prompt
CREATE OR REPLACE TYPE VARRAY_TYPE_IN_OBJECT_TYPE AS VARRAY(5) OF OBJECT_TYPE_FOR_ARRAY;
/

prompt
prompt Creating type OBJECT_TYPE_FOR_ARRAY_IN_ARRAY
prompt ============================================
prompt
CREATE OR REPLACE TYPE OBJECT_TYPE_FOR_ARRAY_IN_ARRAY AS OBJECT(ID NUMBER, NAME  VARRAY_TYPE_IN_OBJECT_TYPE);
/

prompt
prompt Creating type OBJECT_TYPE
prompt =========================
prompt
CREATE OR REPLACE TYPE OBJECT_TYPE AS OBJECT
(
  BREED     VARCHAR2(25),
  NAME      VARCHAR2(25),
  BIRTHDATE DATE
);
/

prompt
prompt Creating type TABLE_TYPE
prompt ========================
prompt
CREATE OR REPLACE TYPE TABLE_TYPE AS TABLE OF OBJECT_TYPE;
/

prompt
prompt Creating type OBJECT_TYPE_AA
prompt ============================
prompt
CREATE OR REPLACE TYPE OBJECT_TYPE_AA AS OBJECT(ID NUMBER, NAME VARCHAR2(6), AGE NUMBER);
/

prompt
prompt Creating type TABLE_TYPE_BB
prompt ===========================
prompt
CREATE OR REPLACE TYPE TABLE_TYPE_BB AS TABLE OF OBJECT_TYPE_AA;
/

prompt
prompt Creating type VARRAY_TYPE_IN_OBJECT_TYPE_1
prompt ==========================================
prompt
CREATE OR REPLACE TYPE VARRAY_TYPE_IN_OBJECT_TYPE_1 AS VARRAY(5) OF OBJECT_TYPE_FOR_ARRAY_IN_ARRAY;
/

prompt
prompt Creating table NESTED_TABLE
prompt ===========================
prompt
create table NESTED_TABLE
(
  c1   VARCHAR2(25),
  c2   TABLE_TYPE,
  c3   TABLE_TYPE_BB,
  name VARRAY_TYPE_IN_OBJECT_TYPE_1
)
nested table C2 store as NESTED_TABLE_TYPE_TAB
nested table C3 store as NESTED_TABLE_TYPE_BB_TAB
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );


prompt
prompt Creating table PACKAGE_TABLE
prompt ============================
prompt
create table PACKAGE_TABLE
(
  empno    NUMBER,
  ename    VARCHAR2(6),
  job      VARCHAR2(8),
  hiredate DATE
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;

prompt
prompt Creating table PARTITION_HASH
prompt =============================
prompt
create table PARTITION_HASH
(
  col NUMBER(8),
  inf VARCHAR2(100)
)
partition by hash (COL, INF)
(
  partition PART01
    tablespace USERS,
  partition PART02
    tablespace USERS,
  partition PART03
    tablespace USERS
);

prompt
prompt Creating table PARTITION_LIST
prompt =============================
prompt
create table PARTITION_LIST
(
  id       NUMBER,
  test_day TIMESTAMP(6)
)
partition by list (ID)
(
  partition SUB_TEST1 values (1)
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
    storage
    (
      initial 8M
      next 1M
      minextents 1
      maxextents unlimited
    ),
  partition sub_test2 values (2, 3, 4)
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
    storage
    (
      initial 8M
      next 1M
      minextents 1
      maxextents unlimited
    ),
  partition sub_test3 values (null)
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
    storage
    (
      initial 8M
      next 1M
      minextents 1
      maxextents unlimited
    ),
  partition SUB_TEST4 values (default)
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
    storage
    (
      initial 8M
      next 1M
      minextents 1
      maxextents unlimited
    )
);

prompt
prompt Creating table PARTITION_RANGE
prompt ==============================
prompt
create table PARTITION_RANGE
(
  id       NUMBER,
  test_day TIMESTAMP(6)
)
partition by range (TEST_DAY)
(
  partition SUB_TEST1 values less than (TIMESTAMP' 2015-01-01 00:00:00')
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
    storage
    (
      initial 8M
      next 1M
      minextents 1
      maxextents unlimited
    ),
  partition SUB_TEST2 values less than (TIMESTAMP' 2016-01-01 00:00:00')
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
    storage
    (
      initial 8M
      next 1M
      minextents 1
      maxextents unlimited
    ),
  partition SUB_TEST3 values less than (MAXVALUE)
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
    storage
    (
      initial 8M
      next 1M
      minextents 1
      maxextents unlimited
    )
);
create index GLOBAL_PARTITION_RANGE_INDEX on PARTITION_RANGE (TEST_DAY)
  nologging  local;
create index LOCAL_PARTITION_RANGE_INDEX on PARTITION_RANGE (ID)
  nologging  local;

prompt
prompt Creating table PERSON
prompt =====================
prompt
create table PERSON
(
  id              INTEGER,
  name            CHAR(50),
  name_english    CHAR(50),
  province        CHAR(30),
  mobile          CHAR(30),
  address         VARCHAR2(1000),
  email           CHAR(100),
  identity_num    CHAR(30),
  creditcard      CHAR(30),
  telephone       CHAR(30),
  birthday        CHAR(30),
  postcode        CHAR(30),
  salary          FLOAT,
  car_plate       CHAR(30),
  company         CHAR(100),
  company_english CHAR(100),
  status_flag     CHAR(30),
  status_flag_1   CHAR(30),
  sex             CHAR(30),
  edu_list        CHAR(30),
  organization    CHAR(30),
  nation_list     CHAR(100),
  comment_list    CHAR(30),
  judge_level     CHAR(30),
  professor_names CHAR(30)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table RUPD$_MVIEW_LOG_TAB_1
prompt ====================================
prompt
create global temporary table RUPD$_MVIEW_LOG_TAB_1
(
  id              NUMBER,
  dmltype$$       VARCHAR2(1),
  snapid          INTEGER,
  change_vector$$ RAW(255)
)
on commit preserve rows;
comment on table RUPD$_MVIEW_LOG_TAB_1
  is 'temporary updatable snapshot log';

prompt
prompt Creating table RUPD$_MVIEW_LOG_TAB_3
prompt ====================================
prompt
create global temporary table RUPD$_MVIEW_LOG_TAB_3
(
  id              NUMBER,
  dmltype$$       VARCHAR2(1),
  snapid          INTEGER,
  change_vector$$ RAW(255)
)
on commit preserve rows;
comment on table RUPD$_MVIEW_LOG_TAB_3
  is 'temporary updatable snapshot log';

prompt
prompt Creating table RUPD$_MVIEW_LOG_TAB_4
prompt ====================================
prompt
create global temporary table RUPD$_MVIEW_LOG_TAB_4
(
  id              NUMBER,
  dmltype$$       VARCHAR2(1),
  snapid          INTEGER,
  change_vector$$ RAW(255)
)
on commit preserve rows;
comment on table RUPD$_MVIEW_LOG_TAB_4
  is 'temporary updatable snapshot log';

prompt
prompt Creating type TYPE_TEST
prompt =======================
prompt
CREATE OR REPLACE TYPE TYPE_TEST AS OBJECT (
  c   NUMBER(4)
);
/

prompt
prompt Creating type type_test_lowercase
prompt =================================
prompt
CREATE OR REPLACE TYPE "type_test_lowercase" AS OBJECT (
  "c1"  TYPE_TEST,
  c2  DATE
);
/

prompt
prompt Creating table SPECIAL_TYPE_TABLE
prompt =================================
prompt
create table SPECIAL_TYPE_TABLE
(
  c3   NUMBER,
  "c4" "type_test_lowercase"
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table SUB_PART_RANGE_HASH_TABLE
prompt ========================================
prompt
create table SUB_PART_RANGE_HASH_TABLE
(
  transaction_id   NUMBER,
  item_id          NUMBER(8) not null,
  item_description VARCHAR2(300),
  transaction_date DATE
)
partition by range (TRANSACTION_DATE)
subpartition by hash (TRANSACTION_ID, ITEM_ID)
(
  partition PART_01 values less than (TO_DATE(' 2016-06-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
  (
    subpartition SYS_SUBP2346 tablespace USERS,
    subpartition SYS_SUBP2347 tablespace USERS,
    subpartition SYS_SUBP2348 tablespace USERS
  ),
  partition PART_02 values less than (TO_DATE(' 2016-12-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
  (
    subpartition SYS_SUBP2349 tablespace USERS,
    subpartition SYS_SUBP2350 tablespace USERS,
    subpartition SYS_SUBP2351 tablespace USERS
  ),
  partition PART_03 values less than (MAXVALUE)
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
  (
    subpartition SYS_SUBP2352 tablespace USERS,
    subpartition SYS_SUBP2353 tablespace USERS,
    subpartition SYS_SUBP2354 tablespace USERS
  )
);

prompt
prompt Creating table SUB_PART_RANGE_LIST_TABLE
prompt ========================================
prompt
create table SUB_PART_RANGE_LIST_TABLE
(
  id        VARCHAR2(32) default sys_guid(),
  datetime  DATE,
  filepath  VARCHAR2(500),
  filestate CHAR(1),
  areacode  VARCHAR2(20),
  fancode   VARCHAR2(20)
)
partition by range (DATETIME) interval (NUMTOYMINTERVAL(1,'YEAR'))
subpartition by list (AREACODE)
(
  partition SP1 values less than (TO_DATE(' 2018-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
  (
    subpartition PART1_0001 values ('0001') tablespace USERS,
    subpartition PART1_0002 values ('0002') tablespace USERS,
    subpartition PART1_0003 values ('0004') tablespace USERS,
    subpartition PART1_0004 values ('0005') tablespace USERS,
    subpartition P1_OTHER values (default) tablespace USERS
  ),
  partition SP2 values less than (TO_DATE(' 2019-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))
    tablespace USERS
    pctfree 10
    initrans 1
    maxtrans 255
  (
    subpartition PART2_0001 values ('0001') tablespace USERS,
    subpartition PART2_0002 values ('0002') tablespace USERS,
    subpartition PART2_0003 values ('0004') tablespace USERS,
    subpartition PART2_0004 values ('0005') tablespace USERS,
    subpartition P2_OTHER values (default) tablespace USERS
  )
);
create index SUB_PART_RANGE_LIST_INDEX on SUB_PART_RANGE_LIST_TABLE (DATETIME, FANCODE)
  nologging  local;

prompt
prompt Creating table SUB_TRIGGER_TABLE
prompt ================================
prompt
create table SUB_TRIGGER_TABLE
(
  empno    NUMBER(4),
  ename    VARCHAR2(10),
  job      VARCHAR2(9),
  mgr      NUMBER(4),
  hiredate DATE,
  sal      NUMBER(7,2),
  comm     NUMBER(7,2),
  deptno   NUMBER(2)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;

prompt
prompt Creating table SYNONYM_TABLE
prompt ============================
prompt
create table SYNONYM_TABLE
(
  dep_id   INTEGER not null,
  dep_name VARCHAR2(100) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;

prompt
prompt Creating table TRIGGER_TABLE
prompt ============================
prompt
create table TRIGGER_TABLE
(
  empno    NUMBER(4),
  ename    VARCHAR2(10),
  job      VARCHAR2(9),
  mgr      NUMBER(4),
  hiredate DATE,
  sal      NUMBER(7,2),
  comm     NUMBER(7,2),
  deptno   NUMBER(2)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;

prompt
prompt Creating type SIMPLE_TYPE
prompt =========================
prompt
CREATE OR REPLACE TYPE SIMPLE_TYPE AS OBJECT (
  test_num   NUMBER(4),
  test_var   VARCHAR2(20),
  test_date  DATE
);
/

prompt
prompt Creating table TYPE_TABLE
prompt =========================
prompt
create table TYPE_TABLE
  of SIMPLE_TYPE
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table TYPE_TABLE modify test_num not null;
alter table TYPE_TABLE
  add primary key (TEST_NUM)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table VIEW_TABLE
prompt =========================
prompt
create table VIEW_TABLE
(
  country_id   CHAR(2) not null,
  country_name VARCHAR2(40),
  region_id    NUMBER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255;
alter table VIEW_TABLE
  add constraint COUNTRY_C_ID_PK primary key (COUNTRY_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255;

prompt
prompt Creating sequence BMSQL_HIST_ID_SEQ
prompt ===================================
prompt
create sequence BMSQL_HIST_ID_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 20;

prompt
prompt Creating sequence SEQUENCE_TEST
prompt ===============================
prompt
create sequence SEQUENCE_TEST
minvalue 1
maxvalue 999999999999999999999999999
start with 2
increment by 1
nocache;

prompt
prompt Creating synonym SYNONYM_SCOTT_EMP
prompt ==================================
prompt
create or replace synonym SYNONYM_SCOTT_EMP
  for SCOTT.SYNONYM_SCOTT_EMP;

prompt
prompt Creating synonym SYNONYM_TEST
prompt =============================
prompt
create or replace synonym SYNONYM_TEST
  for TEST.SYNONYM_TABLE;

prompt
prompt Creating view VIEW_TEST
prompt =======================
prompt
CREATE OR REPLACE FORCE VIEW VIEW_TEST AS
SELECT "COUNTRY_ID","COUNTRY_NAME","REGION_ID" FROM VIEW_TABLE;

prompt
prompt Creating view VIEW_TEST_01
prompt ==========================
prompt
CREATE OR REPLACE FORCE VIEW VIEW_TEST_01 AS
SELECT "COUNTRY_ID","COUNTRY_NAME","REGION_ID" FROM VIEW_TEST;

prompt
prompt Creating view VIEW_TEST_02
prompt ==========================
prompt
CREATE OR REPLACE FORCE VIEW VIEW_TEST_02 AS
SELECT "COUNTRY_ID","COUNTRY_NAME","REGION_ID" FROM VIEW_TEST_01;


prompt
prompt Creating materialized view MVIEW_TEST
prompt =====================================
prompt
CREATE MATERIALIZED VIEW MVIEW_TEST
REFRESH FORCE ON DEMAND
AS
SELECT * FROM MVIEW_TAB;

prompt
prompt Creating package PACKAGE_TEST
prompt =============================
prompt
CREATE OR REPLACE PACKAGE PACKAGE_TEST --PG_MYFIRST为包名
    IS
    PROCEDURE SP_EMP_INSERT; --声明一个过程
   FUNCTION F_GETENAME(I_EMPNO NUMBER) RETURN VARCHAR2; --声明函数
    END PACKAGE_TEST;
/

prompt
prompt Creating type TABLE_TYPE_CC
prompt ===========================
prompt
CREATE OR REPLACE TYPE TABLE_TYPE_CC AS TABLE OF TABLE_TYPE_BB;
/

prompt
prompt Creating type TABLE_TYPE_DD
prompt ===========================
prompt
CREATE OR REPLACE TYPE TABLE_TYPE_DD AS TABLE OF TABLE_TYPE_BB;
/

prompt
prompt Creating type TABLE_TYPE_EE
prompt ===========================
prompt
CREATE OR REPLACE TYPE TABLE_TYPE_EE AS TABLE OF TABLE_TYPE_DD;
/

prompt
prompt Creating type TYPE_BODY_TEST
prompt ============================
prompt
create or replace type TYPE_BODY_TEST   as   object
(FLIGHTNO   VARCHAR2(4)   ,   AIRBUSNO   VARCHAR2(5)   ,
ROUTE_CODE   VARCHAR2(7)   ,   DEPRT_TIME   VARCHAR2(10)   ,
JOURNEY_HURS   VARCHAR2(10)   ,   FLIGHT_DAY1   NUMBER(1)   ,
FLIGHT_DAY2   NUMBER(1)   ,
Member   function   DAYS_FN(FLIGHT_DAY1   in   number)   return   varchar2   ,
Pragma       restrict_references(DAYS_FN   ,   WNDS)
);
/

prompt
prompt Creating function COUNT_ROWS
prompt ============================
prompt
create or replace function count_rows(table_name in varchar2, owner      in varchar2 default null)
  return number authid current_user IS
  num_rows number;
  stmt     varchar2(2000);
begin
  if owner is null then
    stmt := 'select count(*) from "' || table_name || '"';
  else
    stmt := 'select count(*) from "' || owner || '"."' || table_name || '"';
  end if;
  execute immediate stmt
    into num_rows;
  return num_rows;
end;
/


prompt
prompt Creating procedure DBMS_JOB_PROC
prompt ================================
prompt
CREATE OR REPLACE PROCEDURE dbms_job_proc AS
BEGIN
  INSERT INTO dbms_job_table VALUES (TO_CHAR(SYSDATE, 'YYYY-MM-DD HH:MI'));/*向测试表插入数据*/
  COMMIT;
END;
/

prompt
prompt Creating procedure JOB_PROC
prompt ===========================
prompt
create or replace procedure job_proc authid current_user is
         v_count number := 0;
         v_mess varchar2(200) := '';
         begin
         select count(1) into v_count from user_tables t where t.TABLE_NAME = 'BAK_JOB_TABLES';
         if v_count > 0 then
            execute immediate 'drop table bak_job_tables purge';
         end if;
         execute immediate 'create table bak_job_tables as select * from user_tables where 1=2';
         insert into job_table(date_time,mark) values(sysdate,'success');
     exception
     when others then
     v_mess := substr(SQLERRM,0,200);
      insert into job_table(date_time,mark) values(sysdate,v_mess);
end;
/

prompt
prompt Creating procedure PROC_ADD
prompt ===========================
prompt
create or replace procedure proc_add
(
  num1 in integer,
  num2 in integer
)
as
begin
       dbms_output.put_line('num1:'||num1);
       dbms_output.put_line('num2:'||num2);
       dbms_output.put_line('加值结果:'||(num2+num1));
end;
/

prompt
prompt Creating package body PACKAGE_TEST
prompt ==================================
prompt
CREATE OR REPLACE PACKAGE BODY PACKAGE_TEST --包名必须一致
 IS
 PROCEDURE SP_EMP_INSERT --实现规范中的过程
 IS
 BEGIN
 INSERT INTO PACKAGE_TABLE(EMPNO,ENAME,JOB,HIREDATE)
 VALUES(7384,'WangYi','SALESMAN',SYSDATE); --插入数据
 COMMIT;
 END;
 FUNCTION F_GETENAME(I_EMPNO NUMBER) --实现函数
 RETURN VARCHAR2
 IS
 V_ENAME VARCHAR2(200);
 BEGIN
 SELECT ENAME INTO V_ENAME FROM PACKAGE_TABLE WHERE EMPNO=I_EMPNO;
 RETURN V_ENAME;
 END;
 END;
 --PL*SQL执行调用语句CALL PACKAGE_TEST.SP_EMP_INSERT();
 --测试语句SELECT PACKAGE_TEST.F_GETENAME(7384) FROM DUAL;
/

prompt
prompt Creating type body TYPE_BODY_TEST
prompt =================================
prompt
create or replace type   body   TYPE_BODY_TEST   as
        member   function   DAYS_FN(FLIGHT_DAY1   in   number)   return   varchar2
        is
        disp_day   varchar2(20)   ;
        begin
        if   flight_day1   =   1   then
        disp_day   :=   'Sunday'   ;
        elsif   flight_day1   =   2   then
        disp_day   :=   'Monday'   ;
        elsif   flight_day1   =   3   then
        disp_day   :=   'Tuesday'   ;
        elsif   flight_day1   =   4   then
        disp_day   :=   'Wednesday'   ;
        elsif   flight_day1   =   5   then
        disp_day   :=   'Thursday'   ;
        elsif   flight_day1   =   6   then
        disp_day   :=   'Friday   '   ;
        elsif   flight_day1   =   7   then
        disp_day   :=   'Saturday'   ;
        end   if   ;
        return   disp_day   ;
        end   ;
        end;
/

prompt
prompt Creating trigger TRIGGER_TEST
prompt =============================
prompt
create or replace trigger TRIGGER_TEST
   BEFORE DELETE --指定触发时机为删除操作前触发
   ON TRIGGER_TABLE 
   FOR EACH ROW   --说明创建的是行级触发器 
BEGIN
   --将修改前数据插入到日志记录表 DEL_EMP ,以供监督使用。
   INSERT INTO SUB_TRIGGER_TABLE(DEPTNO , EMPNO, ENAME , JOB ,MGR , SAL , COMM , HIREDATE )
       VALUES( :OLD.DEPTNO, :OLD.EMPNO, :OLD.ENAME , :OLD.JOB,:OLD.MGR, :OLD.SAL, :OLD.COMM, :OLD.HIREDATE );
END;
/


prompt Done
spool off
set define on
