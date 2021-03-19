const request = require('request')
const OAuth = require('oauth-1.0a')
const crypto = require('crypto')
const querystring = require('querystring');

// build the oauth object
const oauth = OAuth({
    consumer: {
        key: process.env.TUMBLR_APIKEY,
        secret: process.env.TUMBLR_APISECRETKEY,
    },
    signature_method: 'HMAC-SHA1',
    hash_function(base_string, key) {
        return crypto
            .createHmac('sha1', key)
            .update(base_string)
            .digest('base64')
    },
})

/**
 * Execute the oauth step 1
 * @returns Return the response from tumblr
 */
const oauthStep1 = async () => {

    const request_data = {
        url: 'https://www.tumblr.com/oauth/request_token',
        method: 'POST'
    }
    return await new Promise((resolve, reject) => {
        request(
            {
                url: request_data.url,
                method: request_data.method,
                form: oauth.authorize(request_data),
            },
            function (error, response, body) {
                body = querystring.parse(body)
                delete body.oauth_callback_confirmed
                resolve(body)
            }
        )
    })
}

/**
 * Execute the oauth step 2
 * @param {string} oauth_verifier 
 * @param {string} accessToken 
 * @param {string} secret 
 * @returns {jsonObject} Return the response from tumblr with tokens
 */
const oauthStep2 = async (oauth_verifier, accessToken, secret) => {

    const request_data = {
        url: 'https://www.tumblr.com/oauth/access_token?oauth_verifier=' + oauth_verifier,
        method: 'POST',
    }

    const token = {
        key: accessToken,
        secret: secret
    }

    return await new Promise((resolve, reject) => {
        request(
            {
                url: request_data.url,
                method: request_data.method,
                form: oauth.authorize(request_data, token),
            },
            function (error, response, body) {
                body = querystring.parse(body)
                console.log(body);
                resolve(body)
            }
        )
    })
}

/**
 * Get tumblr user info
 * @param {string} accessToken 
 * @param {string} secretToken 
 * @returns {jsonObject} returns the user info
 */
const getUserInfo = async (accessToken, secretToken) => {
    const token = {
        key: accessToken,
        secret: secretToken
    }

    const request_data = {
        url: 'https://api.tumblr.com/v2/user/info',
        method: 'GET'
    }

    const get_request_args = querystring.stringify(oauth.authorize(request_data, token));

    const url = 'https://api.tumblr.com/v2/user/info?' + get_request_args

    return await new Promise((resolve, reject) => {
        request(
            {
                url: url,
                method: request_data.method,
                query: oauth.authorize(request_data, token),
            },
            function (error, response, body) {
                body = JSON.parse(body)
                resolve(body.response.user)
            }
        )
    })
}

/**
 * Get the Tumblr home feed
 * @param {string} accessToken 
 * @param {string} secretToken 
 * @param {string} accountIndex 
 * @returns {jsonObject} returns the user tumblr home feed remodeled
 */
const getHomeFeed = async (accessToken, secretToken, accountIndex) => {

    const token = {
        key: accessToken,
        secret: secretToken
    }

    const request_data = {
        url: 'https://api.tumblr.com/v2/user/dashboard?notes_info=true&limit=' + process.env.NUMBER_MESSAGE_LIMIT,
        method: 'GET'
    }

    const get_request_args = querystring.stringify(oauth.authorize(request_data, token));
    const url = 'https://api.tumblr.com/v2/user/dashboard?' + get_request_args

    return await new Promise((resolve, reject) => {
        request(
            {
                url: url,
                method: request_data.method,
                query: oauth.authorize(request_data, token),
            },
            function (error, response, body) {

                body = JSON.parse(body)
                var posts = body.response.posts
                var result = []
                posts.forEach((element, index) => {
                    result[index] = {
                        "source": "tumblr",
                        "created_at": element.timestamp,
                        "id_str": element.id_string,
                        "title": element.slug,
                        "text": element.body,
                        "url": element.post_url,
                        "expanded_url": element.post_url,
                        "user": {
                            "id": "",
                            "id_str": element.blog.uuid,
                            "name": element.blog.airlinemaps,
                            "screen_name": element.blog.title,
                            "location": "",
                            "description": element.blog.description,
                            "url": element.blog.url,
                            "followers_count": "",
                            "friends_count": "",
                            "listed_count": "",
                            "created_at": "",
                            "favourites_count": "",
                            "profile_image_url": "",
                            "profile_image_url_https": "",
                            "following": element.followed
                        },
                        "retweet_count": "",
                        "favorite_count": "",
                        "unfavorite_count": "",
                        "comment_count": "",
                        "favorited": element.liked,
                        "retweeted": "",
                        "lang": ""
                    }
                    // Content
                    if (element.body) {
                        result[index].text = element.body
                    } else if (element.caption) {
                        result[index].text = element.caption
                    }
                    // Profile picture
                    if (element.trail[0] && element.trail[0].blog && element.trail[0].blog.theme.header_image) {
                        result[index].profile_image_url = element.trail[0].blog.theme.header_image
                        result[index].profile_image_url_https = element.trail[0].blog.theme.header_image
                    }
                    // Image
                    if (element.photos && element.photos[0].original_size.url) {
                        result[index].media = element.photos[0].original_size.url
                    }
                    // Reblog_key
                    if (element.reblog_key) {
                        result[index].reblog_key = element.reblog_key
                    }
                    // Adding tags
                    if (element.tags) {
                        result[index].tags = element.tags
                    }
                    // Account Index
                    if (accountIndex !== undefined) {
                        result[index].accountIndex = accountIndex
                    }
                    // Count Initialisation
                    result[index].favorite_count = 0
                    result[index].reblog_count = 0
                    result[index].posted_count = 0
                    if (element.notes !== undefined) {
                        element.notes.forEach(e => {
                            if (e.type === "like") {
                                result[index].favorite_count = result[index].favorite_count + 1
                            }
                            else if (e.type === "reblog") {
                                result[index].reblog_count = result[index].reblog_count + 1
                            }
                            else if (e.type === "posted") {
                                result[index].posted_count = result[index].posted_count + 1
                            }
                        })
                    }
                })
                resolve(result)
            }
        )
    })
}

