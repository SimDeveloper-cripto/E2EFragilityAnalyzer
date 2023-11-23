var AjaxVal = "";
var ImgW = 0;
var ImgH = 0;
var ImgPath = "";
var ImgPath9 = "";
var hasgd = true;
var hasexif = true;
var seed = 0;
var pcupload = 0;
var AjaxDelay = 2000;
var isFakeDateTaken = false;

function setFakeDate(wat) {
	isFakeDateTaken = wat;
}

function alertContents(http_request) {
	if (http_request.readyState == 4)
		try {
			if (http_request.status == 200) {
				AjaxVal = http_request.responseText;
				AjaxVal = AjaxVal.substr(6, AjaxVal.length-13); // to clear <ajax></ajax> tag which was added to save XMLity
			}
		} catch(e) {}
}

function makeRequest(url) {
	var http_request = false;
	if (window.XMLHttpRequest) { // Mozilla, Safari,...
		http_request = new XMLHttpRequest();
		if (http_request.overrideMimeType)
			http_request.overrideMimeType('text/xml');
	} else if (window.ActiveXObject) { // IE
		try { http_request = new ActiveXObject("Msxml2.XMLHTTP"); } catch (e) {
			try { http_request = new ActiveXObject("Microsoft.XMLHTTP"); } catch (e) {}
		}
	}
	if (!http_request) {
		alert('Giving up :( Cannot create an XMLHTTP instance');
		return false;
	}
	http_request.onreadystatechange = function() { alertContents(http_request); };
	http_request.open('GET', url, true);
	http_request.send(null);
}

function setExif() {
	if (AjaxVal.charAt(0) != '!') {
		Ajaxify("exif");
		setTimeout("setExif()", AjaxDelay/2);
	}
	else {
		var exifDate = AjaxVal.substr(1, AjaxVal.indexOf(';')-1);
		var exifInfo = AjaxVal.substr(AjaxVal.indexOf(';')+1, AjaxVal.length);
		
		if (exifInfo.length) {
			if ((dg('photoinfo').value != "") && (dg('photoinfo').value != exifInfo)) {
				if (confirm('Replace current photo info with EXIF info?'))
					dg('photoinfo').value = exifInfo;
			}
			else 
				dg('photoinfo').innerHTML = exifInfo;
		}
		

		if (exifDate.length)
			if ((dg('datetake').value != "") && (dg('datetake').value != exifDate) && !isFakeDateTaken) {
				if (confirm('Replace current Date taken ('+dg('datetake').value+') with EXIF date ('+exifDate+')?'))
					dg('datetake').value = exifDate;
			}
			else 
				dg('datetake').value = exifDate;	
	}
}

function imageUploaded(indeed) {
	if (hasgd) {
		hideElem('thumb_note_wrapper');
		showElem('ThumbnailGenerator');
		dg('thePhoto').style.backgroundImage = "";
		dg('thePhoto').style.backgroundImage = "url('"+ImgPath+"')";
		dg('thumbPrev').src = ImgPath;
		dg('thePhoto').style.width = ImgW+'px';
		dg('thePhoto').style.height = ImgH+'px';
		ExpandSkl();
		if (indeed && hasexif) {
			Ajaxify("exif");
			setTimeout("setExif()", AjaxDelay/2);
		}
		//showElem('upload_iframe');
		rethumb();
	}
	else {
		dg('thumb_note').innerHTML = "Photo catched but phpgd not found! :(";
	}
	dg('theImgPath').value = ImgPath;
	dg('finallyAdd').style.display = 'inline';
}

function Ajaxify(action) {
	//alert(action+':'+seed);
	makeRequest("upload.php?mod="+action+"&seed="+seed+"&r="+Math.round(Math.random()*100000)); // to avoid unwanted caching
}

function writeYet() {
	AjaxValRead = true;
	Ajaxify("listen");

	if (AjaxVal == "") {
		setTimeout("writeYet()", AjaxDelay);
		return;
	}
	
	AjaxProp = AjaxVal.substr(6, AjaxVal.length);
	AjaxVal = AjaxVal.substr(0, 5);

	if (AjaxVal == "ERROR") {
		hideElem('upload_uploading');
		showElem('upload_iframe');
		Ajaxify("delphr");
		return;
	}
	
	if (AjaxVal == "EMPTY") {
		dg('thumb_note').innerHTML = ((pcupload)?"Uploading photo":"Connecting the server")+"... Please, wait!";
		dg('upload_uploading_txt').innerHTML = (pcupload)?"Uploading the photo...":"Establishing the connection...";
		setTimeout("writeYet()", AjaxDelay);
		return;
	}

	if (AjaxVal == "START") {
		dg('thumb_note').innerHTML = ((pcupload)?"Uploading":"Catching")+" photo... Please, wait!";
		dg('upload_uploading_txt').innerHTML = (pcupload)?"Uploading the photo...":"Catching the photo...";
		setTimeout("writeYet()", AjaxDelay);
		return;
	}
	
	if (AjaxVal == "ENDED") {
		Ajaxify("delphr");
		var s = new Array();
		var c, i;
		AjaxProp += ";"
		for (i=0; (c = AjaxProp.indexOf(';')) != -1; i++) {
			s[i] = AjaxProp.substr(0, c);
			AjaxProp = AjaxProp.substr(c+1);
		}
		ImgW = parseInt(s[0]);
		ImgH = parseInt(s[1]);
		ImgPath =  "temp/"+seed+"_1.jpg";
		ImgPath9 = "temp/"+seed+"_9.jpg";
		dg('upload_note').innerHTML = "The photo \""+s[2]+"\" is uploaded successfully to <a href=\""+ImgPath9+"\">here</a>!";
		imageUploaded(1);
		return;
	}
}

function uploadSubmitted(theseed, gd, pcup) {
	hasgd = gd;
	seed = theseed;
	pcupload = pcup;
	hearUploading = 1;
	dg('thumb_note').style.color = "#272";
	dg('thumb_note').innerHTML = "Initializing the process... Please, wait!";
	dg('upload_uploading_txt').innerHTML = "Initializing the process...";
	hideElem('upload_iframe');
	showElem('upload_uploading');
	setTimeout("writeYet()", AjaxDelay);
}