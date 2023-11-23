<?php
require_once('funcs.php');

global $hasgd, $hasexif;
function write_frame_header() {
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="files/adminfiles/admin.css">
</head>
<body class="frameBody">

<?php
}

function write_in_phr($txt) {
	global $seed;
	$f = fopen("temp/{$seed}.phr", "w");
	fwrite($f, $txt);
	fclose($f);
}

function dieAlert($msg) {
	write_frame_header();
	die('<div class="accessViol">'.$msg.'</div></body></html>');
}

function dieForward($msg) {
	global $seed;
	write_in_phr("ERROR;");
	header("Location:upload.php?seed=$seed&msg=$msg");
	die();
}

	if (!auth_admin())
		dieAlert('ERROR: Access Denied<br />Only Admin Shall Access This Page.');

	if ((!isset($_GET['seed'])) || (strlen($_GET['seed']) != 5))
		dieAlert('ERROR: Has NOT got the proper seed');
	$seed = $_GET['seed'];

	$mod = isset($_GET['mod'])?$_GET['mod']:'frame';

	if (strcmp($mod, 'exif') == 0) {
		echo "<ajax>!";
		$ppath = "temp/{$seed}_9.jpg";
		if ($hasexif)
			$exif = exif_read_data($ppath, 0, true);
		if (($exif === FALSE) || !$hasexif)
			echo ";";
		else {
			if (isset($exif['EXIF']['DateTimeOriginal'])) {
				$t = array();
				$t2 = array();
				$t = explode(" ", $exif['EXIF']['DateTimeOriginal']);
				$sep =": /-";
				for ($i=0; $i<strlen($sep); $i++) {
					$t2 = explode($sep[$i], $t[0]);
					if (isset($t2[2]))
						break;
				}
				echo date("Y/m/d", mktime(0, 0, 0, $t2[1], $t2[2], $t2[0])).";";
			}
			else
				echo ";";

			if (isset($exif['IFD0']['Model']))
				echo "Model: ".$exif['IFD0']['Model']."\n";
			if (isset($exif['EXIF']['Flash']))
				echo "Flash: ".$exif['EXIF']['Flash']."\n";
			if (isset($exif['EXIF']['ShutterSpeedValue']))
				echo "Shutter Speed Value: ".$exif['EXIF']['ShutterSpeedValue']."\n";
			if (isset($exif['EXIF']['ExposureTime']))
				echo "Exposure Time: ".$exif['EXIF']['ExposureTime']."\n";
			if (isset($exif['EXIF']['ApertureValue']))
				echo "Aperture Value: ".$exif['EXIF']['ApertureValue']."\n";
			if (isset($exif['EXIF']['FNumber']))
				echo "F Number: ".$exif['EXIF']['FNumber']."\n";
			if (isset($exif['EXIF']['MeteringMode']))
				echo "Metering Mode: ".$exif['EXIF']['MeteringMode']."\n";
			if (isset($exif['EXIF']['FocalLength']))
				echo "Focal Length: ".$exif['EXIF']['FocalLength']."\n";

/*
			foreach ($exif as $key => $section)
				foreach ($section as $name => $val)
					echo "$key.$name: $val<br />\n";
*/

		}
		echo "</ajax>";
		die();
	}

	if (strcmp($mod, 'listen') == 0) {
		$src = "temp/{$seed}.phr";
		$txt = file_exists($src)?file_get_contents($src):"EMPTY";
		die("<ajax>".$txt."</ajax>");
	}

	if (strcmp($mod, "delphr") == 0) {
		$src = "temp/{$seed}.phr";
		unlink($src);
		die("<ajax>DELED</ajax>");
	}

	if (strcmp($mod, 'getfile') == 0) {
		$ppath = "temp/{$seed}_9.jpg";

		if (!isset($_GET['uptype']))
			dieForward('ERROR: Has not got the Upload Type');
		$uptype = $_GET['uptype'];

		if (strcmp($uptype, "server") == 0) {
			# $isRemote = strcmp(substr($_POST['theAddress'], 0, 7), "http://") == 0;
			$isRemote = (strcmp(substr($_POST['theAddress'], 0, 1), "/") != 0) &&
						(strcmp(substr($_POST['theAddress'], 0, 3), "../") != 0);
			if ($isRemote)
				if (strcmp(substr($_POST['theAddress'], 0, 7), "http://") != 0)
					$_POST['theAddress'] = "http://".$_POST['theAddress'];
			$theFile = $_POST['theAddress'];
			$file_name = basename($_POST['theAddress']);
			if (!$isRemote && !file_exists($theFile))
				dieForward("ERROR: The file \"$file_name\" does not exist!");
			$file_type = strtolower(substr($file_name, strlen($file_name)-3));
			if (strcmp($file_type, "jpg") != 0)
				dieForward("ERROR: The file \"$file_name\" is not a Jpeg (.jpg) file!");
			$wer = $_POST['theAddress'];
			write_in_phr("START;");
			if ($isRemote) {
				$arr = parse_url($wer);
				if (strcmp($_SERVER['SERVER_NAME'], $arr['host']) == 0)  {
					$depth = substr_count($_SERVER['PHP_SELF'], "/")-1;
					$pref = implode('/',array_fill(0, $depth, '..'));
					copy($pref.$arr['path'], $ppath); #! might be buggy when not installed in root
				}
				else {
					$fr = fopen($wer, "r");
					$fw = fopen($ppath, "w");
					while ($data = fread($fr, 4096))
						fwrite($fw, $data);
				}
			}
			else
				copy($wer, $ppath);
			if ($hasgd)
				$txt = makeTheThumb($ppath, SKL_PHOTO_W, 'min', '1');
			else
				$txt = "ENDED;;";
			write_in_phr($txt.";".$file_name);
		}
		elseif (strcmp($uptype, "pc") == 0) {
			$file_name = $_FILES['theFile']['name'];
			$file_type = $_FILES['theFile']['type'];
			$file_size = $_FILES['theFile']['size'];
			$file_tmpN = $_FILES['theFile']['tmp_name'];
			if (!isset($file_size) || ($file_size == 0))
				dieForward("ERROR: No file was selected!");
			if (strcmp($file_type, "image/jpeg") && strcmp($file_type, "image/pjpeg"))
				dieForward("ERROR: The file \"$file_name\" is not a Jpeg (.jpg) file!");
			write_in_phr("START;");
			if (move_uploaded_file($file_tmpN, $ppath)) {
				if ($hasgd)
					$txt = makeTheThumb($ppath, SKL_PHOTO_W, 'min', '1');
				else
					$txt = "ENDED;;";
				write_in_phr($txt.";".$file_name);
			}
		}
		else
			dieForward("ERROR: Not a valid upload type");

		dieAlert("The file \"$file_name\" <a href=\"$ppath\">uploaded</a> succesfully!");
	}
	if (strcmp($mod, 'frame') == 0) {
		write_frame_header();
?>
<?php
	if (isset($_GET['msg']))
		echo "<div class=\"accessViol\" style=\"margin: 0px 0px 5px; width: 100%;\"><center>"
			.$_GET['msg']
			."</center></div>";
	else
		echo "<div style=\"height: 8px; font-size: 8px\">&nbsp;</div>";
?>
		<table cellpadding="5" width="100%">
		<tr>
		<form name="FilePCForm" enctype="multipart/form-data" method="post" action="upload.php?mod=getfile&uptype=pc&seed=<?php echo $seed; ?>">
		<!-- onsubmit="parent.uploadSubmitted(<?php echo "'".$seed."', ".$hasgd; ?>,1);" -->
			<td valign="top">
				Path on your machine:
				</td><td valign="top">
				<input name="theFile" id="fileAddr" onchange="FilePCForm.submit();parent.uploadSubmitted(<?php echo "'".$seed."', ".$hasgd; ?>,1);" type="file" size="44" class="fileInput"></input>
				</td><td>
				<!-- <input class="submit" type="submit" value="Upload the file"></input> -->
			</td>
		</form>
		</tr>
		<tr>
		<form name="FileServerForm" enctype="multipart/form-data" method="post" action="upload.php?mod=getfile&uptype=server&seed=<?php echo $seed; ?>" onsubmit="parent.uploadSubmitted(<?php echo "'".$seed."', ".$hasgd; ?>,0);">
			<td>
				... or Path on server:
				</td><td>
				<input class="textTT" name="theAddress" type="text" size="31" class="fileInput" value=""></input>
				<!-- onblur="FileServerForm.submit();parent.uploadSubmitted(<?php echo "'".$seed."', ".$hasgd; ?>,0);" -->
				<input class="submit" type="submit" value="&nbsp;Add file &nbsp;"></input>
				</td><td>
			</td>
		</form>
		</tr>
		</table>
	</body>
	</html>
<?php
	die(0);
	} // end of mod frame
	dieAlert('ERROR: No proper mod has been selected;');
?>