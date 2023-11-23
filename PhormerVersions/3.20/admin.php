<?php
	require_once('funcs.php');

	#session_start(); //useless and causes FF in Mac to deal with nothingness!
	global $hasgd;

	$alert_msg = '';
	$ok_msg = '';

	$up = '/'; // for the manner of cookies

	if (!file_exists(ADMIN_PASS_FILE)) {
		$page = (isset($_GET['page']) && (strcmp($_GET['page'], 'doinstall') == 0))?'doinstall':'install';
	}
	else {
		if (isset($_COOKIE['phormer_passwd']) && auth_admin($_COOKIE['phormer_passwd'])) // AUTH: OK
			$page = isset($_GET['page'])?$_GET['page']:'welcome';
		else {
			$invalidpasswd = (!isset($_POST['passwd'])) || (!auth_admin(md5($_POST['passwd'])));
			if ($invalidpasswd) {
				$goafterlogin = isset($_GET['page'])?$_GET['page']:"";
				$page = 'wrong';
				if (isset($_POST['passwd']))
					$alert_msg = "Wrong password!";
			}
			else {
				setcookie('phormer_passwd', md5($_POST['passwd']), time()+3600*24, $up);
				$page = isset($_GET['page'])?$_GET['page']:'welcome';
				### Cleans the temporary files
				if ($handle = opendir('temp')) {
					while (false !== ($file = readdir($handle)))
						if ($file != "." && $file != "..")
							@unlink("temp/".$file);
					closedir($handle);
				}
				foreach (Array('categories', 'comments', 'stories', 'photos', 'basis') as $theSrc)
					@copy('data/'.$theSrc.'.xml', 'data/'.$theSrc.'.xml'.'.bku');

			}
		}
	}

	if (page_is('install') || page_is('doinstall')) // came from $_GET['page'] where user is authenticated!
		if (file_exists(ADMIN_PASS_FILE)) {
			$alert_msg = "Use 'change password' menu to change the password!";
			$page = 'welcome';
		}

	if (page_is('doinstall')) {
		if (!isset($_POST['newpasswd'])) {
			$alert_msg = 'All fields are required!';
			$page = 'install';
		}
		else if (strlen($_POST['newpasswd']) < 4) {
			$alert_msg = 'Too short password!';
			$page = 'install';
		}
		else if ((strcmp($_POST['newpasswd'], $_POST['newpasswd2']) != 0)) {
			$alert_msg = "Entered passwords was not identical. try again!";
			$page = 'install';
		}
	}

	if (page_is('dochangepass')) {
		if ((!isset($_POST['curpasswd'])) || (!isset($_POST['newpasswd1'])) || (!isset($_POST['newpasswd2']))) {
			$alert_msg = 'All fields are required!';
			$page = 'changepass';
		}
		else if (!auth_admin(md5($_POST['curpasswd']))) {
			$alert_msg = 'Wrong current password!';
			$page = 'changepass';
		}
		else if (strcmp($_POST['newpasswd1'], $_POST['newpasswd2']) != 0) {
			$alert_msg = 'New passwords are not identical!';
			$page = 'changepass';
		}
		else if (strlen($_POST['newpasswd1']) < 4) {
			$alert_msg = 'Too short password!';
			$page = 'changepass';
		}
	}

	$firsttime = false;

	if (page_is('doinstall') || page_is('dochangepass')) {
		if (page_is('doinstall')) {
			if (!is_writable('.')) {
				clearstatcache();
				die("ERROR: <a href=\".\">Base directory</a> is not writable.<br />\n"
					."Just give them the a+w (write premission to all) access for installation and remove"
					."it when installation finished.");
			}
			$ok_msg .= '<br /> Now, just fill the fields below and start adding photos in the Admin Page!';
			chmod(".", 0711);

			if (file_exists("temp"))
				chmod("./temp", 0711);
			else
				if (! mkdir("temp", 0711))
					die("could not create admin/temp directory.");

			if (file_exists("data"))
				chmod("data", 0700);
			else
				if (! mkdir("data"))
					die("could not create data directory.");

			if (file_exists("images"))
				chmod("images", 0711);
			else
				if (! mkdir("images"))
					die("could not create images directory.");

			if ($handle = opendir('files/adminfiles/')) {
				while (false !== ($file = readdir($handle)))
					if ($file != "." && $file != ".." && (strcasecmp(substr($file, -4), ".def") == 0)) {
						if (! copy ("files/adminfiles/".$file, "data/".substr($file, 0, -4)))
							die("could not copy admin/files/$file to /data.");
						#unlink("files/adminfiles/".$file); # they might be handy in reinstall
					}
				closedir($handle);
			}

			build_rss();

			$firsttime = true;
		}

		$pass = page_is('doinstall')?$_POST['newpasswd']:$_POST['newpasswd1'];
		$fh = fopen(ADMIN_PASS_FILE, "w");
		fwrite($fh, $pass);
		fclose($fh);

		setcookie('phormer_passwd', md5($pass), time()+3600*24, $up);
		$_COOKIE['phormer_passwd'] = md5($pass);

		if (page_is('doinstall'))
			$ok_msg = "Installation Completed!<br />Now, just fill the following fields and start adding photos.";
		else
			$ok_msg = "Administrator's password updated successfully!";

		$page = page_is('doinstall')?'basis':(strlen($ok_msg)?'welcome':'changepass');
		parse_container('basis', 'Basis', 'data/basis.xml');

		if (!chmod("data/adminPass.inf", 0600))
			$alert_msg = "Could not change adminPass.inf's premission. it might be readable by anyone!";
		else
			$alert_msg = "";
	}

	if (page_is('logout')) {
		setcookie('phormer_passwd', " ", time()+60, $up);
		$_COOKIE['phormer_passwd'] = " ";
		$alert_msg = "Feel free to back, anytime you wished!<br />I'm waiting for you to rephorm me, again! ;)";
		$page = 'doneoutside';
	}

	if (!page_is('install')) {
		$basis = array();
		parse_container('basis', 'Basis', 'data/basis.xml');
	}

	### page is fixed and verified till now,
	### so just do the cmd!

	if (array_search($page, array('welcome', 'wrong', 'doneinside', 'doneoutside', 'logout', 'dochangepass',
								   'doinstall', 'install', 'doinstall',
								  'photos', 'categories', 'stories', 'basis', 'editxml', 'comments',
								  'changepass', 'uninstall', 'configs')) === FALSE) {
		$alert_msg = "Invalid page ($page)! Pick one below:";
		$page = 'welcome';
	}

	$afterfirsttime = isset($_GET['firsttime']);
	$noheader = $afterfirsttime; // ||
	if (!$noheader) {
	switch($page) {
		case 'photos':		$pageNameTitle = "Manage Photos"; 			break;
		case 'categories':	$pageNameTitle = "Manage Categories"; 		break;
		case 'stories':		$pageNameTitle = "Manage Stories"; 			break;
		case 'comments':	$pageNameTitle = "Manage Comments"; 		break;
		case 'basis':		$pageNameTitle = "Adjust Preferences"; 		break;
		case 'editxml':		$pageNameTitle = "Edit XML files"; 			break;
		case 'doneoutside':
		case 'logout':		$pageNameTitle = "Logging out"; 			break;
		case 'install':		$pageNameTitle = "Installation Process";	break;
		case 'wrong':		$pageNameTitle = "Login Page"; 				break;
		case 'changepass':	$pageNameTitle = "Change Password";			break;
		case 'configs':		$pageNameTitle = "Advanced Configurations";	break;
		case 'uninstall':	$pageNameTitle = "Uninstall Process";		break;
		default: 			$pageNameTitle = "Administration Region"; 	break;
	}
	if (isset($basis['pgname']))
		$pageNameTitle .= " of ".$basis['pgname'];
	$headName = isset($basis['pgname'])?$basis['pgname']:"PhotoGallery";
	if (page_is('basis') && isset($_POST['pgname']))
		$headName = $_POST['pgname'];
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="files/adminfiles/admin.css">
	<script type="text/javascript" language="javascript" src="files/adminfiles/admin.js"></script>
	<title><?php echo $pageNameTitle; ?></title>
<?php
	if (isset($basis['icon']) && strlen($basis['icon']))
		echo "\t\t<link rel=\"icon\" href=\"".$basis['icon']."\">\n";
?>
	<?php	if (page_is('photos')) {	?>	<script type="text/javascript" language="javascript" src="files/adminfiles/skeleton.js"></script><?php	}	?>
</head>

<body <?php if (page_is('photos')) echo "onmouseup=\"javascript:ReleaseMouse();\" onload=\"javascript:PrepareBody();\"" ?>>
<center>
<div id="Granny">
	<div id="headerBar">
		<span class="VeryTitle">
			<span class="topHeadAround">&#149;</span>
			<a target="_blank" style="color: black" href=".">&#147;<?php echo $headName; ?>&#148; </a>
		</span>
		 <a style="color: snow;" href="?">The administration region</a>
	</div>
	<div>
<?php
	}

	if (page_is('welcome')) {
		if (!chmod("data/adminPass.inf", 0600))
			$alert_msg = "Could not change adminPass.inf's premission. it might be readable by anyone!";
?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery &gt;&gt; </a></div>
		<div class="clearer"></div>
		<div class="part" style="margin-top: 18px;">
			<div class="title"><a style="color: white" href="?page=welcome">The Administration Region</a></div>
			<div class="inside">
				<?php
					parse_container('comments', 'Comment', 'data/comments.xml');
					$lastcmnt = (int)$comments['lastiid'];
					end($comments);
					if (isset($basis['lastcmntseen']) && ($lastcmnt > $basis['lastcmntseen'])) {
						$notseen = ($lastcmnt - $basis['lastcmntseen']);
						if ($notseen == 1)
							$notseen = "one";
						echo "<div class=\"method\"><div class=\"note_valid\">"
							."<a href=\"?page=comments\">"
							."&nbsp; &nbsp; You have $notseen new comment".(($notseen != "one")?"s":"")."!"
							."&nbsp;<img class=\"logo\" src=\"files/adminfiles/logo_newcomment.gif\" />"
							."</a></div></div><br />\n";
						}
					if (strlen($ok_msg))
						echo "<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />\n";
					if (strlen($alert_msg))
						echo "<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />\n";

				?>
				<noscript>
					<div class="method"><div class="note_invalid">Please activate javascript for the proper performance.</div></div>
					<br />
				</noscript>
				<table width="100%" cellspacing="0" cellpadding="0" style="position: relative;"><tr>
					<td width="36%" valign="top">
						<div class="method">
							<span class="name"><span class="dot">&#149;</span>Manage Works:</span><br />
							<a href="?page=photos"    ><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_photos.gif"   />Manage Photos</a>
								<span style="color: #789"> [ <a href="?page=photos&cmd=doAdd">Add</a> ]</span><br />
							<a href="?page=categories"><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_categs.gif"   />Manage Categories</a><br />
							<a href="?page=stories"   ><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_stories.gif"  />Manage Stories</a><br />
							<a href="?page=comments"  ><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_comments.gif" />Manage Comments</a><br />
						</div>
					</td>
					<td width="32%" valign="top">
						<div class="method">
							<span class="name"><span class="dot">&#149;</span>Basic Actions:</span><br />
							<a href="?page=basis"     ><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_preferences.gif" />Adjust Preferences</a><br />
							<a href="?page=changepass"><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_eye.gif" />Change Password</a><br />
							<br style="margin-top: 6px;" />
							<a href="?page=logout"    ><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_logout.gif" />Log Out</a><br />
						</div>
					</td>
					<td width="32%" valign="top">
						<div class="method">
							<span class="name"><span class="dot">&#149;</span>Technical Actions:</span><br />
							<a href="?page=configs"   ><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_configs.gif" />Advanced Configurations</a><br />
							<a href="?page=editxml"   ><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_xml.gif" />XML Editor</a><br />
							<br style="margin-top: 6px;" />
							<a href="?page=uninstall"  ><img width="20px" height="20px" class="logo" src="files/adminfiles/logo_uninstall.gif" />Uninstall Phormer</a><br />
						</div>
					</td>
				</tr></table>
			</div>
		</div>
<?php
	}

	if (page_is('wrong')) {
?>
		<div class="clearer" style="margin-top: 57px;"></div>
		<div class="method" style="text-align: left">
			<span class="name">Login to Phormer</span>
			<div class="inside">
				<form method="post" action="admin.php<?php if (strlen($goafterlogin)) echo "?page=$goafterlogin"; ?>">
					<center>
						<?php if (strlen($alert_msg)) echo "<div class=\"note_invalid\">$alert_msg</div><br />"; ?>
						Administrator's password: <input name="passwd" type="password" class="text"></input>
						<br /><br />
						<input class="submit" type="submit" value="&nbsp;&nbsp;&nbsp;Login&nbsp;&nbsp;&nbsp;"></input>
					</center>
				</form>
			</div>
		</div>
<?php
	}

	if (page_is('uninstall')) {
		if (!isset($_GET['sure']))
			$_GET['sure'] = 0;

		$ok_msg = "";
		$log_msg = "";
		if ($_GET['sure'] >= 2) {
			$dirs = array('temp', 'images/extraz', 'images', 'data', 'index.xml');
			foreach ($dirs as $dir) {
				if (is_dir($dir)) {
					if ($handle = opendir($dir)) {
						while (false !== ($file = readdir($handle)))
							if ($file != "." && $file != "..")
								unlink($dir."/".$file);
						closedir($handle);
						$log_msg .= "<span class=\"dot\">&#149;</span>Deleted all the files in &quot;<tt>$dir</tt>&quot; Directory.<br/ >\n";
						rmdir($dir);
						$log_msg .= "<span class=\"dot\">&#149;</span>Deleted &quot;<tt>$dir</tt>&quot; Directory.<br />\n";
					}
				} else
					if (file_exists($dir)) { // is file
						unlink($dir);
						$log_msg .= "<span class=\"dot\">&#149;</span>Deleted &quot;<tt>$dir</tt>&quot; File.<br />\n";
					}
			}

			$ok_msg = "Phormer is just uninstalled, successfully<br />\n"
					."This is the very last page of Phormer and it has removed all its files!<br />\n"
					."<br />\n"
					."Now, just <tt>rm -rf *</tt> the installation directory.<br />\n"
					."<br />\n"
					."Have fun!<br />\n"
					."<br />\n";
		}

?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery &gt;&gt; </a></div>
		<div class="back2main"><a href="?">&lt;&lt; Admin Page</a></div>
		<div class="clearer" style="margin-top: 27px;"></div>
		<?php
			if (strlen($ok_msg))
				echo "<div class=\"method\" style=\"text-align: left;\"><span class=\"name\">Phormer Uninstalled!</span>"
					."<div style=\"padding: 10px 40px 20px;\">$log_msg</div>"
					."<div class=\"note_valid\">$ok_msg</div></div><br /><br />";
			else {
		?>
		<div class="method" style="text-align: left">
			<span class="name">Uninstall Phormer</span>
			<div class="inside">
				<center>
					Are you
					<?php
						for ($i=0; $i<$_GET['sure']; $i++)
							echo "<span style=\"color: #900; font-weight: bold;\">REALLY</span> ";
					?>
					sure you want to uninstall phormer?!<br />
					<br />
					<span style="color: #C00; font-weight: bold; font-size: 12px;">Note: This command is not reversible! You'll Lose everything!</span>
					<br /><br />
					<form method="post" action="admin.php?page=uninstall&sure=<?php echo ($_GET['sure']+1); ?>">
						<input class="submit" style="background: #FCB;" type="submit" value="&nbsp;&nbsp;&nbsp;Yes, Dump Phormer!&nbsp;&nbsp;&nbsp;"></input>
					</form>
					&nbsp; &nbsp;
					<form method="post" action="admin.php?">
						<input class="submit" type="submit" value="&nbsp;&nbsp;&nbsp;No! Just Back to Main&nbsp;&nbsp;&nbsp;"></input>
					</form>
					<br />
					<br />
				</center>
			</div>
		</div>
<?php
		}
	}

