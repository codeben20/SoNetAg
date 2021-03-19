auth.onAuthStateChanged(user => {
	if(user){
		//User already connected
		//Redirected to the home page
		document.location.href="index.html";
	}else {
		//User not connected

		//Declaration of all variables
		const div_login = document.getElementById('div_login');
		const div_register = document.getElementById('div_register');
		const logo = document.getElementById('logo');

		const submit_log = document.getElementById('submit_log');
		const submit_reg = document.getElementById('submit_reg');
		const agreement = document.getElementById('agreement');
		const stay_connected = document.getElementById('stay_connected');

		const sub_free = document.getElementById('sub_free');
		const sub_prenium = document.getElementById('sub_prenium');
		const password_forgotten = document.getElementById('password_forgotten');


		const displayRegisterBlock = document.getElementById('displayRegisterBlock');
		const displayLoginBlock = document.getElementById('displayLoginBlock');

		//Disable the submit button for register
		submit_reg.disabled  = true;

		//Listener on the logo for redirection on click to the presentation page
		logo.addEventListener('click', (e) => {
			document.location.href="../";
		});

		//Listener for user agreement accepted
		agreement.addEventListener('click', (e) => {
			if(e.currentTarget.checked){
				submit_reg.disabled  = false;
			}else {
				submit_reg.disabled  = true;
			}
		});

		//If the user has forgotten his password
		//An email will be send for reset it
		password_forgotten.addEventListener('click', (e) => {
			e.preventDefault();
			var email = prompt("Enter your email address. You will receive an email to reset your password in the 5 minutes.", "Email");
			if(email != null){
				auth.sendPasswordResetEmail(email).then(function() {
				  // Email sent.
				}).catch(function(error) {
				  alert("An error has occured. Please, try again.");
				});
			}

		});

		//If the user click on register, the register block is displayed
		displayRegisterBlock.addEventListener('click', (e) => {
			e.preventDefault();
			div_login.style.display = "none";
			div_register.style.display = "table";
		});

		//If the user click on sign in, the sign in block is displayed
		displayLoginBlock.addEventListener('click', (e) => {
			e.preventDefault();
			div_login.style.display = "table";
			div_register.style.display = "none";
		});

		//Submit button for connection
		submit_log.addEventListener('click', (e) => {
			e.preventDefault();
			var email = document.getElementById('login').value;
			var password = document.getElementById('password').value;

			//If the user check the stay login box
			if(stay_connected.checked){
				firebase.auth().setPersistence(firebase.auth.Auth.Persistence.LOCAL);
			}else {
				firebase.auth().setPersistence(firebase.auth.Auth.Persistence.SESSION);
			}

			//Connection
			auth.signInWithEmailAndPassword(email, password).catch((error) => {
				    var errorCode = error.code;
				    var errorMessage = error.message;
				  });			
		});

		//Submit button for registration
		submit_reg.addEventListener('click', (e) => {
			e.preventDefault();
			const firstname = document.getElementById('firstname');
			const name = document.getElementById('name');
			const email = document.getElementById('email');
			const password_1 = document.getElementById('password_1');
			const password_2 = document.getElementById('password_2');

			//Verify if the two passwords are equals
			if(password_1.value.localeCompare(password_2.value) == 0){
				if(sub_free.checked){
					//If the free subscription is selected
					//Creation of the account
					firebase.auth().createUserWithEmailAndPassword(email.value, password_1.value)
					.then((userCredential) => {
						// Signed in 
						var user = userCredential.user;
						db.document('users').doc(user.uid).set({
							name: name.value,
							subscription: "free",
							firstName: firstname.value,
							lang: "En",
							socialAccounts:[]
						});
					})
					.catch((error) => {
						var errorCode = error.code;
						var errorMessage = error.message;
						alert("An error has occured.");
						console.log(errorMessage);
					});
				}else {
					//Premuim subscription

				}
			}else {
				alert("The two password are different");
			}
			
		});

		//Display the register block when loading the page if the parameter register is present.
		var url = window.location.href;
		var data = url.split("#");
		if((data.length > 1) && (data[1].localeCompare("register") == 0)){
			div_login.style.display = "none";
			div_register.style.display = "table";
		}

	}
});
