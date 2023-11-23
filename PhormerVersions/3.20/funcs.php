<?php
define("PHORMER_VERSION", "3.20");
define("PHORMER_BUILD_DATE", "15th Sep. 2006");

$thumbCntArr = array(5, 10, 20, 50, 100);

$pathadd = pathinfo($_SERVER['PHP_SELF']);
$addadd = "http://".$_SERVER['SERVER_NAME'].$pathadd["dirname"];
while (strcmp(substr($addadd, -1), '/') == 0)
	$addadd = substr($addadd, 0, -1);
#echo $addadd."!<br>";

global $_GET, $_POST, $_COOKIE, $hasgd, $hasexif;

if (get_magic_quotes_gpc()) {
	$_POST = str_replace("\\\"", "\"", $_POST);
	$_POST = str_replace("\\'", "'", $_POST);
	$_GET = str_replace("\\\"", "\"", $_GET);
	$_GET = str_replace("\\'", "'", $_GET);
}

$hasgd = in_array("gd", get_loaded_extensions())?1:0;
$hasexif = in_array("exif", get_loaded_extensions())?1:0;

define("PHOTO_PATH", "images/");
$transtable = get_html_translation_table(HTML_SPECIALCHARS, ENT_QUOTES);
$transtable = array_flip($transtable);
$transmanual = array('"' => '&quot;', ">" => '&gt;', '<' => '&lt;'); #'>

define("ADMIN_PASS_FILE", "data/adminPass.inf");
define("UPLOAD_AJAX_FILE", "temp/upload_ajax.inf");
define("SKL_PHOTO_W", 240);
define("MAX_LINKS", 60);
define("DEFAULT_JPEG_QUAL", 80);
define("DEFAULT_OPAC_RATE", 60);
define("WV_LENGTH", 5);

function getImageFileName($pid, $size) {
	$postfix = getPhotoInfo($pid, 'postfix');
	$postfix = (strlen($postfix)?"_":"").$postfix;
	$imgFile = sprintf('%06d%s_%d.jpg', $pid, $postfix, $size);
	return $imgFile;
}

function getPhotoInfo($pid, $prop) {
	$tphoto = array();
	$tphoto = getAllPhotoInfo($pid);
	return isset($tphoto[$prop])?$tphoto[$prop]:"";
}

function getAllPhotoInfo($pid) {
	global $tphoto;
	$tphoto = array();
	$xmlfile = sprintf('data/p_%06d.xml', $pid);
	parse_container('tphoto', '', $xmlfile);
	return $tphoto;
}

function thumb_just_img($pid, $size) {
	return PHOTO_PATH.getImageFileName($pid, $size);
}

function auth_admin($passwd = "") {
	if (!strlen($passwd)) {
		global $_COOKIE;
		$passwd = isset($_COOKIE['phormer_passwd'])?$_COOKIE['phormer_passwd']:"";
	}
	$adminPass = "";
	@$adminPass = md5(trim(file_get_contents(ADMIN_PASS_FILE)));
	return (strcmp($passwd,$adminPass) == 0);
}


function photo_exists($pid) {
	$xmlfile = sprintf('data/p_%06d.xml', $pid);
	return file_exists($xmlfile);
}

function textDirectionEn($txt){
	$en = true;
	for ($i=0; ($i<strlen($txt)) && $en; $i++) {
		$l = $txt[$i];
		$en &= ctype_lower($l) || ctype_upper($l) || ctype_punct($l) || ctype_digit($l) ||
			   ctype_space($l) || (strpos("/\\!@#$%^&*(){}[];\"'", $l) != FALSE); #'
	}
	return $en;
}

function thumbBox($pid, $a_info = "", $force = false, $isInAdmin = false, $targ="_blank") { ### :)
	global $stories, $categs, $photos, $isAdmin, $basis;

	if (isset($basis['linktarget'])) {
		if (strcmp($basis['linktarget'], "_blank") == 0)
			$targ = "_blank";
		else if (strcmp($basis['linktarget'], "_self") == 0)
			$targ = "_self";
	}
	if ($isInAdmin)
		$targ = "_blank";

	$photo = getAllPhotoInfo($pid);

	if (!$force) {
		if (!canthumb($pid))
			return false;
	}
	$imgFile = PHOTO_PATH.getImageFileName($pid, '3');
	$mtime = textdate($photo['dateadd']);
	$rating = array();
	$rating = explode(" ",$photos[$pid]);
	$hits = $rating[0];
	$rate = 0;
	$raters = substr(strrchr($rating[1], '/'), 1);
	@eval("@\$rate =".$rating[1].";");
	$rate = round($rate, 2);
	$theName = $photo['name'];
	$neck = 14;
	if (strlen($theName) == 0)
		if ($isInAdmin)
			$theName = "[Photo #".$pid."]";
		else
			$theName = "&nbsp;";
	if (strlen($theName) > $neck)
		$theName = substr($theName, 0, $neck-3)."&#133;";
	if (strlen($a_info))
		$theName = $a_info;
	$inTitName = $photo['name'];
	$inTitName = (strlen($inTitName) > 0)?($inTitName.": "):"";
	if (file_exists(thumb_just_img($pid, '0')))
		$sizeKB = round(filesize(PHOTO_PATH.getImageFileName($pid, '0'))/1024);
	else
		$sizeKB = round(filesize(PHOTO_PATH.getImageFileName($pid, '9'))/1024);
	$sizeKB .= " KB,";

	$shadow = $isInAdmin?"":
					     " style=\"".((stristr($_SERVER['HTTP_USER_AGENT'], "IE") == true)?
					     	"filter:alpha(opacity=".$basis['opac'].");"
					     	:"-moz-opacity:".($basis['opac']/100).";")
					     ."\" onmouseover=\"javascript: LightenIt(this);\" onmouseout=\"javascript: DarkenIt(this);\"";
	echo "\t\t<div class=\"aThumb\"$shadow>\n";
	echo "\t\t\t<center>\n";
	echo "\t\t\t\t<a target=\"$targ\" href=\".?p=$pid\"
		title=\"$inTitName$sizeKB $hits hits and rated $rate by $raters person\">\n";
	echo "\t\t\t\t\t<img src=\"$imgFile\" height=\"75px\" width=\"75px\" /><br />\n";
	echo "\t\t\t\t\t<div class=\"thumbNameLine\">"
		.($isInAdmin?"<span style=\"padding-left: 5px;\" class=\"dot\">&#149;</span>":"")
		.$theName."</div>\n";
	if (!$isInAdmin)
		echo "\t\t\t\t\t<div class=\"thumbDate\">$mtime</div>\n";
	echo "\t\t\t\t</a>\n";
	echo "\t\t\t</center>\n";
	echo "\t\t</div>\n";
	return true;
}


########################################################### parse

$parse_curTag = "";
$parse_curId = "";
$parse_photoCnt = 0;
$parse_info = "";
$parse_each = "";

function infoCharacterData($parser, $data) {
	global $parse_curTag, $parse_curId, $parse_info, $parse_photoCnt, $$parse_info, $parse_each;
	if ((!strlen($parse_curTag)) && (!strlen(trim($data)))) return;
	if (strcmp($parse_curTag, "Photo") == 0) {
		${$parse_info}[$parse_curId] .= $data;
		return;
	}
	if (strcmp($parse_curTag, 'link') == 0)
		${$parse_info}['links'][$parse_photoCnt]['name'] .= $data;
	else if (!strlen($parse_curId))
		${$parse_info}[$parse_curTag] .= $data;
	else if (strcmp($parse_curTag, 'photo') == 0)
		${$parse_info}[$parse_curId]['photo'][$parse_photoCnt] .= $data;
	else
		if (strcmp($parse_curTag, $parse_each) != 0)
			${$parse_info}[$parse_curId][$parse_curTag] .= $data;
}

function infoStartElement($parser, $name, $attr) {
	global $parse_curTag, $parse_curId, $parse_info, $parse_each, $parse_photoCnt, $$parse_info;
	if (strcmp("Xmldata", $name) == 0) return;
	$parse_curTag = $name;
	if (strcmp($parse_curTag, $parse_each) == 0) {
		$parse_curId = $attr['id'];
		if (strcmp($parse_each, "Photo") == 0) {
			${$parse_info}[$parse_curId] = "";
			return;
		}
		else if (strcmp($parse_each, "Comment") != 0) {
			${$parse_info}[$parse_curId]['photo'] = array();
			$parse_photoCnt = 0;
		}
	}
	if (strcmp($parse_curTag, 'link') == 0) { // in basis.xml
		${$parse_info}['links'][$parse_photoCnt] = array();
		${$parse_info}['links'][$parse_photoCnt]['href'] = $attr['href'];
		${$parse_info}['links'][$parse_photoCnt]['title'] = $attr['title'];
		${$parse_info}['links'][$parse_photoCnt]['name'] = "";
		return;
	}
	if (!strlen($parse_curId))
		${$parse_info}[$parse_curTag] = "";
	else if (strcmp($parse_curTag, 'photo') == 0)
		${$parse_info}[$parse_curId]['photo'][$parse_photoCnt] = "";
	else if (strcmp($parse_curTag, $parse_each) != 0)
		${$parse_info}[$parse_curId][$parse_curTag] = "";
}

