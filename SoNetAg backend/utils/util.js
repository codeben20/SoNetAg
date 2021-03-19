require('dotenv').config()

const parse = (headers, buffer) => {
    const Busboy = require('busboy');
    return new Promise((resolve, reject) => {
        
        const busboy = new Busboy({ headers: headers });

        let files = {};
        let fields = {};

        busboy.on('file', (fieldname, file, filename, encoding, mimetype) => {
            file.on('data', (data) => {
                files[fieldname] = data;
            });
        }).on('field', (fieldname, val) => {
            fields[fieldname] = val;
        }).on('finish', () => {
            resolve({ files, fields });
        });

        busboy.end(buffer);
    });
}

exports.parse = parse