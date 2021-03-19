function getYammerToken(){	
	// Callback url
	const URL_CALLBACK = "http://localhost/project_course/app/yammerCallBack"
	// Tumblr client ID
	const CLIENT_ID = "8Bd5i0QzbROlqggsiKtA";
	var url = "https://www.yammer.com/oauth2/authorize?client_id=" + CLIENT_ID + "&response_type=code&redirect_uri=" + URL_CALLBACK;
	// Open a new window with the url
    window.open(url, 'Yammer', 'height=500, width=500, top=100, left=100, toolbar=no, menubar=yes, location=no, resizable=yes, scrollbars=no, status=no');
}

// Get Yammer comments for a post
// Passing the social network index and the post ID
function getPostComments(index, postId){
	// Get user temporary token
	(async function(USER){
		var idToken = await user.getIdToken();
		return idToken;
	})(user).then(idToken => {
		// Get the comments
		var url = URL_SERVER + '/getYammerApi_comments?idToken=' + idToken + "&index=" + index + "&id=" + postId;
		$.getJSON(url, function(result){
 			console.log(result);
 		});
	});	
}