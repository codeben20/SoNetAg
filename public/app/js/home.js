function homeComponent(user) {
	//Load the HTML code of the page
	loadHTML("components/home.html", container, function () {homeCallback();});

	// Callback function call after the loading of HTML
	function homeCallback(){
		// Constants from elements from HTML page
		const refresh_icon = document.querySelector('#refresh_icon');
		const home = document.querySelector('#home');
		home.classList.add("nav_list_item_active");

		// Set up create post area
		createPost(null);
		// Get home feed
		getHomeFeed(user, true);

		// Display more on scroll
		pageIndex = 0;
		/*scroll(function () {getHomeFeed(user, false);})*/

		refresh_icon.addEventListener('click', (e) => {
			e.preventDefault();
			getHomeFeed(user, true);
		});
	}
}