function infoEndElement($parser, $name) {
	global $parse_curTag, $parse_curId, $parse_info, $parse_each, $parse_photoCnt;
	$parse_curTag = "";
	if (strcmp($name, $parse_each) == 0)
		$parse_curId = "";
	if ((strcmp($name, 'photo') == 0) || (strcmp($name, 'link') == 0))
		$parse_photoCnt++;
}

function parse_container($parse_infoName, $p_each, $xmlfile, $fix = true) {
	global $parse_curTag, $parse_curId, $parse_info, $parse_each, $$parse_infoName, $alert_msg;
	$parse_info = $parse_infoName;
	$parse_each = $p_each;
	$parse_photoCnt = 0;
	$parse_curTag = "";
	$parse_curId = "";

	if (strcmp($p_each, "Basis") == 0)
		${$parse_info}['links'] = array();

	if (! file_exists($xmlfile))
		die("The file $xmlfile, does not exist!");

	$xmlParser = xml_parser_create();
	xml_set_element_handler($xmlParser,"infoStartElement","infoEndElement");
	xml_set_character_data_handler($xmlParser,"infoCharacterData");
	xml_parser_set_option($xmlParser,XML_OPTION_CASE_FOLDING,false);

	if (!($fp = fopen($xmlfile,"r")))
		die ("Could not open $xmlfile for reading.");

	while (($data = fread($fp,4096)))
		if (!xml_parse($xmlParser,$data,feof($fp)))
			die($xmlfile.":".sprintf("XML error at line %d column %d : %s", xml_get_current_line_number($xmlParser),
			xml_get_current_column_number($xmlParser),  xml_error_string(xml_get_error_code($xmlParser))));

	fclose($fp);
	xml_parser_free($xmlParser);

	$parse_info =& $$parse_infoName;
	reset($parse_info);

	global $transtable, $transmanual;

	foreach(array('name', 'date', 'dateadd', 'datetake', 'email', 'url') as $aval) {
		if (isset($parse_info[$aval]) && !is_array($parse_info[$aval])) {
			$parse_info[$aval] = strtr($parse_info[$aval], $transtable);
			$parse_info[$aval] = strtr($parse_info[$aval], $transmanual);
		}

		reset($parse_info);
		while (list($key,$value) = each($parse_info)) {
			if (is_array($parse_info[$key]) && isset($parse_info[$key][$aval]) && !is_array($parse_info[$key][$aval])) {
				$parse_info[$key][$aval] = strtr($parse_info[$key][$aval], $transtable);
				$parse_info[$key][$aval] = strtr($parse_info[$key][$aval], $transmanual);
			}
		}
	}
}

function save_container($parse_infoName, $p_each, $xmlfile) {
	global $$parse_infoName;
	$parse_info =& $$parse_infoName;

	foreach(array('name', 'date', 'dateadd', 'datetake', 'email', 'url') as $aval) {
		if (isset($parse_info[$aval]) && !is_array($parse_info[$aval])) {
			$parse_info[$aval] = htmlspecialchars($parse_info[$aval], ENT_NOQUOTES, "UTF-8");
		}

		reset($parse_info);
		while (list($key,$value) = each($parse_info)) {
			if (is_array($parse_info[$key]) && isset($parse_info[$key][$aval]) && !is_array($parse_info[$key][$aval])) {
				$parse_info[$key][$aval] = htmlspecialchars($parse_info[$key][$aval], ENT_QUOTES, "UTF-8");
			}
		}
	}

	if (! ($fout = fopen($xmlfile,"w")) )
		die("Couldn't open $xmlfile for writing.");
	fputs($fout,"<?xml version='1.0' encoding='UTF-8' ?>\n");
	fputs($fout,"<Xmldata>\n");
	reset($parse_info);
	while (list($key, $value) = each($parse_info)) {
		if (is_array($value)) {
			if (strcmp($p_each, "Basis") == 0) {
				reset($parse_info[$key]);
				while (list($inkey,$invalue) = each($parse_info[$key]))
					fputs($fout,"\t<link href=\"${invalue['href']}\" title=\"${invalue['title']}\">${invalue['name']}</link>\n");
			}
			else {
				fputs($fout,"\t<".$p_each." id=\"$key\">\n");
				reset($parse_info[$key]);
				while (list($inkey,$invalue) = each($parse_info[$key])) {
					if (ctype_lower($inkey))
						if (is_array($invalue))
							while (list($ininkey, $ininvalue) = each($parse_info[$key][$inkey]))
								fputs($fout,"\t\t<$inkey><![CDATA[$ininvalue]]></$inkey>\n");
						else
							fputs($fout,"\t\t<$inkey><![CDATA[$invalue]]></$inkey>\n");
				}
				fputs($fout,"\t</".$p_each.">\n");
			}
		}
		else {
			if (!is_int($key) && ctype_lower($key))
				fputs($fout,"\t<$key><![CDATA[$value]]></$key>\n");
			else
				if ((strcmp($p_each, "Photo") == 0) && is_int($key))
					fputs($fout,"\t<Photo id=\"$key\"><![CDATA[".$value."]]></Photo>\n");
		}
	}
	fputs($fout,"</Xmldata>\n");
	fclose($fout);
	parse_container($parse_infoName, $p_each, $xmlfile, false);
}

######################################################## global admin

function getCommentCount($obj, $fullText) {
	$ret = 1;
	if ($fullText) {
		if ($ret == 0)
			$ret = "No Comment";
		else if ($ret == 1)
			$ret = "One Comment";
		else
			$ret = $ret." Comments";
	}
	return $ret;
}


function getmicrotime()
{
    list($usec, $sec) = explode(" ", microtime());
    return ((float)$usec + (float)$sec);
}

function writeLinkLine($x, $arr = "", $disp = "table-row") {
	if (!is_array($arr))
		$arr = array('href' => '', 'name' => '', 'title' => '');
	echo "\t<tr id=\"linkline$x\" style=\"display: $disp;\">\n\t\t<td>".($x+1)."</td>";
	$m = array('n' => 'name', 'h' => 'href', 't' => 'title');
	reset($m);
	$bold = (strlen($arr['href']) == 0);
	echo "\n\t\t<td><input class=\"textW\" style=\"font-weight:".($bold?"bold":"normal").";\" name=\"l${x}n\" id=\"l${x}n\" value=\"${arr['name']}\"></input></td>";
	echo "\n\t\t<td><input class=\"textW\" onkeyup=\"fixBoldInput($x, this.value);\" onblur=\"fixBoldInput($x, this.value);\" name=\"l${x}h\" id=\"l${x}h\" value=\"${arr['href']}\"></input></td>";
	echo "\n\t\t<td><input class=\"textW\" name=\"l${x}t\" id=\"l${x}t\" value=\"${arr['title']}\"></input></td>";
?>
		<td>
			<a href="#AddBelow" onclick="javascript:linkAddBelow('<?php echo $x; ?>');" title="Add a link below this one">Add</a> |
			<a href="#AddBelow" onclick="javascript:linkDelThis('<?php echo $x; ?>');" title="Delete this Link">Del</a>
		</td>
	</tr>
<?php
}

function page_is($x) {
	global $page;
	return (strcmp($page, $x) == 0);
}

function write_in_ajax($msg = "") {
	$f = fopen(UPLOAD_AJAX_FILE, "w");
	fwrite($f, $msg);
	fclose($f);
}

function makeTheThumb($ppath, $ta, $mood, $tchar) {
	global $basis;
	if (!isset($basis['jpegq']))
		$basis['jpegq'] = DEFAULT_JPEG_QUAL;
	list($w, $h) = getimagesize($ppath);
	$orig = imagecreatefromjpeg($ppath);
	if ((strcmp($mood, 'width') == 0) || (($w > $h) ^ (strcmp($mood, 'min') == 0))) {
		$tw = $ta;
		$th = round($tw*$h/$w);
	}
	else {					// photo is vertical
		$th = $ta;
		$tw = round($th*$w/$h);
	}
	$timg = imagecreatetruecolor($tw, $th);
	$tpath = substr_replace($ppath, $tchar, -5, 1); // _9.jpg => _x.jpg
	imagecopyresampled($timg, $orig, 0, 0, 0, 0, $tw, $th, $w, $h);
	imageinterlace($timg, 1);
	imagejpeg($timg, $tpath, $basis['jpegq']);
	imagedestroy($timg);
	return "ENDED;$tw;$th";
}


function gen_3_thumb($ppath, $sklW, $sklH, $sklT, $sklL, $ta) { // Generate the Square thumb from skeleton
	global $basis;
	list($w, $h) = getimagesize($ppath);
	$orig = imagecreatefromjpeg($ppath);
	$timg = imagecreatetruecolor($ta, $ta);
	$tpath = substr_replace($ppath, '3', -5, 1); // _9.jpg => _3.jpg
	$rr = $w/(($w<$h)?SKL_PHOTO_W:SKL_PHOTO_W*$w/$h);
	//echo $sklL*$rr."|".$rr."|".$sklW*$rr."|".$sklW."<br>";
	imagecopyresampled($timg, $orig, 0, 0, $sklL*$rr, $sklT*$rr, $ta, $ta, $sklW*$rr, $sklH*$rr);
	imageinterlace($timg, 1);
	imagejpeg($timg, $tpath, $basis['jpegq']);
	imagedestroy($timg);
}

