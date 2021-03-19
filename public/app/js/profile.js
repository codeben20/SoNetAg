// Display profile page with user information
function profileComponent(user){
	//Load the HTML code of the page
	loadHTML("components/profile.html", container, function () {profileCallback();});

	// Callback function call after the loading of HTML
	function profileCallback(){
		// Constants from the HTML page
		const profile_name = document.querySelector('#profile_name');
		const profile_firstname = document.querySelector('#profile_firstname');
		const profile_email = document.querySelector('#profile_email');
		const profile_submit = document.querySelector('#profile_submit');
		
		// Get the user information
		db.collection('users').doc(user.uid).get().then((doc) => {
			profile_name.value = doc.data().name;
			profile_firstname.value = doc.data().firstName;
			profile_email.value = user.email;
			div.style.display = "none";
		});

		// Update the email address
		profile_submit.addEventListener('click', (e) => {
			e.preventDefault();
			// Request user password
			var password = prompt("Please enter your password:", "");
			if (password == null || password == "") {
				alert("Your email was not updated");
			} else {
				// Update email address
				auth.signInWithEmailAndPassword(user.email, password)
				  .then((userCredential) => {
				    // Signed in
				    var user = userCredential.user;

				    user.updateEmail(profile_email.value).then(() => {
					    	alert("Email was modified with success");
					    });
					    })
						.catch((error) => {
							// Error
							alert("An error has occured, please try again.");
				    });
			}
		});
	}
}