if (page_is('install')) {
?>
		<div class="clearer" style="margin-top: 57px;"></div>
		<div class="method" style="text-align: left">
			<span class="name">First Time Installation</span>
			<div class="inside">
				<form method="post" action="?page=doinstall" onsubmit="javascript:return checkInstallPass();">
					<center>
						<div class="note_valid">Welcome to Phormer for the first time!</div>
						<span style="color:#567; font-weight: normal;">
							<?php echo $hasgd?'Fortunately, this server has PHPgd!':'Unfortunately, this server does not have PHPgd :('; ?>
						</span>
						<table width="50%" cellpadding="3">
<?php
							if (!is_writable('.')) {
								clearstatcache();
								echo "<div class=\"note_error\">"
										."<b>ERROR: "
										."<a href=\".\">Base directory</a> is not writable by PHP server.<br />\n"
										."</b>Just give it the a+w (write premission to all) access for installation and remove "
										."it when installation completed."
									."</div>";
							}
?>
							<?php if (strlen($alert_msg)) echo "<tr><td colspan=\"2\" class=\"note_invalid\">$alert_msg</td></tr>"; ?>
							<tr><td>Enter your desired Admin password:</td><td><input name="newpasswd" id="newpasswd" type="password" class="text" size="20"></input></td></tr>
							<tr><td>The desired Admin password, again:</td><td><input name="newpasswd2" id="newpasswd2" type="password" class="text" size="20"></input></td></tr>
							<tr><td colspan="2" style="text-align: center"><input class="submit" type="submit" value="&nbsp;&nbsp;&nbsp;Submit&nbsp;&nbsp;&nbsp;"></input></td></tr>
						</table>
					</center>
				</form>
			</div>
		</div>
<?php
	}

if (page_is('doneinside')) {
?>
		<div class="clearer" style="margin-top: 57px;"></div>
		<div class="method" style="text-align: left">
			<span class="name">Action Performed</span>
			<div class="inside" style="padding: 0px 100px">
				<?php if (strlen($alert_msg)) echo "<div class=\"note_valid\">$alert_msg</div><br />"; ?>
				<span class="dot">&#149;</span><a href=".">Main Page</a><br />
				<span class="dot">&#149;</span><a href="?page=logout">Logout</a><br />
				<br />
			</div>
		</div>
<?php
	}

