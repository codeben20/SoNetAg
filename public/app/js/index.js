//Loading screen
const div = document.querySelector('#onload');
 window.onload = function(){
	div.style.display = "none";
 };

const container = document.querySelector('#container');
var USER;

auth.onAuthStateChanged(user => {
	if(!user){
		//If not connected, user redirected to the login page
		document.location.href="login.html";
	}else {
		//User connected

		//Variables
		// Store the current user
		USER = user;
		
		const nav = document.querySelector('.nav');
		const sonetag = document.querySelector('#sonetag');
		const nav_arrow = document.querySelector('.nav_arrow');
		var nav_list_items = document.querySelectorAll('.nav_list_item');

		
		const add_account_sub_list = document.querySelector('#add_account_sub_list');
		const signOut = document.querySelector('#signOut');

		const header_title = document.querySelector('#header_title');
		
		//Display home interface
		header_title.textContent = "Feed";
		homeComponent(user);

		//Display the list of social networks in the menu
		(async function(user){
			//Get the networks list
			const doc = await db.collection('users').doc(user.uid);
			doc.onSnapshot(docSnapshot => {
			  listSocialNetworks = docSnapshot.data().socialAccounts;
				displayListSocialNetworks();
				nav_list_items = document.querySelectorAll('.nav_list_item');
				nav_list_items.forEach((link) => link.addEventListener('click', (e) => {
					e.preventDefault();
					nav_list_items.forEach((link) => link.classList.remove("nav_list_item_active"));
					e.currentTarget.classList.add("nav_list_item_active");
					setUpDisplay(e.currentTarget.id);
				}));
			  
			}, err => {
			  console.log(`Encountered error: ${err}`);
			});

			var r = await doc.get();
			return r.data().socialAccounts;
		})(user).then(r => {
			listSocialNetworks = r;
		});

			
		//Redirected to the sonetag presentation page on the click on the logo
		sonetag.addEventListener('click', () => {
			document.location.href="../index.html";
		});

		//Show/Hide the menu on the arrow click
		nav_arrow.addEventListener('click', (e) => {
			e.preventDefault();
			nav.classList.toggle("nav_closed");
			if(nav.classList.contains("nav_closed")){
				add_account_sub_list.classList.remove("open");
			}
		});

		
		//Sign out on click
		signOut.addEventListener('click', (e) => {
			e.preventDefault();
			firebase.auth().signOut().then(() => {
			  document.location.href="login.html";
			}).catch((error) => {
			  alert("An error has appeared, try again later!");
			});
		});

		//Display the interface corresponding to the selected page
		function setUpDisplay(page){
			scroll(null);
			if(page.localeCompare("addAccount") != 0 && !(parseInt(page) >= 0)){
				if(page.localeCompare("home") == 0){
					console.log("home");
					header_title.textContent = "Feed";
					homeComponent(user);
				}else if(page.localeCompare("profile") == 0){
					header_title.textContent = "Profile";
					profileComponent(user);
				}else if(page.localeCompare("twitter") == 0){
					getTwitterToken();
				}else if(page.localeCompare("reddit") == 0){
					getRedditToken();
				}else if(page.localeCompare("yammer") == 0){
					getYammerToken();
				}else if(page.localeCompare("tumblr") == 0){
					getTumblrToken();
				}else if(page.localeCompare("linkedin") == 0){
					alert("Linkedin is still under development");
				}
				if(!nav.classList.contains("nav_closed")){
					nav.classList.add("nav_closed");
					add_account_sub_list.classList.remove("open");
				}
			}else {
				if(page.localeCompare("addAccount") == 0){
					add_account_sub_list.classList.toggle("open");
					if(nav.classList.contains("nav_closed")){
						nav.classList.remove("nav_closed");
					}
				}else{
					var index = parseInt(page);
					if(index < listSocialNetworks.length){
						header_title.textContent = listSocialNetworks[index].accountName;
						displayOneSocial(index, user);
					}
				}
			}
		}


	}

	//Set up a presentation tour when a user is just registered
	var url = window.location.href;
	var data = url.split("#");
	if((data.length > 1) && (data[1].localeCompare("new") == 0)){
		(new Tourguide()).start();
	}

});

//Get the home feed from all networks
async function getHomeFeed(user, state){
	var idToken = await user.getIdToken();
	getFeed(URL_SERVER + '/getHomeFeed?idToken=' + idToken, state);
}


//Load HTML code from a file
function loadHTML(url, element, callback){
	var xhr= new XMLHttpRequest();
	xhr.open('GET', url, true);
	xhr.onreadystatechange= function() {
	    if (this.readyState!==4) return;
	    if (this.status!==200) return; // or whatever error handling you want
	    element.innerHTML= this.responseText;
	    callback();
	};
	xhr.send();
}


//Load older elements when user is at the bottom of a page
function scroll(fct){
	var position = true;
	$(window).off('scroll');
	$(window).scroll(function() {
		if(fct != null){
			var deviceAgent = navigator.userAgent.toLowerCase();
	    	var agentID = deviceAgent.match(/(iphone|ipod|ipad)/);

	        if(($(window).scrollTop() + $(window).height() + 100) > $(document).height()
	        || agentID && ($(window).scrollTop() + $(window).height()) + 150 > $(document).height()) {
	        	if(position){
	            	position = false;
	            	pageIndex ++;
	            	fct();
	             }
	        }else {
	        	position = true;
	        }
	    }
    });
}