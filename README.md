googleplayservices
==================

Easily access the Google Play Services API with Javascript.

Usage / Function List
----------------------

``initialize``: Initialize and potentially ask the user to authorize and log in.
````
window.plugins.GooglePlayServices.initialize ({
 success : function (result) {},
 error   : function (result) {}
})
````

``getPlayerId``: Get the player ID string.
````
window.plugins.GooglePlayServices.getPlayerId ({
 success : function (result) {},
 error   : function (result) {}
})
````
Notes:

1) At present, this seems to be a 21 character string... in a DB, perhaps store it as a maximum 24 character string to be safe.