if (page_is('doneoutside')) {
?>
		<div class="clearer" style="margin-top: 57px;"></div>
		<div class="method" style="text-align: left">
			<span class="name">Action Performed</span>
			<div class="inside" style="padding: 0px 100px">
				<?php if (strlen($alert_msg)) echo "<div class=\"note_valid\">$alert_msg</div><br />"; ?>
				<span class="dot">&#149;</span><a href="?">Login</a><br />
				<span class="dot">&#149;</span><a href=".">View PhotoGallery</a><br />
				<br />
			</div>
		</div>
<?php
	}

	if (page_is('editxml')) {
		$theSrc = isset($_GET['src'])?$_GET['src']:(isset($_POST['src'])?$_POST['src']:'');
		$cmd = isset($_GET['cmd'])?$_GET['cmd']:'';
		$open = false;
		$f = '';
		$isOpen = (strcmp($cmd, 'open') == 0);
		$isRestore = (strcmp($cmd, 'restore') == 0);
		$isSave = (strcmp($cmd, 'save') == 0);
		if ((strlen($theSrc) > 0) && ($isOpen || $isRestore || $isSave)) {
			if ($isRestore) {
				if (!file_exists($theSrc.".bku"))
					$alert_msg = "No Backup of the file \"$theSrc\" is available :(!";
				else {
					copy($theSrc.".bku", $theSrc);
					$ok_msg = "Backup successfully restored!";
				}
			}
			else if ($isOpen) {
				if (!file_exists($theSrc))
					$alert_msg = "File not exists!";
				else {
					$f = file_get_contents($theSrc);
					$open = true;
					//$f = strtr($f, $transtable);
					$f = htmlspecialchars($f, ENT_COMPAT, 'UTF-8');
				}
			}
			else if ($isSave) {
				copy($theSrc, $theSrc.".bku");
				$f = fopen($theSrc, "w");
				//echo $_POST['txt']."\n";
				//$_POST['txt'] = strtr($_POST['txt'], $transtable);
				fputs($f, $_POST['txt']);
				fclose($f);
				$ok_msg = "Changes Saved successfully!";
			}
		}
		$defAdd = ((strlen($theSrc)>0) && (strlen($alert_msg) == 0))?$theSrc:"data/categories.xml";
?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery &gt;&gt; </a></div>
		<div class="back2main"><a href="?">&lt;&lt; Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?page=editxml">XML Editor:</a></div>
			<div class="inside">
				<?php if (strlen($alert_msg)) echo "<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
				<?php if (strlen($ok_msg)) echo "<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<table cellspacing="0" cellpadding="0" width="100%" style="position:relative"><tr><td width="50%">
					<div class="method">
						<span class="name">Edit Content:</span><br />
						<table width="100%" cellspacing="0" cellpadding="0">
							<tr><td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=open&src=data%2Fcategories.xml">Categories</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=open&src=data%2Fstories.xml">Stories</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=open&src=data%2Fphotos.xml">Photos</a>
							</td>
							</tr><tr>
							<td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=open&src=data%2Fcomments.xml">Comments</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=open&src=data%2Fbasis.xml">Basis</a>
							</td><td>
							</td></tr>
						</table>
						<center><hr size="1" color="#BBB" width="60%" style="margin: 15px 0px;" /></center>
						<form method="get" action="?" onsubmit="return true;">
							<input type="hidden" name="page" value="editxml"></input>
							<input type="hidden" name="cmd" value="open"></input>
							<span class="dot">&#149;</span>XML Source:
							<input name="src" class="text" size="35" type="text" value="<?php echo $defAdd; ?>"></input>
							<br />
							<center><input style="margin-top: 15px;" class="submit" type="submit" value="Open the File"></input></center>
						</form>
					</div>
				</td><td width="50%">
					<div class="method">
						<span class="name">Restore Backup:</span><br />
						<table width="100%" cellspacing="0" cellpadding="0">
							<tr><td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=restore&src=data%2Fcategories.xml">Categories</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=restore&src=data%2Fstories.xml">Stories</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=restore&src=data%2Fphotos.xml">Photos</a>
							</td>
							</tr><tr>
							<td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=restore&src=data%2Fcomments.xml">Comments</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?page=editxml&cmd=restore&src=data%2Fbasis.xml">Basis</a>
							</td><td>
							</td></tr>
						</table>
						<center><hr size="1" color="#BBB" width="60%" style="margin: 15px 0px;" /></center>
						<form method="get" action="?" onsubmit="return ConfirmRestore();">
							<span class="dot">&#149;</span>XML Source:
							<input type="hidden" name="page" value="editxml"></input>
							<input type="hidden" name="cmd" value="restore"></input>
							<input name="src" class="text" size="35" type="text" value="<?php echo $defAdd; ?>"></input>
							<br />
							<center><input style="margin-top: 15px;" class="submit" type="submit" value="Restore Backup"></input></center>
						</form>
					</div>
				</td></tr></table>
				<?php if ($open) { ?>
				<div class="method" style="margin-top: 25px;">
					<span class="name">XML Content of "<tt><?php echo $theSrc; ?></tt>":</span><br />
					<center>
						<form enctype="multipart/form-data" method="post" action="?page=editxml&cmd=save" onsubmit="return ConfirmSave();">
							<textarea class="textarea" rows="10" cols="70" name="txt"><?php echo $f; ?></textarea><br /><br />
							<input class="text" type="hidden" name="src" value="<?php echo $theSrc; ?>"></input>
							<input class="submit" type="submit" value="&nbsp;Save Changes&nbsp;"></input>
							<span style="padding-left: 20px;"></span>
							<input class="reset" type="reset" value="&nbsp;Reset Changes &nbsp;"></input>
						</form>
					</center>
				</div>
				<?php } ?>
			</div>
		</div>
<?php
	}

if (page_is('categories')) {
	$categs = array();
	$edit = false;
	$doDel = false;
	$ok_msg = '';
	$alert_msg = '';
	$cmd = '';
	$cid = '';
	parse_container('categs', 'Category', 'data/categories.xml');
	handle_container('categs', 'Category', 'c');
	if (!strlen($alert_msg) && strlen($cmd))
		save_container('categs', 'Category', 'data/categories.xml');
	print_container('categs', 'Category', 'Categories', 'c');
?>
<?php
	}

if (page_is('stories')) {
	$stories = array();
	$edit = false;
	$doDel = false;
	$ok_msg = '';
	$alert_msg = '';
	$cmd = '';
	$sid = '';
	parse_container('stories', 'Story', 'data/stories.xml');
	handle_container('stories', 'Story', 's');
	if (!strlen($alert_msg) && strlen($cmd))
		save_container('stories', 'Story', 'data/stories.xml');
	print_container('stories', 'Story', 'Stories', 's');
?>

<?php
	}

if (page_is('basis') || page_is('configs')) {
	if (isset($_GET['cmd'])) {
		$cmd = $_GET['cmd'];
		if (strcmp($cmd, 'save') == 0) {
			if (!isset($_POST['extra']) && !isset($_POST['nLink']))
				$alert_msg = "The page is corrupted, come again!";
			else {
				if (isset($_POST['extra']))
					$_POST['extra'] = htmlspecialchars($_POST['extra'], ENT_NOQUOTES, "UTF-8");

				foreach ($_POST as $key => $value)
					$basis[$key] = $value;
				if (page_is('basis')) {
					$basis['links'] = array();
					while (NULL !== array_pop($basis['links']));
					for ($i=0; $i<$_POST['nLink']; $i++)
						array_push($basis['links'],
							array('name' => $_POST["l${i}n"], 'href' => $_POST["l${i}h"], 'title' => $_POST["l${i}t"]));
				}
				$ok_msg = "Changes to ".(page_is('basis')?"basis":"configurations").", saved successfully!";
				# echo "<pre>".print_r($basis, true)."</pre>";
				save_container('basis', 'Basis', 'data/basis.xml');
				if (isset($_GET['firsttime']))
					header('Location:?page=welcome');
			}
		}
		else
			$alert_msg = $cmd.' is not a valid command!';
	}
}

if (page_is('basis')) {
?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery &gt;&gt; </a></div>
		<div class="back2main"><a href="?">&lt;&lt; Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?page=basis">Preferences:</a></div>
			<div class="inside">
				<?php if (strlen($alert_msg)) echo "<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
				<?php if (strlen($ok_msg)) echo "<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<div class="method">
					<span class="name">Basic Preferences:</span><br />
					<form enctype="multipart/form-data" method="post" action="?page=basis&cmd=save<?php if (isset($firsttime) && ($firsttime)) echo "&firsttime=true"; ?>">
					<center>
						<table width="80%" cellspacing="0" cellpadding="2" style="text-align: left; position: relative; top: -10px;">
						<tr><td colspan="2">
							<div class="basisTitle" style="border-top-width: 0px;">PhotoGallery's ...</div>
						</td></tr>

						<tr><td>
							<span class="dot">&#149;</span>Name:
						</td><td>
							<input name="pgname" class="text" size="30" type="text" value="<?php echo $basis['pgname']; ?>"></input>
						</td></tr>
						<tr><td valign="top">
							<span class="dot">&#149;</span>Description:
						</td><td>
							<textarea name="pgdesc" class="textarea" cols="40" type="text" rows="3"><?php echo $basis['pgdesc']; ?></textarea>
						</td></tr>
						<tr><td>
							<span class="dot">&#149;</span>Index mode:
						</td><td>
							<span style="padding-left: 7px;"></span>
							<select id="mode" name="mode" class="select" size="1" onchange="javascript:changePrev(this.value, '');">
							<?php
								function isBM($x) {
									global $basis;
									return (strcmp($x, $basis['mode']) == 0)?" selected=\"selected\"":"";
								}
							?>
								<option<?php echo isBM("box");          ?> value="box"         >Box: A Jungle of Overlapping Thumbnails</option>
								<option<?php echo isBM("stories");      ?> value="stories"     >Stories: Recently Added Stories</option>
								<option<?php echo isBM("first-all");    ?> value="first-all"   >Last Added Photo, then Recently Addeds</option>
								<option<?php echo isBM("first-rate");   ?> value="first-rate"  >Last Added Photo, then Top Rateds</option>
								<option<?php echo isBM("first-hits");   ?> value="first-hits"  >Last Added Photo, then Most Visiteds</option>
								<option<?php echo isBM("first-recent"); ?> value="first-recent">Last Added Photo, then Recently Visiteds</option>
								<option<?php echo isBM("all-rate");     ?> value="all-rate"    >Recently Added Photos, then Top Rateds</option>
								<option<?php echo isBM("all-hits");     ?> value="all-hits"    >Recently Added Photos, then Most Visiteds</option>
								<option<?php echo isBM("all-recent");   ?> value="all-recent"  >Recently Added Photos, then Recently Visiteds</option>
							</select>
							<span style="padding-left: 10px;"></span>
							[<a target="_blank" id="prevmode1" href=".?mode=<?php echo $basis['mode']; ?>">Preview</a>]
						</td></tr>

						<tr><td>
							<span class="dot">&#149;</span>Theme:
						</td><td>
							<span style="padding-left: 7px;"></span>
							<select id="theme" name="theme" class="select" size="1" onchange="javascript:changePrev(this.value, '');">
							<?php
								$styles = array('white',	'wheat', 	'silver', 	'dusty', 	'sky',
												'bog', 		'purple',	'slate', 	'timber', 	'blacky');
								foreach ($styles as $theme) {
									$sel = (strcmp($theme, $basis['theme']) == 0);
									echo "\t\t\t\t\t\t<option ".($sel?"selected=\"selected\"":"")."value=\"$theme\">".$theme."</option>\n";
								}
							?>
							</select>
							<span style="padding-left: 10px;"></span>
							[<a target="_blank" style="" id="prevmode2" href=".?theme=<?php echo $basis['theme']; ?>">Preview</a>]
						</td></tr>

						<tr><td colspan="2">
							<div class="basisTitle">Author's...</div>
						</td></tr>

						<tr><td>
							<span class="dot">&#149;</span>Name:
						</td><td>
							<input name="auname" class="text" size="30" type="text" value="<?php echo $basis['auname']; ?>"></input>
						</tr>
						<tr><td>
							<span class="dot">&#149;</span>Email:
						</td><td>
							<input name="auemail" class="text" size="30" type="text" value="<?php echo $basis['auemail']; ?>"></input>
						</tr>
						<tr><td valign="top">
							<span class="dot">&#149;</span>Display Email:
						</td><td>
							&nbsp;&nbsp;
							<select name="showemail" class="select" size="1">
							<?php
								global $basis;
								if (!isset($basis['showemail']))
									$basis['showemail'] = 'link';
								$values = array('asis', 'link', 'text', 'hide');
								foreach ($values as $v)
									$sel[$v] = ($basis['showemail'] == $v)?" selected=\"selected\"":"";
							?>
								<option value="asis"<?php echo $sel['asis']; ?>>mailto: link (e.g. mailto:me@here.com)</option>
								<option value="link"<?php echo $sel['link']; ?>>Modified mailto link (e.g. mailto:me[at]here[dot]com)</option>
								<option value="text"<?php echo $sel['text']; ?>>Simple text (e.g. me[at]here[dot]com)</option>
								<option value="hide"<?php echo $sel['hide']; ?>>Just hide it private!</option>

							</select>
						</td></tr>

						<tr><td colspan="2">
							<div class="basisTitle">Word Vercification:
								<span style="font-weight:normal; color: #444;">
									(Last chance to stand against spam-commenters!)
								</span>
							</div>
						</td></tr>
						<tr><td valign="top">
							<span class="dot">&#149;</span>Word Verify:
						</td><td>
							&nbsp;&nbsp;
							<select name="haswvw" class="select" size="1">
							<?php
								global $basis;
								if (!isset($basis['haswvw']))
									$basis['haswvw'] = "yes";

								$sel = ("yes" == $basis['haswvw'])?" selected=\"selected\"":"";
								echo "\t\t\t\t<option$sel value=\"yes\">Enabled</option>\n";
								$sel = ("no" == $basis['haswvw'])?" selected=\"selected\"":"";
								echo "\t\t\t\t<option$sel value=\"no\">Disabled</option>\n";
							?>
							</select>
						</td></tr>

						<tr><td colspan="2">
							<div class="basisTitle">External Links &#133;
								<span style="font-weight:normal; color: #444;">
									(Tip: Links with empty Link-URL field, would be assumed as headers)
								</span>
							</div>
						</td></tr>

						<tr><td colspan="2">
							<table width="100%" cellspacing="0" cellpadding="2">
								<tr style="text-align: center; font-weight: bold;">
									<td> No </td><td> Link Name </td> <td> Link URL </td> <td> Link Title </td><td> Add/Del </td>
								</tr>
<?php
	$n = 0;
	reset($basis['links']);
	while (list($key, $val) = each($basis['links']))
		writeLinkLine($n++, $val);
	for ($i=$n; $i<MAX_LINKS; $i++)
		writeLinkLine($i, "", "none");
	reset($basis['links']);
?>
								</tr>
							</table>
							<input type="hidden" name="nLink" id="nLink" value="<?php echo $n; ?>"></input>
						</td></tr>
					</table>
					<center><input style="margin-top: 15px;" class="submit" type="submit" value="Save Changes"></input></center>
					<div class="basisTitle" style="border-top-width: 0px;"></div>
					</center>
					</form>
				</div>
			</div>
		</div>
<?php
}

