CREATE OR REPLACE
package adp_debug as
 --终止调试会话
  procedure abort;

  function get_stack return varchar2;
  function get_lineNumber return varchar2;
  function get_dblog return varchar2;
  -- highly expermiental
  procedure current_prg;
	--查询所有断点
  function breakpoints return varchar2;
	--继续调试
  function continue_(break_flags in number) return varchar2;
 --运行到下一个断点
  function continue return varchar2;
	--逐行调试
  function step return varchar2;
  --逐步调试
  function step_into return varchar2;
--跳出方法
  function step_out return varchar2;

  --查看变量
  function print_var(name in varchar2) return varchar2;

  procedure start_debugger(debug_session_id in varchar2);

  function start_debugee return varchar2;

  procedure print_proginfo(prginfo dbms_debug.program_info);

  procedure print_runtime_info(runinfo dbms_debug.runtime_info);
--获取调试程序源码
  function print_source(
    runinfo       dbms_debug.runtime_info,
    lines_before  number default 0,
    lines_after   number default 0
  ) return varchar2;


  procedure print_runtime_info_with_source(
                 runinfo        dbms_debug.runtime_info
                 --v_lines_before in number,
                 --v_lines_after  in number,
                -- v_lines_width  in number
                 );

  procedure self_check;
 --设置断点
  function set_breakpoint(
    p_line     in number,
	p_name in varchar2 default null,
	p_owner in varchar2 default null,
	p_entry in varchar2 default null,
	p_ns in varchar2 default null) return varchar2;

--删除断点
	function delete_bp(breakpoint in binary_integer)return varchar2;

  function  str_for_namespace(nsp in binary_integer) return varchar2;

  function  str_for_reason_in_runtime_info(rsn in binary_integer) return varchar2;

  procedure wait_until_running;
--是否在运行
  procedure is_running;
--版本信息
  procedure version;
--停止调试（不会通知会话已关闭）
  procedure detach;

  function  libunittype_as_string(lut binary_integer) return varchar2;

  function  bp_status_as_string(bps binary_integer) return varchar2;

  function  general_error(e in binary_integer) return varchar2;

  -- the following vars are used whenever continue returnes and shows
  -- the lines arount line
  cont_lines_before_ number;
  cont_lines_after_  number;
--  cont_lines_width_  number;

  -- Store the current line of execution
  cur_line_ dbms_debug.runtime_info;

end;
//

CREATE OR REPLACE
package body adp_debug as
 --终止调试会话
  procedure abort is
    runinfo dbms_debug.runtime_info;
    ret     binary_integer;
  begin
