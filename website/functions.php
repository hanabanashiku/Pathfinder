<?php
	
	function build_page_url($page = 'home', $userlevel = 'basic', $p1 = '', $p2 = '', $p3 = '', $p4 = '', $p5 = '', $p6 = '', $p7 = '', $p8 = '')
	{
		$retval = '';
		
		$retval = append_param($retval, 'page', $page);
		$retval = append_param($retval, 'userlevel', $userlevel);
		$retval = append_param($retval, 'p1', $p1);
		$retval = append_param($retval, 'p2', $p2);
		$retval = append_param($retval, 'p3', $p3);
		$retval = append_param($retval, 'p4', $p4);
		$retval = append_param($retval, 'p5', $p5);
		$retval = append_param($retval, 'p6', $p6);
		$retval = append_param($retval, 'p7', $p7);
		$retval = append_param($retval, 'p8', $p8);
		
		return $retval;
	}
	
	function append_param($paramstring, $param, $value)
	{
		if ($param != '' && $value != '')
		{
			return ($paramstring == '' ? '?' : $paramstring . '&') . $param . '=' . $value;
		}
		else
		{
			return $paramstring;
		}
	}
	
	function param_link($text = 'no text', $page = '', $userlevel = '', $p1 = '', $p2 = '', $p3 = '', $p4 = '', $p5 = '', $p6 = '', $p7 = '', $p8 = '')
	{
		echo '<a href="' . build_page_url($page, $userlevel, $p1, $p2, $p3, $p4, $p5, $p6, $p7, $p8) . '">' . $text . '</a>';
	}
	
	function uptime()
	{
		return exec('uptime');
	}
	
	function program_project_info()
	{
		return exec('sh /opt/SIEBMEYER/bin/knight/user_program_info.sh');
	}
	
	function isrunning($name)
	{
		return print_updown(run_command('ps | grep "' . $name . '" | grep -v "grep"'));
	}
	
	function isreachable($addr)
	{
		return print_updown(run_command('ping -c 1 -w 5 ' . $addr));
	}
	
	function print_updown($retval)
	{
		return ($retval == 0 ? '<font color="green">[UP]</font>' : '<font color="red">[DOWN]</font>');
	}
	
	function card_is_ok()
	{
		return print_goodbad(!(run_command('dmesg | grep "mmcblk" | grep "error"')));
	}
	
	function print_goodbad($retval)
	{
		return ($retval == 0 ? '<font color="green">[GOOD]</font>' : '<font color="red">[BAD]</font>');
	}
	
	function run_command($cmd)
	{
		ob_start();
		$retval = 0;
		exec($cmd, $output, $retval);
		ob_end_clean();
		return $retval;
	}
	
	function run_command_root($cmd)
	{
		run_command('echo sd3n5 | su -c "' . $cmd . '" root');
	}
	
	function run_command_wait($cmd, $delay = 5)
	{
		$retval = run_command($cmd);
		sleep($delay);
		return $retval;
	}
	
	function run_command_root_wait($cmd, $delay = 5)
	{
		$retval = run_command_root($cmd);
		sleep($delay);
		return $retval;
	}
?>