if (page_is('configs')) {
	global $basis;
?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery &gt;&gt; </a></div>
		<div class="back2main"><a href="?">&lt;&lt; Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?page=configs">Configurations:</a></div>
			<div class="inside">
				<?php if (strlen($alert_msg)) echo "<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
				<?php if (strlen($ok_msg)) echo "<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<div class="method">
					<span class="name">Advanced Configurations:</span><br />
					<form enctype="multipart/form-data" method="post" action="?page=configs&cmd=save<?php if (isset($firsttime) && ($firsttime)) echo "&firsttime=true"; ?>">
					<center>
						<table width="80%" cellspacing="0" cellpadding="2" style="text-align: left; position: relative; top: -10px;">


							<tr><td colspan="2">
								<div class="basisTitle" style="border-top-width: 0px;">Default Number of items &#133;
									<span style="font-weight:normal; color: #444;">(Default thumbnails / stories count in various areas)</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Photos in Box Mode :
							</td><td>
								&nbsp;&nbsp;
								<select name="defjbn" class="select" size="1">
								<?php
									if (!isset($basis['defjbn']))
										$basis['defjbn'] = 50;
									$values = array(5, 10, 20, 50, 75, 100, 200, 1000);
									foreach ($values as $v) {
										$sel = ($basis['defjbn'] == $v)?" selected=\"selected\"":"";
										echo "\t\t\t\t\t<option value=\"$v\"$sel>$v</option>\n";
									}
								?>
								</select>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Photos in Recent Photos:
							</td><td>
								&nbsp;&nbsp;
								<select name="defrpc" class="select" size="1">
								<?php
									if (!isset($basis['defrpc']))
										$basis['defrpc'] = 20;
									$values = array(5, 10, 15, 20, 25, 30, 40, 50, 75, 100, 200, 1000);
									foreach ($values as $v) {
										$sel = ($basis['defrpc'] == $v)?" selected=\"selected\"":"";
										echo "\t\t\t\t\t<option value=\"$v\"$sel>$v</option>\n";
									}
								?>
								</select>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Photos in Top Rated/Visited:
							</td><td>
								&nbsp;&nbsp;
								<select name="deftrc" class="select" size="1">
								<?php
									if (!isset($basis['deftrc']))
										$basis['deftrc'] = 10;
									$values = array(5, 10, 15, 20, 25, 30, 40, 50, 75, 100, 200, 1000);
									foreach ($values as $v) {
										$sel = ($basis['deftrc'] == $v)?" selected=\"selected\"":"";
										echo "\t\t\t\t\t<option value=\"$v\"$sel>$v</option>\n";
									}
								?>
								</select>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Stories in Story mode:
							</td><td>
								&nbsp;&nbsp;
								<select name="defrsc" class="select" size="1">
								<?php
									if (!isset($basis['defrsc']))
										$basis['defrsc'] = 5;
									$values = array(1, 3, 5, 10, 20, 50, 1000);
									foreach ($values as $v) {
										$sel = ($basis['defrsc'] == $v)?" selected=\"selected\"":"";
										echo "\t\t\t\t\t<option value=\"$v\"$sel>$v</option>\n";
									}
								?>
								</select>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Stories in Sidebar:
							</td><td>
								&nbsp;&nbsp;
								<select name="defrss" class="select" size="1">
								<?php
									if (!isset($basis['defrss']))
										$basis['defrss'] = 100;
									$values = array(1, 3, 5, 10, 20, 30, 50, 75, 100, 1000);
									foreach ($values as $v) {
										$sel = ($basis['defrss'] == $v)?" selected=\"selected\"":"";
										echo "\t\t\t\t\t<option value=\"$v\"$sel>$v</option>\n";
									}
								?>
								</select>
							</td></tr>



							<tr><td colspan="2">
								<div class="basisTitle">Thumbs' Opacity Percent
									<span style="font-weight:normal; color: #444;">
										(More percentage, More darkness on thumbs)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Transparency Percentage:
							</td><td>
								&nbsp;&nbsp;
								<select name="opac" class="select" size="1">
								<?php
									global $basis;
									if (!isset($basis['opac']))
										$basis['opac'] = DEFAULT_OPAC_RATE;
									for ($i=0; $i<=100; $i += 10) {
										$sel = ($i == $basis['opac'])?" selected=\"selected\"":"";
										echo "\t\t\t\t<option$sel value=\"$i\">$i %</option>\n";
									}
								?>
								</select>
							</td></tr>


							<tr><td colspan="2">
								<div class="basisTitle">Jpeg Compression Ratio
									<span style="font-weight:normal; color: #444;">
										(More percentage, More Quality, More Size!)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Jpeg Quality Percentage:
							</td><td>
								&nbsp;&nbsp;
								<select name="jpegq" class="select" size="1">
								<?php
									global $basis;
									if (!isset($basis['jpegq']))
										$basis['jpegq'] = DEFAULT_JPEG_QUAL;
									for ($i=0; $i<=100; $i += 10) {
										$sel = ($i == $basis['jpegq'])?" selected=\"selected\"":"";
										echo "\t\t\t\t<option$sel value=\"$i\">$i %</option>\n";
									}
								?>
								</select>
							</td></tr>


							<tr><td colspan="2">
								<div class="basisTitle">Thumbnails' Clicking Target
									<span style="font-weight:normal; color: #444;">
										(To open in new window or Not to open in new window!)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Links Target:
							</td><td>
								&nbsp;&nbsp;
								<select name="linktarget" class="select" size="1">
								<?php
									global $basis;
									if (!isset($basis['linktarget']))
										$basis['linktarget'] = 'default';
									$values = array('_blank', '_self', 'default');
									foreach ($values as $v)
										$sel[$v] = ($basis['linktarget'] == $v)?" selected=\"selected\"":"";
								?>
									<option value="default"<?php echo $sel['default']; ?>>Default: As Phormer suggest</option>
									<option value="_blank" <?php echo $sel['_blank' ]; ?>>New window: Always open in New window</option>
									<option value="_self"  <?php echo $sel['_self'  ]; ?>>Same window: Always open in same window</option>
								</select>
							</td></tr>


							<tr><td colspan="2">
								<div class="basisTitle">Pick Random Neighbours From
									<span style="font-weight:normal; color: #444;">
										(Choose which photos appear below a photo while viewing)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Pick Neighbours :
							</td><td>
								&nbsp;&nbsp;
								<select name="pickneigh" class="select" size="1">
								<?php
									global $basis;
									if (!isset($basis['pickneigh']))
										$basis['pickneigh'] = 'all';
									$values = array('all', 'categs', 'stories');
									foreach ($values as $v)
										$sel[$v] = ($basis['pickneigh'] == $v)?" selected=\"selected\"":"";
								?>
									<option value="all"     <?php echo $sel['all'    ]; ?>>Both from its Category and Story</option>
									<option value="categs"  <?php echo $sel['categs' ]; ?>>Only from its Category</option>
									<option value="stories" <?php echo $sel['stories']; ?>>Only from its Story</option>
								</select>
							</td></tr>


							<tr><td colspan="2">
								<div class="basisTitle">HTML Page's Icon
									<span style="font-weight:normal; color: #444;">
										(Leave URL or blank if want to use no icon)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>HTML icon URL:
							</td><td>
								<input name="icon" class="text" size="50" type="text" value="<?php echo (isset($basis['icon']))?$basis['icon']:""; ?>"></input>
							</td></tr>


							<tr><td colspan="2">
								<div class="basisTitle">Additional HTML Codes
									<span style="font-weight:normal; color: #444;">
										(external hit counters, ad-sense or etc)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Extra HTML Code:
							</td><td>
								<textarea name="extra" class="textarea" cols="35" type="text" rows="4"><?php echo $basis['extra']; ?></textarea>
							</td></tr>


							<tr><td colspan="2">
								<div class="basisTitle">Ban Unwanted Cmmentator's IPs
									<span style="font-weight:normal; color: #444;">
										(just paste the IPs, one per line)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Banned Unwanted IPs:
							</td><td>
								<textarea name="bannedip" class="textarea" cols="35" type="text" rows="4"><?php echo isset($basis['bannedip'])?$basis['bannedip']:''; ?></textarea>
							</td></tr>


						</table>
						<center><input style="margin-top: 15px;" class="submit" type="submit" value="Save Changes"></input></center>
						<div class="basisTitle" style="border-top-width: 0px;"></div>
					</center>
					</form>
				</div> <!-- method -->
			</div> <!-- inside -->
		</div> <!-- part -->
<?php
}

