function getMyXY(eve, t) {
	var tt
	if(!eve)
		var eve=window.event;
	if (document.all)
		tt = document.body.scrollTop?document.body.scrollTop:document.documentElement.scrollTop;
	return t?document.all?eve.clientY+tt:eve.pageY:document.all?eve.clientX:eve.pageX;
}

function wordize(s) {
	return s[0].toUpperCase()+s.substring(1);
}

function dg(x) {
	return document.getElementById(x);
}

function hideElem(x) {
	dg(x).style.display = 'none';
}

function showElem(x) {
	dg(x).style.display = 'block';
}

function checkChangePass() {
	var np1 = dg('newpasswd1').value;
	var np2 = dg('newpasswd2').value;
	if (np1 != np2) {
		alert('New passwords are not identic!');
		return false;
	}
	if (np1.length < 4) {
		alert('New password is too short! (less than 4 character)');
		return false;
	}
	return true;
}

function checkInstallPass() {
	var np = dg('newpasswd').value;
	if (np.length < 4) {
		alert('New password is too short! (less than 4 character)');
		return false;
	}
	return true;
}

function checkPrivacyRow() {
	var p = dg('public').checked;
	if (p) {
		dg('password').value = '';
		dg('passwordRow').style.display = 'none';
	}
	else {
		dg('passwordRow').style.display = 'block';
		try {
			dg('passwordRow').style.display = 'table-row';
		} catch (e) {}
		
	}
}

function checkHasPass() {
	if (dg('name').value == "") {
		alert('"Name" field can not be left blank!');
		return false;
	}
	if ((! dg('public').checked) && (dg('password').value == "")) {
		alert('Either assign a password or make it public!');
		return false;
	}
	return true; 
}

function confirmDelete(x) {
	return confirm('Are you sure you want to delete "'+x+'"?');
}

function checkDate() {
	if (dg('date').value.length == 0) {
		alert('Date is required');
		return false;
	}
	re = /^\d{4}\/\d{2}\/\d{2}$/;
	if (! re.test(dg('date').value)) {
		alert('Date must be in YYYY/MM/DD format');
		return false;
	}
	return true;
}

function CheckAddPhotoTime() {
	retime = /^\d{4}\/\d{2}\/\d{2}[ ]\d{2}[:]\d{2}$/;
	re = /^\d{4}\/\d{2}\/\d{2}$/;
	rehit = /^\d+ \d+\/\d+$/;
	if (! re.test(dg('datetake').value)) {
		alert('Date Taken must be in YYYY/MM/DD HH:mm format');
		return false;
	}
	if (! retime.test(dg('dateadd').value)) {
		alert('Date Added must be in YYYY/MM/DD HH:mm format');
		return false;
	}
	if (! rehit.test(dg('hits').value)) {
		alert('Hits must be in HITS RATE_SUM/RATE_COUNT format like 288 12/7');
		return false;
	}
	return true;
}

function CheckAddPhoto() {
	if (!CheckAddPhotoTime())
		return false;
	if (dg('theImgPath').value == "") {
		alert('You must acquire a photo first!');
		return false;
	}
	return true;
}

function PrepareBody() {
/*
	try {
		dg('theImgPath').value = "";
	} catch(e) {}
*/	
}

function ConfirmDelPhotoID(x) {
	return confirm('Are you sure you want to delete photo number "'+x+'"?');
}

function ConfirmDelPhoto() {
	return ConfirmDelPhotoID(dg('piddel').value);
}

function ConfirmRestore() {
	return confirm('Are you sure you want to restore the backup file?');
}

function ConfirmSave() {
	return confirm('Are you sure you want to save your modification?');
}

function showlinkline(x) {
	dg('linkline'+x).style.display = 'table-row';
}

function hidelinkline(x) {
	dg('linkline'+x).style.display = 'none';
}

function linkAddBelow(x) {
	var lets = new Array('n', 'h', 't');
	var def = new Array('', 'http://', '');
	var n = dg('nLink').value;
	x++;
	if (n >= 100-1) {
		alert("Sorry, Just Reached maximum number of links!");
		return;
	}
	dg('nLink').value = ++n;
	for (var i=n; i>x; i--)
		for (var l=0; l<3; l++) {
			var j = i-1;
			dg('l'+i+lets[l]).value = dg('l'+j+lets[l]).value;
			dg('l'+i+lets[l]).style.fontWeight = dg('l'+j+lets[l]).style.fontWeight;
		}
	for (var l=0; l<3; l++)
		dg('l'+x+lets[l]).value = def[l];
	dg('l'+x+'n').style.fontWeight = 'normal';
	
	showlinkline(n-1);
}

function linkDelThis(x) {
	if (!confirm('Are you sure you want to delete link "'+dg('l'+x+'n').value+'"?'))
		return;
	var lets = new Array('n', 'h', 't');
	var n = dg('nLink').value;
	if (n == 1) {
		alert("You can't omit all the links! One must survive.");
		return;
	}
	x = parseInt(x);
	dg('nLink').value = --n;
	for (var i=x; i<n; i++)
		for (var l=0; l<3; l++) {
			var j = i+1;
			dg('l'+i+lets[l]).value = dg('l'+j+lets[l]).value;
			dg('l'+i+lets[l]).style.fontWeight = dg('l'+j+lets[l]).style.fontWeight;
		}
	hidelinkline(n);
}

function fixBoldInput(x, val) {
	dg('l'+x+'n').style.fontWeight = (val.length > 0)?'normal':'bold';
}

function changePrev() {
	var mode = dg('mode').value;
	var theme = dg('theme').value;
	dg('prevmode1').setAttribute("href", './?mode='+mode+'&theme='+theme);
	dg('prevmode2').setAttribute("href", './?mode='+mode+'&theme='+theme);
}

function rethumb_fill(ImgPath) {
	dg('thumbPrev').src = ImgPath;
	dg('thePhoto').style.backgroundImage = "url('"+ImgPath+"')";
}

function rethumb() {
	var ImgPath = dg('thumbPrev').src;
	dg('thumbPrev').src = '';
	dg('thePhoto').style.backgroundImage = "url('"+"')";
	setTimeout("rethumb_fill(ImgPath)", 1000);
}

function ToggleAdvPref() {
	var pref = dg('AdvPref');
	if (pref.style.display == 'block') {
		pref.style.display = 'none';
		dg('ShowHideAdvPref').innerHTML = 'Show';
	}
	else {
		pref.style.display = 'block';
		dg('ShowHideAdvPref').innerHTML = 'Hide';
	}
}