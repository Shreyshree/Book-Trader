// Define functions related to Books here

Parse.Cloud.define("getBookByName", function(request, response) {
  var Book = Parse.Object.extend("Book");
  var query = new Parse.Query(Book);
  query.equalTo("name", request.params.name);
  query.find().then(function(results) {
    console.log(results);
    let bookObject = results[0]
    if (bookObject != null) {
        response.success(bookObject);
    } else {
        response.error("No Book was found by the name " + request.params.name)
    }
  }, function(error) {
    response.error("Book lookup failed");
  });
});
