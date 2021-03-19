require('dotenv').config()
const firebase = require("firebase");

/**
 * 
 * @param {string} code 
 * @returns {jsonObject} Return the response from Reddit
 */
const getAccessTokken = async (code) => {
    var request = require('request');

    var BasicAuth = Buffer.from(process.env.REDDIT_APIKEY + ":" + process.env.REDDIT_APISECRETKEY).toString('base64')

    var options = {
        'method': 'POST',
        'url': 'https://www.reddit.com/api/v1/access_token',
        'headers': {
            'Authorization': 'Basic ' + BasicAuth,
            'user-agent': 'Browser:OneFeed:v0.1 (by /u/Geoffrey-Mo)'
        },
        formData: {
            'grant_type': 'authorization_code',
            'code': code,
            'redirect_uri': process.env.DOMAIN_NAME + 'app/js/redditCallBack',
        }
    };
    return await new Promise((resolve, reject) => {
        request(options, (error, response) => {
            if (error) {
                reject(error)
            } else {
                resolve(JSON.parse(response.body))
            }
        });
    })
}

/**
 * Refresh the reddit token
 * @param {string} refreshToken 
 * @param {string} uid 
 * @returns {jsonObject} returns the new tokens
 */
const refreshingToken = async (refreshToken, uid) => {
    var request = require('request');

    var BasicAuth = Buffer.from(process.env.REDDIT_APIKEY + ":" + process.env.REDDIT_APISECRETKEY).toString('base64')

    var options = {
        'method': 'POST',
        'url': 'https://www.reddit.com/api/v1/access_token',
        'headers': {
            'Authorization': 'Basic ' + BasicAuth,
            'user-agent': 'Browser:OneFeed:v0.1 (by /u/Geoffrey-Mo)'
        },
        formData: {
            'grant_type': 'refresh_token',
            'refresh_token': refreshToken,
            'duration': 'permanent'
        }
    };

    return await new Promise((resolve, reject) => {
        request(options, async (error, response) => {
            if (error) {
                reject(error)
            } else {
                var newToken = JSON.parse(response.body).access_token

                const doc = firebase.firestore().collection('users').doc(uid).get().then(async (doc) => {
                    // Get the user social accounts array in the database
                    var socialAccounts = doc.data().socialAccounts

                    var object
                    var idObject
                    // Update the array with the new reddit tokens
                    socialAccounts.forEach((element, i) => {
                        if (element.refreshToken === refreshToken) {
                            object = element
                            idObject = i
                        }
                    })
                    // Update the new tokens in the firestore database
                    await firebase.firestore().collection('users').doc(uid).update({
                        socialAccounts: firebase.firestore.FieldValue.arrayRemove(object)
                    })
                    return object
                }).then((object) => {
                    object.accessToken = newToken
                    return firebase.firestore().collection('users').doc(uid).update({
                        socialAccounts: firebase.firestore.FieldValue.arrayUnion(object)
                    })
                })
                resolve(newToken)
            }
        });
    })
}


/**
 * Get the reddit home feed
 * @param {string} accessToken 
 * @param {string} refreshToken
 * @param {string} uid
 * @param {string} accountIndex
 * @returns {jsonObject} Returns an array of posts from reddit remodeled
 */
