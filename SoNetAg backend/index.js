const functions = require('firebase-functions');
const admin = require('firebase-admin');
const cors = require('cors')({ origin: true });
require('dotenv').config()

const utils = require('./utils/util')
const Aggregator = require('./utils/Aggregator')
const Twitter = require('./utils/Twitter')
const Reddit = require('./utils/Reddit')
const Tumblr = require('./utils/Tumblr')
const Yammer = require('./utils/Yammer')

const firebase = require('firebase');

const serviceAccountCredentials = require('./service-account-credentials.json')
firebase.initializeApp(serviceAccountCredentials)

admin.initializeApp({
    credential: admin.credential.applicationDefault(),
    databaseURL: 'https://onefeedtest-b74c6.firebaseio.com'
});

// Set the region to europe
const regionalFunctions  = functions.region('europe-west1')

/**
 * Aggregation features
 */

/**
 * Get the global feed
 * @param {string} idToken
 * @return {jsonObject} Return the home feed remodeled
 */
exports.getHomeFeed = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {

        var idToken = req.query.idToken

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        arrayCreds = doc.data().socialAccounts

        res.json(await Aggregator.getHomeFeed(arrayCreds, user.uid))
    })
});

/**
 * Twitter Features
 */

/**
 * Get the twitter home feed
 * @param {string} idToken
 * @param {string} index
 * @return {jsonObject} Return the home feed remodeled
 */
exports.getTwitterApi_homeFeed = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {

        var idToken = req.query.idToken
        var index = req.query.index

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        accessTokenSecret = doc.data().socialAccounts[index].accessTokenSecret

        res.json(await Twitter.getHomeFeed(accessToken, accessTokenSecret, index))
    })
});

/**
 * Create a new twitter post
 * Include content and file as form-data in the request
 * @param {string} idToken
 * @param {string} index
 * @return {jsonObject} Return the response from twitter
 */
exports.postTwitterApi_newPost = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {

        var idToken = req.query.idToken
        var index = req.query.index

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        accessTokenSecret = doc.data().socialAccounts[index].accessTokenSecret

        const { files, fields } = await utils.parse(req.headers, req.body);

        var content = fields.content
        res.json(await Twitter.postNewPost(accessToken, accessTokenSecret, content, files.file))
    })
});

/**
 * Like a twitter post
 * @param {string} idToken
 * @param {string} index
 * @param {string} postId
 * @return {jsonObject} Return the response from twitter
 */
exports.getTwitterApi_like = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {

        var idToken = req.query.idToken
        var index = req.query.index
        var postId = req.query.postId

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        accessTokenSecret = doc.data().socialAccounts[index].accessTokenSecret

        res.json(Twitter.postLike(accessToken, accessTokenSecret, postId))
    })
});

/**
 * UnLike a twitter post
 * @param {string} idToken
 * @param {string} index
 * @param {string} postId
 * @return {jsonObject} Return the response from twitter
 */
exports.getTwitterApi_unlike = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {

        var idToken = req.query.idToken
        var index = req.query.index
        var postId = req.query.postId

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        accessTokenSecret = doc.data().socialAccounts[index].accessTokenSecret

        res.json(Twitter.postUnlike(accessToken, accessTokenSecret, postId))
    })
});

/**
 * Retweet a twitter post
 * @param {string} idToken
 * @param {string} index
 * @param {string} postId
 * @return {jsonObject} Return the response from twitter
 */
exports.getTwitterApi_retweet = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {

        var idToken = req.query.idToken
        var index = req.query.index
        var postId = req.query.postId

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        accessTokenSecret = doc.data().socialAccounts[index].accessTokenSecret

        res.json(Twitter.postRetweet(accessToken, accessTokenSecret, postId))
    })
});

/**
 * Unretweet a twitter post
 * @param {string} idToken
 * @param {string} index
 * @param {string} postId
 * @return {jsonObject} Return the response from twitter
 */
exports.getTwitterApi_unretweet = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {

        var idToken = req.query.idToken
        var index = req.query.index
        var postId = req.query.postId

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        accessTokenSecret = doc.data().socialAccounts[index].accessTokenSecret

        res.json(Twitter.postUnRetweet(accessToken, accessTokenSecret, postId))
    })
});

/**
 * REDDIT Features
 */

/**
 * Get the reddit access token
 * @param {string} code
 * @return {jsonObject} Return the response from reddit
 */
exports.getRedditApi_accessToken = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var code = req.query.code

        res.json(await Reddit.getAccessTokken(code))
    })
});

/**
 * Get the reddit home feed
 * @param {string} idToken
 * @param {string} index Account index
 * @return {jsonObject} Return the home feed remodeled
 */
exports.getRedditApi_homeFeedBest = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        var bearerToken = doc.data().socialAccounts[index].accessToken
        var refreshToken = doc.data().socialAccounts[index].refreshToken

        res.json(await Reddit.getHomeFeedBest(bearerToken, refreshToken, user.uid, index))
    })
});

/**
 * Create a new post for reddit
 * Include content, title, subreddit and file as form-data in the request
 * @param {string} idToken
 * @param {string} index Account index
 * @return {jsonObject} Return the response from reddit
 */
