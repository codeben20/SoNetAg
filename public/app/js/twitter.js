// Get Twitter tokens using firebase API
function getTwitterToken(){	
	var provider = new firebase.auth.TwitterAuthProvider();

	firebase
	  .auth()
	  .signInWithPopup(provider)
	  .then((result) => {
	    
	    var credential = result.credential;

	    // Twitter OAuth 1.0 Access Token and Secret.
	    var accessToken = credential.accessToken;
	    var secretToken = credential.secret;

	    // The signed-in user info.
	    var user = result.user;

	    var params = {
                type: "twitter",
                accountName: result.additionalUserInfo.profile.name,
                accountNameId: result.additionalUserInfo.profile.screen_name,
                accessToken: accessToken,
                accessTokenSecret: secretToken
            };

	   db.collection('users').doc(user.uid).update({socialAccounts: firebase.firestore.FieldValue.arrayUnion(params)});

	   /*listSocialNetworks.push(params);*/

	  }).catch((error) => {
	    // Handle Errors here.
	    var errorCode = error.code;
	    var errorMessage = error.message;
	    // The email of the user's account used.
	    var email = error.email;
	    // The firebase.auth.AuthCredential type that was used.
	    var credential = error.credential;
	  });
}


// Function for set up the reactions/interactions icons for a post
// Passing as parameters the final div, the like value of the post from the user, number of likes, if retweeted from the user, number of retweet
function setReactionsTwitter(div, like, likeCount, retweet, retweetCount) {
	var code3 = '<div id="share"><svg ><use xlink:href="img/share.svg#share_svg"></use></svg><span>0</span></div>';

	if(retweet){
		// Post retweeted by user
		var code1 = '<div id="tweet" class="like2"><svg><use xlink:href="img/retweet.svg#retweet_svg"></use></svg><span>' + retweetCount + '</span></div>';
	}else {
		// Post not retweeted by user
		var code1 = '<div id="tweet"><svg><use xlink:href="img/retweet.svg#retweet_svg"></use></svg><span>' + retweetCount + '</span></div>';
	}
	
	if(like){
		// Liked by user
		var code2 = '<div id="like" class="like2"><svg><use xlink:href="img/thumb_up.svg#react"></use></svg><span>' + likeCount + '</span></div>';
	}else {
		// Not liked
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