function deletePhoto($pid) {
	global $photo, $categs, $stories, $comments, $photos;

	if (!is_array($categs)) {
		$categs = array();
		parse_container('categs', 	'Category', 'data/categories.xml');
	}
	if (!is_array($stories)) {
		$stories = array();
		parse_container('stories', 	'Story', 	'data/stories.xml');
	}

	if (!is_array($comments)) {
		$comments = array();
		parse_container('comments', 'Comment', 	'data/comments.xml');
	}

	if (!is_array($photos)) {
		$photos = array();
		parse_container('photos', 	'Photo', 	'data/photos.xml');
	}

	$xmlfile = sprintf("data/p_%06d.xml", $pid);
	parse_container('photo', '', $xmlfile);
	$postfix = isset($photo['postfix'])?('_'.$photo['postfix']):"";
	unlink($xmlfile);
	for ($i=0; $i<10; $i++) {
		if (file_exists($filename = PHOTO_PATH.sprintf('%06d%s_%d.jpg', $pid, $postfix, $i)))
			unlink($filename);
	}

	unset($categs [$photo['categ']][array_search($pid, $categs [$photo['categ']])]);
	unset($stories[$photo['story']][array_search($pid, $stories[$photo['story']])]);
	unset($photos[$pid]);

	reset($categs);
	while (list($key, $val) = each($categs))
		if (isset($val['photo']) && is_array($val['photo']))
			if (($t = array_search($pid, $val['photo'])) !== FALSE)
				unset($categs[$key]['photo'][$t]);

	reset($stories);
	while (list($key, $val) = each($stories))
		if (isset($val['photo']) && is_array($val['photo']))
			if (($t = array_search($pid, $val['photo'])) !== FALSE)
				unset($stories[$key]['photo'][$t]);

	reset($comments);
	while (list($key, $val) = each($comments))
		if (is_array($val))
			if (strcmp($val['owner'], "p".$pid) == 0)
				unset($comments[$key]);

	save_container('comments', 'Comment', 'data/comments.xml');
	save_container('categs', 'Category', 'data/categories.xml');
	save_container('stories', 'Story', 'data/stories.xml');
	save_container('photos', 'Photo', 'data/photos.xml');

	build_rss();
}

function handle_container($contArr, $contName, $contChar) {
	global $edit, $ok_msg, $alert_msg, $cmd, $$contArr, $cid, $doDel;
	$contId = $contChar.'id';
	$conts =& $$contArr;
	if (isset($_GET['cmd'])) {
		$cmd = $_GET['cmd'];
		$isAdd = (strcmp($cmd, 'add') == 0);
		$isEdt = (strcmp($cmd, 'edt') == 0);
		if (!isset($_GET[$contId]) && !isset($_POST[$contId]))
			$alert_msg = "Please enter $contName"."ID as $contId!";
		else {
			$cid = ((isset($_GET[$contId]))?$_GET[$contId]:$_POST[$contId])+0;
			if (strcmp($cmd, 'doEdt') == 0) {
				$edit = true;
				if (!isset($conts[$cid]['name']))
					$alert_msg = "No $contName with this $contName"."ID ($cid) exists!";
			}
			else if (strcmp($cmd, 'doDel') == 0) {
				$doDel = true;
				if (!isset($conts[$cid]['name']))
					$alert_msg = "No $contName with this $contName"."ID ($cid) exists!";
			}
			else if (strcmp($cmd, 'del') == 0) {
				$howto = isset($_POST['howto'])?$_POST['howto']:"";
				if (!isset($conts[$cid]) || !is_array($conts[$cid]))
					$alert_msg = "No $contName with this $contName"."ID (".$cid.") exists!";
				else if (!isset($_POST['howto']))
					$alert_msg = "The method of removation is not posted!";
				else if (($cid == 1) && strcmp($howto, 'empty') != 0)
					$alert_msg = "You can not delete Default $contName!";

				else {
					$subc = $conts[$cid]['sub'];
					$field = ($contChar == 'c')?'categ':'story';
					global $photo;
					$photo = array();
					if ($subc == -1)
						$subc = 1;

					if ((strcmp($howto,'justc') == 0) || (strcmp($howto,'whole') == 0)) {
						reset($conts);
						while (list($acid, $acvals) = each($conts)) {
							if (is_array($acvals) && ($acvals['sub'] == $cid))
								$conts[$acid]['sub'] = $conts[$cid]['sub'];			//to save the connectedness!
						}
						reset($conts);
					}

					if ((strcmp($howto,'empty') == 0) || (strcmp($howto,'justc') == 0)) {
						reset($conts[$cid]['photo']);
						while (list($key, $pid) = each($conts[$cid]['photo'])) {
							if (!in_array($pid, $conts[$subc]['photo']))
								array_push($conts[$subc]['photo'], $pid);
							if ($cid == getPhotoInfo($pid, $field)) {
								$xmlfile = sprintf("data/p_%06d.xml", $pid);
								parse_container('photo', '', $xmlfile);
								$photo[$field] = $subc;
								save_container('photo', 'Photo', $xmlfile);
							}
						}
						$conts[$cid]['photo'] = array();
						if (strcmp($howto,'empty') == 0)
							$ok_msg = "$contName ".$conts[$cid]['name']."'s photos got erased successfully";
						else {
							$ok_msg = "$contName \"".$conts[$cid]['name']."\" ($contName"."ID: $cid) deleted successfully!";
							unset($conts[$cid]);
						}
					}
					else if (strcmp($howto,'whole') == 0) {
						reset($conts[$cid]['photo']);
						while (list($key, $pid) = each($conts[$cid]['photo']))
							if ($cid == getPhotoInfo($pid, $field)) {
								$ok_msg .= "Photo $pid (".getPhotoInfo($pid, 'name').") Deleted Successfully.<br />\n";
								deletePhoto($pid);
							}
						$ok_msg .= "$contName \"".$conts[$cid]['name']."\" ($contName"."ID: $cid) deleted successfully!<br />\n";
						unset($conts[$cid]);
					}
				}
			}
			else if ($isEdt || $isAdd) {
				if (!isset($_POST['name']) || (strlen($_POST['name']) == 0))
					$alert_msg = 'The "Name" filed is required!';
				else if ($isAdd && isset($conts[$cid]))
					$alert_msg = "The $contName \"".$_POST['name']."\" is already added as this $contName"."ID ($cid)!";
				else {
					$p = array();
					if ($isEdt)
						$p = $conts[$cid]['photo'];
					$conts[$cid] = array('name' => $_POST['name'], 'desc' => $_POST['desc'],
										 'list' => $_POST['list'], 'pass' => $_POST['pass'],
										 'sub' => $_POST['sub'], 'photo' => ($isEdt)?$conts[$cid]['photo']:array());
					foreach (array('date', 'getcmnts') as $tk => $tv)
						if (isset($_POST[$tv]))
							$conts[$cid][$tv] = $_POST[$tv];
					if (strcmp($cmd, 'add') == 0) {
						//$conts[$cid]['hits'] = 0;
						$conts['last'.$contId] = $cid;
					}
					$ok_msg = "$contName \"".$conts[$cid]['name']."\" ".((strcmp($cmd, 'add') == 0)?"added":"edited")." succesfully!";
				}
			}
			else
				$alert_msg = $cmd.' is not a valid command!';
		}
	}
	$edit &= !strlen($alert_msg);
}

