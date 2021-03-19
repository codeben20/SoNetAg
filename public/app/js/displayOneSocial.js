// Funstion for displaying the feed of one social network
async function displayOneSocial(index, user){
	//Load the HTML code of the page
	loadHTML("components/home.html", container, function () {displayOneSocialCallback();});

	// Callback function call after the loading of HTML
	function displayOneSocialCallback(){
		createPost(index);
		//Get the user temporary token
		(async function(user){
			var idToken = await user.getIdToken();
			return idToken;
		})(user).then(idToken => {
			//Get the feed from the social network selected
			var url;
			if(listSocialNetworks[index].type.localeCompare("twitter") == 0){
				url = URL_SERVER + '/getTwitterApi_homeFeed?idToken=' + idToken + "&index=" + index;
				
			}else if(listSocialNetworks[index].type.localeCompare("reddit") == 0){
				url = URL_SERVER + '/getRedditApi_homeFeedBest?idToken=' + idToken + "&index=" + index;
			}else if(listSocialNetworks[index].type.localeCompare("tumblr") == 0){
				url = URL_SERVER + '/getTumblrApi_homeFeed?idToken=' + idToken + "&index=" + index;
			}else if(listSocialNetworks[index].type.localeCompare("yammer") == 0){
				url = URL_SERVER + '/getYammerApi_homeFeed?idToken=' + idToken + "&index=" + index;
			}
			// Get social feed
			getFeed(url, true);

			// Display more on scroll
			pageIndex = 0;
			//scroll(function () {getFeed(url, false);})

			refresh_icon.addEventListener('click', (e) => {
				e.preventDefault();
				getFeed(url, true);
			});
		});
	}
}