var AjaxVal = "";
var isAjaxing = false;

function dg(x) {
	return document.getElementById(x);
}

function setOpac(cur, strength) {
	if (cur.style.MozOpacity)
		cur.style.MozOpacity=strength;
	else 
		if (cur.filters)
			cur.filters.alpha.opacity=strength*100;
}

function LightenIt(cur) {
	setOpac(cur, 0.99);
}

function DarkenIt(cur, t) {
	if ((!t) || (t == ''))	t = DarkenVal / 100;
	setOpac(cur, t);
}

function toggleInfo(wut) {
	if ((!wut) || (wut == ''))
		wut = dg('hin').innerHTML;
	if (wut == 'Show') {
		dg('hin').innerHTML = 'Hide&nbsp;';
		dg('photoBoxes').style.display = 'block';
		dg('theImage').style.cssFloat = 'left';
		dg('theImage').style.styleFloat = 'left';
		dg('theImage').style.marginRight = '15px';
		setCookie('hideinfo', 'false');
	}
	else {
		dg('hin').innerHTML = 'Show';
		dg('photoBoxes').style.display = 'none';
		dg('theImage').style.cssFloat = 'none';
		dg('theImage').style.styleFloat = 'none';
		dg('theImage').style.marginRight = '55px';
		setCookie('hideinfo', 'true');
	}
}

function cookieVal(cookname) {
	thiscook = document.cookie.split("; ");
	for (i=0; i<thiscook.length; i++)
		if (cookname == thiscook[i].split("=")[0])
			return thiscook[i].split("=")[1];
	return -1;
}

function setCookie(key, val) {
	newd = new Date;
	newd.setMonth(newd.getMonth()+6);
	document.cookie = key+"="+val+";expires=" + newd.toGMTString();
}

function reToggleInfo() {
	toggleInfo((cookieVal('hideinfo') != 'true')?'Show':'Hide');
}

function rand(x) {
	return Math.round(Math.random()*x);
}

function reshuffle() {
	var maxRand = 400-75;
	var n = dg('thumbscount').value;
	for (var i=0; i<n; i++) {
		dg('ThumbInBox'+i).style.top = rand(maxRand)+'px';
		dg('ThumbInBox'+i).style.left = rand(maxRand)+'px';
	}
}

function updateIndic() {
	var v = dg('indicator').innerHTML;
	var l = v.length;
	var neck = 52;
	if (l > neck)
		v = v.substring(0, l-3*7)
	if ((l%3) == 0)
		dg('indicator').innerHTML = '&#149;      '+v;
	else
		dg('indicator').innerHTML = '&nbsp; '+v;
	if (isAjaxing)
		setTimeout("updateIndic();", 500);
	else
		dg('indicator').innerHTML = '';
}

function alertContents(http_request) {
	if (http_request.readyState == 4)
		try {
			if (http_request.status == 200) {
				AjaxVal = http_request.responseText;
				AjaxVal = AjaxVal.substr(6, AjaxVal.length-13); // to clear <ajax></ajax> tag which was added to save XMLity
				AjaxValRead = false; // to avoid duplicate the reading of the content
				if (AjaxVal.substr(0, 4) == 'Done') {
					dg('rateStatus').innerHTML = 'Your rating saved!';
					dg('sumRate').innerHTML = AjaxVal.substr(4, AjaxVal.length-1);
				}
				AjaxVal = "";
				isAjaxing = false;
			}
		} catch (e) {}
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

function SaveRating(pid, rate) {
	if (rate == 0) {
		alert('Select your rate among the other options!');
		return;
	}	
	isAjaxing = true;
	dg('rateStatus').innerHTML = 'Saving your rate ';
	updateIndic();
	makeRequest("./?cmd=rate&p="+pid+"&rate="+rate+"&r="+Math.round(Math.random()*100000)); // to avoid unwanted caching
}

function prepareBody() {
	try {
		reToggleInfo();
	} catch(e) {}
	try {
		dg('wvwimg').src = "wv.php?rand="+rand(10000000);
	} catch(e) {}
}