function print_container($contArr, $contName, $contNames, $contChar) {
	global $page, $ok_msg, $alert_msg, $$contArr, $cid, $edit, $doDel;
	$conts =& $$contArr;
	$ccid = $contChar.'id';
?>
		<div class="back2mainR"><a href=".">&nbsp;View Gallery >> </a></div>
		<div class="back2main"><a href="?"><< Admin Page</a></div>
		<div class="part">
			<div class="title"><a style="color: white" href="?page=<?php echo $page; ?>">Manage <?php echo $contNames; ?></a></div>
			<div class="inside">
<?php if (strlen($alert_msg)) echo "\t\t\t\t<div class=\"method\"><div class=\"note_invalid\">$alert_msg</div></div><br />"; ?>
<?php if (strlen($ok_msg))    echo "\t\t\t\t<div class=\"method\"><div class=\"note_valid\">$ok_msg</div></div><br />"; ?>
				<div class="method">
<?php
	if ($doDel) {
		$theName = $contName." ".$conts[$cid]['name'];
		$directN = 0;
		reset($conts[$cid]['photo']);
		$field = ($contChar == 'c')?'categ':'story';

		while (list($key, $val) = each($conts[$cid]['photo']))
			if ($cid == getPhotoInfo($val, $field))
				$directN++;
?>
					<span class="name">Delete <a href=".?<?php echo $contChar."=".$cid; ?>"><?php echo $theName; ?></a> </span><br />
					<div style="padding: 5px 30px 15px">
						<form method="post" action="?page=<?php echo $page."&".$contChar."id=".$cid; ?>&cmd=del" onsubmit="javascript:return confirmDelete('<?php echo $conts[$cid]['name']; ?>');">
							<span class="dot">&#149;</span>Which action do you wish to do?<br />
							<table width="100%" style="margin-left: 20px;">
							<tr><td width="25%"><input name="howto" value="justc" type="radio" class="radio" checked="checked" >&nbsp;<b>Just <?php echo $contName; ?>:</b></input></td>
								<td> Delete <?php echo $theName; ?> but NOT its photos.</td></tr>
							<tr><td><input name="howto" value="empty" type="radio" class="radio"                   >&nbsp;<b>Empty It:</b></input></td>
								<td> Empty <?php echo $theName; ?> of all its own (direct) photos.</td></tr>
							<tr><td><input name="howto" value="whole" type="radio" class="radio"                   >&nbsp;<b>With direct photos:</b></input></td>
								<td> Delete <?php echo $theName; ?> and its <?php echo $directN; ?> direct photos.</td></tr>
							</table>
							<br />
							<input class="submit" type="submit" value="&nbsp;&nbsp;&nbsp;Delete it!&nbsp;&nbsp;&nbsp;"></input>
							<span style="padding-left: 20px;"></span>
						</form>
						<form method="post" action="?page=<?php echo $page; ?>">
							<input class="reset" type="submit" value="&nbsp;&nbsp;&nbsp;Oops, get Back!&nbsp;&nbsp;&nbsp;"></input>
						</form>
					</div>
<?php
	} else {
?>
					<span class="name">Current <?php echo $contNames; ?> List
					<span class="thumbcntarr" style="color: #666" >
					<?php
						global $thumbCntArr;
						for($i=0; $i<count($thumbCntArr); $i++)
							echo "\t\t\t&nbsp;[<a href=\"admin.php?page=$page&n=".$thumbCntArr[$i]."\">".$thumbCntArr[$i]."</a>]\n"
					?>
					</span>
					</span>
					<br />
					<div style="padding-left: 30px">
<?php
					reset($conts);
					$n = isset($_GET['n'])?$_GET['n']:-1;
					while (list($accid, $accval) = each($conts)) 		//a ceratin container value!
						if (is_array($accval)) { 					// might be info!
							if ($n-- == 0) {
								echo "\t\t\t\t\t<center><a href=\"admin.php?page=$page&n=-1\">View all categories</a></center><br />\n";
								break;
							}
							echo "\t\t\t\t\t\t\t<div style=\"position: relative;\">"
									."<span style=\"color: #333; position: absolute; top: 0px; right: 80px; \">"
								." <a href=\"?page=$page&cmd=doEdt&$ccid=$accid#add\">Edit it</a>"
								." :: <a href=\"?page=$page&cmd=doDel&$ccid=$accid\">Delete it</a>"
								."</span>\n"
								."<span class=\"dot\">&#149;</span>"
								."<a name=\"$accid\" href=\"./?$contChar=$accid\">".$accval['name']."</a> "
								."<span class=\"categinfo\">["
									.count($accval['photo']).' Photos'
									.((strcmp($contName, "Story") == 0)?
										" ".'since '.$accval['date']
										.", ".((strcmp($accval['getcmnts'], "yes") == 0)?
											getCommentCount('s'.$accid, true)
											:'Doesnt get comments')
										:'')
									.", ".(strlen($accval['pass'])?'Protected by "'.$accval['pass'].'"':'Public')
									." & ".(strcmp($accval['list'], 'list') == 0?'Listed':'Not listed')
								."]</span> "
							."\t\t\t\t\t\t\t\t<div class=\"categdesc\">".nl2br($accval['desc'])."</div>\n"
							."</div>";
						}
					reset($conts);
?>

					</div>
				</div>
				<a name="add"></a>
				<div class="method" style="margin-top: 25px;">
					<span class="name"><?php echo $edit?('Edit the '.$contName.' "'.$conts[$cid]['name'].'"'):"<a style=\"color: black\" href=\"?page=$page\">Add a new ".$contName.'</a>'; ?></span><br />
					<center>
						<form method="post" action="?page=<?php echo $page; ?>&cmd=<?php echo $edit?'edt':'add'; ?>" onsubmit="javascript:return (checkHasPass()<?php echo (strcmp($contName, "Story") == 0)?'&& checkDate()':''; ?>);">
						<input type="hidden" name="<?php echo $ccid; ?>" value="<?php echo $edit?$cid:($conts['last'.$ccid]+1); ?>"></input>
						<table width="60%" cellpadding="5" style="position: relative; text-align: left; ">
							<tr><td>Name:</td><td><input id="name" name="name" type="text" class="text" size="32" value="<?php echo $edit?$conts[$cid]['name']:''; ?>" autocomplete="off"></input></td></tr>
							<tr><td valign="top">Description:</td><td><textarea cols="32" rows="5" name="desc"><?php echo $edit?$conts[$cid]['desc']:''; ?></textarea></td></tr>
							<?php if (strcmp($contName, "Story") == 0) { ?>
								<tr><td>Date:</td><td><input id="date" name="date" type="text" class="text" size="32" value="<?php echo $edit?$conts[$cid]['date']:date('Y/m/d'); ?>"></input></td></tr>
								<tr><td>Get Comments:</td><td><span style="margin-left: 5px"></span><input <?php echo ($edit && isset($conts[$cid]['getcmnts']) && !strcmp($conts[$cid]['getcmnts'], "yes"))?'checked="checked" ':''; ?>name="getcmnts" value="yes" type="radio" class="radio">Yes</input>
																						<span style="margin-left: 22px"></span><input <?php echo ($edit && isset($conts[$cid]['getcmnts']) && !strcmp($conts[$cid]['getcmnts'], "yes"))?'':'checked="checked" '; ?>name="getcmnts" value="no" type="radio" class="radio">No</input></td></tr>
							<?php } else echo "\n"; ?>
							<tr><td>Visibility:</td><td><span style="margin-left: 5px"></span><input <?php echo ($edit && !strcmp($conts[$cid]['list'], "list") == 0)?'':'checked="checked" '; ?>name="list" value="list" type="radio" class="radio">Listed</input>
														<span style="margin-left: 42px"></span><input <?php echo ($edit && !strcmp($conts[$cid]['list'], "list") == 0)?'checked="checked" ':''; ?>name="list" value="hide" type="radio" class="radio">Not Listed</input></td></tr>
							<tr><td>Privacy:</td><td><span style="margin-left: 5px"></span><input <?php echo ($edit && (strlen($conts[$cid]['pass'])))?'':'checked="checked" '; ?>name="passRadio" value="" type="radio" class="radio" id="public" onclick="javascript:checkPrivacyRow();">Public</input>
													 <span style="margin-left: 42px"></span><input <?php echo ($edit && (strlen($conts[$cid]['pass'])))?'checked="checked" ':''; ?>name="passRadio" value="" type="radio" class="radio" onclick="javascript:checkPrivacyRow();">Passworded</input></td></tr>
							<tr id="passwordRow" <?php echo ($edit && (strlen($conts[$cid]['pass'])))?'':'style="display: none"'; ?>><td>Password:</td><td><input name="pass" id="password" type="text" class="text" autocomplete="off" size="20" value ="<?php echo $edit?$conts[$cid]['pass']:''; ?>"></input></td></tr>
							<tr><td>Child of:</td><td>
								<span style="margin-left: 10px"></span>
								<select class="select" name="sub" type="text">
									<option value="-1" <?php echo ($edit && ($conts[$cid]['sub'] != -1))?"":"selected=\"selected\""; ?>>No Inheritance</option>
								<?php
									reset($conts);
									while (list($acid, $acvals) = each($conts))
										if (is_array($acvals) && !($edit && ($acid == $cid))) {
											$sel = $edit?($acid == $conts[$cid]['sub']):false;
											echo "\t\t\t\t\t\t\t\t<option ".($sel?"selected=\"selected\" ":"")."value=\"$acid\">".$acid.": ".$acvals['name']."</option>\n";
										}
									reset($conts);
								?>
								</select></td></tr>
							<tr><td colspan="2" style="text-align: center"> </td></tr>
							<tr><td colspan="2" style="text-align: center">
								<input class="submit" type="submit" value="&nbsp;&nbsp;&nbsp;<?php echo $edit?'Save Changes':'Add '.$contName; ?>&nbsp;&nbsp;&nbsp;"></input>
								<span style="padding-left: 20px;"></span>
								<input class="reset" type="Reset" value="&nbsp;&nbsp;&nbsp;Reset Changes&nbsp;&nbsp;&nbsp;"></input>
							</td></tr>
						</table>
						</form>
					</center>
<?php
		}
?>
				</div>
			</div>
		</div>
<?php
}

######################################################## RSS

function bannedIP($ip) {
	global $basis;
	$bans = explode("\n", $basis['bannedip']);
	foreach ($bans as $banip) {
		if (strcmp($banip, $ip) == 0)
			return true;
	}
	return false;
}

