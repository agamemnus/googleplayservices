googleplayservices
==================

Easily access the Google Play Services API with Javascript.

Usage
------
Get the player ID string: (at present, it seems to be a 21 character string... store as 24 character to be safe?)
````
window.plugins.GooglePlayServices.getPlayerId ({
 success : function (result) {},
 error   : function (result) {}
})
````