--     continue_(dbms_debug.abort_execution);
		dbms_output.put_line(continue_(dbms_debug.abort_execution));
  end abort;
  
  function get_dblog return varchar2 as
    info varchar2(512);
	status number;	
  begin
	dbms_output.get_line(info, status);
	if status = 1 then
		info := 'EOF';
	end if;
	return info;
  end get_dblog;
  
  function get_stack return varchar2 is
    pkgs dbms_debug.backtrace_table;
	i    number;
	message varchar2(3000); 	
  begin
    dbms_debug.print_backtrace(pkgs);
    i := pkgs.first();

    while i is not null loop
      message := message || '  ' || i || ': name=' || pkgs(i).name || ',type=' || libunittype_as_string(pkgs(i).libunittype) || ',line=' || pkgs(i).line#;
      i := pkgs.next(i);
    end loop;
	return message;
	
   exception
    when others then
     dbms_output.put_line('  backtrace exception: ' || sqlcode);
     dbms_output.put_line('                       ' || sqlerrm(sqlcode));
     return '';
  end;
  
  function get_lineNumber return varchar2 is
    pkgs dbms_debug.backtrace_table;
	i    number;
	message varchar2(32); 	
  begin
    dbms_debug.print_backtrace(pkgs);
    i := pkgs.first();

    while i is not null loop
      message := pkgs(i).line#;
      i := pkgs.next(i);
    end loop;
	return message;
	
   exception
    when others then
	 dbms_output.put('  backtrace exception: ' || sqlcode || sqlerrm(sqlcode));
     return '-1';
  end;
  
  --查询所有断点
  function breakpoints return varchar2 is
    brkpts dbms_debug.breakpoint_table;
    i      number;
    v_line number;
		message varchar2(1024);   --断点ID和断点对应的行号: id,line,name|
  begin
    dbms_debug.show_breakpoints(brkpts);
    i := brkpts.first();
    dbms_output.put_line('');
    while i is not null loop
      dbms_output.put(to_char(i,'999') || ': '|| '|');
      dbms_output.put(rpad(coalesce(brkpts(i).name, ' '), 31)|| '|');
      dbms_output.put(rpad(coalesce(brkpts(i).owner,' '), 31)|| '|');
      dbms_output.put(libunittype_as_string(brkpts(i).libunittype)|| '|');
      dbms_output.put(bp_status_as_string  (brkpts(i).status     )|| '|');
      v_line:=brkpts(i).line#;
			if v_line is not null then
				dbms_output.put( to_char(v_line , '99999') || '=' || v_line || '|');
				message := message || to_char(i,'999') || ',' || to_char(v_line , '99999')
				|| ',' || rpad(coalesce(brkpts(i).name, ' '), 31) || '|';
      else
				dbms_output.put(' |');
      end if;
      dbms_output.put_line('');
      i := brkpts.next(i);
    end loop;
		return message;
  end breakpoints;

  function libunittype_as_string(lut binary_integer)
  return varchar2 is
  begin
    if lut = dbms_debug.libunitType_cursor         then return 'CURSOR'; end if;
    if lut = dbms_debug.libunitType_procedure      then return 'PROCEDURE'  ; end if;
    if lut = dbms_debug.libunitType_function       then return 'FUNCTION'  ; end if;
    if lut = dbms_debug.libunitType_package        then return 'PACKAGE'   ; end if;
    if lut = dbms_debug.libunitType_package_body   then return 'PACKAGE_BODY'; end if;
    if lut = dbms_debug.libunitType_trigger        then return 'TRIGGER'  ; end if;
    if lut = dbms_debug.libunitType_unknown        then return 'UNKNOWN'   ; end if;
    return '???';
  end libunittype_as_string;

  function  bp_status_as_string(bps binary_integer) return varchar2 is
  begin
    if bps = dbms_debug.breakpoint_status_unused   then return 'unused'  ; end if;
    if bps = dbms_debug.breakpoint_status_active   then return 'active'  ; end if;
    if bps = dbms_debug.breakpoint_status_disabled then return 'disabled'; end if;
    if bps = dbms_debug.breakpoint_status_remote   then return 'remote'  ; end if;
    return '???';
  end bp_status_as_string;

--继续调试
  function continue_(break_flags in number) return varchar2 is
    ret     binary_integer;
    v_err   varchar2(512);
		message varchar2(512);
  begin
    ret := dbms_debug.continue(cur_line_,break_flags,
       0 + dbms_debug.info_getlineinfo + dbms_debug.info_getbreakpoint +
			 dbms_debug.info_getstackdepth + dbms_debug.info_getoerinfo + 0);

    if ret = dbms_debug.success then
		message := '  reason for break: ' || str_for_reason_in_runtime_info(cur_line_.reason);
		print_runtime_info(cur_line_);
       if cur_line_.reason  = dbms_debug.reason_knl_exit then
			dbms_output.put_line('reason_knl_exit');
			return '0';
       end if;
       if cur_line_.reason  = dbms_debug.reason_exit then
			dbms_output.put_line('reason_exit');
			return '0';
       end if;
	   return get_lineNumber();
		--return print_source(cur_line_, cont_lines_before_, cont_lines_after_);
     elsif ret = dbms_debug.error_timeout then
		dbms_output.put_line('  continue: error_timeout');
     elsif ret = dbms_debug.error_communication then
		dbms_output.put_line('  continue: error_communication');
     else
		v_err := general_error(ret);
		dbms_output.put_line('  continue: general error' || v_err);
     end if;
	return '-1';		 
  end continue_;