if (page_is('changepass')) {
?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery &gt;&gt; </a></div>
		<div class="back2main"><a href="?">&lt;&lt; Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?page=basis">Change Password:</a></div>
			<div class="inside">
				<?php if (strlen($alert_msg)) echo "<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
				<?php if (strlen($ok_msg)) echo "<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<div class="method" style="text-align: left">
					<span class="name">Change Administrator's Password</span>
					<div class="inside">
						<form method="post" action="?page=dochangepass" onsubmit="javascript:return checkChangePass();">
							<center>
								<table width="50%" cellpadding="3" style="text-align: left;">
									<tr><td>Current password:</td><td><input name="curpasswd" type="password" class="text" size="20"></input></td></tr>
									<tr><td>New password:</td><td><input name="newpasswd1" id="newpasswd1" type="password" class="text" size="20"></input></td></tr>
									<tr><td>New password, again:</td><td><input name="newpasswd2" id="newpasswd2" type="password" class="text" size="20"></input></td></tr>
									<tr><td colspan="2"></td></tr>
									<tr><td colspan="2" style="text-align: center"><input class="submit" type="submit" value="&nbsp;&nbsp;&nbsp;Change Password&nbsp;&nbsp;&nbsp;"></input></td></tr>
								</table>
							</center>
						</form>
					</div>
				</div> <!-- method -->
			</div> <!-- inside -->
		</div> <!-- part -->
<?php
	}