function build_rss() {
	global $basis, $photos, $nphotos, $categs, $stories, $addadd;
	$nphotos = count($photos)-1;

	$filename = 'index.xml';
    $h = fopen($filename, 'w');

	fputs ($h, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	fputs ($h, "<rss version=\"2.0\">\n");
	fputs ($h, "<channel>\n");
	fputs ($h, "\t<title>${basis['pgname']}</title>\n");
	fputs ($h, "\t<link>$addadd/</link>\n");
	fputs ($h, "\t<description>${basis['pgdesc']}</description>\n");
	fputs ($h, "\t<language>en</language>\n");
	fputs ($h, "\t<generator>http://p.horm.org/er</generator>\n");
	fputs ($h, "\t<lastBuildDate>".date("r")."</lastBuildDate>\n");
	fputs ($h, "\t<managingEditor>${basis['auemail']}</managingEditor>\n");

	if ($nphotos > 0) {
		end($photos);
		$neck = 10;
		$n = min($neck, $nphotos);

		for ($i=0; ($i<$n) && (strcmp(key($photos), 'lastpid') != 0);) {
			if (canthumb(key($photos))) {
				$i++;
				$pid = key($photos);
				$photo = getAllPhotoInfo($pid);
				$dates = sscanf($photo['dateadd'], "%d/%d/%d %d:%d");
				$mtime = date("r", mktime($dates[3], $dates[4], 0, $dates[1], $dates[2], $dates[0]));
				$src = thumb_just_img($pid, 9);
				$desc = "<![CDATA[<img src=\"$addadd/$src\" alt=\""
					.htmlspecialchars($photo['desc'], ENT_QUOTES, "UTF-8")
					."\" />]]>";
				fputs ($h, "\t<item>\n");
					fputs ($h, "\t\t<title>".htmlspecialchars($photo['name'], ENT_QUOTES, "UTF-8")."</title>\n");
					fputs ($h, "\t\t<link>$addadd/?p=$pid</link>\n");
					fputs ($h, "\t\t<guid isPermaLink=\"true\">$addadd/?p=$pid</guid>\n");
					fputs ($h, "\t\t<description>\n");
					fputs ($h, "\t\t\t$desc\n");
					fputs ($h, "\t\t</description>\n");
					fputs ($h, "\t\t<pubDate>$mtime</pubDate>\n");
					fputs ($h, "\t\t<category>".htmlspecialchars($categs[$photo['categ']]['name'], ENT_QUOTES, "UTF-8")."</category>\n");
				fputs ($h, "\t</item>\n");
			}
			prev($photos);
		}
	}

	fputs ($h, "</channel>\n");
	fputs ($h, "</rss>\n");
    fclose($h);
}

#################################################################################################
############################# ABOVE WAS COMMON.PHP IN PRIOR VERSIONS ############################
#################################################################################################

function get_email_link($name="") {
	global $basis;

	if (!isset($name) || !strlen($name))
		$name = $basis['auname'];

	if (!isset($basis['showemail']))
		$basis['showemail'] = 'link';

	$emailat = $basis['auemail'];
	if ($basis['showemail'] != 'asis')
		$emailat = str_replace(array("@", "."), array("[at]", "[dot]"), $basis['auemail']);

	$email = $name;
	if (strcmp($basis['showemail'], 'text') == 0)
		$email = $name." ($emailat)";
	else if (strcmp($basis['showemail'], 'link') == 0)
		$email = "<a href=\"mailto:$emailat?subject=${basis['pgname']} Photos\">$name</a>";
	else if (strcmp($basis['showemail'], 'asis') == 0)
		$email = "<a href=\"mailto:$emailat?subject=${basis['pgname']} Photos\">$name</a>";
	return $email;
}

function write_footer() {
	global $basis;
	$email = get_email_link();
?>
	<div style="clear:both;"></div>
	<div style="width: 100%;">
		<div class="footer">
			<a href=".">This photo gallery</a> is powered by <a href="http://p.horm.org/er">Phormer, version <?php echo PHORMER_VERSION; ?></a>.<br />
			Unless stated otherwise, all photos are taken, edited, and copyright by
			<?php echo $email; ?>.<br />
			Copying or otherwise using these photos requires the permission of author</a>.<br />
		</div>
	</div>
<?php
}

function write_headers($title) {
	global $basis, $p, $s, $hasexif;
	if (strcmp(substr($basis['theme'], -4), '.css') == 0)
		$basis['theme'] = substr($basis['theme'], 0, -4);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="Content-Language" content="en-us" />
		<link rel="stylesheet" type="text/css" href="css.php?theme=<?php echo $basis['theme']; ?>" />
		<link rel="alternate" type="application/rss+xml" title="RSS" href="index.xml" />
		<script language="javascript" type="text/javascript" src="files/phorm.js"></script>
		<script lanugage="javascript" type="text/javascript">
			var DarkenVal = <?php echo $basis['opac']; ?>
		</script>
<?php
	if (isset($basis['icon']) && strlen($basis['icon']))
		echo "\t\t<link rel=\"icon\" href=\"".$basis['icon']."\">\n";
?>
		<title><?php echo $title ?></title>
	</head>
	<body<?php if (($p != -1) || ($s != -1)) echo " onfocus=\"prepareBody();\""; ?>>
<?php
}

function write_top() {
	global $basis;
?>
	<div class="topPhorm">
		<span class="headerDot">&#149;&nbsp;</span>
		<a href="."><?php echo $basis['pgname']; ?></a>
		<div class="topPhormAbout">
			<?php echo nl2br($basis['pgdesc']); ?>
		</div>
	</div>
	<center><div id="Granny">
<?php
}

function cutNeck($s, $neck = 20) {
	if (strlen($s) > $neck)
		return substr($s, 0, $neck-3)."&#133; ";
	else
		return $s;
}

function dfsCategStory($contName, $cid, $depth) {
	global $basis, $_GET;
	global $$contName, $rpn, $wStories;
	$conts =& $$contName;
	$clet = strtolower(substr($contName, 0, 1));
	if ($wStories >= $basis['defrss']-2)
		return;

	reset($conts);
	while (list($acid, $acval) = each($conts)) {
		if (is_array($acval) && ($acval['sub'] == $cid) && (strcmp($acval['list'], 'list') == 0)) {
			echo "\t\t<div class=\"item\">\n";
			for ($i=0; $i<$depth; $i++)
				echo "\t\t\t<span style=\"padding-left: 10px;\"></span>\n";
			$date = (isset($acval['date']))?"[${acval['date']}]":"";
			$isCur = (isset($_GET[$clet]) && (strcmp($acid, $_GET[$clet]) == 0));

			$acval['name'] = cutNeck($acval['name'], 20);

			echo "\t\t\t<span class=\"categeach\"><a "
				."href=\".?$clet=$acid".($rpn == $basis['defrpc']?"":"&rpn=$rpn")
				."\" title=\"".strip_tags(html_entity_decode($acval['desc']))." $date\">";
			if ($isCur)
				echo "<span class=\"reddot\" style=\"font-size: 1em; padding-right: 4px;\">&#149;</span>";
			else
				echo "<span class=\"dot\">&#149;</span>";
			echo (strlen($acval['pass'])?"* ":"")
				.$acval['name']
				."<span class=\"categinfo\">"
				."[".count($acval['photo'])."]</span> ";
			echo "</a></span>\n";
			echo "\t\t</div>\n";
			if (strcasecmp($contName, 'stories') == 0)
				$wStories++;
			if ($wStories < $basis['defrss']-2)
				dfsCategStory($contName, $acid, $depth+1);
		}
	}

	reset($conts);
	if ($cid != -1)
		while (list($acid, $acval) = each($conts))
			if ($acid == $cid) {
				//next($conts);
				return;
			}
}

function cont_cmp($a, $b) {
	return strcmp($a['name'], $b['name']);
}


function write_conts($contarr, $contName) {
	//global ${$contarr};  <span class=\"thumbcntarr\"> :: ".count(${$contarr})."</span>
	global $categs, $stories, $wStories, $basis;

	echo "\t<div class=\"part\">\n";
	echo "\t\t<div class=\"titlepart\"><span class=\"reddot\">&#149;</span>"
		."<a href=\".".((strcasecmp($contName, "stories") == 0)?"?mode=stories":"")."\">"
		.$contName
		."</a>"
		."</div>\n";
	echo "\t\t<div class=\"submenu\">\n";

	if (strcasecmp($contName, "stories") != 0)
		uasort(${$contarr}, "cont_cmp");

	dfsCategStory($contarr, -1, 0, 0);
	if ((strcasecmp($contarr, 'stories') == 0) && ($wStories >= $basis['defrss']))
		echo "\t\t\t<br /><a href=\".?alls=1\">[Show all Stories]</a>\n";
	echo "\t\t</div>\n";
	echo "\t</div>\n";
}

function write_credits() {
?>
	<div class="part">
		<div class="titlepart"><span class="reddot">&#149;</span>Powered by</div>
		<div class="submenu">
			<div class="item"><span class="dot">&#149;</span>&nbsp;<a href="http://p.horm.org/er" title="Rephorm Your Phormer Pharm!">Phormer <?php echo PHORMER_VERSION; ?></a></div>
			<br />
<?php
	global $basis;
	if (isset($basis['showemail'])&&
	 	((strcmp($basis['showemail'], 'asis') == 0) || (strcmp($basis['showemail'], 'link') == 0))) {
	 		$email = get_email_link('Email');
?>
			<div class="item"><span class="dot">&#149;</span>&nbsp;<?php echo $email; ?></div>
<?php
	 	}
?>
			<div class="item"><span class="dot">&#149;</span>&nbsp;<a href="index.xml" title="RSS Feed">RSS</a></div>
			<div class="item"><span class="dot">&#149;</span>&nbsp;<a href="admin.php" title="Login to the Administration Region">Admin Page</a></div>
		</div>

	</div>
<?php
}

function writeNextz($p) {
	global $photos, $categs, $stories, $basis;
	end($photos);
	while(key($photos) != $p)
		prev($photos);
	do {
		if (!prev($photos)) {  break; }
	} while (!canthumb(key($photos)));
	$prev = key($photos);
	if (!$prev)
		{ $prev = $p; reset($photos); }

	next($photos);
	do {
		if (!next($photos)) break;
	} while (!canthumb(key($photos)));
	$next = key($photos);
	if (!$next) $next = $p;

	$photo = getAllPhotoInfo($p, "./");
?>
	<div class="navigation">
		<div class="title"><span class="darkdot">&#149; </span>Prev.</div>
			<?php thumbBox($prev, "", false, false, "_self"); ?>
		<div class="bottitle">&nbsp;</div>
	</div>

	<div class="navigation">
		<div class="title" style="text-align: center">
			<span class="darkdot">&#149;</span>
			Random Neighbours
			<span class="darkdot">&#149;</span>
		</div>
<?php
	$arr = array();
	if (!isset($basis['pickneigh']))
		$basis['pickneigh'] = 'all';

	if ((strcmp($basis['pickneigh'], 'categs' ) == 0) || (strcmp($basis['pickneigh'], 'all') == 0))
		$arr = array_merge($arr, array_values($categs[$photo['categ']]['photo']));
	if ((strcmp($basis['pickneigh'], 'stories') == 0) || (strcmp($basis['pickneigh'], 'all') == 0))
		$arr = array_merge($arr, array_values($stories[$photo['story']]['photo']));

	if (strcmp($basis['pickneigh'], 'all') == 0)
		$arr = array_values(array_unique($arr));

	$nc = count($arr);
	srand(time());
	$outed = array();
	for ($i=0; $i<4; $i++) {
		$rp = rand(0, $nc-1);
		while (!canthumb($arr[$rp]))
			$rp = ($rp+1)%$nc;
		if ($nc > 4)
			for ($j=0; $j<$i; $j++)
				if (($rp == $outed[$j]) || ($arr[$rp] == $p)) {
					do {
						$rp = ($rp+1)%$nc;
					} while (!canthumb($arr[$rp]));
					$j = -1;
				}
		$outed[$i] = $rp;
		thumbBox($arr[$rp]);
	}
?>
		<div class="bottitle">&nbsp;</div>
	</div>

	<div class="navigation">
		<div class="title" style="text-align: right">Next<span class="darkdot"> &#149;</span></div>
			<?php thumbBox($next, "", false, false, "_self"); ?>
		<div class="bottitle">&nbsp;</div>
	</div>
	<div class="divClear"></div>
<?php
}

function random_string() {
	srand(time());
	$ret = "";
	for ($i=0; $i<WV_LENGTH; $i++)
		$ret .= chr(rand(ord('0'), ord('9')));
	return $ret;
}

function writeCommenting($t, $c) {
	global $alert_msg;
	$cname = ($t == 's')?'Story':'Photo';
?>
	<center>
	<div class="Commenting">
	<div class="title"><span class="reddot" style="font-size: 14px">&#149;</span>
		Comments on this <?php echo $cname; ?>
	</div>
<?php
	global $comments, $isAdmin, $basis;
	$own = $t.$c;
	$cmntfound = false;
	reset($comments);
	//echo "<pre>".print_r($comments, true)."</pre>";
	while (list($key, $val) = each($comments))
		if (strcmp($key, "lastiid") != 0)
			if ( strcmp($val['owner'], $own) == 0) {
				$cmntfound = true;
				echo "<div class=\"cell\">\n";
				echo "<div class=\"head\">\n";

				$name = trim($val['name']);
				if (strlen($name) == 0) $name = "Anonymous";
				if ($isAdmin)
					$name .= "&lrm; :: {".$val['ip']."}";
				echo "<span class=\"dot\">&#149;</span>&nbsp;&nbsp;".$name;

				$www = trim($val['url']);
				if (strtolower(substr($www, 0, 7)) != "http//")
					$www = substr($www, 6);
				if (strtolower(substr($www, 0, 7)) != "http://")
					$www = "http://".$www;

				$email = trim($val['email']);

				if ((strlen($www) > 9) || (strlen($email)))
					echo " [ ";
				if (strlen($www) > 9) {
					echo "<a href=\"$www\">www</a>";
					if (strlen($email))
						echo " | ";
				}
				if (strlen($email)) {
					$emailat = str_replace(array("@", "."), array("[at]", "[dot]"), $email);
					echo "<a href=\"mailto:".$emailat."\">email</a>";
				}
				if ($isAdmin) {
					echo " | <a href=\".?$t=$c&cmd=delcmnt&cmntid=$key\">";
					echo "del</a>";
				}
				if ((strlen($www) > 9) || (strlen($email)))
					echo " ]";
				$dates = sscanf($val['date'], "%d-%d-%d %d:%d");
				if ($dates[0] == 0)
					$mtime = "";
				else
					$mtime = " on ".date("F jS \o\f y \a\\t H:i", mktime($dates[3], $dates[4], 0, $dates[1], $dates[2], $dates[0]));
				echo $mtime." said:</div>\n";
				$en = textDirectionEn($val['txt']);
				$dir = $en?"":" class=\"r\"";
				echo "<blockquote$dir>".nl2br($val['txt'])."\n";
				echo "</blockquote>\n";
				echo "</div>\n";
			}
		if (!$cmntfound)
			echo "<div class=\"bcell\">No Comment yet.</div>\n";
?>
		<div class="bottitle">&nbsp;</div>
		<div class="title"><span class="reddot">&#149;</span> Leave your own comment</div>
		<div class="bcell">
			<form action="<?php echo ".?$t=$c#cmnts"; ?>" method="post">
			<table cellspacing="2" cellpadding="2">
					<tr><td width="40%">  Name:   </td><td><input type="text" size="20" name="name" value="<?php if (strlen($alert_msg) && isset($_POST['name'])) echo $_POST['name']; ?>"></td></tr>
					<tr><td>  Email:  </td><td><input type="text" size="20" name="email" value="<?php if (strlen($alert_msg) && isset($_POST['email'])) echo $_POST['email']; ?>"></td></tr>
					<tr><td>  Webpage:</td><td><input type="text" size="20" name="url" value="<?php if (strlen($alert_msg) && isset($_POST['url'])) echo $_POST['url']; ?>"></td></tr>
<?php
	if (!isset($basis['haswvw']) || strcmp($basis['haswvw'], "no") != 0) {
					$basis = array();
					parse_container('basis', 'Basis', 'data/basis.xml');
					$basis['wvw'] = random_string();
					save_container('basis', 'Basis', 'data/basis.xml');
?>
					<tr>
						<td valign="bottom">
							Word verification:
						</td>
						<td>
							<input type="text" size="10" name="wvw" autocomplete="off">
							<img id="wvwimg" src="wv.php?rand=<?php echo rand(1, 1000000000); ?>" style="position: relative; top: 4px;" />
						</td>
					</tr>
<?php
	}
?>
<?php if ($isAdmin)	echo "\t\t\t\t\t\t<tr><td>  Dateless:</td><td><input type=\"checkbox\" class=\"checkBox\" name=\"date\"></input></td></tr>\n"; ?>
					<tr><td colspan="2">
						<textarea rows="6" cols="40" type="text" name="txt"><?php
							if (strlen($alert_msg) && isset($_POST['txt'])) echo $_POST['txt'];
						?></textarea>
					</td></tr>
					<tr><td colspan="2" style="text-align: center">
						<input type="hidden" name="cmd" value="addcmnt<?php echo $t; ?>"></input>
						<input type="submit" value=" &nbsp; Submit Comment &nbsp; "></input>
					</td></tr>
			</table>
			</form>
		</div>
		<div class="bottitle">&nbsp;</div>
	</div>
	</center>
<?php
}

function unauthorized($clet, $cid, $p) {
	$cname = ($clet == 'c')?"category":"story";
	echo "<div class=\"pvTitle\"><span class=\"reddot\">&#149;</span>Authentication Failed.</div>\n";
	echo "<div class=\"authFailed\">\n";
	echo "This is a private photo which is being stored in a private $cname.<br />\n";
	echo "Login to view it.<br />\n";
	echo "<form action=\".?$clet=$cid&cmd=login&done=$p\" method=\"POST\">\n";
	echo "<center>Password for $cname #$cid: &nbsp; <input name=\"pass\" type=\"password\" style=\"position: relative; top: 5px; margin: 5px auto;\" size=\"10\"></input></center>\n";
	echo "</form>\n";
	echo "</div>\n";
	echo "<div class=\"pvEnd\">&nbsp;</div>\n";
}

function write_container($clet) {
	global $photos, $nphotos, $categs, $stories, $n, $ns, $$clet, $isAdmin, $alert_msg;
	$cname = ($clet == "c")?"Category":"Story";
	$cid = $$clet;
	$contarr = ($clet == "c")?"categs":"stories";
	$contPage = ($clet == "c")?"categories":"stories";
	$cont = array();
	$cont = ${$contarr}[$cid];
	if (!checkThePass($clet, $cid)) {
?>
	<div class="partmain">
		<div class="titlepart"><span class="reddot">&#149;</span>Authentication Failed</div>
		<div class="midInfo">
			<?php
				if (strlen($alert_msg) > 0)
					echo "<div class=\"alert_msg\">$alert_msg</div>\n";
				echo "This $cname (#$cid) is a private one. <br />\n";
			?>
			<div style="text-align: center; padding: 5px 0px;">
			<form action='<?php echo ".?$clet=$cid&cmd=login"; ?>' method='POST'>
				Password : &nbsp; <input name="pass" type="password" style="position: relative; top: 5px; margin: 5px auto;" size="10"></input>
			</form>
			</div>
		</div>
		<div class="end"></div>
	</div>
<?php
	}
	else {
?>
	<div class="partmain">
		<div class="titlepart">
			<?php
				if ($isAdmin)
					echo "\t\t\t\t<span class=\"pvTitleInfo\" style=\"position: relative; top: -8px;\">[<a href=\"admin.php?page=$contPage#$cid\">Edit</a>]</span>\n";
			?>
			<span class="reddot">&#149;</span>
			<?php
				if ($clet == 's') {
					$dates = sscanf($cont['date'], "%d/%d/%d %d:%d");
					$mtime = date("F jS y", mktime($dates[3], $dates[4], 0, $dates[1], $dates[2], $dates[0]));
				}
				else
					$mtime = '';
				echo "<span style=\"font-size: 1.2em;\"><a href=\".?$clet=$cid\" class=\"theTitleA\">"
					.$cont['name']."</a></span> <span class=\"small\">$mtime</span>";
			?>
			<span class="thumbcntarr">
			<?php
				global $thumbCntArr;
				for($i=0; $i<count($thumbCntArr); $i++)
					echo "\t\t\t&nbsp;[<a href=\".?$clet=$cid&n=".$thumbCntArr[$i]."\">".$thumbCntArr[$i]."</a>]\n";
			?>
			</span>
		</div>
		<?php if (strlen($cont['desc'])) { ?>
		<div class="midInfo">
			<?php
				echo nl2br($cont['desc'])."\n<br />\n";
			?>
		</div>
		<?php } ?>
		<div class="submenu">
		<?php
			$np = count($cont['photo']);
			end($cont['photo']);
			$tns = 0;
			for ($i=0; $i<$ns; ) {
				#echo $i."+".$tns."+".current($cont['photo'])."!<br />";
				if (photo_exists(current($cont['photo'])))
					if (canthumb(current($cont['photo'])))
						$i++;
					prev($cont['photo']);
				if (++$tns>$np)
					break;
			}
			for ($i=0; ($i<$n);) {
				if ($tns++>$np)
					break;
				if (photo_exists(current($cont['photo'])))
					if (thumbBox(current($cont['photo'])))
						$i++;
				prev($cont['photo']);
			}
		?>
		</div>
		<div class="end">
<?php
	if ($ns != 0)
		echo "<span class=\"titlepartlinkL\">[ <a href=\".?$clet=$cid&ns=".max(0, $ns-$n).($n == DEFAULT_N?"":"&n=$n")."\">Previous Photos</a> ]<br />&nbsp;</span>";
	if ($ns+$n<=$np)
		echo "<span class=\"titlepartlinkR\">[ <a href=\".?$clet=$cid&ns=".($ns+$n).($n == DEFAULT_N?"":"&n=$n")."\">Next Photos</a> ]<br />&nbsp;</span>";
?>
		</div>
<?php
	global $ok_msg, $alert_msg;
	if (strlen($alert_msg))
		echo "<div class=\"alert_msg\">$alert_msg</div>\n";
	else if (strlen($ok_msg))
		echo "<div class=\"ok_msg\">$ok_msg</div>\n";
	if (isset($cont['getcmnts']) && (strcmp($cont['getcmnts'], 'yes') == 0))
		writeCommenting('s', $cid);
	}
}

function dropthebox($pid, $x) {
	global $photos, $basis;
	$imgFile = PHOTO_PATH.getImageFileName($pid, '3');
	$rating = array();
	$rating = explode(" ",$photos[$pid]);
	$hits = $rating[0];
	$rate = 0;
	$raters = substr(strrchr($rating[1], '/'), 1);
	eval("@\$rate =".$rating[1].";");
	$rate = round($rate, 2);
	$photo = getAllPhotoInfo($pid, "./");
	$theName = $photo['name'];
	$title = $photo['name'].": $hits hits and rated $rate by $raters";
?>
	<div class="aThumbInBox" style="<?php echo (stristr($_SERVER['HTTP_USER_AGENT'], "IE") == true)?"filter:alpha(opacity=".$basis['opac'].");":"-moz-opacity:".($basis['opac']/100).";"; ?>;
		<?php echo "top: ".rand(0, 325)."px; left: ".rand(0, 325)."px; z-index: ".$pid.";"; ?>;"
		onmouseover="javascript: this.style.zIndex=10000;LightenIt(this);"
		id="ThumbInBox<?php echo $x; ?>"
		onmouseout="javascript: this.style.zIndex=<?php echo $pid;?>;DarkenIt(this, 0.45);">
	<center>
		<a href=".?p=<?php echo $pid; ?>" title="<?php echo $title; ?>">
			<img src="<?php echo $imgFile; ?>" /><br />
		</a>
	</center>
	</div>
<?php
}

function write_boxPhotos() {
	global $stories, $basis, $rsn, $rss, $photos, $nphotos, $n, $thumbCntArr;
	$neck = (isset($_GET['n']))?$n:$basis['defjbn'];
?>
	<div class="partmain">
		<div class="titlepart">
			<span class="reddot">&#149;</span>Jungle of the Shots
			<span class="thumbcntarr">
			<?php
				$thumbCntArr = array(10, 20, 50, 100, 200);
				for($i=0; $i<count($thumbCntArr); $i++)
					echo "\t\t\t&nbsp;[<a href=\".?mode=box&n=".$thumbCntArr[$i]."\">".$thumbCntArr[$i]."</a>]\n"
			?>
				:: <a href="javascript:reshuffle();">Reshuffle now</a>
			</span>
		</div>

		<div class="submenu">
			<div id="jungleBox">
		<?php
			end($photos);
			$arr = array();
			for ($i=0; ($i < $nphotos) && ($i < $neck); prev($photos))
				if ((strcmp(key($photos), 'lastpid') != 0) && canthumb(key($photos))) {
					array_push($arr, key($photos));
					$i++;
				}
			$arr = array_reverse($arr);
			$x = 0;
			foreach ($arr as $pid)
				dropthebox($pid, $x++);
		?>
			<input type="hidden" id="thumbscount" value="<?php echo $x; ?>"></input>
			</div>
		</div>
		<div class="end"></div>
	</div>
<?php
}

function canStoryBox($sid) {
	global $stories;
	if ((!checkThePass("s", $sid)) || (count($stories[$sid]['photo']) < 1))
		return false;
	return true;
}

function storyBox($sid) {
	global $stories;
	if (!canStoryBox($sid))
		return false;

	$ps = array_rand(array_flip($stories[$sid]['photo']), min(4, count($stories[$sid]['photo'])));
?>
	<div class="aStory">
		<div class="titlepart">
			<span class="dot" style="font-size: 14px;">&#149;</span>
			<a href=".?s=<?php echo $sid; ?>">
				<?php echo $stories[$sid]['name']; ?>
			<span class="thumbcntarr">
				since <?php echo textdate($stories[$sid]['date']); ?>
				:
				<?php echo $stories[$sid]['desc']; ?>
			</span>
			</a>
		</div>
		<div class="submenu">
<?php
	if (count($ps) <= 1)
		$ps = array($ps);
	foreach ($ps as $pid)
		thumbBox($pid);
?>
		</div>
		<div class="end" style="text-align: right;">
			[ <a href=".?s=<?php echo $sid; ?>">
			View all <?php echo count($stories[$sid]['photo']); ?> photos
			of <?php echo $stories[$sid]['name']; ?>
			</a> ]
<?php
?>
		</div>
	</div>
<?php
	return true;
}

function write_lastStories() {
	global $stories, $nstories, $basis, $rsn, $rss;
?>
	<div class="partmain">
		<div class="titlepart">
			<span class="reddot">&#149;</span>Recent Stories
			<span class="thumbcntarr">
			<?php
				global $thumbCntArr;
				for($i=0; $i<count($thumbCntArr); $i++)
					echo "\t\t\t&nbsp;[<a href=\".?mode=stories&rsn=".$thumbCntArr[$i].($rss == 0?"":"&rss=$rss")."\">".$thumbCntArr[$i]."</a>]\n"
			?>
			</span>
		</div>

		<div class="submenu">
		<?php
			$rsn = min($rsn, $nstories);
			for ($i=0; $i<$rss;) {
				if (canStoryBox(key($stories)))
					$i++;
				next($stories);
			}

			for ($i=0; ($i<$rsn) && (strcmp(key($stories), 'lastsid') != 0);) {
				if (storyBox(key($stories)))
					$i++;
				next($stories);
			}
		?>
		</div>
		<div class="end">
<?php
		if ($rss != 0)
			echo "<span class=\"titlepartlinkL\">[ <a href=\".?mode=stories&rss=".max(0, $rss-$rsn).($rsn == $basis['defrsc']?"":"&rsn=$rsn")."\">Previous Recent Stories</a> ]<br />&nbsp;</span>";
		if ($rss+$rsn<$nstories)
			echo "<span class=\"titlepartlinkR\">[ <a href=\".?mode=stories&rss=".($rss+$rsn).($rsn == $basis['defrsc']?"":"&rsn=$rsn")."\">Next Recent Stories</a> ]<br />&nbsp;</span>";
?>
		</div>
	</div>
<?php
}

function below_GetRate($exp) {
	$rate = 0;
	$raters = substr(strrchr($exp[1], '/'), 1);
	@eval("@\$rate =".$exp[1].";");
	return round($rate, 2)*100000000+$raters;
}

function below_GetHits($exp) {
	return $exp[0];
}

function below_GetRecency($exp) {
	if (!isset($exp[2]))
		return -(365*30*24*60*60+$exp[0]);
	else {
		$dates = sscanf($exp[2], "%d/%d/%d-%d:%d:%d");
		$today = getdate();
		$utoday = $today[0];
		$udate = mktime($dates[3], $dates[4], $dates[5], $dates[1], $dates[2], $dates[0]);
#echo $udate-$utoday."!<br />";
		return $udate-$utoday;
	}
}

function write_belowIndex($func, $obj_name) {
	global $rps, $rpn, $trn, $trs, $rsn, $rss;
	global $photos, $nphotos, $basis, $stories;
	$isRecent = (strcmp($obj_name, "Recently Visited") == 0);
?>
	<div class="partmain">
		<div class="titlepart">
			<a name="tr"></a>
			<span class="reddot">&#149;</span><?php echo ($isRecent?"":"Random ").$obj_name; ?> Photos
			<span class="thumbcntarr">
			<?php
				global $thumbCntArr;
				for($i=0; $i<count($thumbCntArr); $i++)
					echo "\t\t\t&nbsp;[<a href=\".?trn=".$thumbCntArr[$i].($trs == 0?"":"&trs=$trs")."#tr\">".$thumbCntArr[$i]."</a>]\n"
			?>
			</span>
		</div>
		<div class="submenu">
<?php
			global $tphoto;
			$tphoto = array();

			$r = array();

			$treshhold = 100;
			if ($nphotos<$treshhold)
				$prob = 100;
			else
				$prob = round(100*($treshhold/$nphotos));

			srand(time());
			reset($photos);
			$rating = array();
			while (list($key, $value) = each($photos))
				if (strcmp($key, 'lastpid') != 0) {
					if (rand(0, 100) < $prob) {
						$exp = explode(" ",$value);
						$r[$i++] = array(0=>$key, $func($exp));
					}
				}

			function cmp($a, $b) {
				if ($a[1] == $b[1])
					return 0;
				else
					return (($a[1] > $b[1])?-1:1);
			}
			usort($r, "cmp");

			reset($r);
			$cnt = 0;
			$prob = $isRecent?100:80;
			for ($i=0; ($cnt<$trn) && isset($r[$i+$trs]); $i++)
				if ((($nphotos-$i-$trs) < 1.5*($trn-$cnt)) || (rand(0, 100) < $prob))
					if (thumbBox($r[$i+$trs][0],
						($isRecent && ($r[$i+$trs][1] > -365*30*24*60*60))
							?SecsToText(-1*$r[$i+$trs][1]):""))
						$cnt++;
?>
		</div>
		<div class="end">
<?php
		if ($trs != 0)
			echo "<span class=\"titlepartlinkL\">[ <a href=\".?trs=".max(0, $trs-$trn).
				($trn == $basis['deftrc']?"":"&trn=$trn")."#tr\">Previous $obj_name Photos</a> ]<br />&nbsp;</span>";
		if (isset($r[$trs+$trn]))
			echo "<span class=\"titlepartlinkR\">[ <a href=\".?trs=".($trs+$trn).
				($trn == $basis['deftrc']?"":"&trn=$trn")."#tr\">Next $obj_name Photos</a> ]<br />&nbsp;</span>";
?>
		</div>
	</div>

<?php
}

function write_firstPhoto() {
	global $photos, $nphotos, $basis, $stories, $_SERVER, $hasgd;
	end($photos);
	while (!canthumb(key($photos)))
		prev($photos);
	$pid = key($photos);
	$photo = getAllPhotoInfo($pid);
	$imgAddress = PHOTO_PATH.getImageFileName($pid, 4);
	$wh = "";
	$wdiv = "100%";
	if ($hasgd) {
		list($w, $h) = getimagesize($imgAddress);
		$wh = "width=\"${w}px\" height=\"${h}px\" ";
		$wdiv = "${w}px";
	}
?>
	<div class="partmain">
		<div class="titlepart">
			<span class="reddot">&#149;</span>Last Photo:
			<a href=".?p=<?php echo $pid; ?>"><?php echo $photo['name']; ?></a>
		</div>
		<div class="photoTheImg">
			<br />
			<center>
			<div class="firstThumb" style="width: <?php echo $wdiv; ?>;
						 <?php echo ((stristr($_SERVER['HTTP_USER_AGENT'], "IE") == true)?
						 "filter:alpha(opacity=".$basis['opac'].");":
						 "-moz-opacity:".($basis['opac']/100).";"); ?>"
						 onmouseover="javascript: LightenIt(this);" onmouseout="javascript: DarkenIt(this);">
				<a title="Click to view larger size of &quot;<?php echo $photo['name']; ?>&quot;" href=".?p=<?php echo $pid; ?>">
					<img style="margin:0px" src="<?php echo $imgAddress; ?>" <?php echo $wh; ?>/>
				</a>
			</div>
			</center>
		</div>
		<div class="end">
		</div>
	</div>
<?php
}

function write_lastPhotos() {
	global $rps, $rpn, $trn, $trs, $rsn, $rss;
	global $photos, $nphotos, $basis, $stories;
?>
	<div class="partmain">
		<div class="titlepart">
			<span class="reddot">&#149;</span>Recent Photos
			<span class="thumbcntarr">
			<?php
				global $thumbCntArr;
				for($i=0; $i<count($thumbCntArr); $i++)
					echo "\t\t\t&nbsp;[<a href=\".?rpn=".$thumbCntArr[$i].($rps == 0?"":"&rps=$rps")."\">".$thumbCntArr[$i]."</a>]\n"
			?>
			</span>
		</div>
		<div class="submenu">
		<?php
			end($photos);
			$rpn = min($rpn, $nphotos);
			for ($i=0; $i<$rps; $i++)
				prev($photos);

			for ($i=0; ($i<$rpn) && (strcmp(key($photos), 'lastpid') != 0);) {
				if (thumbBox(key($photos)))
					$i++;
				prev($photos);
			}
		?>
		</div>
		<div class="end">
<?php
		if ($rps != 0)
			echo "<span class=\"titlepartlinkL\">[ <a href=\".?rps=".max(0, $rps-$rpn).($rpn == $basis['defrpc']?"":"&rpn=$rpn")."\">Previous Recent Photos</a> ]<br />&nbsp;</span>";
		if ($rps+$rpn<$nphotos)
			echo "<span class=\"titlepartlinkR\">[ <a href=\".?rps=".($rps+$rpn).($rpn == $basis['defrpc']?"":"&rpn=$rpn")."\">Next Recent Photos</a> ]<br />&nbsp;</span>";
?>
		</div>

<?php
}

function write_actions($cname, $clet, $cid) {
	global $$cname;
?>
	<div class="part">
		<div class="titlepart"><span class="reddot">&#149;</span>Navigation</div>
		<div class="submenu">
			<div class="item"><span class="dot">&#149;</span>&nbsp;<a href=".">Home</a></div>
<?php
			if (strlen(${$cname}[$cid]['pass']) > 0)
				echo "<div class=\"item\"><span class=\"dot\">&#149;</span>&nbsp;<a href=\".?$clet=$cid&cmd=logout\">Logout</a></div>";
?>
		</div>
	</div>
<?php
}

function checkThePass($conChar, $contId) {
	global $categs, $stories, $isAdmin, $_COOKIE;
	$thePass = ($conChar == 'c')?$categs[$contId]['pass']:$stories[$contId]['pass'];
	if ($isAdmin || (strlen($thePass) == 0))
		return true;
	if (isset($_COOKIE['pass_'.$conChar.$contId]) && (strcmp($_COOKIE['pass_'.$conChar.$contId], md5($thePass)) == 0))
		return true;
	return false;
}

function canthumb($pid) {
	global $stories, $categs, $photos, $isAdmin;
	if (!photo_exists($pid))
		return false;
	$photo = getAllPhotoInfo($pid);
	return checkThePass("s", $photo['story']) && checkThePass("c", $photo['categ']);
}

function textdate($d) {
	$dates = sscanf($d, "%d/%d/%d");
	$today = getdate();
	$utoday = mktime(0, 0, 0, $today["mon"], $today["mday"], $today["year"]);
	$udate = mktime(0, 0, 0, $dates[1], $dates[2], $dates[0]);
	$dayspast = round(($utoday-$udate)/(24*60*60));
	switch ($dayspast) {
		case 0: return 'Today';
		case 1: return 'Yesterday';
		case 2: case 3: case 4: case 5: case 6:
			return $dayspast." days ago";
		case 7 :
			return "one week ago";
		default:
			return date("F jS \o\f y", $udate);
	}
}

function SecsToText($d) {
	if ($d < 60)
		$ret = round($d)." seconds";
	else if ($d < 60*60)
		$ret = round($d/60)." minutes";
	else if ($d < 24*60*60)
		$ret = round($d/(60*60))." hours";
	else if ($d < 30*24*60*60)
		$ret = round($d/(24*60*60))." days";
	else
		$ret = round($d/(30*24*60*90))." months";
	$ret .= " ago";

	if (strcmp($ret, "1 days ago") == 0)
		$ret = "yesterday";

	return $ret;
}

?>
