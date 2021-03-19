require('dotenv').config()

const firebase = require('firebase-functions')

const Twitter = require('twitter')

/**
 * Get the twitter home feed
 * @param {string} accessToken 
 * @param {string} accessTokenSecret 
 * @param {string} accountIndex 
 * @returns {jsonObject} returns the twitter home remodeled
 */
const getHomeFeed = async (accessToken, accessTokenSecret, accountIndex) => {

    var apiKey = process.env.TWITTER_APIKEY
    var apiSecretKey = process.env.TWITTER_APISECRETKEY

    var twitter = new Twitter({
        consumer_key: apiKey,
        consumer_secret: apiSecretKey,
        access_token_key: accessToken,
        access_token_secret: accessTokenSecret
    })
    var params = { count: 20, tweet_mode: 'extended' };

    return await new Promise((resolve, reject) => {
        twitter.get('statuses/home_timeline', params, (error, tweets, response) => {
            if (!error) {
                var result = []

                tweets.forEach((element, index) => {

                    //date
                    const created_at = Date.parse(element.created_at);

                    result[index] = {
                        "source": "twitter",
                        "created_at": created_at / 1000,
                        "id_str": element.id_str,
                        "text": element.full_text,
                        "url": element.url,
                        "expanded_url": element.expanded_url,
                        "user": {
                            "id": element.user.id,
                            "id_str": element.user.id_str,
                            "name": element.user.name,
                            "screen_name": element.user.screen_name,
                            "location": element.user.location,
                            "description": element.user.description,
                            "url": element.user.url,
                            "followers_count": element.user.followers_count,
                            "friends_count": element.user.friends_count,
                            "listed_count": element.user.listed_count,
                            "created_at": element.user.created_at,
                            "favourites_count": element.user.favorite_count,
                            "profile_image_url": element.user.profile_image_url,
                            "profile_image_url_https": element.user.profile_image_url_https,
                            "following": element.user.following
                        },
                        "retweet_count": element.retweet_count,
                        "favorite_count": element.favorite_count,
                        "favorited": element.favorited,
                        "retweeted": element.retweeted,
                        "lang": element.lang
                    }
                    result[index].media = typeof element.entities.media !== 'undefined' ? element.entities.media[0].media_url_https : undefined

                    // Account index
                    if (accountIndex !== undefined) {
                        result[index].accountIndex = accountIndex
                    }
                });
                resolve(result)
            } else {
                reject(error)
            }
        })
    }).catch((err) => {
        console.error(err)
    })
}

/**
 * Create a new twitter post
 * @param {string} accessToken 
 * @param {string} accessTokenSecret 
 * @param {string} status 
 * @param {string} mediaData 
 * @returns {jsonObject} returns the twitter response
 */
const postNewPost = async (accessToken, accessTokenSecret, status, mediaData) => {
    const FileType = require('file-type');

    var apiKey = process.env.TWITTER_APIKEY
    var apiSecretKey = process.env.TWITTER_APISECRETKEY

    var client = new Twitter({
        consumer_key: apiKey,
        consumer_secret: apiSecretKey,
        access_token_key: accessToken,
        access_token_secret: accessTokenSecret
    })

    var mediaType
    var mediaSize
    if(mediaData !== undefined){
        mediaType = await FileType.fromBuffer(mediaData)
        mediaSize = Buffer.byteLength(mediaData)
    }

    /**
     * Step 1 of 3: Initialize a media upload
     * @return Promise resolving to String mediaId
     */
    function initUpload() {
        return makePost('media/upload', {
            command: 'INIT',
            total_bytes: mediaSize,
            media_type: mediaType,
        }).then(data => data.media_id_string);
    }

    /**
     * Step 2 of 3: Append file chunk
     * @param String mediaId    Reference to media object being uploaded
     * @return Promise resolving to String mediaId (for chaining)
     */
    function appendUpload(mediaId) {
        return makePost('media/upload', {
            command: 'APPEND',
            media_id: mediaId,
            media: mediaData,
            segment_index: 0
        }).then(data => mediaId);
    }

    /**
     * Step 3 of 3: Finalize upload
     * @param String mediaId   Reference to media
     * @return Promise resolving to mediaId (for chaining)
     */
    function finalizeUpload(mediaId) {
        return makePost('media/upload', {
            command: 'FINALIZE',
            media_id: mediaId
        }).then(data => mediaId);
    }

    /**
     * (Utility function) Send a POST request to the Twitter API
     * @param String endpoint  e.g. 'statuses/upload'
     * @param Object params    Params object to send
     * @return Promise         Rejects if response is error
     */
    function makePost(endpoint, params) {
        return new Promise((resolve, reject) => {
            client.post(endpoint, params, (error, data, response) => {
                if (error) {
                    reject(error);
                } else {
                    resolve(data);
                }
            });
        });
    }

    return await new Promise((resolve, reject) => {
        if (mediaData !== undefined) {
            // eslint-disable-next-line promise/catch-or-return
            initUpload().then(appendUpload).then(finalizeUpload)
                // eslint-disable-next-line promise/always-return
                .then(mediaId => {
                    var params = {
                        status: status,
                        media_ids: mediaId
                    }
                    client.post('statuses/update', params, (error, tweet, response) => {
                        if (!error) {
                            resolve(response)
                        } else {
                            reject(error)
                        }
                    });
                });
        } else {
            client.post('statuses/update', {status: status}, (error, tweet, response) => {
                if (!error) {
                    resolve(response)
                } else {
                    reject(error)
                }
            });
        }

    })
}

