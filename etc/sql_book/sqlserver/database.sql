--切换到master数据库
use master  

--创建数据库
IF EXISTS(SELECT 1 FROM sysdatabases WHERE NAME=N'HkTemp') 
BEGIN 
DROP DATABASE HkTemp --如果数据库存在先删掉数据库 
END 
GO 
CREATE DATABASE HkTemp 
ON 
PRIMARY --创建主数据库文件 
( 
NAME='HkTemp', 
FILENAME='E:\Databases\HkTemp.dbf', 
SIZE=5MB, 
MaxSize=20MB, 
FileGrowth=1MB 
) 
LOG ON --创建日志文件 
( 
NAME='HkTempLog', 
FileName='E:\Databases\HkTemp.ldf', 
Size=2MB, 
MaxSize=20MB, 
FileGrowth=1MB 
) 

