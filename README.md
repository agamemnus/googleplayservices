googleplayservices
==================

Easily access the Google Play Services API with Javascript.

NOTICE: At present, the activity .java file must be modified with the addition of a ``onActivityResult`` function inside your ``CordovaActivity`` class. A sample is provided in ``/sample_activity_file.java``.

Usage / Function List
----------------------

initialize: Initialize, potentially ask the user to authorize and log in, and get and store an access token.
````
window.plugins.GooglePlayServices.initialize ({
 success : function (result) {},
 error   : function (result) {}
})
````

getPlayerId: Get the player ID string. ***NOTE***: At present, this seems to be a 21 character string... in a DB, perhaps store it as a maximum 24 character string to be safe.
````
window.plugins.GooglePlayServices.getPlayerId ({
 success : function (result) {},
 error   : function (result) {}
})
````

getAccessToken: Retrieve the stored access token generated in the initialization.
````
window.plugins.GooglePlayServices.getAccessToken ({
 success : function (result) {},
 error   : function (result) {}
})
````