/**
 * Like a twitter post
 * @param {string} accessToken 
 * @param {string} accessTokenSecret 
 * @param {string} postId 
 * @returns {jsonObject} returns the twitter response
 */
const postLike = async (accessToken, accessTokenSecret, postId) => {
    var apiKey = process.env.TWITTER_APIKEY
    var apiSecretKey = process.env.TWITTER_APISECRETKEY

    var twitter = new Twitter({
        consumer_key: apiKey,
        consumer_secret: apiSecretKey,
        access_token_key: accessToken,
        access_token_secret: accessTokenSecret
    })

    var params = {
        id: postId
    }
    return await new Promise((resolve, reject) => {
        twitter.post('favorites/create', params, (error, tweet, response) => {
            if (!error) {
                resolve(response)
            } else {
                reject(error)
            }
        });
    })
}

/**
 * Unlike a twitter post
 * @param {string} accessToken 
 * @param {string} accessTokenSecret 
 * @param {string} postId 
 * @returns {jsonObject} returns the twitter response
 */
const postUnlike = async (accessToken, accessTokenSecret, postId) => {
    var apiKey = process.env.TWITTER_APIKEY
    var apiSecretKey = process.env.TWITTER_APISECRETKEY


    var twitter = new Twitter({
        consumer_key: apiKey,
        consumer_secret: apiSecretKey,
        access_token_key: accessToken,
        access_token_secret: accessTokenSecret
    })

    var params = {
        id: postId
    }
    return await new Promise((resolve, reject) => {
        twitter.post('favorites/destroy', params, (error, tweet, response) => {
            if (!error) {
                resolve(response)
            } else {
                reject(error)
            }
        });
    })
}

/**
 * Retweet a twitter post
 * @param {*} accessToken 
 * @param {*} accessTokenSecret 
 * @param {*} postId 
 * @returns {jsonObject} returns the twitter response
 */
const postRetweet = async (accessToken, accessTokenSecret, postId) => {
    var apiKey = process.env.TWITTER_APIKEY
    var apiSecretKey = process.env.TWITTER_APISECRETKEY

    var twitter = new Twitter({
        consumer_key: apiKey,
        consumer_secret: apiSecretKey,
        access_token_key: accessToken,
        access_token_secret: accessTokenSecret
    })

    var params = {
        id: postId
    }
    return await new Promise((resolve, reject) => {
        twitter.post('statuses/retweet', params, (error, tweet, response) => {
            if (!error) {
                resolve(response)
            } else {
                reject(error)
            }
        });
    })
}

/**
 * Unretweet a twitter post
 * @param {string} accessToken 
 * @param {string} accessTokenSecret 
 * @param {string} postId 
 * @returns {jsonObject} returns the twitter response
 */
const postUnRetweet = async (accessToken, accessTokenSecret, postId) => {
    var apiKey = process.env.TWITTER_APIKEY
    var apiSecretKey = process.env.TWITTER_APISECRETKEY


    var twitter = new Twitter({
        consumer_key: apiKey,
        consumer_secret: apiSecretKey,
        access_token_key: accessToken,
        access_token_secret: accessTokenSecret
    })

    var params = {
        id: postId
    }
    return await new Promise((resolve, reject) => {
        twitter.post('statuses/unretweet', params, (error, tweet, response) => {
            if (!error) {
                resolve(response)
            } else {
                reject(error)
            }
        });
    })
}

exports.getHomeFeed = getHomeFeed
exports.postNewPost = postNewPost
exports.postLike = postLike
exports.postUnlike = postUnlike
exports.postRetweet = postRetweet
exports.postUnRetweet = postUnRetweet