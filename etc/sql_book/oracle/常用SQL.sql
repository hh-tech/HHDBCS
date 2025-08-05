
--生成死循环函数
create or replace function func_test(a int) return  number as
    b number;
begin
    while true
        loop
            b := b + 1;
        end loop;
    return 1;
end func_test;
