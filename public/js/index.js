// Costant elements of the index.html page
const avatar_account = document.getElementById('avatar_account');
const btns_register = document.querySelectorAll('.full');
const btn_signIn = document.getElementById('btn_signIn');
const user_number = document.getElementById('user_number');

// On account icon click
// Open the login page
avatar_account.addEventListener('click', () => {
	document.location.href="./app/login.html";
});

// On register buttons click
// Open the register page
btns_register.forEach((btn) => btn.addEventListener('click', () => {
	document.location.href="./app/login.html#register";
})
);

// On sign in button click
// Open the login page
btn_signIn.addEventListener('click', () => {
	document.location.href="./app/login.html";
});

// Get the number of users of SoNetAg
var url = "https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/getAccountRegisteredCount";
$.getJSON(url, function(result){
	user_number.textContent = result.accountRegisteredCount;
});
