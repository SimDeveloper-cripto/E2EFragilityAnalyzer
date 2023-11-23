var AjaxVal = new Array();
var seedArr	= new Array();
var ImgW = 0;
var ImgH = 0;
var ImgPath = "";
var ImgPath9 = "";
var hasgd = true;
var hasexif = true;
var pcupload = 0;
var AjaxDelay = 1500;
var isFakeDateTaken = false;

function setFakeDate(wat) {
	isFakeDateTaken = wat;
}

function alertContents(http_request, seed) {
	if (http_request.readyState == 4)
		try {
			if (http_request.status == 200) {
				AjaxVal[seedId(seed)] = http_request.responseText;
				// to clear <ajax></ajax> tag which was added to save XMLity
				AjaxVal[seedId(seed)] = AjaxVal[seedId(seed)].substr(6, AjaxVal[seedId(seed)].length-13);
			}
		} catch(e) {}
}

function makeRequest(url, seed) {
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
	http_request.onreadystatechange = function() { alertContents(http_request, seed); };
	http_request.open('GET', url, true);
	http_request.send(null);
}

function setExif(seed) {
	if (AjaxVal[seedId(seed)].charAt(0) != '!') {
		Ajaxify("exif", seed);
		setTimeout("setExif(seed)", AjaxDelay/2);
	}
	else {
		av = AjaxVal[seedId(seed)];
		var exifDate = av.substr(1, av.indexOf(';')-1);
		var exifInfo = av.substr(av.indexOf(';')+1, av.length);

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

function imageUploaded(indeed, seed) {
	if (hasgd) {
		hideElem('thumb_note_wrapper_'+seed);
		showElem('ThumbnailGenerator');
		dg('thePhoto').style.backgroundImage = "";
		dg('thePhoto').style.backgroundImage = "url('"+ImgPath+"')";
		dg('thumbPrev').src = ImgPath;
		dg('thePhoto').style.width = ImgW+'px';
		dg('thePhoto').style.height = ImgH+'px';

		dg('kines_l').style.height = ImgH+'px';
		dg('kines_r').style.height = ImgH+'px';

		ExpandSkl();
		if (indeed && hasexif) {
			Ajaxify("exif", seed);
			setTimeout("setExif(seed)", AjaxDelay/2);
		}
		//showElem('upload_iframe');
		rethumb();
	}
	else {
		dg('thumb_note_'+seed).innerHTML = "Photo catched but phpgd not found! "
								+ "(<a class=\"q\" onclick=\"ShowHelp('GD Not Found', event)\"> ? </a>)";
	}
	dg('theImgPath').value = ImgPath;
	dg('finallyAdd').style.display = 'inline';
}

function Ajaxify(action, seed) {
	//dg('jj').innerHTML += '&&&&&&&&&&&&&&& AJAX &&&&&&&&&&&&&&& '+action+':'+seed+'<br />';
	makeRequest("upload.php?mod="+action+"&seed="+seed+"&r="+Math.round(Math.random()*100000), seed); // to avoid unwanted caching
}

function writeYet(seed, draft) {
	//dg('jj').innerHTML += draft + 'seed=>'+seed+' , draft=>' +draft+', AV=>'+AjaxVal[seedId(seed)]+' ::' +seedArr[0]+','+seedArr[1]+' ::' +AjaxVal[0]+','+AjaxVal[1]+'<br />';
	Ajaxify("listen", seed);

	if (AjaxVal[seedId(seed)] == "") {
		setTimeout("writeYet('"+seed+"',draft)", AjaxDelay);
		return;
	}

	var AjaxProp = AjaxVal[seedId(seed)].substr(6, AjaxVal[seedId(seed)].length);
	AjaxVal[seedId(seed)] = AjaxVal[seedId(seed)].substr(0, 5);

	//dg('jj').innerHTML += ">>> going to pick "+seed+":"+AjaxVal[seedId(seed)]+"<br />";

	if ((AjaxVal[seedId(seed)] == "ERROR") || (AjaxVal[seedId(seed)] == "DELED")) {
		if (!draft)
			dg('thumb_note_'+seed).innerHTML = "An Error occured during upload...";
		hideElem('upload_uploading_'+seed);
		showElem('upload_iframe_'+seed);
		Ajaxify("delphr", seed);
		AV = AjaxVal[seedId(seed)] = "";
		return;
	}

	if (AjaxVal[seedId(seed)] == "EMPTY") {
		if (!draft)
			dg('thumb_note_'+seed).innerHTML = ((pcupload)?"Uploading photo":"Connecting the server")+"... Please, wait!";
		//dg('jj').innerHTML += ">>> in pick "+seed+":"+AjaxVal[seedId(seed)]+":::::::"+('upload_uploading_txt_'+seed)+"<br />";
		if (dg('upload_uploading_txt_'+seed))
			dg('upload_uploading_txt_'+seed).innerHTML = (pcupload)?"Uploading the photo...":"Establishing the connection...";
		setTimeout("writeYet('"+seed+"', draft)", AjaxDelay);
		return;
	}

	if (AjaxVal[seedId(seed)] == "START") {
		if (!draft)
			dg('thumb_note_'+seed).innerHTML = ((pcupload)?"Uploading":"Catching")+" photo... Please, wait!";
		dg('upload_uploading_txt_'+seed).innerHTML = (pcupload)?"Uploading the photo...":"Catching the photo...";
		setTimeout("writeYet('"+seed+"', draft)", AjaxDelay);
		return;
	}

	if (AjaxVal[seedId(seed)] == "THUMB") {
		if (!draft)
			dg('thumb_note_'+seed).innerHTML = "Creating Thumbnails ... Please, wait!";
		dg('upload_uploading_txt_'+seed).innerHTML = "Creating Thumbnails ...";
		setTimeout("writeYet('"+seed+"', draft)", AjaxDelay);
		return;
	}

	if (AjaxVal[seedId(seed)] == "ENDED") {
		Ajaxify("delphr", seed);
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
		if (draft)
			ImgPath9 = s[3];
		else
			ImgPath9 = "temp/"+seed+"_9.jpg";
		if (ImgPath9.substr(ImgPath9.length-4) == ".zip")
			s =	"The zip file \""+s[2]+"\" is uploaded successfully "
				+" and "+s[4]+" photos in it saved in Drafts folder!";
		else
			s =	"The photo \""+s[2]+"\" is uploaded successfully "
				+" and is saved in <a href=\""+ImgPath9+"\">Drafts folder</a>!";
		dg('upload_note_'+seed).innerHTML = s;
		if (!draft)
			imageUploaded(1, seed);
		return;
	}
}

function seedId(seed) {
	var n = seedArr.length;
	var ret = -1;

	for (var i=0; i<n; i++)
		if (seedArr[i] == seed)
			ret = i;
	if (ret == -1) {
		ret = n;
		seedArr[n++] = seed;
	}
	return ret;
}

function uploadSubmitted(theseed, gd, tdraft, pcup) {
	hasgd = gd;
	seed = theseed;
	draft = tdraft;
	AjaxVal[seedId(seed)] = ""; // May has comen from an err!
	pcupload = pcup;
	hearUploading = 1;
	try {
		if (!draft && dg('PhotoTitleId'))
			dg('PhotoTitleId').focus();
	} catch(e) {}
	if (!draft) {
		dg('thumb_note_'+seed).style.color = "#272";
		dg('thumb_note_'+seed).innerHTML = "Initializing the process... Please, wait!";
	}
	dg('upload_uploading_txt_'+seed).innerHTML = "Initializing the process...";
	hideElem('upload_iframe_'+seed);
	showElem('upload_uploading_'+seed);
	setTimeout("writeYet('"+seed+"', draft)", AjaxDelay/2);
}