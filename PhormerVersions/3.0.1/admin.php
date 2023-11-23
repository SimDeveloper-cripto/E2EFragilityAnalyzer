<?php
	require_once('funcs.php');

	#session_start(); //useless and causes FF in Mac to deal with nothingness!
	global $hasgd;

	$alert_msg = '';
	$ok_msg = '';

	$up = '/'; // for the manner of cookies

	if (!file_exists(ADMIN_PASS_FILE)) {
		$mod = (isset($_GET['mod']) && (strcmp($_GET['mod'], 'doinstall') == 0))?'doinstall':'install';
	}
	else {
		if (isset($_COOKIE['phormer_passwd']) && auth_admin($_COOKIE['phormer_passwd'])) // AUTH: OK
			$mod = isset($_GET['mod'])?$_GET['mod']:'welcome';
		else {
			$invalidpasswd = (!isset($_POST['passwd'])) || (!auth_admin(md5($_POST['passwd'])));
			if ($invalidpasswd) {
				$goafterlogin = isset($_GET['mod'])?$_GET['mod']:"";
				$mod = 'wrong';
				if (isset($_POST['passwd']))
					$alert_msg = "Wrong password!";
			}
			else {
				setcookie('phormer_passwd', md5($_POST['passwd']), time()+3600*24, $up);
				$mod = isset($_GET['mod'])?$_GET['mod']:'welcome';
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

	if (mod_is('install') || mod_is('doinstall')) // came from $_GET['mod'] where user is authenticated!
		if (file_exists(ADMIN_PASS_FILE)) {
			$alert_msg = "Use 'change password' menu to change the password!";
			$mod = 'welcome';
		}

	if (mod_is('doinstall')) {
		if (!isset($_POST['newpasswd'])) {
			$alert_msg = 'All fields are required!';
			$mod = 'install';
		}
		else if (strlen($_POST['newpasswd']) < 4) {
			$alert_msg = 'Too short password!';
			$mod = 'install';
		}
		else if ((strcmp($_POST['newpasswd'], $_POST['newpasswd2']) != 0)) {
			$alert_msg = "Entered passwords was not identical. try again!";
			$mod = 'install';
		}
	}

	if (mod_is('dochangepass')) {
		if ((!isset($_POST['curpasswd'])) || (!isset($_POST['newpasswd1'])) || (!isset($_POST['newpasswd2']))) {
			$alert_msg = 'All fields are required!';
			$mod = 'basis';
		}
		else if (!auth_admin(md5($_POST['curpasswd']))) {
			$alert_msg = 'Wrong current password!';
			$mod = 'basis';
		}
		else if (strcmp($_POST['newpasswd1'], $_POST['newpasswd2']) != 0) {
			$alert_msg = 'New passwords are not identical!';
			$mod = 'basis';
		}
		else if (strlen($_POST['newpasswd1']) < 4) {
			$alert_msg = 'Too short password!';
			$mod = 'basis';
		}
	}

	$firsttime = false;

	if (mod_is('doinstall') || mod_is('dochangepass')) {
		if (mod_is('doinstall')) {
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

			if (file_exists("images/extraz"))
				chmod("images/extraz", 0711);
			else
				if (! mkdir("images/extraz", 0711))
					die("could not create images/extraz directory.");

			if ($handle = opendir('files/adminfiles/')) {
				while (false !== ($file = readdir($handle)))
					if ($file != "." && $file != ".." && (strcasecmp(substr($file, -4), ".def") == 0)) {
						if (! copy ("files/adminfiles/".$file, "data/".substr($file, 0, -4)))
							die("could not copy admin/files/$file to /data.");
						unlink("files/adminfiles/".$file);
					}
				closedir($handle);
			}
			$firsttime = true;
		}

		$pass = mod_is('doinstall')?$_POST['newpasswd']:$_POST['newpasswd1'];
		$fh = fopen(ADMIN_PASS_FILE, "w");
		fwrite($fh, $pass);
		fclose($fh);
		setcookie('phormer_passwd', md5($pass), time()+3600*24, $up);
		$_COOKIE['phormer_passwd'] = md5($pass);

		if (mod_is('doinstall'))
			$ok_msg = "Installation Completed!<br />Now, just fill the following fields and start adding photos.";
		else
			$ok_msg = "Administrator's password updated successfully!";

		$mod = 'basis';
		parse_container('basis', 'Basis', 'data/basis.xml');

		if (!chmod("data/adminPass.inf", 0600))
			$alert_msg = "Could not change adminPass.inf's premission. it might be readable by anyone!";
		else
			$alert_msg = "";
	}

	if (mod_is('logout')) {
		setcookie('phormer_passwd', " ", time()+60, $up);
		$_COOKIE['phormer_passwd'] = " ";
		$alert_msg = "Feel free to back, anytime you wished!<br />I'm waiting for you to rephorm me, again! ;)";
		$mod = 'doneoutside';
	}

	if (!mod_is('install')) {
		$basis = array();
		parse_container('basis', 'Basis', 'data/basis.xml');
	}

	### mod is fixed and verified till now,
	### so just do the cmd!

	if (array_search($mod, array('welcome', 'wrong', 'doneinside', 'doneoutside', 'logout', 'dochangepass', 'doinstall', 'install', 'doinstall', 'welcome',
								'photos', 'galleries', 'stories', 'basis', 'editxml', 'comments')) === FALSE) {
		$alert_msg = "Invalid mod ($mod)! Pick one below:";
		//$mod = 'welcome';
	}

	$afterfirsttime = isset($_GET['firsttime']);
	$noheader = $afterfirsttime; // ||
	if (!$noheader) {
	switch($mod) {
		case 'photos':		$modNameTitle = "Manage Photos"; 		break;
		case 'categories':	$modNameTitle = "Manage Categories"; 	break;
		case 'stories':		$modNameTitle = "Manage Stories"; 		break;
		case 'comments':	$modNameTitle = "Manage Comments"; 		break;
		case 'basis':		$modNameTitle = "Adjust Preferences"; 	break;
		case 'editxml':		$modNameTitle = "Edit XML files"; 		break;
		case 'doneoutside':
		case 'logout':		$modNameTitle = "Logging out"; 			break;
		case 'install':		$modNameTitle = "Installation Process";	break;
		case 'wrong':		$modNameTitle = "Login Page"; 			break;
		default: 			$modNameTitle = "Administration Region"; break;
	}
	if (isset($basis['pgname']))
		$modNameTitle .= " of ".$basis['pgname'];
	$headName = isset($basis['pgname'])?$basis['pgname']:"PhotoGallery";
	if (mod_is('basis') && isset($_POST['pgname']))
		$headName = $_POST['pgname'];
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="files/adminfiles/admin.css">
	<script type="text/javascript" language="javascript" src="files/adminfiles/admin.js"></script>
	<title><?php echo $modNameTitle; ?></title>
	<?php	if (mod_is('photos')) {	?>	<script type="text/javascript" language="javascript" src="files/adminfiles/skeleton.js"></script><?php	}	?>
</head>

<body <?php if (mod_is('photos')) echo "onmouseup=\"javascript:ReleaseMouse();\" onload=\"javascript:PrepareBody();\"" ?>>
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

	if (mod_is('welcome')) {
		if (!chmod("data/adminPass.inf", 0600))
			$alert_msg = "Could not change adminPass.inf's premission. it might be readable by anyone!";
		else
			$alert_msg = "";

?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery >> </a></div>
		<div class="clearer"></div>
		<div class="part" style="margin-top: 18px;">
			<div class="title"><a style="color: white" href="?mod=welcome">The Administration Region</a></div>
			<div class="inside">
				<?php
					if (strlen($alert_msg))
						echo "<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />";
					parse_container('comments', 'Comment', 'data/comments.xml');
					end($comments);
					$lastcmntseen = key($comments);
					if (is_int($lastcmntseen) && isset($basis['lastcmntseen']) && ($lastcmntseen > $basis['lastcmntseen'])) {
						$notseen = ($lastcmntseen - $basis['lastcmntseen']);
						if ($notseen == 1)
							$notseen = "one";
						echo "<div class=\"method\"><div class=\"note_valid\">"
							."<a href=\"?mod=comments\">You have $notseen new comment".(($notseen != "one")?"s":"")."!"
							."</div></div><br />";
						}
				?>
				<noscript>
					<div class="method"><div class="note_invalid">Please activate javascript for the proper performance.</div></div>
					<br />
				</noscript>
				<table width="100%" cellspacing="0" cellpadding="0" style="position: relative;"><tr>
					<td width="50%" valign="top">
						<div class="method">
							<span class="name">Manage Works:</span><br />
							<span class="dot">&#149;</span><a href="?mod=photos">Manage Photos</a>
								<span style="color: #789"> [ <a href="?mod=photos&cmd=doAdd">Add</a> ]</span><br />
							<span class="dot">&#149;</span><a href="?mod=categories">Manage Categories</a><br />
							<span class="dot">&#149;</span><a href="?mod=stories">Manage Stories</a><br />
							<span class="dot">&#149;</span><a href="?mod=comments">Manage Comments</a><br />
						</div>
					</td>
					<td width="50%" valign="top">
						<div class="method">
							<span class="name">Other Actions:</span><br />
							<span class="dot">&#149;</span><a href="?mod=basis">Preferences</a><br />
							<span class="dot">&#149;</span><a href="?mod=editxml">XML Editor</a><br />
							<br />
							<span class="dot">&#149;</span><a href="?mod=logout">Logout</a><br />
						</div>
					</td>
				</tr></table>
			</div>
		</div>
<?php
	}

	if (mod_is('wrong')) {
?>
		<div class="clearer" style="margin-top: 57px;"></div>
		<div class="method" style="text-align: left">
			<span class="name">Login to Phormer</span>
			<div class="inside">
				<form method="post" action="admin.php<?php if (strlen($goafterlogin)) echo "?mod=$goafterlogin"; ?>">
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

if (mod_is('install')) {
?>
		<div class="clearer" style="margin-top: 57px;"></div>
		<div class="method" style="text-align: left">
			<span class="name">First Time Installation</span>
			<div class="inside">
				<form method="post" action="?mod=doinstall" onsubmit="javascript:return checkInstallPass();">
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

if (mod_is('doneinside')) {
?>
		<div class="clearer" style="margin-top: 57px;"></div>
		<div class="method" style="text-align: left">
			<span class="name">Action Performed</span>
			<div class="inside" style="padding: 0px 100px">
				<?php if (strlen($alert_msg)) echo "<div class=\"note_valid\">$alert_msg</div><br />"; ?>
				<span class="dot">&#149;</span><a href=".">Main Page</a><br />
				<span class="dot">&#149;</span><a href="?mod=logout">Logout</a><br />
				<br />
			</div>
		</div>
<?php
	}

if (mod_is('doneoutside')) {
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

	if (mod_is('editxml')) {
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
					$f = strtr($f, $transtable);
				}
			}
			else if ($isSave) {
				copy($theSrc, $theSrc.".bku");
				$f = fopen($theSrc, "w");
				#echo $_POST['txt']."\n";
				fputs($f, stripslashes($_POST['txt']));
				fclose($f);
				$ok_msg = "Changes Saved successfully!";
			}
		}
		$defAdd = ((strlen($theSrc)>0) && (strlen($alert_msg) == 0))?$theSrc:"data/categories.xml";
?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery >> </a></div>
		<div class="back2main"><a href="?"><< Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?mod=editxml">XML Editor:</a></div>
			<div class="inside">
				<?php if (strlen($alert_msg)) echo "<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
				<?php if (strlen($ok_msg)) echo "<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<table cellspacing="0" cellpadding="0" width="100%" style="position:relative"><tr><td width="50%">
					<div class="method">
						<span class="name">Edit Content:</span><br />
						<table width="100%" cellspacing="0" cellpadding="0">
							<tr><td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=open&src=data%2Fcategories.xml">Categories</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=open&src=data%2Fstories.xml">Stories</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=open&src=data%2Fphotos.xml">Photos</a>
							</td>
							</tr><tr>
							<td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=open&src=data%2Fcomments.xml">Comments</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=open&src=data%2Fbasis.xml">Basis</a>
							</td><td>
							</td></tr>
						</table>
						<center><hr size="1" color="#BBB" width="60%" style="margin: 15px 0px;" /></center>
						<form method="get" action="." onsubmit="return true;">
							<input type="hidden" name="mod" value="editxml"></input>
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
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=restore&src=data%2Fcategories.xml">Categories</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=restore&src=data%2Fstories.xml">Stories</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=restore&src=data%2Fphotos.xml">Photos</a>
							</td>
							</tr><tr>
							<td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=restore&src=data%2Fcomments.xml">Comments</a>
							</td><td>
								<span class="dot">&#149;</span><a href="?mod=editxml&cmd=restore&src=data%2Fbasis.xml">Basis</a>
							</td><td>
							</td></tr>
						</table>
						<center><hr size="1" color="#BBB" width="60%" style="margin: 15px 0px;" /></center>
						<form method="get" action="." onsubmit="return ConfirmRestore();">
							<span class="dot">&#149;</span>XML Source:
							<input type="hidden" name="mod" value="editxml"></input>
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
						<form enctype="multipart/form-data" method="post" action="?mod=editxml&cmd=save" onsubmit="return ConfirmSave();">
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

if (mod_is('categories')) {
	$categs = array();
	$edit = false;
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

if (mod_is('stories')) {
	$stories = array();
	$edit = false;
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

if (mod_is('basis')) {
	if (isset($_GET['cmd'])) {
		$cmd = $_GET['cmd'];
		if (strcmp($cmd, 'save') == 0) {
			$basis = array();
			$_POST['extra'] = htmlspecialchars($_POST['extra'], ENT_NOQUOTES, "UTF-8");
			foreach ($_POST as $key => $value)
				$basis[$key] = $value;
			$basis['links'] = array();
			while (NULL !== array_pop($basis['links']));
			for ($i=0; $i<$_POST['nLink']; $i++)
				array_push($basis['links'],
					array('name' => $_POST["l${i}n"], 'href' => $_POST["l${i}h"], 'title' => $_POST["l${i}t"]));
			$ok_msg = "Changes to basis, saved successfully!";
			# echo "<pre>".print_r($basis, true)."</pre>";
			save_container('basis', 'Basis', 'data/basis.xml');
			if (isset($_GET['firsttime']))
				header('Location:?mod=welcome');
		}
		else
			$alert_msg = $cmd.' is not a valid command!';
	}

?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery >> </a></div>
		<div class="back2main"><a href="?"><< Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?mod=basis">Preferences:</a></div>
			<div class="inside">
				<?php if (strlen($alert_msg)) echo "<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
				<?php if (strlen($ok_msg)) echo "<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<div class="method">
					<span class="name">Basic Preferences:</span><br />
					<form enctype="multipart/form-data" method="post" action="?mod=basis&cmd=save<?php if (isset($firsttime) && ($firsttime)) echo "&firsttime=true"; ?>">
					<center>
						<table width="80%" cellspacing="0" cellpadding="2" style="text-align: left; position: relative;">
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
							<span class="dot">&#149;</span>Display Mode:
						</td><td>
							<span style="padding-left: 7px;"></span>
							<select id="mode" name="mode" class="select" size="1" onchange="javascript:changePrev(this.value, '');">
							<?php
								function isBM($x) {
									global $basis;
									return (strcmp($x, $basis['mode']) == 0)?" selected=\"selected\"":"";
								}
							?>
								<option<?php echo isBM("photos"); ?> value="photos">Photos : Recent and Top rated </option>
								<option<?php echo isBM("stories"); ?> value="stories">Stories: Recent</option>
								<option<?php echo isBM("box"); ?> value="box">Box: Jungle Box</option>
								<option<?php echo isBM("random"); ?> value="random">Random of the above!</option>
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
								if ($handle = opendir('./files')) {
									while (false !== ($file = readdir($handle)))
										if ($file != "." && $file != ".." && (strcasecmp(substr($file, -4), ".css") == 0)) {
											$sel = (strcmp($file, $basis['theme']) == 0);
											echo "\t\t\t\t\t\t<option ".($sel?"selected=\"selected\"":"")."value=\"$file\">".substr($file, 0, -4)."</option>\n";
										}
									closedir($handle);
									$file = 'random';
									$sel = (strcmp($file, $basis['theme']) == 0);
									echo "\t\t\t\t\t\t<option ".($sel?"selected=\"selected\"":"")."value=\"$file\">Random!</option>\n";
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

						<tr><td colspan="2">
							<div class="basisTitle">External Links ...
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
						<tr><td colspan="2">
							<div class="basisTitle">Advanced Preferences ...
								<span style="font-weight:normal; color: #444;">
									(Essential Configuration of Phormer; Be ware!)
								</span>
							</div>
						</td></tr>
					</table>
					<div id="AdvPrefCont">
						<center>
							[<a href="javascript:ToggleAdvPref();">
								<span id="ShowHideAdvPref">Show</span>
								Advanced Preferences</a>]
						</center>
						<table id="AdvPref" width="85%" cellspacing="0" cellpadding="2" style="display: none; text-align: left; position: relative;">
							<tr><td colspan="2">
								<div class="basisTitle">Thumbs' Opacity Percent
									<span style="font-weight:normal; color: #444;">
										(More percentage, More darkness on thumbs)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Transparence:
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
								<div class="basisTitle">Jpeg Compression Ratio:
									<span style="font-weight:normal; color: #444;">
										(More percentage, More Quality, More Size!)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Jpeg Quality:
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
								<div class="basisTitle">Additional HTML Codes ...
									<span style="font-weight:normal; color: #444;">
										(external hit counters, ad-sense or etc)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Extra Code:
							</td><td>
								<textarea name="extra" class="textarea" cols="35" type="text" rows="4"><?php echo $basis['extra']; ?></textarea>
							</td></tr>

							<tr><td colspan="2">
								<div class="basisTitle">Ban spammer's IPs ...
									<span style="font-weight:normal; color: #444;">
										(just paste the spammer's IPs, one per line)
									</span>
								</div>
							</td></tr>
							<tr><td valign="top">
								<span class="dot">&#149;</span>Banned IPs:
							</td><td>
								<textarea name="bannedip" class="textarea" cols="35" type="text" rows="4"><?php echo isset($basis['bannedip'])?$basis['bannedip']:''; ?></textarea>
							</td></tr>
						</table>
					</div>
					<center><input style="margin-top: 15px;" class="submit" type="submit" value="Save Changes"></input></center>
					<div class="basisTitle" style="border-top-width: 0px;"></div>
					</center>
					</form>
				</div>
			</div>
		</div>
		<div class="part">
			<div class="title"><a style="color: white" href="?mod=basis">Change Password:</a></div>
			<div class="inside">
				<div class="method" style="text-align: left">
					<span class="name">Change Administrator's Password</span>
					<div class="inside">
						<form method="post" action="?mod=dochangepass" onsubmit="javascript:return checkChangePass();">
							<center>
								<table width="50%" cellpadding="3" style="text-align: left;">
									<tr><td>Current password:</td><td><input name="curpasswd" type="password" class="text" size="20"></input></td></tr>
									<tr><td>New password:</td><td><input name="newpasswd1" id="newpasswd1" type="password" class="text" size="20"></input></td></tr>
									<tr><td>New password, again:</td><td><input name="newpasswd2" id="newpasswd2" type="password" class="text" size="20"></input></td></tr>
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

if (mod_is('comments')) {
	$comments = array();
	$ok_msg = '';
	$alert_msg = '';
	$cmd = '';
	$iid = '';
	parse_container('comments', 'Comment', 'data/comments.xml');
	ksort($comments);
	/*
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
	$basis['lastcmntseen'] = key($comments);
	save_container('basis', 'Basis', 'data/basis.xml');
	reset($comments);

	$n = isset($_GET['n'])?$_GET['n']:10;
	if ($n < 0)
		$n = count($comments);

	$st = isset($_GET['st'])?$_GET['st']:0;
	$st = max($st, 0);
	$st = min($st, count($comments)-1);
?>
		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery >> </a></div>
		<div class="back2main"><a href="?"><< Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?mod=comments">Manage Comments:</a></div>
			<div class="inside">
<?php if (strlen($alert_msg)) echo "\t\t\t\t<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
<?php if (strlen($ok_msg))    echo "\t\t\t\t<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<div class="method">
					<span class="name">Recent <?php echo $n; ?> Comments  &nbsp;: :&nbsp; Per Page
												[ <a href="?mod=comments&n=5">5</a> |
												<a href="?mod=comments&n=10">10</a> |
												<a href="?mod=comments&n=20">20</a> |
												<a href="?mod=comments&n=50">50</a> |
												<a href="?mod=comments&n=100">100</a> |
												<a href="?mod=comments&n=-1">All</a> ]
												&nbsp;: :&nbsp; [
												<a href="?mod=comments&n=<?php echo "$n&st=".($st-$n); ?>">Prev Page</a> |
												<a href="?mod=comments&n=<?php echo "$n&st=".($st+$n); ?>">Next Page</a> ]
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
								." [<a href=\"?mod=$mod&cmd=del&iid=$aiid\" onclick=\"javascript:return confirmDelete('".$comments[$aiid]['name']."'+'\'s comment');\">Delete</a>]"
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

	if (mod_is('photos')) {
		$categs = array();
		$stories = array();
		$photos = array();
		parse_container('categs', 'Category', 'data/categories.xml');
		parse_container('stories', 'Story', 'data/stories.xml');
		parse_container('photos', 'Photo', 'data/photos.xml');
		$edit = false;
		$ok_msg = '';
		$alert_msg = '';
		$cmd = '';
		$pid = '';
		$photo = array();
		$isdoAdd = false;
		if (isset($_GET['cmd'])) {
			$cmd = $_GET['cmd'];
			$isdoAdd = (strcmp($cmd, 'doAdd') == 0);
			$isAdd = (strcmp($cmd, 'add') == 0);
			if (!($isAdd || $isdoAdd || isset($_GET['pid']) || isset($_POST['pid'])))
				$alert_msg = "Unknown command or Pid.<br />Please enter PhotoID as pid for the command \"".$cmd."\"!";
			else
				if (! $isdoAdd)	{
					if ($isAdd)
						$pid = ++$photos['lastpid'];
					else
						$pid = (isset($_GET['pid'])?$_GET['pid']:$_POST['pid'])+0;
					$xmlfile = sprintf("data/p_%06d.xml", $pid);
					if (!$isAdd && !file_exists($xmlfile))
						$alert_msg = "No photo with this PhotoID (pid: $pid) exists!";
					else {
						if (strcmp($cmd, 'doEdt') == 0) {
							$edit = true;
							parse_container('photo', '', $xmlfile);
							$curppath = PHOTO_PATH.getImageFileName($pid, '9');
							$curthumb = substr_replace($curppath, '3', -5, 1);
						}
						else {
							$isDel = (strcmp($cmd, 'del') == 0);
							$isEdt = (strcmp($cmd, 'edt') == 0);
							if ($isAdd || $isEdt || $isDel) {
								if ($isDel) {
									parse_container('photo', '', sprintf('data/p_%06d.xml', $pid));
									$ok_msg = "Photo \"".$photo['name']."\" (PhotoID: $pid) deleted successfully!";
									$postfix = isset($photo['postfix'])?('_'.$photo['postfix']):"";
									unlink($xmlfile);
									for ($i=0; $i<10; $i++)
										if (file_exists($filename = PHOTO_PATH.sprintf('%06d%s_%d.jpg', $pid, $postfix, $i)))
											unlink($filename);
									unset($categs[$photo['categ']][array_search($pid, $categs[$photo['categ']])]);
									unset($stories[$photo['story']][array_search($pid, $stories[$photo['story']])]);
									unset($photos[$pid]);
									reset($categs);
									while (list($key, $val) = each($categs))
										if (isset($val['photo']) && is_array($val['photo'])) {
											$t = array_search($pid, $val['photo']);
											if ($t !== FALSE)
												unset($categs[$key]['photo'][$t]);
										}
									reset($stories);
									while (list($key, $val) = each($stories))
										if (isset($val['photo']) && is_array($val['photo'])) {
											$t = array_search($pid, $val['photo']);
											if ($t !== FALSE)
												unset($stories[$key]['photo'][$t]);
										}
									save_container('categs', 'Category', 'data/categories.xml');
									save_container('stories', 'Story', 'data/stories.xml');
									save_container('photos', 'Photo', 'data/photos.xml');
								}
								else if (!isset($_POST['name'])) {
									$alert_msg = "No Name! You shall come here from administration page only!";
								}
								else if ($isEdt || $isAdd) {
									if ($isEdt) {
										parse_container('photo', '', sprintf('data/p_%06d.xml', $pid));
										$t = array_search($pid, $stories[$photo['story']]['photo']);
										if ($t !== FALSE)
											unset($stories[$photo['story']]['photo'][$t]);
										$t = array_search($pid, $categs[$photo['categ']]['photo']);
										if ($t !== FALSE)
											unset($categs[$photo['categ']]['photo'][$t]);
										$postfix = isset($photo['postfix'])?$photo['postfix']:'';
									}
									else { //$isAdd
										$postfix = $_POST['seed'];
									}

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

									$photos[$pid] = $_POST['hits'];
									$postfix = (strlen($postfix)?"_":"").$postfix;
									$pid6let = sprintf("%06d%s_9.jpg", $pid, $postfix);
									$ppath = PHOTO_PATH.$pid6let;
									$curppath = $ppath;
									$curthumb = substr_replace($ppath, '3', -5, 1);

									$reget = ($isEdt && isset($_POST['regetSrc']) && (strcmp($_POST['regetSrc'], 'get') == 0));
									if ($reget) {
										foreach (array('0', '1', '2', '3', '4', '9') as $ind)
											if (file_exists(substr_replace($ppath, $ind, -5, 1)))
												unlink(substr_replace($ppath, $ind, -5, 1));
									}

									if ($isAdd || $reget)
										$ppath = substr_replace($_POST['ImgUrl'], '9', -5, 1);


									if (!file_exists($ppath)) {
										$alert_msg = "The photo is not exist in the <a href=\"$ppath\">expected place</a>!";
									}
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
											if (max($w, $h) > 640)
												makeTheThumb($ppath, 640, 'max', '0');
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
							}
							else {
								$isadCat = (strcmp($cmd, 'adddelC') == 0);
								$isadStr = (strcmp($cmd, 'adddelS') == 0);
								if ($isadCat || $isadStr) {
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
												$ok_msg = "The Photo \"$pname\" (pid: $pid) added to Category \"$cname\" (cid: $cid), successfully!";
											}
											else {
												unset($categs[$cid]['photo'][array_search($pid, $categs[$cid]['photo'])]);
												$ok_msg = "The Photo \"$pname\" (pid: $pid) removed from Category \"$cname\" (cid: $cid), successfully!";
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
												$ok_msg = "The Photo \"$pname\" (pid: $pid) added to Story \"$sname\" (sid: $sid), successfully!";
											}
											else {
												unset($stories[$sid]['photo'][array_search($pid, $stories[$sid]['photo'])]);
												$ok_msg = "The Photo \"$pname\" (pid: $pid) removed from Story \"$sname\" (sid: $sid), successfully!";
											}
											save_container('stories', 'Story', 'data/stories.xml');
										}
									}

								}
								else
									$alert_msg = $cmd.' is not a valid command!';
							}
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
		if (photo_exists($lastp)) {
			$dateTakenPrev = getPhotoInfo($lastp, 'datetake');
			$storyPrev = getPhotoInfo($lastp, 'story');
		}

		//print_r($photos);
?>
		<script type="text/javascript" language="javascript" src="files/adminfiles/addphoto.js"></script>

		<div class="back2mainR"><a target="_blank" href=".">&nbsp;View Gallery >> </a></div>
		<div class="back2main"><a href="?">&nbsp;&nbsp;<< Admin Page&nbsp;&nbsp;&nbsp;</a></div>
		<noscript>
			<br />
			<div class="method"><div class="note_invalid">Please activate javascript for the proper performance.</div></div>
		</noscript>
<?php if (!$showConsole) { ?>
		<div class="clearer" style="margin-top: 15px;"> </div>
		<div class="back2main"><span style="padding-left: 13px"></span><a href="?mod=photos"><< Manage Photos</a></div>
<?php
	}
	if (strlen($alert_msg)) echo "\t\t\t<div class=\"method\" style=\"margin-top: 20px;\"><div class=\"note_invalid\">$alert_msg</div></div>";
 	if (strlen($ok_msg))    echo "\t\t\t<div class=\"method\" style=\"margin-top: 20px;\"><div class=\"note_valid\">$ok_msg</div></div>";
 	if ($showConsole) {
?>
		<div class="part">
			<div class="title"><a style="color: white" href="?mod=photos">Manage Photos:</a></div>
			<div class="inside">
				<table width="100%" cellspacing="0" cellpadding="0" style="position: relative;"><tr>
					<td width="50%" valign="top">
						<div class="method" style="margin-bottom: 1px;">
							<span class="name">
								<span class="lightdot">&#149;</span>
								<b>Add</b> photo ::
							</span><br />
							<span class="dot"><b>: :</b></span> <a href="?mod=photos&cmd=doAdd">Add a new photo!</a>
						</div>
						<br />
						<div class="method">
							<span class="name">
								<span class="lightdot">&#149;</span>
								<b>Edit</b> Photo ::
								<?php echo $n; ?>
								Recent :
								<a href="?mod=photos&n=3">[3]</a>
								<a href="?mod=photos&n=6">[6]</a>
								<a href="?mod=photos&n=12">[12]</a>
								<a href="?mod=photos&n=24">[24]</a>
								<a href="?mod=photos&n=48">[48]</a>
								<a href="?mod=photos&n=96">[96]</a>
							</span><br />
<?php
	$cur = end($photos);
	for ($i=min(count($photos), $n); $i>0; $i--) {
		$thePid = key($photos);
		if (strcmp("lastpid",  $thePid) != 0) {
			echo "\t<div class=\"aThumbToEdit\">\n";
			thumbBox($thePid, '', true, true);

			echo "\t\t<a href=\"?mod=photos&cmd=doEdt&pid=$thePid\">Edit</a>\n"
				."\t\t<span class=\"dot\">::</span>\n"
				."\t\t<a href=\"?mod=photos&cmd=del&pid=$thePid\""
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
							<input type="hidden" name="mod" value="photos"></input>
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
							<input type="hidden" name="mod" value="photos"></input>
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
							<input type="hidden" name="mod" value="photos"></input>
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
							<input type="hidden" name="mod" value="photos"></input>
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
		<form name="TheGlobalForm" enctype="multipart/form-data" method="post" action="?mod=photos&cmd=<?php echo $edit?'edt':'add'; ?>" onsubmit="return CheckAddPhoto<?php echo $edit?'Time':''; ?>();">
		<input name="ImgUrl" id="theImgPath" class="text" size="40" type="hidden" readonly="readonly" value="<?php echo $edit?PHOTO_PATH.getImageFileName($pid, '9'):""; ?>"></input>
		<input name="seed" id="inputSeed" class="text" size="20" type="hidden" readonly="readonly" value="<?php echo $seed; ?>"></input>
		<div class="part" style="margin-top: 22px;">
			<div class="title">&#149; <a style="color: white" href="?mod=photos<?php echo $edit?"&cmd=doEdt&pid=$pid":'&cmd=doAdd'; ?>"><?php echo $edit?"Edit Photo #$pid \"".$photo['name']."\"":'Add Photo'; ?>:</a></div>
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
									<img src="files/ind.gif" class="ind" alt="" /> &nbsp; &nbsp; &nbsp; <span id="upload_uploading_txt">Preparing the process...</span>
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
								<tr><td>Get Comments:</td><td><span style="margin-left: 5px"></span><input <?php echo ($edit && (strcmp($photo['getcmnts'], "no") == 0))?'':'checked="checked"'; ?> name="getcmnts" value="yes" type="radio" class="radio">Yes</input>
															  <span style="margin-left: 25px"></span><input <?php echo ($edit && (strcmp($photo['getcmnts'], "no") == 0))?'checked="checked"':''; ?> name="getcmnts" value="no"  type="radio" class="radio">No</input></td></tr>
							</table>
						</div>
					</td>
					<td width="50%" valign="top">
						<div class="method">
							<span class="name">Step 2.2 - Special features (optional):</span>
							<table width="100%" cellpadding="4" cellspacing="1">
								<tr><td valign="top">Photo info:</td><td><textarea cols="20" rows="3" name="photoinfo"><?php echo $edit?$photo['photoinfo']:''; ?></textarea></td></tr>
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
																$prv = (strlen($cvals['pass']))?'[private] ':'';
																$sel = $edit?($cid == $photo['categ']):($cid == 1);
																echo "\t\t\t\t\t\t\t\t<option ".($sel?"selected=\"selected\"":"")."value=\"$cid\">".$cid.": ".$prv.$cvals['name']."</option>\n";
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
																$prv = (strlen($svals['pass']))?'[private] ':'';
																$sel = $edit?($sid == $photo['story']):($sid == $storyPrev);
																echo "\t\t\t\t\t\t\t\t<option ".($sel?"selected=\"selected\"":"")."value=\"$sid\">".$sid.": ".$prv.$svals['name']."</option>\n";
															}
													?>
												 </input></td></tr>
								<tr><td>Date Taken:</td><td><input id="datetake" name="datetake" type="text" class="text" size="21" value="<?php echo $edit?$photo['datetake']:$dateTakenPrev; ?>"></input></td></tr>
								<tr><td>Hits Rate(Sum/N):</td><td><input id="hits" name="hits" type="text" class="text" size="21" value="<?php echo $edit?$photos[$pid]:'0 0/0'; ?>" autocomplete="off"></input></td></tr>
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
						<form enctype="multipart/form-data" method="get" action="?mod=photos">
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
						."\t\t\t\t\timageUploaded();\n"
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