const getHomeFeedBest = async (accessToken, refreshToken, uid, accountIndex) => {
    var request = require('request');

    var options = {
        'method': 'GET',
        'url': 'https://oauth.reddit.com/new',
        'headers': {
            'Authorization': 'Bearer ' + accessToken,
            'user-agent': 'Browser:OneFeed:v0.1 (by /u/Geoffrey-Mo)'
        }
    };

    return await new Promise((resolve, reject) => {
        request(options, async (error, response) => {
            var result = []
            if (error) {
                reject(error)
            } else {
                //Try to read the response from reddit. If the response doesn't contain the feed then the reddit token needs to be refreshed
                try {
                    var data = JSON.parse(response.body).data.children
                    data.forEach((element, index) => {
                        result[index] = {
                            "source": "reddit",
                            "created_at": element.data.created_utc,
                            "id_str": element.data.id,
                            "title": element.data.title,
                            "text": element.data.selftext,
                            "url": "https://www.reddit.com" + element.data.permalink,
                            "expanded_url": element.data.url,
                            "user": {
                                "id": "",
                                "id_str": "",
                                "name": element.data.author,
                                "screen_name": element.data.author,
                                "location": "",
                                "description": "",
                                "url": "https://www.reddit.com/user/" + element.data.author,
                                "followers_count": "",
                                "friends_count": "",
                                "listed_count": "",
                                "created_at": "",
                                "favourites_count": "",
                                "profile_image_url": "",
                                "profile_image_url_https": "",
                                "following": ""
                            },
                            "retweet_count": "",
                            "favorite_count": element.data.ups,
                            "unfavorite_count": element.data.down,
                            "comment_count": element.data.num_comments,
                            "favorited": "",
                            "retweeted": "",
                            "lang": ""
                        }
                        // Image
                        if (element.data.media) {
                            result[index].media = element.data.media
                        }
                        // Account Index
                        if (accountIndex !== undefined) {
                            result[index].accountIndex = accountIndex
                        }
                    });
                    resolve(result)
                } catch (err) {
                    var newToken = await refreshingToken(refreshToken, uid)
                    if (refreshToken === null) {
                        reject(error)
                    } else {
                        // re-execute the function with the new tokens
                        resolve(getHomeFeedBest(newToken, refreshToken, uid, accountIndex))
                    }
                }
            }
        });
    })
}

/**
 * Publish a new post on reddit
 * @param {string} accessToken 
 * @param {string} refreshToken 
 * @param {string} uid 
 * @param {string} content 
 * @param {string} title 
 * @param {string} subReddit 
 * @param {string} file 
 * @returns {jsonObject} returns the response from reddit
 */
const postNewPost = async (accessToken, refreshToken, uid, content, title, subReddit, file) => {
    var request = require('request')
    const FileType = require('file-type');
    const { Storage } = require('@google-cloud/storage')

    const postToReddit = async (imageUrl) => {

        // If an image is provided as an url link
        var options = undefined
        if (imageUrl) {
            options = {
                'method': 'POST',
                'url': 'https://oauth.reddit.com/api/submit',
                'headers': {
                    'Authorization': 'Bearer ' + accessToken,
                    'user-agent': 'Browser:OneFeed:v0.1 (by /u/Geoffrey-Mo)'
                },
                formData: {
                    'sr': 'r/' + subReddit,
                    'title': title,
                    'text': content,
                    'kind': 'image',
                    'url': imageUrl
                }
            }
        } else {
            options = {
                'method': 'POST',
                'url': 'https://oauth.reddit.com/api/submit',
                'headers': {
                    'Authorization': 'Bearer ' + accessToken,
                    'user-agent': 'Browser:OneFeed:v0.1 (by /u/Geoffrey-Mo)'
                },
                formData: {
                    'sr': 'r/' + subReddit,
                    'title': title,
                    'text': content,
                    'kind': 'self'
                }
            }
        }

        return await new Promise((resolve, reject) => {
            request(options, async (error, response) => {
                if (error) {
                    reject(error)
                } else {
                    //Check if the token is still valid
                    try {
                        resolve(JSON.parse(response.body))
                    } catch (err) {
                        var newToken = await refreshingToken(refreshToken, uid)
                        if (refreshToken === null) {
                            reject(error)
                        } else {
                            resolve(postNewPost(newToken, refreshToken, uid, content, title, subReddit, imageUrl))
                        }
                    }
                }
            });
        })
    }

    // Store temporarly the image into a google cloud platform bucket. The bucket is configured to automatically remove files frequently
    const gc = new Storage()
    const bucket = gc.bucket("gs://onefeedtest-b74c6")
    const fileType = await FileType.fromBuffer(file)

    const theFile = bucket.file(Date.now() + '.' + fileType.ext)
    return await new Promise((resolve, reject) => {
        theFile.save(file, (err) => {
            if (!err) {
                theFile.get(async (err, f, apiResponse) => {
                    if (!err) {
                        theFile.makePublic()
                        var imageUrl = f.metadata.mediaLink
                        resolve(await postToReddit(imageUrl))
                    }
                })
            }
        })
    })
}

exports.getAccessTokken = getAccessTokken
exports.getHomeFeedBest = getHomeFeedBest
exports.postNewPost = postNewPost