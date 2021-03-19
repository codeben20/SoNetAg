//Get Tumblr tokens
function getTumblrToken(){	
    (async function(){
    	// URL to the server
		var url = URL_SERVER + "/tumblrAPI_oauth_1";

		// Request tokens to the server
		$.getJSON(url, function(result){

			var oauth_token = result.oauth_token;
			var oauth_token_secret = result.oauth_token_secret;

			var url = "https://www.tumblr.com/oauth/authorize?oauth_token=" + oauth_token + "&source=" + oauth_token_secret;
			// Open a new window with the url
    		window.open(url, 'Tumblr', 'height=500, width=500, top=100, left=100, toolbar=no, menubar=yes, location=no, resizable=yes, scrollbars=no, status=no');
			
	 	});
	})();
}

//Set up reactions/interactions for tumblr post
function setReactionsTumblr(div, like, likeCount, reblogCount) {
	var code3 = '<div id="share"><svg ><use xlink:href="img/share.svg#share_svg"></use></svg><span>0</span></div>';
	var code1 = '<div id="tweet"><svg><use xlink:href="img/retweet.svg#retweet_svg"></use></svg><span>' + reblogCount + '</span></div>';
	
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
	d.innerHTML = code1 + code2 + code3;
	div.appendChild(d);

	// Return the div
	return div;
}