if (page_is('comments')) {
	$comments = array();
	$ok_msg = '';
	$alert_msg = '';
	$cmd = '';
	$iid = '';
	parse_container('comments', 'Comment', 'data/comments.xml');
	/*
	ksort($comments);
	echo "<div style=\"dir: ltr; text-align: left;\"><pre>";
	print_r($comments);
	echo "</pre></div>";
	*/
	save_container('comments', 'Comment', 'data/comments.xml');
	$stories = array();
	parse_container('stories', 'Story', 'data/stories.xml');
	parse_container('photos', 'Photo', 'data/photos.xml');

	if (isset($_GET['cmd'])) {
		$cmd = $_GET['cmd'];
		if (!isset($_GET['iid']) && !isset($_POST['iid']))
			$alert_msg = "Please enter CommentID as iid!";
		else {
			$iid = ((isset($_GET['iid']))?$_GET['iid']:$_POST['iid'])+0;
			if (strcmp($cmd, 'del') == 0) {
				if (!isset($comments[$iid]) || !is_array($comments[$iid]))
					$alert_msg = "No Comment with this CommentID (cid: $iid) exists!";
				else {
					$ok_msg = $comments[$iid]['name']."'s Comment (CommentID: $iid) deleted successfully!";
					unset($comments[$iid]);
					save_container('comments', 'Comment', 'data/comments.xml');
				}
			}
			else
				$alert_msg = $cmd.' is not a valid command!';
		}
	}

	end($comments);
	$basis['lastcmntseen'] = $comments['lastiid'];
	save_container('basis', 'Basis', 'data/basis.xml');
	reset($comments);

	$n = isset($_GET['n'])?$_GET['n']:10;
	if ($n < 0)
		$n = count($comments);

	$st = isset($_GET['st'])?$_GET['st']:0;
	$st = max($st, 0);
	$st = min($st, count($comments)-1);
?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery &gt;&gt; </a></div>
		<div class="back2main"><a href="?">&lt;&lt; Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?page=comments">Manage Comments:</a></div>
			<div class="inside">
<?php if (strlen($alert_msg)) echo "\t\t\t\t<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
<?php if (strlen($ok_msg))    echo "\t\t\t\t<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<div class="method">
					<span class="name">Recent <?php echo $n; ?> Comments  &nbsp;: :&nbsp; Per Page
												[ <a href="?page=comments&n=5">5</a> |
												<a href="?page=comments&n=10">10</a> |
												<a href="?page=comments&n=20">20</a> |
												<a href="?page=comments&n=50">50</a> |
												<a href="?page=comments&n=100">100</a> |
												<a href="?page=comments&n=-1">All</a> ]
												&nbsp;: :&nbsp; [
												<a href="?page=comments&n=<?php echo "$n&st=".($st-$n); ?>">Prev Page</a> |
												<a href="?page=comments&n=<?php echo "$n&st=".($st+$n); ?>">Next Page</a> ]
					</span><br />
					<div style="padding-left: 30px">
					<table width="100%">
<?php
					$c = 0;
					for (end($comments); $c < $st; prev($comments))
						$c++;

					for ($c = 0; $aival = current($comments); prev($comments)){// a idea value!
						if (is_array($aival)) { 					// might be info!
							$aiid = key($comments);
							if ($c++ >= $n)
								break;
							sscanf($aival['owner'], "%c%d", $tl, $tv);
							$date = $aival['date'];
							if (strcmp($date, "00-01-01 00:00") == 0)
								$date = "";
							else
								$date .= " :: ";

							echo "\t\t\t\t\t\t\t<tr><td class=\"c\" width=\"20%\">";
							if ($tl == 'p')
								thumbBox($tv, '', true, true);
							else
								echo "Story <br /><a href=\".?s=$tv\">\"".$stories[$tv]['name']."\"</a>";
							echo "</td>\n";
							$infoatw = "";
							if (strlen($aival['email']) > 0)
								$infoatw .= "<a href=\"mailto:".$aival['email']."\">@</a>";
							if (strlen($aival['url']) > 0) {
								if (strlen($infoatw) > 0)
									$infoatw .= " | ";
								$infoatw .= "<a href=\"http://".$aival['url']."\">w</a>";
							}
							if (strlen($infoatw) > 0)
								$infoatw = " [ ".$infoatw." ] ";
							$en = textDirectionEn($aival['txt']);
							$dir = $en?"":"r";
							echo "\t\t\t\t\t\t\t<td><span style=\"padding-left: 20px;\" class=\"dot\">&#149;</span>"
								."<span class=\"categinfo\">"
								."$date</span> "
								.$aival['name']."&lrm; {".$aival['ip']."}".$infoatw
								." said: "
								."<span style=\"color: #333; \">"
								." [<a href=\"?page=$page&cmd=del&iid=$aiid\" onclick=\"javascript:return confirmDelete('".$comments[$aiid]['name']."'+'\'s comment');\">Delete</a>]"
								."</span><br />\n"
							."\t\t\t\t\t\t\t<div class=\"categdescnob$dir\">".nl2br($aival['txt'])."</div></td></tr>\n"
							."\t\t\t\t\t\t\t<tr><td colspan=\"2\"><hr coor=\"#BCD\" width=\"80%\" size=\"1\" /></td></tr>\n";
						}
					}
					reset($comments);
?>
					</table>
					</div>
				</div>
			</div>
		</div>
<?php
	}

	if (page_is('photos')) {
		$categs = array();
		$stories = array();
		$photos = array();
		parse_container('comments', 'Comment', 'data/comments.xml');
		parse_container('categs', 'Category', 'data/categories.xml');
		parse_container('stories', 'Story', 'data/stories.xml');
		parse_container('photos', 'Photo', 'data/photos.xml');
		$edit = false;
		$isdoAdd = false;
		$ok_msg = '';
		$alert_msg = '';
		$cmd = '';
		$pid = '';
		$photo = array();

		if (isset($_GET['cmd'])) {
			$cmd = $_GET['cmd'];
			$isAdd   = (strcmp($cmd, 'add')   == 0);
			$isEdt   = (strcmp($cmd, 'edt')   == 0);
			$isdoAdd = (strcmp($cmd, 'doAdd') == 0);
			$isdoEdt = (strcmp($cmd, 'doEdt') == 0);
			$isadCat = (strcmp($cmd, 'adddelC') == 0);
			$isadStr = (strcmp($cmd, 'adddelS') == 0);

			if ($isAdd)
				$pid = ++$photos['lastpid'];
			else
				$pid = (isset($_GET ['pid'])?$_GET ['pid']:
					   (isset($_POST['pid'])?$_POST['pid']+0:-1));
			$xmlfile = sprintf("data/p_%06d.xml", $pid);

			if (!($isAdd || $isdoAdd || isset($_GET['pid']) || isset($_POST['pid'])))
				$alert_msg = "Unknown command or Pid.<br />Please enter PhotoID as pid for the command \"".$cmd."\"!";
			else if ($isdoAdd)
				;
			else if (strcmp($cmd, 'del') == 0) {
				$ok_msg = "Photo \"".getPhotoInfo($pid, 'name')."\" (PID: $pid) deleted successfully!";
				deletePhoto($pid);
			}
			else if (!$isAdd && !file_exists($xmlfile))
				$alert_msg = "No photo with this PhotoID (pid: $pid) exists!";
			else if ($isdoEdt) {
					$edit = true;
					parse_container('photo', '', $xmlfile);
					$curppath = PHOTO_PATH.getImageFileName($pid, '9');
					$curthumb = substr_replace($curppath, '3', -5, 1);
			}
			else if ($isAdd || $isEdt) {
				if (!isset($_POST['name']))
					$alert_msg = "No Name! You shall come here from administration page only!";
				else {
					if ($isEdt) {
						parse_container('photo', '', $xmlfile);
						if (($t = array_search($pid, $stories[$photo['story']]['photo'])) !== FALSE)
							unset($stories[$photo['story']]['photo'][$t]);
						if (($t = array_search($pid, $categs[$photo['categ']]['photo'])) !== FALSE)
							unset($categs[$photo['categ']]['photo'][$t]);
						$postfix = isset($photo['postfix'])?$photo['postfix']:'';
					}
					else
						$postfix = $_POST['seed'];

					$photo = array('name' => $_POST['name'], 'desc' => $_POST['desc'],
								   'getcmnts' => $_POST['getcmnts'], 'dateadd' => $_POST['dateadd'],
								   'pid' => $pid, 'photoinfo' => $_POST['photoinfo'],
								   'categ' => $_POST['categ'], 'story' => $_POST['story'],
								   'datetake' => $_POST['datetake']
								   );
					$photo['postfix'] = $postfix;

					if ($isAdd || !in_array($pid, $categs[$photo['categ']]['photo']))
						array_push($categs[$photo['categ']]['photo'], $pid);
					if ($isAdd || !in_array($pid, $stories[$photo['story']]['photo']))
						array_push($stories[$photo['story']]['photo'], $pid);

					if (!isset($photos[$pid]))
						$photos[$pid] = "";
					$t = array();
					$t = explode(" ", $photos[$pid]);
					$photos[$pid]  = $_POST['hits']." ".(isset($t[2])?$t[2]:date("Y/m/d-G:i:s"));

					$postfix = (strlen($postfix)?"_":"").$postfix;
					$pid6let = sprintf("%06d%s_9.jpg", $pid, $postfix);
					$ppath = PHOTO_PATH.$pid6let;
					$curppath = $ppath;
					$curthumb = substr_replace($ppath, '3', -5, 1);

					$reget = ($isEdt && isset($_POST['regetSrc']) && (strcmp($_POST['regetSrc'], 'get') == 0));
					if ($reget)
						for ($ind=0; $ind<10; $ind++)
							if (file_exists(substr_replace($ppath, $ind, -5, 1)))
								unlink(substr_replace($ppath, $ind, -5, 1));

					if ($isAdd || $reget)
						$ppath = substr_replace($_POST['ImgUrl'], '9', -5, 1);

					if (!file_exists($ppath))
						$alert_msg = "The photo is not exist in the <a href=\"$ppath\">expected place</a>!";
					else {
						if ($isAdd || $reget) {
							copy($ppath, $curppath);
							unlink($ppath);
							$thumpath = substr_replace($ppath, '1', -5, 1);
							if (file_exists($thumpath))
								unlink($thumpath);
							$ppath = PHOTO_PATH.$pid6let;
						}
						if ($hasgd) {
							list($w, $h) = getimagesize($ppath);
							if ($w > $h) {
								if ($w > 640)
									makeTheThumb($ppath, 640, 'width', '0');
							} else
								if ($w > 480)
									makeTheThumb($ppath, 480, 'width', '0');
							makeTheThumb($ppath, 240, 'width', '1');
							makeTheThumb($ppath, 120, 'max', '2');
							if ($isAdd || ($isEdt && (isset($_POST['genThumb'])) && (strcmp($_POST['genThumb'], "gen") == 0)))
								gen_3_thumb($ppath, $_POST['sklW'], $_POST['sklH'], $_POST['sklT'], $_POST['sklL'], 75);
							makeTheThumb($ppath, 240, 'min', '4');
						}
						$ok_msg = "Photo \"<a href=\".?p=$pid\">".$photo['name']."</a>\" (pid: $pid) ".($isAdd?"added":"edited")." succesfully!";
						save_container('photo', 'Photo', $xmlfile);
					}
				}
				if (strlen($alert_msg) == 0) {
					save_container('categs', 'Category', 'data/categories.xml');
					save_container('stories', 'Story', 'data/stories.xml');
					save_container('photos', 'Photo', 'data/photos.xml');
				}
				build_rss();
			}  // if !($isAdd || $isEdt) {
			else if (!$isadCat && !$isadStr)
				$alert_msg = $cmd.' is not a valid command!';
			else {
				$isCAdd = (strcmp($_GET['tcmd'], 'add') == 0);
				$isCDel = (strcmp($_GET['tcmd'], 'del') == 0);
				$photo = getAllPhotoInfo($pid);
				$pname = $photo['name'];
				if ($isadCat) {
					$cid = $_GET['cid'];
					$cname = isset($categs[$cid])?$categs[$cid]['name']:'';
					if (!isset($categs[$cid]) || !is_array($categs[$cid]))
						$alert_msg = "No category with this CategoryID ($cid) exists!";
					else if ($isCDel && !in_array($pid, $categs[$cid]['photo']))
						$alert_msg = "The Photo \"$pname\" (pid: $pid) is not in the Category \"$cname\" (cid: $cid)!";
					else if ($isCDel && ($cid == $photo['categ']))
						$alert_msg = "The Photo \"$pname\" (pid: $pid) can not be deleted from its default Category!";
					else if ($isCAdd && in_array($pid, $categs[$cid]['photo']))
						$alert_msg = "The Photo \"$pname\" (pid: $pid) is already added in the Category \"$cname\" (cid: $cid)!";
					else {
						if ($isCAdd) {
							array_push($categs[$cid]['photo'], $pid);
							$ok_msg = "The Photo \"$pname\" (pid: $pid) added to Category \"$cname\" (cid: $cid), successfully!"
									." [<a title=\"Delete it!\" href=\"admin.php?page=photos&cmd=adddelC&tcmd=del&cid=$cid&pid=$pid\">undo!</a>]";
						}
						else {
							unset($categs[$cid]['photo'][array_search($pid, $categs[$cid]['photo'])]);
							$ok_msg = "The Photo \"$pname\" (pid: $pid) removed from Category \"$cname\" (cid: $cid), successfully!"
									." [<a title=\"Add again!\" href=\"admin.php?page=photos&cmd=adddelC&tcmd=add&cid=$cid&pid=$pid\">undo!</a>]";
						}
						save_container('categs', 'Category', 'data/categories.xml');
					}
				}
				else {
					$sid = $_GET['sid'];
					$sname = isset($stories[$sid])?$stories[$sid]['name']:'';
					if (!isset($stories[$sid]) || !is_array($stories[$sid]))
						$alert_msg = "No story with this StoryID ($sid) exists!";
					else if ($isCDel && !in_array($pid, $stories[$sid]['photo']))
						$alert_msg = "The Photo \"$pname\" (pid: $pid) is not in the Story \"$sname\" (sid: $sid)!";
					else if ($isCDel && ($sid == $photo['story']))
						$alert_msg = "The Photo \"$pname\" (pid: $pid) can not be deleted from its default Story!";
					else if ($isCAdd && in_array($pid, $stories[$sid]['photo']))
						$alert_msg = "The Photo \"$pname\" (pid: $pid) is already added in the Story \"$sname\" (sid: $sid)!";
					else {
						if ($isCAdd) {
							array_push($stories[$sid]['photo'], $pid);
							$ok_msg = "The Photo \"$pname\" (pid: $pid) added to Story \"$sname\" (sid: $sid), successfully!"
									." [<a title=\"Delete it!\" href=\"admin.php?page=photos&cmd=adddelS&tcmd=del&sid=$sid&pid=$pid\">undo!</a>]";;
						}
						else {
							unset($stories[$sid]['photo'][array_search($pid, $stories[$sid]['photo'])]);
							$ok_msg = "The Photo \"$pname\" (pid: $pid) removed from Story \"$sname\" (sid: $sid), successfully!"
									." [<a title=\"Add again!\" href=\"admin.php?page=photos&cmd=adddelS&tcmd=add&sid=$sid&pid=$pid\">undo!</a>]";;
						}
						save_container('stories', 'Story', 'data/stories.xml');
					}
				}
			}
		}
		$edit &= !strlen($alert_msg);
		$SklH = 75;
		$SklW = $SklH;
		$n = isset($_GET['n'])?$_GET['n']:3;

		$showConsole = !$edit && !$isdoAdd;

		$lastp = $photos['lastpid'];
		$dateTakenPrev = date("Y/m/d");
		$storyPrev = 1;
		$categPrev = 1;
		$cmntsPrev = "yes";
		if (photo_exists($lastp)) {
			#$dateTakenPrev = getPhotoInfo($lastp, 'datetake');
			$storyPrev = getPhotoInfo($lastp, 'story');
			$categPrev = getPhotoInfo($lastp, 'categ');
			$cmntsPrev = getPhotoInfo($lastp, 'getcmnts');
		}
		$curHits = '0 0/0';
		if ($edit) {
			$t = array();
			$t = explode(" ", $photos[$pid]);
			$curHits = $t[0]." ".$t[1];
		}
		$cmntsDefault = (strcmp($cmntsPrev, "yes") == 0);
		if ($edit)
			$cmntsDefault = (strcmp($photo['getcmnts'], "yes") == 0);

		//print_r($photos);
?>
		<script type="text/javascript" language="javascript" src="files/adminfiles/addphoto.js"></script>
		<script lanugage="javascript" type="text/javascript">
			var hasexif = <?php echo $hasexif; ?>;
		</script>

		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery &gt;&gt; </a></div>
		<div class="back2main"><a href="?">&nbsp;&nbsp;&lt;&lt; Admin Page&nbsp;&nbsp;&nbsp;</a></div>
		<noscript>
			<br />
			<div class="method"><div class="note_invalid">Please activate javascript for the proper performance.</div></div>
		</noscript>
<?php if (!$showConsole) { ?>
		<div class="clearer" style="margin-top: 15px;"> </div>
		<div class="back2main"><span style="padding-left: 13px"></span><a href="?page=photos"><< Manage Photos</a></div>
<?php
	}
	if (strlen($alert_msg)) echo "\t\t\t<div class=\"method\" style=\"margin-top: 20px;\"><div class=\"note_invalid\">$alert_msg</div></div>";
 	if (strlen($ok_msg))    echo "\t\t\t<div class=\"method\" style=\"margin-top: 20px;\"><div class=\"note_valid\">$ok_msg</div></div>";
 	if ($isdoAdd)			echo "\t\t\t<script language=\"javascript\" type=\"text/javascript\">setFakeDate(1);</script>\n";
 	if ($showConsole) {
?>
		<div class="part">
			<div class="title"><a style="color: white" href="?page=photos">Manage Photos:</a></div>
			<div class="inside">
				<table width="100%" cellspacing="0" cellpadding="0" style="position: relative;"><tr>
					<td width="50%" valign="top">
						<div class="method" style="margin-bottom: 1px;">
							<span class="name">
								<span class="lightdot">&#149;</span>
								<b>Add</b> photo ::
							</span><br />
							<span class="dot"><b>: :</b></span> <a href="?page=photos&cmd=doAdd">Add a new photo!</a>
						</div>
						<br />
						<div class="method">
							<span class="name">
								<span class="lightdot">&#149;</span>
								<b>Edit</b> Photo ::
								<?php echo $n; ?>
								Recent :
								<a href="?page=photos&n=3">[3]</a>
								<a href="?page=photos&n=6">[6]</a>
								<a href="?page=photos&n=12">[12]</a>
								<a href="?page=photos&n=24">[24]</a>
								<a href="?page=photos&n=48">[48]</a>
								<a href="?page=photos&n=96">[96]</a>
							</span><br />
<?php
	$cur = end($photos);
	for ($i=min(count($photos), $n); $i>0; $i--) {
		$thePid = key($photos);
		if (strcmp("lastpid",  $thePid) != 0) {
			echo "\t<div class=\"aThumbToEdit\">\n";
			thumbBox($thePid, '', true, true);

			echo "\t\t<a href=\"?page=photos&cmd=doEdt&pid=$thePid\">Edit</a>\n"
				."\t\t<span class=\"dot\">::</span>\n"
				."\t\t<a href=\"?page=photos&cmd=del&pid=$thePid\""
				." onclick=\"javascript: return ConfirmDelPhotoID(".$thePid.");\">Delete</a>\n"
				."\t</div>\n";
			$cur = prev($photos);
		}
	}
?>
							<div class="clearer"> </div>
						</div>
					</td>
					<td width="50%" valign="top">
						<table width="100%" cellspacing="0" cellpadding="0">
						<tr>
						<form method="get" action="?" onsubmit="return true;">
							<input type="hidden" name="page" value="photos"></input>
							<input type="hidden" name="cmd" value="doEdt"></input>
							<td width="50%" valign="top">
							<div class="method">
								<span class="name">Edit Later Photos:</span><br />
								<span class="dot">&#149;</span>PhotoID:
								<input name="pid" class="text" size="6" type="text" value="<?php echo (strlen($ok_msg))?$pid:''; ?>" autocomplete="off"></input>
								<br />
								<center><input style="margin: 10px 0px 5px" class="submit" type="submit" value="&nbsp;Edit &nbsp;it!&nbsp;"></input></center>
							</div>
							</td>
						</form>
						<form method="get" action="?" onsubmit="return ConfirmDelPhoto();">
							<input type="hidden" name="page" value="photos"></input>
							<input type="hidden" name="cmd" value="del"></input>
							<td>
							<div class="method">
								<span class="name">Delete Later Photos:</span><br />
								<span class="dot">&#149;</span>PhotoID:
								<input name="pid" id="piddel" class="text" size="6" type="text" value="" autocomplete="off"></input>
								<br />
								<center><input style="margin: 10px 0px 5px" class="submit" type="submit" value="Delete it!"></input></center>
							</div>
							</td>
						</form>
						</tr><tr>
						<form method="get" action="?">
							<input type="hidden" name="page" value="photos"></input>
							<input type="hidden" name="cmd" value="adddelC"></input>
							<td>
							<div class="method" style="margin-top: 21px">
								<span class="name">Add/Del Photo to Cat.:</span><br />
								<table cellspacing="0" cellpadding="0" width="100%"><tr>
								<td><span class="dot">&#149;</span>PhotoID:</td>
								<td><input name="pid" class="text" size="6" type="text" value="<?php echo (strlen($ok_msg))?$pid:''; ?>" autocomplete="off"></input></td>
								</tr><tr>
								<td><span class="dot">&#149;</span>CategID:</td>
								<td><input name="cid" class="text" size="6" type="text" value="" autocomplete="off"></input></td>
								</tr><tr>
								<td><span class="dot">&#149;</span>Command:&nbsp;</td>
								<td><select style="margin-left: 10px;" name="tcmd" class="select" type="text" onchange="dg('AddDelCSubmit').value=wordize(this.value)+' the Photo!';">
									<option value="add" selected="selected">Add</option>
									<option value="del">Del</option>
								</select></td>
								</tr><tr>
								<td colspan="2"><center><input style="margin: 10px 0px 5px" class="submit" type="submit" id="AddDelCSubmit" value="Add the Photo!"></input></center></td>
								</tr></table>
							</div>
							</td>
						</form>
						<form method="get" action="?">
							<input type="hidden" name="page" value="photos"></input>
							<input type="hidden" name="cmd" value="adddelS"></input>
							<td>
							<div class="method" style="margin-top: 21px">
								<span class="name">Add/Del Photo to Story:</span><br />
								<table cellspacing="0" cellpadding="0" width="100%"><tr>
								<td><span class="dot">&#149;</span>PhotoID:</td>
								<td><input name="pid" class="text" size="6" type="text" value="<?php echo (strlen($ok_msg))?$pid:''; ?>" autocomplete="off"></input></td>
								</tr><tr>
								<td><span class="dot">&#149;</span>StoryID:</td>
								<td><input name="sid" class="text" size="6" type="text" value="" autocomplete="off"></input></td>
								</tr><tr>
								<td><span class="dot">&#149;</span>Command:&nbsp;</td>
								<td><select style="margin-left: 10px;" name="tcmd" class="select" type="text" onchange="dg('AddDelSSubmit').value=wordize(this.value)+' the Photo!';">
									<option value="add" selected="selected">Add</option>
									<option value="del">Del</option>
								</select></td>
								</tr><tr>
								<td colspan="2"><center><input style="margin: 10px 0px 5px;" class="submit" type="submit" id="AddDelSSubmit" value="Add the Photo!"></input></center></td>
								</tr></table>
							</div>
							</td>
						</form>
						</tr>
						</table>
					</td>
				</tr></table>
			</div>
		</div>
<?php
	}
	else { // !showConsole
		if ($edit)
			$seed = isset($photo['postfix'])?$photo['postfix']:"";
		else {
			$seed = '';
			for ($i=0; $i < 5; $i ++)
				$seed .= chr(ord('a')+rand(0, 25));
		}
?>
		<form name="TheGlobalForm" enctype="multipart/form-data" method="post" action="?page=photos&cmd=<?php echo $edit?'edt':'add'; ?>" onsubmit="return CheckAddPhoto<?php echo $edit?'Time':''; ?>();">
		<input name="ImgUrl" id="theImgPath" class="text" size="40" type="hidden" readonly="readonly" value="<?php echo $edit?PHOTO_PATH.getImageFileName($pid, '9'):""; ?>"></input>
		<input name="seed" id="inputSeed" class="text" size="20" type="hidden" readonly="readonly" value="<?php echo $seed; ?>"></input>
		<div class="part" style="margin-top: 22px;">
			<div class="title">&#149; <a style="color: white" href="?page=photos<?php echo $edit?"&cmd=doEdt&pid=$pid":'&cmd=doAdd'; ?>"><?php echo $edit?"Edit Photo #$pid \"".$photo['name']."\"":'Add Photo'; ?>:</a></div>
			<?php if ($edit) { ?>
				<input name="pid" class="text" size="6" type="hidden" value="<?php echo $pid; ?>"></input>
			<?php } ?>
			<div class="inside">
				<div class="method" id="contUploadNow">
				<span class="name">Step 1 - Locate the Photo:</span>
				<?php if ($edit) { ?>
					<div style="margin-left: 20px;">
						<input type="radio" class="radio" name="regetSrc" value="noget" checked="checked" onclick="javascript:showElem('ThumbnailGenSelector');hideElem('gettingTheFileDiv');TheGlobalForm.genThumb[0].checked=true;"></input> Keep current file<br />
						<input type="radio" class="radio" name="regetSrc" value="get"  onclick="javascript:hideElem('ThumbnailGenSelector');TheGlobalForm.genThumb[1].checked=true;showElem('ThumbnailGenerator');showElem('gettingTheFileDiv');"></input> Upload a new file<br />
					</div>
				<?php } ?>
					<div id="gettingTheFileDiv" style="display: <?php echo $edit?"none":"block"; ?>">
						<center>
							<div class="note_wrapper" id="upload_uploading" style="display: none; margin: 27px 0px; position: relative;">
								<span class="note_content" id="upload_note">
									<img src="files/adminfiles/ind.gif" class="ind" alt="" /> &nbsp; &nbsp; &nbsp; <span id="upload_uploading_txt">Preparing the process...</span>
								</span>
							</div>
						</center>
						<iframe id="upload_iframe" class="upload_iframes" src="upload.php?seed=<?php echo $seed; ?>" frameborder="0"
							scrolling="no" marginheight="0" marginwidth="0" height="80" width="90%">
						</iframe>
					</div>
				</div>
			</div>
			<div class="inside">
				<table width="100%" cellspacing="0" cellpadding="0" style="position: relative;"><tr>
					<td width="50%" valign="top">
						<div class="method">
							<span class="name">Step 2.1 - Global data (required):</span><br />
							<table width="100%" cellpadding="3">
								<tr><td>Title:</td><td><input name="name" type="text" class="text" size="30" value="<?php echo $edit?$photo['name']:''; ?>" autocomplete="off"></input></td></tr>
								<tr><td valign="top">Description:</td><td><textarea cols="21" rows="5" name="desc"><?php echo $edit?$photo['desc']:''; ?></textarea></td></tr>
								<tr><td>Time Added:</td><td><input name="dateadd" id="dateadd" type="text" class="text" size="21" value="<?php echo $edit?$photo['dateadd']:date("Y/m/d H:i"); ?>" autocomplete="off"></input></td></tr>
								<tr><td>Get Comments:</td><td><span style="margin-left: 5px"></span><input <?php echo ($cmntsDefault)?'checked="checked"':''; ?> name="getcmnts" value="yes" type="radio" class="radio">Yes</input>
															  <span style="margin-left: 25px"></span><input <?php echo ($cmntsDefault)?'':'checked="checked"'; ?> name="getcmnts" value="no"  type="radio" class="radio">No</input></td></tr>
							</table>
						</div>
					</td>
					<td width="50%" valign="top">
						<div class="method">
							<span class="name">Step 2.2 - Special features (optional):</span>
							<table width="100%" cellpadding="4" cellspacing="1">
								<tr><td valign="top">Photo info:</td><td><textarea cols="20" rows="3" id="photoinfo" name="photoinfo"><?php echo $edit?$photo['photoinfo']:''; ?></textarea></td></tr>
								<tr><td>Default Category:</td><td><span style="margin-left: 10px"></span><select name="categ" class="select" size="1">
													<?php
														# $categs2 = array_reverse($categs, true);
														# array_pop($categs2);
														# array_pop($categs2);
														# $categs2 = array(1 => $categs[1]) + $categs2;
														$categs2 = $categs;
														reset($categs2);
														while (list($cid, $cvals) = each($categs2))
															if (is_array($cvals)) {
																$prv = (strlen($cvals['pass']))?'* ':'';
																$sel = $edit?($cid == $photo['categ']):($cid == $categPrev);
																echo "\t\t\t\t\t\t\t\t<option ".($sel?"selected=\"selected\"":"")."value=\"$cid\">".$cid.": ".$prv.cutNeck($cvals['name'])."</option>\n";
															}
													?>
												 </input></td></tr>
								<tr><td>Default Story:</td><td><span style="margin-left: 10px"></span><select name="story" class="select" size="1">
													<?php
														$stories2 = array_reverse($stories, true);
														array_pop($stories2);
														array_pop($stories2);
														$stories2 = array(1 => $stories[1]) + $stories2;
														reset($stories2);
														while (list($sid, $svals) = each($stories2))
															if (is_array($svals)) {
																$prv = (strlen($svals['pass']))?'* ':'';
																$sel = $edit?($sid == $photo['story']):($sid == $storyPrev);
																echo "\t\t\t\t\t\t\t\t<option ".($sel?"selected=\"selected\"":"")."value=\"$sid\">".$sid.": ".$prv.cutNeck($svals['name'])."</option>\n";
															}
													?>
												 </input></td></tr>
								<tr><td>Date Taken:</td><td><input id="datetake" name="datetake" type="text" class="text" size="21" onfocus="javascript:setFakeDate(0);" value="<?php echo $edit?$photo['datetake']:$dateTakenPrev; ?>"></input></td></tr>
								<tr><td>Hits & Rate:</td><td><input id="hits" name="hits" type="text" class="text" size="21" value="<?php echo $curHits; ?>" autocomplete="off"></input></td></tr>
							</table>
						</div>
					</td>
				</tr></table>
			</div>
			<a name="genThumb"></a>
			<div class="inside">
				<div class="method">
				<span class="name">Step 3 - Thumbnail:</span><br />
					<?php if ($edit) { ?>
					<div id="ThumbnailGenSelector">
						<input type="radio" class="radio" name="genThumb" value="nogen" checked="checked" onclick="javascript:hideElem('ThumbnailGenerator');showElem('currentThumb');"></input> Keep current thumbnail<br />
							<div id="currentThumb" style="margin: 5px 50px 10px;"><img border="1" src="<?php echo $curthumb; ?>" /></div>
						<input type="radio" class="radio" name="genThumb" value="gen"  onclick="javascript:showElem('ThumbnailGenerator');hideElem('currentThumb');"></input> Generate a new thumbnail<br />
					</div>
					<?php } ?>
					<center>
					<div class="note_wrapper" id="thumb_note_wrapper" style="display: block;">
						<span class="note_content" id="thumb_note">You have not uploaded anything! do it first. </span>
					</div>
					<div id="ThumbnailGenerator" style="display: none; padding: 5px; margin: 10px 0px;">
						<table width="80%" cellspacing="0" cellpadding="0" style="position: relative; "><tr>
							<td width="60%" valign="top">
							<center>
							<div id="thePhoto" style="background: #CDE url(''); width: 0px; height: 0px; margin: 0px 20px;"
								onmousemove="javascript:MouseMoveInside(event);">
								<div id="skeleton" style="top: 0px; left: 0px; width: <?php echo $SklW; ?>px; height: <?php echo $SklH; ?>px; "
									onmouseup="javascript:ReleaseMouse();" ondblclick="javascript:ExpandSkl();" onmousedown="javascript:MouseDownTheSkeleton(event);">
<?php
	$c = array("nw", "ne", "sw", "se");
	for ($i=0; $i<count($c); $i++)
		echo "\t\t\t\t\t\t\t\t\t\t\t<div id=\"c_$c[$i]\" class=\"corners\" onmousedown=\"javascript:MouseDown($i, event);\"></div>\n";
?>
								</div>
							</div>
							</center>
							</td>
							<td valign="top" style="display: none;">
								<div class="method" style="text-align: left;">
									<span class="name">Thumbnail Info:</span>
									<table width="100%" cellpadding="2" cellspacing="2">
											<tr style="display: none;"><td>Lock Ratio:</td><td><input id="keepRatio" class="checkbox" type="checkbox" checked="checked" onchange="javascript:SaveRatio();"></input></td></tr>
											<tr style="display: none;"><td>Picker's height:</td><td><input name="sklH" id="sklH" type="text" class="text" size="5" value="<?php echo $SklH; ?>"></input></td></tr>
										<tr><td>Picker's width :</td><td><input name="sklW" id="sklW" type="text" readonly="readonly" class="textTTRO" size="5" value="<?php echo $SklW; ?>"></input></td></tr>
										<tr><td>Picker's H Pos.:</td><td><input name="sklL" id="sklL" type="text" readonly="readonly" class="textTTRO" size="5" value="0"></input></td></tr>
										<tr><td>Picker's V Pos.:</td><td><input name="sklT" id="sklT" type="text" readonly="readonly" class="textTTRO" size="5" value="0"></input></td></tr>
									</table>
								</div>
								<br />
							</td>
							<td valign="top">
								<div class="method" style="text-align: left; ">
									<span class="name"><a style="color:black" href="#reputThumb" onclick="javascript:rethumb();">Thumbnail Preview:</a></span><br />
									<center>
										<div id="thumbPrevCont" style="<?php echo "width: $SklW"."px; height: $SklH"."px;"; ?>">
											<img id="thumbPrev" style="top: 0px; left: 0px;" src="" />
										</div>
									</center>
								</div>
							</td>
						</tr></table>
					</div>
					</center>
				</div>
			</div>

			<div class="inside">
				<div class="method" style="padding-bottom: 20px;">
					<span class="name">Step 4 - Submit:</span>
					<center>
						<span id="finallyAdd" style="display:none;">
							<input class="submit" type="submit" value="&nbsp;<?php echo $edit?'Save Changes&nbsp':'Add &nbsp;the&nbsp; Photo &nbsp'; ?>;"></input>
							<span style="padding-left: 20px;"></span>
							<input class="reset" type="reset" value="&nbsp;Reset Changes &nbsp;"></input>
						</span>
						</form>
						<span style="padding-left: 20px;"></span>
						<form enctype="multipart/form-data" method="post" action="?page=photos">
							<input class="reset" type="submit" value="Sorry, Just leave here!"></input>
						</form>
						<div class="clearer"></div>
					</center>
				</div>
			</div>
			<?php
				if ($edit && $hasgd) {
					$ppath = substr_replace($curppath, '4', -5, 1);
					if (file_exists($ppath))
						@list($w, $h) = getimagesize($ppath);
					else {
						$w = 0; $h = 0;
					}
					echo "\t\t\t\t<script type=\"text/javascript\" language=\"javascript\">\n"
						."\t\t\t\t\tImgPath = \"$ppath\";\n"
						."\t\t\t\t\tImgW = $w;\n"
						."\t\t\t\t\tImgH = $h;\n"
						."\t\t\t\t\timageUploaded(0);\n"
						."\t\t\t\t\thideElem('ThumbnailGenerator');\n"
						."\t\t\t\t</script>\n";
				}
			?>
		</div>
	</div>
<?php
		}
	}
?>

	<div class="footnote">
		<a href=".">This PhotoGallery</a> is powered by <a href="http://p.horm.org/er">Phormer</a>, <br />
		a simple <a href="http://php.net">PHP</a> Photo Gallery Manager, under
		<a href="http://gnu.org/licenses/gpl.txt">GPL</a>.
	</div>
</div> <!-- Granny -->
</center>
</body>
</html>