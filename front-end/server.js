//https://github.com/cloudhead/node-static
/* Run:
 * npm install
 * node server.js
 *
 * Visit:
 * http://localhost:8080/static
 * http://localhost:8080/saladdays
 */

const statik = require('node-static');

//
// Create a node-static server instance to serve the './public' folder
//
const file = new statik.Server('./public');

require('http').createServer(function (request, response) {
    request.addListener('end', function () {
        //
        // Serve files!
        //
        file.serve(request, response);
    }).resume();
}).listen(8080);