/**
 * Create a new post on tumblr
 * @param {string} accessToken 
 * @param {string} secretToken 
 * @param {string} blogId 
 * @param {string} content 
 * @param {string} mediaData 
 * @returns {jsonObject} returns the response from tumblr
 */
const postNewPost = async (accessToken, secretToken, blogId, content, mediaData) => {
    var request = require('request');
    var fs = require('fs');
    const FileType = require('file-type');

    const token = {
        key: accessToken,
        secret: secretToken
    }

    const request_data = {
        url: 'https://api.tumblr.com/v2/blog/' + blogId + '/posts',
        method: 'POST'
    }

    var oauthParams = querystring.stringify(oauth.authorize(request_data, token))

    var url = 'https://api.tumblr.com/v2/blog/' + blogId + '/posts?' + oauthParams

    var fileType = await FileType.fromBuffer(mediaData)

    var jsonObject = undefined
    if (fileType.mime === 'video/mp4') {
        jsonObject = {
            "content": [
                {
                    "type": "text",
                    "text": content
                },
                {
                    "type": "video",
                    "media": [
                        {
                            "type": fileType.mime,
                            "identifier": "data"
                        }
                    ]
                }
            ]
        }
    } else if (fileType.mime === 'image/jpeg' || fileType.mime === 'image/png' || fileType.mime === 'image/gif') {
        jsonObject = {
            "content": [
                {
                    "type": "text",
                    "text": content
                },
                {
                    "type": "image",
                    "media": [
                        {
                            "type": fileType.mime,
                            "identifier": "data"
                        }
                    ]
                }
            ]
        }
    }

    var options = {
        'method': 'POST',
        'url': url,
        'headers': {
        },
        formData: {
            'data': {
                'value': mediaData,
                'options': {
                    'filename': 'image.' + fileType.ext,
                    'contentType': fileType.mime
                }
            },
            'json': JSON.stringify(jsonObject)
        }
    }

    return await new Promise((resolve, reject) => {
        request(options, function (error, response) {
            if (error) throw new Error(error)
            resolve(response.body)
        });
    })
}

/**
 * Post a like on a tumblr post
 * @param {string} accessToken 
 * @param {string} secretToken 
 * @param {string} postId 
 * @param {string} reblogKey 
 * @returns {jsonObject} returns the response from tumblr
 */
const postLike = async (accessToken, secretToken, postId, reblogKey) => {
    const token = {
        key: accessToken,
        secret: secretToken
    }

    const request_data = {
        url: 'https://api.tumblr.com/v2/user/like',
        method: 'POST'
    }

    const get_request_args = querystring.stringify(oauth.authorize(request_data, token));
    const url = 'https://api.tumblr.com/v2/user/like?' + get_request_args

    const body = { "id": postId, "reblog_key": reblogKey }

    const options = {
        url: url,
        method: request_data.method,
        formData: {
            'json': JSON.stringify(body)
        }
    }

    return await new Promise((resolve, reject) => {
        request(options, function (error, response) {
            resolve(JSON.parse(response.body))
        })
    })
}

/**
 * Unlike a tumblr post
 * @param {string} accessToken 
 * @param {string} secretToken 
 * @param {string} postId 
 * @param {string} reblogKey 
 * @returns {jsonObject} returns the tumblr response
 */
const postUnLike = async (accessToken, secretToken, postId, reblogKey) => {
    const token = {
        key: accessToken,
        secret: secretToken
    }

    const request_data = {
        url: 'https://api.tumblr.com/v2/user/unlike',
        method: 'POST'
    }

    const get_request_args = querystring.stringify(oauth.authorize(request_data, token));
    const url = 'https://api.tumblr.com/v2/user/unlike?' + get_request_args

    const body = { "id": postId, "reblog_key": reblogKey }

    const options = {
        url: url,
        method: request_data.method,
        formData: {
            'json': JSON.stringify(body)
        }
    }

    return await new Promise((resolve, reject) => {
        request(options, function (error, response) {
            resolve(JSON.parse(response.body))
        })
    })
}

/*const postComment = async (accessToken, secretToken, postId, reblogKey, content) => {
    const token = {
        key: accessToken,
        secret: secretToken
    }

    const request_data = {
        url: 'https://api.tumblr.com/v2/blog/' + blogId + '/post/reblog',
        method: 'POST'
    }

    var oauthParams = querystring.stringify(oauth.authorize(request_data, token))

    var url = 'https://api.tumblr.com/v2/blog/' + blogId + '/post/reblog?' + oauthParams

    const options = {
        url: url,
        method: request_data.method,
        formData: {
            'json': JSON.stringify(body)
        }
    }

    return await new Promise((resolve, reject) => {
        request(options, function (error, response) {
            resolve(JSON.parse(response.body))
        })
    })
}*/

exports.oauthStep1 = oauthStep1
exports.oauthStep2 = oauthStep2
exports.getUserInfo = getUserInfo
exports.getHomeFeed = getHomeFeed
exports.postNewPost = postNewPost
exports.postLike = postLike
exports.postUnLike = postUnLike
//exports.postComment = postComment