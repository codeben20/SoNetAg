var request = require('request');

/**
 * Get the reddit access token
 * @param {string} code 
 * @returns {jsonObject} returns access token
 */
const getAccessTokken = async (code) => {

    var options = {
        'method': 'GET',
        'url': 'https://www.yammer.com/oauth2/access_token.json?client_id=' + process.env.YAMMER_APIKEY + '&client_secret=' + process.env.YAMMER_APISECRETKEY + '&code=' + code
    }

    return await new Promise((resolve, reject) => {
        request(options, (error, response) => {
            if (error) {
                reject(error)
            } else {
                const body = JSON.parse(response.body)

                const data = {
                    access_token: body.access_token.token,
                    network_name: body.user.network_name
                }
                resolve(data)
            }
        });
    })
}

/**
 * Get the yammer home feed
 * @param {string} accessToken 
 * @returns {jsonObject} return the home feed remodeled
 */
const getHomeFeed = async (accessToken) => {

    var options = {
        'method': 'GET',
        'url': 'https://www.yammer.com/api/v1/messages/my_feed.json',
        'headers': {
            'Authorization': 'Bearer ' + accessToken
        }
    }

    return await new Promise((resolve, reject) => {
        request(options, async (error, response) => {
            if (error) {
                reject(error)
            } else {
                const body = JSON.parse(response.body)

                body.source = 'yammer'

                resolve(body)
            }
        })
    })
}

/**
 * Get comments of a yammer post
 * @param {string} accessToken 
 * @param {string} id The id of the post
 * @returns {jsonObject} return the array of comments of the post
 */
const getComments = async (accessToken, id) => {

    var options = {
        'method': 'GET',
        'url': 'https://www.yammer.com/api/v1/messages/in_thread/' + id + '.json',
        'headers': {
            'Authorization': 'Bearer ' + accessToken
        }
    }

    return await new Promise((resolve, reject) => {
        request(options, async (error, response) => {
            if (error) {
                reject(error)
            } else {
                const body = JSON.parse(response.body)

                body.source = 'yammer'

                console.log(body);

                resolve(body)
            }
        })
    })
}

exports.getAccessTokken = getAccessTokken
exports.getHomeFeed = getHomeFeed
exports.getComments = getComments