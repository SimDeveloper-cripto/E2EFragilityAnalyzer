<?php
	require_once("funcs.php");
	$time_start = getmicrotime();

	if (!file_exists('data/adminPass.inf'))
		header('Location: admin.php');

	$categs = array();
	$photos = array();
	$stories = array();
	$comments = array();
	$basis = array();
	parse_container('categs', 	'Category', 'data/categories.xml');
	parse_container('stories', 	'Story', 	'data/stories.xml');
	parse_container('photos', 	'Photo', 	'data/photos.xml');
	parse_container('comments', 'Comment', 	'data/comments.xml');
	parse_container('basis', 	'Basis', 	'data/basis.xml');

	$stories = array_reverse($stories, true);

	$nphotos = count($photos)-1; // for lastpid!
	$nstories = count($stories)-1; // for lastsid!

	if (isset($_GET['mode']))
		$basis['mode'] = $_GET['mode'];
	if (isset($_GET['theme']))
		$basis['theme'] = $_GET['theme'];
	$alls = (isset($_GET['alls']))?$_GET['alls']:0;
	$wStories = $alls*(-count($stories));

	if (!isset($basis['defjbn']))
		$basis['defjbn'] = 50;   // photos in box mode
	if (!isset($basis['defrpc']))
		$basis['defrpc'] = 20;   // recent photos in index
	if (!isset($basis['deftrc']))
		$basis['deftrc'] = 10;   // top rated in index
	if (!isset($basis['defrsc']))
		$basis['defrsc'] = 5;    // stories in index
	if (!isset($basis['defrss']))
		$basis['defrss'] = 1000; // stories in side bar

	$rps = (isset($_GET['rps']))?$_GET['rps']:0;
	$rpn = (isset($_GET['rpn']))?$_GET['rpn']:$basis['defrpc']; // recent photos count (number)
	$trs = (isset($_GET['trs']))?$_GET['trs']:0;
	$trn = (isset($_GET['trn']))?$_GET['trn']:$basis['deftrc']; // top rated count (number)
	$rss = (isset($_GET['rss']))?$_GET['rss']:0;
	$rsn = (isset($_GET['rsn']))?$_GET['rsn']:$basis['defrsc']; // recent stories count (number)

	define("DEFAULT_N", $basis['defrpc']+$basis['deftrc']);
	$n  = (isset($_GET['n'] ))?$_GET['n']:DEFAULT_N;

	$p = (isset($_GET['p']))?$_GET['p']:-1;
	$c = (isset($_GET['c']))?$_GET['c']:-1;
	$s = (isset($_GET['s']))?$_GET['s']:-1;
	$u = (isset($_GET['u']))?$_GET['u']:-1;
	$j = (isset($_GET['j']))?$_GET['j']:-1;

	if ($u != -1) {
		reset($categs);
		while (list($ck, $cv) = each($categs))
			if (is_array($cv))
				if (strcasecmp($cv['name'], $u) == 0) {
					$c = $ck;
					$u = -1;
					break;
				}
		reset($categs);
	}
	if ($u != -1) {
		reset($stories);
		while (list($ck, $cv) = each($stories))
			if (is_array($cv))
				if (strcasecmp($cv['name'], $u) == 0) {
					$s = $ck;
					$u = -1;
					break;
				}
		reset($stories);
	}


	$cmd = (isset($_GET['cmd']))?$_GET['cmd']:(isset($_POST['cmd'])?$_POST['cmd']:'');
	$ok_msg = '';
	$ok_rate = false;
	$alert_msg = '';
	$alert_query = '';

	if (($p != -1) && !isset($photos[$p]))
		$alert_query = "The requested photo [pid = $p] does not exist!";
	if (($c != -1) && !isset($categs[$c]))
		$alert_query = "The requested category [cid = $c] does not exist!";
	if (($s != -1) && !isset($stories[$s]))
		$alert_query = "The requested story [sid = $s] does not exist!";

	$bad_query = (strlen($alert_query) > 0);
	if ($bad_query)
		$p = $c = $s = -1;

	$isAdmin = (isset($_COOKIE['phormer_passwd']) && auth_admin($_COOKIE['phormer_passwd'], "admin/"));

	if (strlen($cmd) && !$bad_query)
		switch ($cmd) {
			case 'logout':
				$carr = array();
				$let = ($c != -1)?"c":"s";
				$carr = ($c != -1)?$categs[$c]:$stories[$s];
				setcookie('pass_'.$let.$$let, "", time()-3600);
				$_COOKIE['pass_'.$let.$$let] = "";
				break;
			case 'login':
				if (!isset($_POST['pass']))
					$alert_msg = "Password field should not be blank.";
				else {
					$carr = array();
					$let = ($c != -1)?"c":"s";
					$carr = ($c != -1)?$categs[$c]:$stories[$s];
					if (strcmp(md5($_POST['pass']), md5($carr['pass'])) != 0)
						$alert_msg = "Invalid Password!";
					else {
						setcookie('pass_'.$let.$$let, md5($_POST['pass']), time()+3600*24*7);
						$_COOKIE['pass_'.$let.$$let] = md5($_POST['pass']);
						if (isset($_GET['done']))
							header("Location: .?p={$_GET['done']}");
					}
				}
				break;
			case 'rate':
				if ($p == -1)
					$alert_msg = "PhotoID can not be left blank.";
				else {
					$urrate = (isset($_COOKIE['rate_'.$p])?$_COOKIE['rate_'.$p]:0);
					$rating = array();
					$rating = explode(" ", $photos[$p]);
					$hits = $rating[0];
					$rating = explode("/", $rating[1]);
					$rating[0] += $_GET['rate']-$urrate;
					$rating[1] += ($urrate == 0);
					$photos[$p] = sprintf("%d %d/%d", $hits, $rating[0], $rating[1]);
					$ok_rate = true;
					$ok_msg = "Your rating \"{$_GET['rate']}\" for this photo saved successfully!".
						(($urrate==0)?"":" (and your previous rating ($urrate) dismissed.)");
					save_container('photos', 'Photo', './data/photos.xml');
					setcookie('rate_'.$p, $_GET['rate'], time()+3600*24*365);
					$_COOKIE['rate_'.$p] = $_GET['rate'];
					$rate = 0;
					@$rate = round($rating[0]/$rating[1], 2);
					$rate .= " by ".$rating[1];
					echo "<ajax>Done $rate</ajax>";
					die();
				}
				break;
			case 'addcmntp':
				if (!isset($_POST['name']))
					$alert_msg = "Name field can not be left blank.";
				else if (!isset($_POST['txt']) || (strlen($_POST['txt']) == 0))
					$alert_msg = "Text body of the comment can not be left empty.";
				else {
					$photo = getAllPhotoInfo($p, "./");
					if (strcmp($photo['getcmnts'], 'yes') != 0)
						$alert_msg = "This Photo doesn't get Comment!";
					else if (bannedIP($_SERVER['REMOTE_ADDR']))
						$alert_msg = "Your IP is locked on this PhotoGallery. Contact webmaster!";
					else if (isset($basis['wvw']) &&
							 isset($_POST['wvw']) &&
							 (strcasecmp($_POST['wvw'], $basis['wvw']) != 0) &&
							 (!isset($basis['haswvw']) || strcmp($basis['haswvw'], "no") != 0)) {
						$alert_msg = "Incorrect word verification entry!";
						if (isset($basis['wvw']))
							$alert_msg .= " You should have entered \"".$basis['wvw']."\". Try again!"; #\"".$_POST['wvw']."\"";
						}
					else {
						if (isset($_POST['date']) && ($_POST['date']))
							$date = "00-01-01 00:00";
						else
							$date = date("Y-m-d H:i");
						$comments[++$comments['lastiid']] = array('name' => $_POST['name'],
									'email' => $_POST['email'], 'url' => $_POST['url'],
									'date' => $date, 'txt' => htmlspecialchars($_POST['txt'], ENT_COMPAT, 'UTF-8'),
									'ip' => $_SERVER['REMOTE_ADDR'], 'owner' => "p".$_GET['p']);
						$ok_msg = "Comment added successfully";
						save_container('comments', 'Comment', './data/comments.xml');
					}
				}
				break;
			case 'addcmnts':
				if (!isset($_POST['name']))
					$alert_msg = "Name field can not be left blank.";
				else if (!isset($_POST['txt']) || (strlen($_POST['txt']) == 0))
					$alert_msg = "Text Body can not be left empty.";
				else if (strcmp($stories[$s]['getcmnts'], 'yes') != 0)
					$alert_msg = "This Story doesn't get Comment!";
				else if (bannedIP($_SERVER['REMOTE_ADDR']))
					$alert_msg = "Your IP is locked on this PhotoGallery. Contact webmaster!";
				else {
					if (isset($_POST['date']) && ($_POST['date']))
						$date = "00-01-01 00:00";
					else
						$date = date("Y-m-d H:i");
					$comments[++$comments['lastiid']] = array('name' => $_POST['name'],
								'email' => $_POST['email'], 'url' => $_POST['url'],
								'date' => $date, 'txt' => htmlspecialchars($_POST['txt'], ENT_COMPAT, 'UTF-8'),
								'ip' => $_SERVER['REMOTE_ADDR'], 'owner' => "s".$_GET['s']);
					$ok_msg = "Comment added successfully.";
					save_container('comments', 'Comment', './data/comments.xml');
				}
				break;
			case 'delcmnt':
				if (!$isAdmin)
					$alert_msg = "Only Administrator can delete comments!";
				else if (!isset($_GET['cmntid']))
					$alert_msg = "CommentId not posted.";
				else {
					$cmntid = (int)$_GET['cmntid'];
					if (!isset($comments[$cmntid]))
						$alert_msg = "Comment #$cmntid not found.";
					else {
						$ok_msg = "Comment #$cmntid by \"{$comments[$cmntid]['name']}\" deleted successfully!";
						unset($comments[$cmntid]);
						save_container('comments', 'Comment', './data/comments.xml');
					}
				}
				break;
			default:
				$alert_msg = "Unknown command \"".$cmd."\"!";
		}

	if ($j != -1) {
		$path = pathinfo($_SERVER['PHP_SELF']);
		$add = "http://".$_SERVER['SERVER_NAME'].$path["dirname"];
		if (strcmp($path["dirname"], "/") != 00)
			$add .= "/";
		if (photo_exists($j)) {
			$size = (isset($_GET['size']))?$_GET['size']:1;
			$img = thumb_just_img($j, $size, "./");
			echo "document.write('<a href=\"$add?p=$j\"><img src=\"$add/$img\" /></a>');";
		}
		else
			echo "document.write('<a href=\"$add\">The photo #$j does not exist</a>');";
		die();
	}

	if ($p != -1) {
		$photo = getAllPhotoInfo($p);
		if (!checkThePass("s", $photo['story'])) {
			write_headers("Unauthorized :: ".$basis['pgname']);
			write_top();
			unauthorized("s", $photo['story'], $p);
		}
		else if (!checkThePass("c", $photo['categ'])) {
			write_headers("Unauthorized :: ".$basis['pgname']);
			write_top();
			unauthorized("c", $photo['categ'], $p);
		}
		else {
			$curLastVisit = array();
			if (strlen($cmd) == 0) {
				$rating = array();
				$rating = explode(" ", $photos[$p]);
				$hits = $rating[0];
				$hits++;
				@eval("@\$rate =".$rating[1].";");
				if (($rate < 1) || ($rate > 5))
					$rating[1] = "0/0";
				if (isset($rating[2]))
					$curLastVisit = $rating;

				$photos[$p] = sprintf("%d %s %s", $hits, $rating[1], date("Y/m/d-G:i:s"));
				save_container('photos', 'Photo', './data/photos.xml');
			}
			$hr = "<center><div class=\"hr\"></div></center>";
			$dd = "<span class=\"darkdot\">&#149;</span>";
			$spc = "\t\t<div class=\"spc\"> </div>\n";
			$imgAddress = PHOTO_PATH.getImageFileName($p, 0);
			if (!file_exists($imgAddress))
				$imgAddress = PHOTO_PATH.getImageFileName($p, 9);
			$name = $photo['name'];
			if (strlen($name) == 0)
				write_headers($basis['pgname']);
			else
				write_headers("\"$name\" of ".$basis['pgname']);
			write_top();

			echo "\t<div class=\"pvTitle\">"
					."<span class=\"pvTitleInfo\">"
						.($isAdmin?"[<a target=\"_top\" "
						."href=\"admin.php?page=photos&cmd=del&pid=$p\" onclick=\"return confirmDelete('Photo #$p')\" title=\"Delete this photo in the Administration Area\"> Delete </a>]&nbsp; &nbsp;":"")
						.($isAdmin?"[<a target=\"_top\" "
						."href=\"admin.php?page=photos&cmd=doEdt&pid=$p\" title=\"Edit this photo in the Administration Area\"> Edit </a>]&nbsp; &nbsp;":"")
						."[<a target=\"_top\" href=\"javascript:toggleInfo();\" title=\"Show/Hide additional info and de/centralize the photo\">"
							."  <span id=\"hin\">Hide&nbsp;</span> info "
						."</a>]&nbsp; &nbsp;"
						."[<a target=\"_top\" href=\".?c={$photo['categ']}\" title=\"Navigate to container Category ({$categs[$photo['categ']]['name']})\"> Up </a>]"
					."</span>\n";
			if (strlen($name))
				echo "<span class=\"dt\">&#147;</span> "
					."<span class=\"titleName\"><a title=\"Refresh this page\" href=\".?p=$p\">$name</a></span>"
					."<span class=\"dt\">&#148;</span>\n";
			else
				echo "&nbsp;";
			echo "</div>\n";
			$photoDesc = (strlen($photo['desc']) > 0)?nl2br($photo['desc']):"No Descripton.";
			$photoInfo = nl2br($photo['photoinfo']);
			$tuple = (strlen($photo['photoinfo']));
			global $hasgd;
			$widthHeight = "";
			if ($hasgd) {
				list($width, $height) = getimagesize($imgAddress);
				$widthHeight = "width=\"${width}px\" height=\"${height}px\" ";
			}
			$larger = PHOTO_PATH.getImageFileName($p, 9);
			$haslarger = (file_exists($larger) && (strcasecmp($larger, $imgAddress) != 0));
			$largerInfo = "";
			if ($haslarger && $hasgd) {
				list($w, $h) = getimagesize($larger);
				$sizekb = round(filesize($larger)/1024);
				$largerInfo = ":: {$w}x{$h} :: {$sizekb}KB";
			}
			echo "\t<div class=\"wholePhoto\">\n";
			echo "\t\t<div class=\"photoTheImg\">\n"
				.($haslarger?"<a title=\"Click to view original size $largerInfo\" href=\"$larger\">":"")
				."<img src=\"$imgAddress\" $widthHeight alt=\"{$photo['name']}\" id=\"theImage\" style=\"float: left;\" />\n"
				.($haslarger?"</a>":"")
				."</div>\n";
			echo "\t\t<div id=\"photoBoxes\">\n";
			echo "\t\t<div class=\"photoBox\">\n"
					."<span class=\"titlePhotoBox\">Photo Notes "
					."</span><br />"
					.$dd.$photoDesc
					.($tuple?$hr.$dd:"")
					.$photoInfo
					."</div>\n";
			echo "\t\t<div class=\"photoBox\">\n";
				echo "\t\t\t<span class=\"titlePhotoBox\">Further Details</span><br />\n";
				echo "\t\t\t$dd Date added: {$photo['dateadd']}<br />\n";
				if (strncmp($photo['dateadd'], $photo['datetake'], 10) != 0)
					echo "\t\t\t$dd Date taken: {$photo['datetake']}<br />\n";
				echo $spc;
				echo "\t\t\t$dd Photo ID: <a target=\"_top\" href=\".?p=$p\">$p</a><br />\n";
				echo "\t\t\t$dd Category: <a href=\".?c={$photo['categ']}\">{$categs[$photo['categ']]['name']}</a><br />\n";
				echo "\t\t\t$dd Story: <a href=\".?s={$photo['story']}\">{$stories[$photo['story']]['name']}</a><br />\n";
				echo $spc;
				echo "\t\t\t$dd Other Sizes: "
						.($haslarger?"<a href=\"".thumb_just_img($p, 9, "./")."\" title=\"Photo $p $largerInfo\">Original</a>, \n":"")
						."<a href=\"".thumb_just_img($p, 1, "./")."\" title=\"width: 240px\">BlogSize</a>\n"
						."<br />\n";
			echo "\t\t</div>\n ";
			echo "\t\t<div class=\"photoBox\" style=\"line-height: 150%\">\n";
				echo "\t\t\t<span class=\"titlePhotoBox\">Photo Statistics</span><br />\n";
				$rating = array();
				$rating = explode(" ", $photos[$p]);
				echo "\t\t\t$dd Hits: $rating[0]<br />\n";

				if (isset($curLastVisit[2]))
					echo "\t\t\t$dd Last Visit: ".SecsToText(-1*below_GetRecency($curLastVisit))."<br />\n";

				$rate = 0;
				@eval("@\$rate = ".$rating[1].";");
				$rate = round($rate, 2);
				$rating = explode("/", $rating[1]);
				echo "\t\t\t$dd Rated <span id=\"sumRate\">$rate by {$rating[1]}</span> persons<br />\n";
				$status = (isset($_COOKIE['rate_'.$p])?'Modify your rating':'Rate now');
?>
				<div>
					<noscript>
					<span class="darkdot">&#149;</span> <b>Rating needs javascript</b><br />
					</noscript>
					<span class="darkdot">&#149;</span>
					<span id="rateStatus">
						<?php echo $status; ?>:
					</span>
					<span id="indicator" class="dot" style="padding: 0px; color: #454; font-size: 1em;"></span>
				</div>
				<span style="margin-left: 7px;"></span>
				<select name="rate" class="rate" id="rateSelect" onchange="javascript:SaveRating(<?php echo $p; ?>, this.value);">
<?php
				$urrate = (isset($_COOKIE['rate_'.$p])?$_COOKIE['rate_'.$p]:0);
				$rname = array("Select Your Rate","Damn it!", "I dislike it!", "Umm...", "Nice at all!", "Excellent!");
				for ($i=0; $i<=5; $i++)
					echo "\t\t\t\t\t<option value=\"$i\"".(($i == $urrate)?" selected=\"selected\"":"").">".(($i>0)?"$i: ":"")."{$rname[$i]}</option>\n";
?>
				</select>
			</div>
			</div>
			<script language="javascript" type="text/javascript">
				reToggleInfo();
			</script>
		<div class="divClear"></div>
		</div>
		<br />
<?php
			writeNextz($p);

			echo "<a name=\"cmnts\"></a>";
			if (strlen($alert_msg))
				echo "<div class=\"alert_msg\">$alert_msg</div>\n";
			else if (strlen($ok_msg) && !$ok_rate)
				echo "<div class=\"ok_msg\">$ok_msg</div>\n";
			if (strcmp($photo['getcmnts'], 'yes') == 0)
				writeCommenting('p', $p);
		}
	}
	else {
	$t = "";
	if ($c != -1)
		$t = "\"".$categs[$c]['name']."\" Category of ";
	else if ($s != -1)
		$t = "\"".$stories[$s]['name']."\" Story of ";
	write_headers($t.$basis['pgname']);
	write_top();
?>
	<div id="sidecol">
		<div id="sidecolinner">
			<?php
				if ($c != -1) {
					if ((strlen($categs[$c]['pass']) > 0) && (checkThePass('c', $c)))
						write_actions('categs', 'c', $c);
				} else if ($s != -1) {
					if ((strlen($stories[$s]['pass']) > 0) && (checkThePass('s', $s)))
						write_actions('stories', 's', $s);
				}
				write_conts('categs', 'Categories');
				write_conts('stories', 'Stories');
			?>
<?php
	reset($basis['links']);
	$t = current($basis['links']);
	if ((count($basis['links']) > 1) || (strlen($t['name']))) {
	?>

			<div class="part">
				<div class="submenu">
<?php
		reset($basis['links']);
		while (list($key, $val) = each($basis['links'])) {
			if (strlen($val['href']) == 0)
				echo "\t\t\t\t</div>\n"
					."\t\t\t\t<div class=\"titlepart\"><span class=\"reddot\">&#149;</span>${val['name']}</div>\n"
					."\t\t\t\t<div class=\"submenu\">\n";
			else
				echo "\t\t\t\t\t<div class=\"item\"><span class=\"dot\">&#149;</span><a href=\"${val['href']}\" title=\"${val['title']}\">${val['name']}</a></div>\n";
		}
?>
				</div>
			</div>
<?php	} ?>

<?php
	$basis['extra'] = strtr($basis['extra'], $transtable);
	if (strlen($basis['extra']) > 0) {
?>
			<div class="part">
			<div class="titlepart"><span class="reddot">&#149;</span>Etc</div>
				<div class="submenu">
					<?php echo $basis['extra']; ?>
				</div>
			</div>
<?php
	}

	write_credits();
?>
		</div>
	</div>
	<div id="maincol">
		<div id="maincolinner">
<?php
	if ($c != -1)
		write_container("c");
	else if ($s != -1)
		write_container("s");
	else {
		if ($bad_query)
			echo "<div class=\"alert_msg\">$alert_query</div>";

		if (strcmp($basis['mode'], 'stories') == 0)
			write_lastStories();
		else if (strcmp($basis['mode'], 'box') == 0)
			write_boxPhotos();
		else {
			$t = array();
			$t = explode("-", $basis['mode']);
			if (!isset($t[1]))
				$t = explode("-", 'all-recent');

			if (strcmp($t[0], "first") == 0)
				write_firstPhoto();
			else
				write_lastPhotos();

			if (strcmp($t[1], 'all') == 0)
				write_lastPhotos();
			else if (strcmp($t[1], 'rate') == 0)
				write_belowIndex("below_GetRate", "Top Rated");
			else if (strcmp($t[1], 'hits') == 0)
				write_belowIndex("below_GetHits", "Most Visited");
			else if (strcmp($t[1], 'recent') == 0)
				write_belowIndex("below_GetRecency", "Recently Visited");
		}
	}
}
?>
		</div>
	</div>
	</div>
</div></center> <!-- Granny -->
<?php
	$time_end = getmicrotime();
	write_footer();
	echo "<!-- Created in ".round($time_end-$time_start, 3)." seconds -->\n";
	echo "</body>\n</html>\n";
?>
