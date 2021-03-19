// Create a post for publishing it on networks function
function createPost(index){
	// Constants from the HTML file
	const post_img = document.getElementById('post_img');
	const post_title = document.querySelector('#post_title');
	const post_text = document.querySelector('#post_text');
	const post_selected_img = document.getElementById('post_selected_img');
	const create_post_delete = document.getElementById('create_post_delete');
	const post_submit = document.getElementById('post_submit');
	const publish = document.getElementById('publish');

	const hov = document.querySelector('.hov');

	// Tumblr network list index 
	var tumblrAccountIndex = -1;

	// Display the selected image
	post_img.addEventListener("change", () => {
		var file = post_img.files[0];
		if(file != null){
			post_selected_img.setAttribute("src", URL.createObjectURL(post_img.files[0]));
		}
	});

	// On click, delete the image
	create_post_delete.addEventListener('click', () => {
		deleteImgPost();
	});

	// Manage the display of thedelete cross for the image
	$(function(){
		var dark = $('#create_post_delete');
		var img = $('#post_selected_img');

		dark.hide();
		img.mouseenter(function(){
			dark.fadeIn('slow');
		});
		img.mouseleave(function(){
			if(!dark.is(':hover')){
				dark.fadeOut('slow');
			}
		});

		dark.mouseleave(function(){
			if(!img.is(':hover')){
				dark.fadeOut('slow');
			}
		});
	});

	// Submit the post to be publish
	post_submit.addEventListener('click', (e) => {
		e.preventDefault();
		// If post created in the one social page display and it is not a Tumblr post
		// Then post it
		if((index != null) && (listSocialNetworks[index].type.localeCompare('tumblr') != 0)){
			postOnSocial(listSocialNetworks[index].type, index);
			// Reset form
			post_title.value = "";
			post_text.value = "";
			deleteImgPost();
			
		}else {
			// Display list networks pop up
			var hov_card = document.querySelector('.hov_card');
			let div = document.createElement("div");

			hov.style.display = "block";

			hov.addEventListener('click', (e) => {
				if(!$('.hov_card').is(':hover')){
					hov.style.display = "none";
				}
			});

			div.setAttribute("id", "list_networks");
			div.setAttribute("style", "display:flex;flex-direction:column;");

			// If post created in the one social page display and it is a Tumblr post
			if((index != null) && (listSocialNetworks[index].type.localeCompare('tumblr') == 0)){
				tumblrAccountIndex = index;
				// Display list of Tumblr blogs
				getTumblrBlogs(div, index);
			}else{
				// Display list of all socials
				listSocialNetworks.forEach((l, i) => {
					if(l.type.localeCompare('tumblr') == 0){
						getTumblrBlogs(div, i);
					}else {
						let input = document.createElement("input");
						let label = document.createElement("label");
						let d = document.createElement("div");

						input.setAttribute("id", i);
						input.setAttribute("type", "checkbox");
						label.innerHTML = l.accountName + " (" + l.type + ")";
						label.setAttribute("for", i);
						d.append(input);
						d.append(label);
						div.append(d);
					}
				});
			}

			hov_card.replaceChild(div,hov_card.childNodes[3]);
		}
	});

	// Publish the post
	publish.addEventListener('click', (e) => {
		e.preventDefault();
		let div = document.getElementById("list_networks");
		let inputs = div.querySelectorAll("input");
		if(tumblrAccountIndex >= 0){
			// If there is a Tumblr account
			inputs.forEach(i => {
				// For each networks, if it was selected, published on it
				if(i.checked){
					postOnSocial(listSocialNetworks[i.id].type, i.id, tumblrAccountIndex);
				}
			});
		}else {
			inputs.forEach(i => {
				// For each networks, if it was selected, published on it
				if(i.checked){
					if(i.id < listSocialNetworks.length){
						postOnSocial(listSocialNetworks[i.id].type, i.id, 0);
					}
				}
			});
		}
		// Close networks list
		hov.style.display = "none";

		// Reset form
		post_title.value = "";
		post_text.value = "";
		deleteImgPost();
	});


	// Selected the right social media to post on it
	async function postOnSocial(type, id, index){
		var url;
		// Get user temporary token
		var idToken = await auth.currentUser.getIdToken();
		if(type.localeCompare("twitter") == 0){
			// Post on Twitter
			url = URL_SERVER + "/postTwitterApi_newPost?idToken=" + idToken + "&index=" + id;
			sendPostHttp(type, url);
		}else if(type.localeCompare("reddit") == 0){
			// Post on Reddit
			url = "";
			sendPostHttp(type, url);
		}else if(type.localeCompare("tumblr") == 0){
			// Post on Tumblr
			url = URL_SERVER + "/postTumblrApi_newPost?idToken=" + idToken + "&index=" + index + "blogId" + id;
			sendPostHttp(type, url);
		}
	}

	// Send post to the server for publishing on the different selected networks
	function sendPostHttp(type, url){
		var formdata = new FormData();
		// If the user has added an image
		if(post_img.files[0] != null){
			formdata.append("file", post_img.files[0], URL.createObjectURL(post_img.files[0]));
		}
		// If the the social can handle a title
		if((type.localeCompare("reddit") == 0) && (type.localeCompare("tumblr") == 0)){
			if(post_title.value != null && post_title.value.localeCompare("") != 0){
				formdata.append("title", post_title.value);
			}
		}
		// Add the post text
		formdata.append("content", post_text.value);

		// Request parameters
		var requestOptions = {
		  method: 'POST',
		  body: formdata,
		  redirect: 'follow'
		};

		// Request
		fetch(url, requestOptions)
		  .then(response => response.text())
		  //.then(result => console.log(result))
		  .catch(error => console.log('error', error));
	}

	// Delete the selected img
	function deleteImgPost(){
		post_selected_img.setAttribute("src", "");
		post_img. files = null;
	}

	// Get the different Tumblr blogs for user selections for posted on it
	function getTumblrBlogs(div, index){
		(async function () {
			// Get user temporary token
			var idToken = await USER.getIdToken();
			// URL server
			const url = URL_SERVER + '/getTumblrApi_userInfo?index=' + index + '&idToken=' + idToken;
			// Request blogs list to server
			$.getJSON(url, function(result){
				var blogs = result.blogs;
				// Display list
				blogs.forEach((l, i) => {
					let input = document.createElement("input");
					let label = document.createElement("label");
					let d = document.createElement("div");

					input.setAttribute("id", l.uuid);
					input.setAttribute("type", "checkbox");
					label.innerHTML = l.name + " (tumblr)";
					label.setAttribute("for", i);
					d.append(input);
					d.append(label);
					div.append(d);
				});
		 	});
		})();
	}

}