exports.postRedditApi_newPost = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index

        const { files, fields } = await utils.parse(req.headers, req.body);

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        var bearerToken = doc.data().socialAccounts[index].accessToken
        var refreshToken = doc.data().socialAccounts[index].refreshToken

        var content = fields.content
        var title = fields.title
        var subReddit = fields.subReddit
        var file = files.file

        // If the subreddit is not provided, the user profile subreddit will be use
        if (subReddit === undefined) {
            subReddit = doc.data().socialAccounts[index].accountName
        }
        
        res.json(await Reddit.postNewPost(bearerToken, refreshToken, user.uid, content, title, subReddit, file))
    })
})

/**
 * TUMBLR Features
 */

/**
 * Tumblr authentication step 1
 * @return {jsonObject} Return the response from tumblr
 */
exports.tumblrAPI_oauth_1 = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        res.json(await Tumblr.oauthStep1())
    })
})

/**
 * Tumblr authentication step 2
 * @param {string} oauth_verifier
 * @param {string} accessToken
 * @param {string} secretToken
 * @return {jsonObject} Return the response from tumblr
 */
exports.tumblrAPI_oauth_2 = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        const oauth_verifier = req.query.oauth_verifier
        const accessToken = req.query.accessToken
        const secretToken = req.query.secretToken

        var response = await Tumblr.oauthStep2(oauth_verifier, accessToken, secretToken)

        userInfo = await Tumblr.getUserInfo(response.oauth_token, response.oauth_token_secret)

        response.name = userInfo.name

        res.json(response)
    })
})

/**
 * Get tumblr home feed
 * @param {string} idToken
 * @param {string} index Account index
 * @return {jsonObject} Return the home feed remodeled
 */
exports.getTumblrApi_homeFeed = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        var accessToken = doc.data().socialAccounts[index].accessToken
        var secretToken = doc.data().socialAccounts[index].secretToken

        res.json(await Tumblr.getHomeFeed(accessToken, secretToken, index))
    })
})

/**
 * Get tumblr user info
 * @param {string} idToken
 * @param {string} index Account index
 * @return {jsonObject} Return the response from tumblr api
 */
exports.getTumblrApi_userInfo = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        var accessToken = doc.data().socialAccounts[index].accessToken
        var secretToken = doc.data().socialAccounts[index].secretToken

        res.json(await Tumblr.getUserInfo(accessToken, secretToken))
    })
})

/**
 * Create a new Tumblr post
 * @param {string} idToken
 * @param {string} index Account index
 * @param {string} blogId
 * @return {jsonObject} Return the response from the tumblr api
 */
exports.postTumblrApi_newPost = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index
        var blogId = req.query.blogId


        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        var accessToken = doc.data().socialAccounts[index].accessToken
        var secretToken = doc.data().socialAccounts[index].secretToken

        const { files, fields } = await utils.parse(req.headers, req.body);
        var content = fields.content

        res.json(await Tumblr.postNewPost(accessToken, secretToken, blogId, content, files.file))
    })
})

/**
 * Like on a yammer post
 *  @param {string} idToken
 *  @param {string} index Account index
 *  @param {string} postId Post id
 *  @param {string} reblogKey Post reblog key
 *  @return {jsonObject} return the yammer news feed
 */
exports.postTumblrApi_like = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index
        var postId = req.query.postId
        var reblogKey = req.query.reblogKey

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        secretToken = doc.data().socialAccounts[index].secretToken

        res.json(await Tumblr.postLike(accessToken, secretToken, postId, reblogKey))
        
    })
})

/**
 * Unlike on a yammer post
 *  @param {string} idToken
 *  @param {string} index Account index
 *  @param {string} postId Post id
 *  @param {string} reblogKey Post reblog key
 *  @return {jsonObject} return the yammer news feed
 */
exports.postTumblrApi_unlike = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index
        var postId = req.query.postId
        var reblogKey = req.query.reblogKey

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        secretToken = doc.data().socialAccounts[index].secretToken

        res.json(await Tumblr.postUnLike(accessToken, secretToken, postId, reblogKey))
    })
})

/*exports.postTumblrApi_comment = functions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index
        var postId = req.query.postId
        var reblogKey = req.query.reblogKey
        var content = req.query.content

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken
        secretToken = doc.data().socialAccounts[index].secretToken

        res.json(await Tumblr.postComment(accessToken, secretToken, postId, reblogKey, content))
    })
})*/


/** YAMMER features */


/**
 * Get the yammer access token
 * @param {string} code
 * @return {jsonObject} Return the yammer access token
 */
exports.getYammerApi_accessToken = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        const code = req.query.code

        res.json(await Yammer.getAccessTokken(code))
    })
})

/**
 * Get the yammer home feed
 *  @param {string} idToken
 *  @param {string} index Account index
 *  @return {jsonObject} return the yammer news feed
 */
exports.getYammerApi_homeFeed = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken

        res.json(await Yammer.getHomeFeed(accessToken))
    })
})

/**
 * Get the comments of a post
 * @return {jsonObject} Return comments from the Yammer post
 */
exports.getYammerApi_comments = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var idToken = req.query.idToken
        var index = req.query.index
        var id = req.query.id

        var user = await admin.auth().verifyIdToken(idToken)
        var doc = await firebase.firestore().collection('users').doc(user.uid).get()
        accessToken = doc.data().socialAccounts[index].accessToken

        res.json(await Yammer.getComments(accessToken, id))
    })
})

/**
 * Retrieves the number of registered users
 * @return {jsonObject} Return account registered count
 */
exports.getAccountRegisteredCount = regionalFunctions.https.onRequest((req, res) => {
    cors(req, res, async () => {
        var listUsers = await admin.auth().listUsers()
        res.json({
            'accountRegisteredCount': listUsers.users.length
        })
    })
})