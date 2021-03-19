//Build a post and display it
function designPost(data){
	//Variables needed
	let div = document.createElement("div");
	let div2 = document.createElement("div");
	let div3 = document.createElement("div");
	let header = document.createElement("div");
	let body = document.createElement("div");
	let footer = document.createElement("div");
	let img = document.createElement("img");
	let iconComments = document.createElement("img");
	let iconReactions = document.createElement("svg");
	let divReactions = document.createElement("div");
	let iconNetwork = document.createElement("img");
	let avatarAccount = document.createElement("img");
	let date = document.createElement("p");
	let nameAccount = document.createElement("p");
	let title = document.createElement("h5");
	let text = document.createElement("p");
	let video = null;

	//Set up attributes for each components
	div.setAttribute("class", "post");
	div2.setAttribute("style", "display:flex; flex-direction: column;");
	header.setAttribute("class", "post_header");
	body.setAttribute("class", "post_body");
	footer.setAttribute("class", "post_footer");
	text.setAttribute("class", "post_text");
	iconNetwork.setAttribute("class", "post_icon_network");
	avatarAccount.setAttribute("class", "post_account_avatar");
	nameAccount.setAttribute("class", "post_account_name");
	date.setAttribute("class", "post_date");
	img.setAttribute("class", "post_img");
	title.setAttribute("class", "post_reddit_title");

	//Get the post text
	if(data.hasOwnProperty("text")){
		text.innerHTML = data.text;
	}
	
	//Display the published date
	date.innerHTML = getDateFromTimestamp(data.created_at);

	//Display post account avatar
	if(data.user.profile_image_url_https.localeCompare("") == 0){
		avatarAccount.setAttribute("src", "./img/avatar.png");
	}else {
		avatarAccount.setAttribute("src", data.user.profile_image_url_https);
	}

	//Display comments icon
	iconComments.setAttribute("src", "./img/comments.png");
	iconComments.setAttribute("style", "float: left;");
	div3.setAttribute("style", "float: right;");
	
	footer.setAttribute("id", data.id_str);
	footer.setAttribute("data-source", data.source);
	footer.setAttribute("data-account_index", data.accountIndex);

	//Display the reactions icon and set up the different post interactions, depending the network
	xhr = new XMLHttpRequest();
	xhr.open("GET","img/thumb_up.svg",false);
	xhr.overrideMimeType("image/svg+xml");
	xhr.onload = function(e) {
		if((data.source).localeCompare('twitter') == 0){
			div3.appendChild(setReactionsTwitter(divReactions, data.favorited, data.favorite_count, data.retweeted, data.retweet_count));
		}else if((data.source).localeCompare('reddit') == 0){
			div3.appendChild(setReactionsReddit(divReactions, data.favorited, data.favorite_count));
		}else if((data.source).localeCompare('tumblr') == 0){
			div3.appendChild(setReactionsTumblr(divReactions, data.favorited, data.favorite_count, data.reblog_count));
		}
		div3.appendChild(xhr.responseXML.documentElement);
	}
	xhr.send("");

	//Check if the post has a photo or video and display it
	if(data.hasOwnProperty("media")){
		if((typeof data.media).localeCompare("string") == 0){
			img.setAttribute("src", data.media);
		}else {
			if ((data.media).hasOwnProperty("reddit_video")) {
				video = document.createElement("video");
				let source = document.createElement("source"); 
				video.setAttribute("class", "post_img");
				video.setAttribute("controls", true);
				source.setAttribute("src", data.media.reddit_video.fallback_url);
				video.append(source);
			}
		}
	}

	//Display the published account name with a special treatment for Tumblr posts
	if((data.source).localeCompare("tumblr") == 0){
		nameAccount.innerHTML = data.user.screen_name;
		if(data.hasOwnProperty("profile_image_url_https")){
			if((data.profile_image_url_https.localeCompare("") == 0)){
				avatarAccount.setAttribute("src", "./img/avatar.png");
			}else {
				avatarAccount.setAttribute("src", data.profile_image_url_https);
			}
		}
	}else {
		nameAccount.innerHTML = data.user.name;
	}


	//Display the social network icon corresponding to the post source
	if((data.source).localeCompare('twitter') == 0){
		iconNetwork.setAttribute("src", "./img/twitter.png");
		if(data.favorited){
			div3.setAttribute("class", "like");
		}
	} else if((data.source).localeCompare('reddit') == 0){
		iconNetwork.setAttribute("src", "./img/reddit.png");
		title.innerHTML = data.title;
	}else if((data.source).localeCompare("yammer") == 0){
		iconNetwork.setAttribute("src", "./img/yammer.png");
	}else if((data.source).localeCompare("tumblr") == 0){
		iconNetwork.setAttribute("src", "./img/tumblr.png");
		title.innerHTML = data.title;
		//Set up the like on a post
		if(data.favorited){
			div3.setAttribute("class", "like");
		}
		footer.setAttribute("data-reblog_key", data.reblog_key);
		
		//Add the post tags
		if(data.hasOwnProperty("tags")){
			var tags = data.tags;
			text.innerHTML = "</br>";
			tags.forEach(e => {
				text.innerHTML = "#"+ e + " ";
			});
		}
	}else if((data.source).localeCompare("linkedin") == 0){
		iconNetwork.setAttribute("src", "./img/linkedin.png");
	}
		
	//Build the post
	header.append(avatarAccount);
	div2.append(nameAccount);
	header.append(iconNetwork);
	div2.append(date);
	header.append(div2);
	
	if(((data.source).localeCompare('reddit') == 0) || ((data.source).localeCompare('tumblr') == 0)){
		body.append(title);
	}
	body.append(title);
	body.append(text);
	if(video != null){
		body.append(video);
	}else {
		body.append(img);
	}
	footer.append(iconComments);
	footer.append(div3);
	
	div.append(header);
	div.append(body);
	div.append(footer);

	//Return the post to be displayed
	return div;
}

//Get a date with the timestamp
function getDateFromTimestamp(timestamp){
	var date;

	const dateNow = new Date();
    var duration = dateNow.getTime() / 1000 - timestamp;

	if (duration < 60) {
        date = Math.trunc(duration) + ' s ago';
    } else if (duration < 3600) {
        date = Math.trunc(duration / 60) + ' m ago';
    } else if (duration < 86400) {
        date = Math.trunc(duration / 3600) + ' h ago';
    } else {
        const d = new Date(timestamp * 1000);
        date = new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric', year: 'numeric' }).format(d);
    }

    return date;
}

