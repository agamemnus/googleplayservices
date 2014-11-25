cordova.define("com.flyingsoftgames.googleplayservices.GooglePlayServices", function(require, exports, module) { var argscheck = require ('cordova/argscheck')
var exec      = require ('cordova/exec')

module.exports = function () {
 var exports = {}
 
 exports.initialize = function (init) {
  var success = (typeof init.success != "undefined") ? init.success : function () {}
  var error   = (typeof init.error   != "undefined") ? init.error   : function () {}
  cordova.exec (success, error, "GooglePlayServices", "tryConnect", [])
 }
 
 exports.getPlayerId = function (init) {
  var success = (typeof init.success != "undefined") ? init.success : function () {}
  var error   = (typeof init.error   != "undefined") ? init.error   : function () {}
  cordova.exec (success, error, "GooglePlayServices", "getPlayerId", [])
 }
 
 exports.getAccessToken = function (init) {
  var success = (typeof init.success != "undefined") ? init.success : function () {}
  var error   = (typeof init.error   != "undefined") ? init.error   : function () {}
  cordova.exec (success, error, "GooglePlayServices", "getAccessToken", [])
 }
 
 return exports
} ()

});
