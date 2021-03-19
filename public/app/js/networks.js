// List of networks
var listSocialNetworks = [];
// HTML list of networks
var div_list_networks_ul = document.getElementById('div_list_networks_ul');

// List all user linked account in the menu
async function displayListSocialNetworks(){
	div_list_networks_ul.replaceChildren();
	listSocialNetworks.forEach((l, i) => {
		// Variables
		let li = document.createElement("li");
		let img = document.createElement("img");
		let a = document.createElement("a");
		let p = document.createElement("p");
		let div = document.createElement("div");

		// Get vertical dots svg
		xhr = new XMLHttpRequest();
		xhr.open("GET","img/three_dots.svg",false);
		xhr.overrideMimeType("image/svg+xml");
		xhr.onload = function(e) {
			div.setAttribute("class", "three_dots");
			div.innerHTML = '<div class="div_delete"><div>Delete</div></div>';
			div.appendChild(xhr.responseXML.documentElement);			
		}
		xhr.send("");

		li.setAttribute("class", "nav_list_item");
		li.setAttribute("id", i);
		img.setAttribute("width", "20px");

		// Add network icon
		if(l.type.localeCompare("twitter") == 0){
			img.setAttribute("src", "./img/twitter.png");
		}else if(l.type.localeCompare("reddit") == 0){
			img.setAttribute("src", "./img/reddit.png");
		}else if(l.type.localeCompare("yammer") == 0){
			img.setAttribute("src", "./img/yammer.png");
		}else if(l.type.localeCompare("tumblr") == 0){
			img.setAttribute("src", "./img/tumblr.png");
		}else if(l.type.localeCompare("linkedin") == 0){
			img.setAttribute("src", "./img/linkedin.png");
		}
		
		// Add elemnts to the menu
		p.innerHTML = l.accountName;
		a.append(img);
		a.append(p);
		a.append(div);
		li.append(a);
		div_list_networks_ul.append(li);

	});

	// Manage the display of the delete option
	$( ".three_dots" ).hover(
	  function() {
	    var index = this.parentNode.parentNode.id;
	    this.childNodes[0].style.display = "block";
	  }, function() {
	  	this.childNodes[0].style.display = "none";
	  	$( this.childNodes[0] ).hover(
		  function() {
		   	this.style.display = "block";
		  }, function() {
		  	this.style.display = "none";
		  }
		);
	  }
	);

	// Action on delete account 
	$( ".div_delete" ).click(
	  function() {
	    var index = this.parentNode.parentNode.parentNode.id;
	    var conf = confirm("Are you sure you want to remove this network ?");
	    if(conf){
	    	listSocialNetworks.splice(index, 1);
	    	db.collection("users").doc(USER.uid).update({
		     socialAccounts: listSocialNetworks
		   })
		  .catch(function(error) {
		      console.error("Error removing document: ", error);
		  });
	    }
	  }
	);
}

// Get Feed from URL
 async function getFeed(url, reset){
 	// Element of the HTML code
	var article_list_post = document.querySelector('#article_list_post');	
	var last_refresh = document.querySelector('#last_refresh');	

	div.style.display = "block";
	// Display last refresh date
    last_refresh.innerHTML = "Last update: " + new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric', year: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric' }).format(new Date());

	if(reset){
		article_list_post.replaceChildren();
	}

	// Request feed to the server and display each post
 	$.getJSON(url, function(result){
 		result.forEach(d => {
 			if(d != null){
 				article_list_post.append(designPost(d));
 			}
 			div.style.display = "none";
 		});

 		//Interactions
	$( ".react" ).hover(
	  function() {
	    this.parentNode.childNodes[0].childNodes[0].style.display = 'flex';
	  }, function() {
	    this.parentNode.childNodes[0].childNodes[0].style.display = 'none';	    
	  }
	);

	$( ".div" ).hover(
	  function() {
	    this.style.display = 'flex';
	  }, function() {
	    this.style.display = 'none';	    
	  }
	);


	// Modify post reaction
	$( ".div svg" ).click(
		async function() {
			// Variables
			// Get the post ID
			var idPost = this.parentNode.parentNode.parentNode.parentNode.parentNode.id;
			// Get the source code
			var source = this.parentNode.parentNode.parentNode.parentNode.parentNode.dataset.source;
			// Get the account index in the list
			var accountIndex = this.parentNode.parentNode.parentNode.parentNode.parentNode.dataset.account_index;
			var parent = this.parentNode;
			var span = parent.childNodes[1];
			var val = parseInt(span.innerHTML);
			var idToken = await USER.getIdToken();
			var url = null;
			var url2 = null;

			// Request options
			var requestOptions = {
			  method: 'POST',
			  redirect: 'follow'
			};

			// Get the url depending the source of the post
			// Like/Unlike a post
			if(parent.id.localeCompare("like") == 0){
				if(parent.classList.contains("like2")){
					val = val - 1;
					if(source.localeCompare("tumblr") == 0){
			    		url2 = "/postTumblrApi_unlike";
			    	}
				}else {
					val = val + 1;
					if(source.localeCompare("tumblr") == 0){
			    		url2 = "/postTumblrApi_like";
			    	}
				}
				if(source.localeCompare("tumblr") == 0){
					var reblogKey = this.parentNode.parentNode.parentNode.parentNode.parentNode.dataset.reblog_key;
					url = URL_SERVER + url2 + "?idToken=" + idToken + "&index=" + accountIndex + "&postId=" + idPost + "&reblogKey=" + reblogKey;
				}

				// Dispaly new value for number of like
				span.innerHTML = val;
				parent.classList.toggle("like2");
				this.parentNode.parentNode.parentNode.parentNode.classList.toggle("like");
			}else if(parent.id.localeCompare("tweet") == 0){
				span.innerHTML = val + 1;
			}else if(parent.id.localeCompare("share") == 0){
				span.innerHTML = val + 1;
			}

			if(url != null){
				fetch(url, requestOptions) 
	 				.catch(error => console.log('error', error));
 			}

		}
	);
 	});

 }


