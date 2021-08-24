create table dep(
	dep_id int not null primary key,
	dep_name char(100)
);
create table emp(
  emp_id int not null primary key,
  dep_id int,
  dep_name char(100),
  emp_name char(100),
  salary int,
  FOREIGN KEY (dep_id) REFERENCES dep(dep_id) 
);
insert into dep values(1,'研发部');insert into dep values(2,'销售部');insert into dep values(3,'市场部');

insert into emp values(1 ,1,'研发部', '张三','5000');
insert into emp values(2 ,1,'研发部', '李四','6000');
insert into emp values(3 ,1,'研发部', '王五','7000');

insert into emp values(4 ,2, '销售部','Tom','5000');
insert into emp values(5 ,2, '销售部','Jack','6000');

insert into emp values(6 ,3, '市场部','小白','8000');
insert into emp values(7 ,3, '市场部','小明','9000');

select * from emp;
select count(1) from dep;

drop table emp;
drop table dep;
