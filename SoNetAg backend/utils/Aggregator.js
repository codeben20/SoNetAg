const Twitter = require('./Twitter')
const Reddit = require('./Reddit')
const Tumblr = require('./Tumblr')

/**
 * Get the feed of all the user account linked
 * @param {object} creds
 * @param {string} uid 
 * @returns {jsonObject} returns an array of post remodeled
 */
const getHomeFeed = async (creds, uid) => {
    const promises = []

    // Prepare all request
    creds.map((objectCreds, index) => {
        if (objectCreds.type === 'twitter') {
            promises.push(Twitter.getHomeFeed(objectCreds.accessToken, objectCreds.accessTokenSecret, index))
        } else if (objectCreds.type === 'reddit') {
            promises.push(Reddit.getHomeFeedBest(objectCreds.accessToken, objectCreds.refreshToken, uid, index))
        } else if (objectCreds.type === 'tumblr') {
            promises.push(Tumblr.getHomeFeed(objectCreds.accessToken, objectCreds.secretToken, index))
        }
    })

    // Execute all request
    var data = await Promise.all(promises)

    data = [].concat(...data)

    // Sort the data base on the post creation time
    data.sort((a, b) =>  b.created_at - a.created_at)

    return data
}

exports.getHomeFeed = getHomeFeed
