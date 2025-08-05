--创建死循环函数
CREATE FUNCTION rrr(@ProductID int)  
RETURNS int   
AS   

BEGIN  
    declare @i int  
	set @i=0
	while @i>=0
		begin
		set @i=@i +1
		end
	return @i;
END;
select dbo.rrr(1);