--运行到下一个断点
  function continue return varchar2 is
  begin
		return continue_(0);
  end continue;

	--逐行调试
  function step return varchar2 is
  begin
		return continue_(dbms_debug.break_next_line);
  end step;

  --逐步调试
  function step_into return varchar2 is
  begin
		return continue_(dbms_debug.break_any_call);
  end step_into;

	--跳出方法
  function step_out return varchar2 is
  begin
		return continue_(dbms_debug.break_any_return);
  end step_out;

 --查看变量
  function print_var(name in varchar2) return varchar2 is
    ret binary_integer;
    val varchar2(4000);
    frame number;
		message varchar2(200);
  begin
    frame := 0;
    ret := dbms_debug.get_value(name,frame,val,null);
    if ret = dbms_debug.success then
			message := val;
    elsif ret = dbms_debug.error_bogus_frame then
			message := '  print_var: frame does not exist';
    elsif ret = dbms_debug.error_no_debug_info then
			message := '  print_var: Entrypoint has no debug info';
    elsif ret = dbms_debug.error_no_such_object then
			message := '  print_var: variable ' || name || ' does not exist in in frame ' || frame;
    elsif ret = dbms_debug.error_unknown_type then
			message := '  print_var: The type information in the debug information is illegible';
    elsif ret = dbms_debug.error_nullvalue then
			message := 'NULL';
    elsif ret = dbms_debug.error_indexed_table then
			message := '  print_var: The object is a table, but no index was provided.';
    else
			message := '  print_var: unknown error';
    end if;
		return message;
  end print_var;


  /*

     This is the first call the debugging session must make. It, in turn, calls
     dbms_debug.attach_session.

     After attaching to the session, it waits for the first event (wait_until_running), which is interpreter starting.

  */
  procedure start_debugger(debug_session_id in varchar2) as
  begin
    dbms_debug.attach_session(debug_session_id);
    --cont_lines_before_ :=   5;
    --cont_lines_after_  :=   5;
    --cont_lines_width_  := 100;

    wait_until_running;
  end start_debugger;

  /* This is the first call the debugged session must make.

     The return value must be passed to the debugging session and used in start_debugger
  */

  
  function start_debugee return varchar2 as
    debug_session_id varchar2(20);
  begin
    --select dbms_debug.initialize into debug_session_id from dual;
    debug_session_id := dbms_debug.initialize;
    dbms_debug.debug_on;
    return debug_session_id;
  end start_debugee;


  procedure print_proginfo(prginfo dbms_debug.program_info) as
  begin
    dbms_output.put_line('  Namespace:  ' || str_for_namespace(prginfo.namespace));
    dbms_output.put_line('  Name:       ' || prginfo.name);
    dbms_output.put_line('  owner:      ' || prginfo.owner);
    dbms_output.put_line('  dblink:     ' || prginfo.dblink);
    dbms_output.put_line('  Line#:      ' || prginfo.Line#);
    dbms_output.put_line('  lib unit:   ' || prginfo.libunittype);
    dbms_output.put_line('  entrypoint: ' || prginfo.entrypointname);
  end print_proginfo;


  procedure print_runtime_info(runinfo dbms_debug.runtime_info) as
    --rsnt varchar2(40);
  begin
    --rsnt := str_for_reason_in_runtime_info(runinfo.reason);
    dbms_output.put_line('');
    dbms_output.put_line('Runtime Info');
    dbms_output.put_line('Prg Name:      ' || runinfo.program.name);
    dbms_output.put_line('Line:          ' || runinfo.line#);
    dbms_output.put_line('Terminated:    ' || runinfo.terminated);
    dbms_output.put_line('Breakpoint:    ' || runinfo.breakpoint);
    dbms_output.put_line('Stackdepth     ' || runinfo.stackdepth);
    dbms_output.put_line('Interpr depth: ' || runinfo.interpreterdepth);
    --dbms_output.put_line('Reason         ' || rsnt);
    dbms_output.put_line('Reason:        ' || str_for_reason_in_runtime_info(runinfo.reason));

    print_proginfo(runinfo.program);
  end print_runtime_info;

--获取调试程序源码
  function print_source (
    runinfo       dbms_debug.runtime_info,
    lines_before  number default 0,
    lines_after   number default 0 ) return varchar2 is

    first_line binary_integer;
    last_line  binary_integer;
    prefix varchar2(  99);
    suffix varchar2(4000);
    source_lines dbms_debug.vc2_table;
    cur_line         binary_integer;
    cur_real_line    number;
		message varchar2(200); --当前行
  begin
    first_line := greatest(runinfo.line# - cont_lines_before_,1);
    last_line  := runinfo.line# + cont_lines_after_;
    if first_line is null or last_line is null then
			dbms_output.put_line('first_line or last_line is null');
      print_runtime_info(runinfo);
      return null;
    end if;

    if runinfo.program.name is not null and runinfo.program.owner is not null then
      dbms_output.put_line('');
      dbms_output.put_line('  ' || runinfo.program.owner || '.' || runinfo.program.name);
          for r in (
                select
                  rownum line,
                  substr(text,1,90) text
                from
                  all_source
                where
                  name   = runinfo.program.name   and
                  owner  = runinfo.program.owner  and
                  type  <> 'PACKAGE'              and
                  line  >= first_line             and
                  line  <= last_line
                order by
                  line )
              loop
        source_lines(r.line) := r.text;
      end loop;
    else
      dbms_debug.show_source(first_line, last_line, source_lines);
    end if;
    dbms_output.put_line('');
    cur_line := source_lines.first();
    while cur_line is not null loop
				cur_real_line := cur_line + first_line -1;
        prefix := to_char(cur_real_line,'9999');
        if cur_real_line = runinfo.line# then
					message := prefix;
          prefix := prefix || ' -> ';
        else
          prefix := prefix || '    ';
        end if;
        suffix := source_lines(cur_line);
        suffix := translate(suffix,chr(10),' ');
        suffix := translate(suffix,chr(13),' ');

        dbms_output.put_line(prefix || suffix);
				cur_line := source_lines.next(cur_line);
    end loop;
    dbms_output.put_line('');
		return message;
  end print_source;


  procedure print_runtime_info_with_source(runinfo dbms_debug.runtime_info ) is
  begin
    print_runtime_info(runinfo);
    dbms_output.put_line(print_source(runinfo));
  end print_runtime_info_with_source;


  procedure self_check as
    ret binary_integer;
  begin
    dbms_debug.self_check(5);
  exception
    when dbms_debug.pipe_creation_failure     then
      dbms_output.put_line('  self_check: pipe_creation_failure');
    when dbms_debug.pipe_send_failure      then
      dbms_output.put_line('  self_check: pipe_send_failure');
    when dbms_debug.pipe_receive_failure   then
      dbms_output.put_line('  self_check: pipe_receive_failure');
    when dbms_debug.pipe_datatype_mismatch then
      dbms_output.put_line('  self_check: pipe_datatype_mismatch');
    when dbms_debug.pipe_data_error        then
      dbms_output.put_line('  self_check: pipe_data_error');
    when others then
      dbms_output.put_line('  self_check: unknown error');
  end self_check;

	--设置断点
  function set_breakpoint(
    p_line     in number,
	p_name in varchar2 default null,
	p_owner in varchar2 default null,
	p_entry in varchar2 default null,
	p_ns in varchar2 default null) return varchar2
  as
	message varchar2(200);
    proginfo dbms_debug.program_info;
    ret      binary_integer;
    bp       binary_integer;
  begin

	proginfo.namespace      := null;
	if p_ns is not null then
		if p_ns = 'TOP_LEVEL' then
			proginfo.namespace      := dbms_debug.namespace_pkgspec_or_toplevel;
		elsif p_ns = 'PACKAGE_SPEC' then
			proginfo.namespace      := dbms_debug.namespace_pkgspec_or_toplevel;
		elsif p_ns = 'PACKAGE_BODY' then
			proginfo.namespace      := dbms_debug.namespace_pkg_body;
		elsif p_ns = 'CURSOR' then
			proginfo.namespace      := dbms_debug.namespace_cursor;
		elsif p_ns = 'TRIGGER' then
			proginfo.namespace      := dbms_debug.namespace_trigger;
		else
			proginfo.namespace      := null;
		end if;
	end if;
	
	proginfo.entrypointname := null;
	if p_entry is not null then
		if p_entry = 'PACKAGE' then
			proginfo.entrypointname := dbms_debug.libunitType_package;
		elsif p_entry = 'PACKAGE_BODY' then
			proginfo.entrypointname := dbms_debug.libunitType_package_body;
		elsif p_entry = 'FUNCTION' then
			proginfo.entrypointname := dbms_debug.libunitType_function;
		elsif p_entry = 'PROCEDURE' then
			proginfo.entrypointname := dbms_debug.libunitType_procedure;
	    elsif p_entry = 'TRIGGER' then
			proginfo.entrypointname := dbms_debug.libunitType_trigger;
	    elsif p_entry = 'CURSOR' then
			proginfo.entrypointname := dbms_debug.libunitType_cursor;
		else
			proginfo.entrypointname := null;
		end if;
	end if;

    proginfo.name           := p_name;
    proginfo.owner          := p_owner;
    proginfo.dblink         := null;

	print_proginfo(proginfo);
    ret := dbms_debug.set_breakpoint(proginfo,p_line,bp);
    if ret = dbms_debug.success then
			message := '  breakpoint set: ' || bp;
    elsif ret = dbms_debug.error_illegal_line then
			message := '  set_breakpoint: error_illegal_line';
    elsif ret = dbms_debug.error_bad_handle then
			message := '  set_breakpoint: error_bad_handle';
    else
			message := '  set_breakpoint: unknown error (' || ret || ')';
    end if;
		return message;
  end set_breakpoint;

--删除断点
  function delete_bp(breakpoint in binary_integer) return varchar2 is
    ret binary_integer;
		message varchar2(200);
  begin
    ret := dbms_debug.delete_breakpoint(breakpoint);
    if ret = dbms_debug.success then
			message := '  breakpoint deleted';
    elsif ret = dbms_debug.error_no_such_breakpt then
			message := '  No such breakpoint exists';
    elsif ret = dbms_debug.error_idle_breakpt then
			message := '  Cannot delete an unused breakpoint';
    elsif ret = dbms_debug.error_stale_breakpt then
			message := '  The program unit was redefined since the breakpoint was set';
    else
			message := '  Unknown error';
    end if;
		return message;
  end delete_bp;

  function str_for_namespace(nsp in binary_integer) return varchar2 is
    nsps   varchar2(40);
  begin
    if nsp = dbms_debug.Namespace_cursor then
      nsps := 'Cursor (anonymous block)';
    elsif nsp = dbms_debug.Namespace_pkgspec_or_toplevel then
      nsps := 'package, proc, func or obj type';
    elsif nsp = dbms_debug.Namespace_pkg_body then
      nsps := 'package body or type body';
    elsif nsp = dbms_debug.Namespace_trigger then
      nsps := 'Triggers';
    else
      nsps := 'Unknown namespace';
    end if;


    return nsps;
  end str_for_namespace;


  function  str_for_reason_in_runtime_info(rsn in binary_integer) return varchar2 is
    rsnt varchar2(40);
  begin
    if rsn = dbms_debug.reason_none then
      rsnt := 'none';
    elsif rsn = dbms_debug.reason_interpreter_starting then
      rsnt := 'Interpreter is starting.';
    elsif rsn = dbms_debug.reason_breakpoint then
      rsnt := 'Hit a breakpoint';
    elsif rsn = dbms_debug.reason_enter then
      rsnt := 'Procedure entry';
    elsif rsn = dbms_debug.reason_return then
      rsnt := 'Procedure is about to return';
    elsif rsn = dbms_debug.reason_finish then
      rsnt := 'Procedure is finished';
    elsif rsn = dbms_debug.reason_line then
      rsnt := 'Reached a new line';
    elsif rsn = dbms_debug.reason_interrupt then
      rsnt := 'An interrupt occurred';
    elsif rsn = dbms_debug.reason_exception then
      rsnt := 'An exception was raised';
    elsif rsn = dbms_debug.reason_exit then
      rsnt := 'Interpreter is exiting (old form)';
    elsif rsn = dbms_debug.reason_knl_exit then
      rsnt := 'Kernel is exiting';
    elsif rsn = dbms_debug.reason_handler then
      rsnt := 'Start exception-handler';
    elsif rsn = dbms_debug.reason_timeout then
      rsnt := 'A timeout occurred';
    elsif rsn = dbms_debug.reason_instantiate then
      rsnt := 'Instantiation block';
    elsif rsn = dbms_debug.reason_abort then
      rsnt := 'Interpreter is aborting';
    else
      rsnt := 'Unknown reason';
    end if;
    return rsnt;
  end str_for_reason_in_runtime_info;



  procedure wait_until_running as
    runinfo dbms_debug.runtime_info;
    ret     binary_integer;
    v_err   varchar2(100);
  begin
    ret:=dbms_debug.synchronize(runinfo, dbms_debug.info_getlineinfo + dbms_debug.info_getbreakpoint +
			 dbms_debug.info_getstackdepth);
    if ret = dbms_debug.success then
	  dbms_output.put_line('  synchronize: success');
      print_runtime_info(runinfo);
	  dbms_output.put_line('  end synchronize: success');
    elsif ret = dbms_debug.error_timeout then
      dbms_output.put_line('  synchronize: error_timeout');
    elsif ret = dbms_debug.error_communication then
      dbms_output.put_line('  synchronize: error_communication');
    else
       v_err := general_error(ret);
       dbms_output.put_line('  synchronize: general error' || v_err);
      --dbms_output.put_line('  synchronize: unknown error');
    end if;
  end wait_until_running;

--是否在运行
  procedure is_running is
  begin
    if dbms_debug.target_program_running then
      dbms_output.put_line('  target (debugee) is running');
    else
      dbms_output.put_line('  target (debugee) is not running');
    end if;
  end is_running;

  function  general_error(e in binary_integer) return varchar2 is
  begin

    if e = dbms_debug.error_unimplemented then return 'unimplemented'       ; end if;
    if e = dbms_debug.error_deferred      then return 'deferred'            ; end if;
    if e = dbms_debug.error_exception     then return 'probe exception'     ; end if;
    if e = dbms_debug.error_communication then return 'communication error' ; end if;
    if e = dbms_debug.error_unimplemented then return 'unimplemented'       ; end if;
    if e = dbms_debug.error_timeout       then return 'timeout'             ; end if;

    return '???';

  end general_error;
--版本信息
  procedure version as
    major binary_integer;
    minor binary_integer;
  begin
    dbms_debug.probe_version(major,minor);
    dbms_output.put_line('  probe version is: ' || major || '.' || minor);
  end version;
--停止调试
	procedure detach is
  begin
    dbms_debug.detach_session;
  end detach;

  procedure current_prg is
    ri dbms_debug.runtime_info;
    pi dbms_debug.program_info;
    ret binary_integer;
  begin
    ret := dbms_debug.get_runtime_info(
       0             +
       dbms_debug.info_getlineinfo   +
       dbms_debug.info_getbreakpoint +
       dbms_debug.info_getstackdepth +
       dbms_debug.info_getoerinfo    +
       0,
      ri
       );
     pi := ri.program;

     print_proginfo(pi);
  end current_prg;

  begin
    cont_lines_before_ :=   5;
    cont_lines_after_  :=   5;
end;