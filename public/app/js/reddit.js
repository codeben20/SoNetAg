//Get the Reddit tokens
function getRedditToken(){	
	// Callback url
	const URL_CALLBACK = "http://localhost/project_course/app/js/redditCallBack"
	// Reddit client ID
	const CLIENT_ID = "DF4aAj_lkA0v7w";
	//Step 1: get the temporary code
	var url = "https://www.reddit.com/api/v1/authorize?client_id=" + CLIENT_ID + "&response_type=code&state=eqrghzzeretherthertyhjreh&redirect_uri=" + URL_CALLBACK + "&duration=permanent&scope=identity,mysubreddits,read,submit";
    // Open a new window with the url
    window.open(url, 'Reddit', 'height=500, width=500, top=100, left=100, toolbar=no, menubar=yes, location=no, resizable=yes, scrollbars=no, status=no');
}


//Set up reactions/interactions icons for reddit posts
function setReactionsReddit(div, like, likeCount) {
	var code3 = '<div id="share"><svg ><use xlink:href="img/share.svg#share_svg"></use></svg><span>0</span></div>';
	
	if(like){
		//Liked
		var code2 = '<div id="like" class="like2"><svg><use xlink:href="img/thumb_up.svg#react"></use></svg><span>' + likeCount + '</span></div>';
	}else {
		//Not liked
		var code2 = '<div id="like"><svg><use xlink:href="img/thumb_up.svg#react"></use></svg><span>' + likeCount + '</span></div>';
	}

	// Add elements to a div
	var d = document.createElement("div");
	d.setAttribute("class", "div");
	d.innerHTML = code2 + code3;
	div.appendChild(d);

	// Return the div
	return div;
}