// Declare all cloud functions related to search.

// Search.
// params: keywords, filter
Parse.Cloud.define("search", function (request, response) {
    let keywords = request.params.keywords
    let lowerPriceRange = request.params.lowerPriceRange
    let upperPriceRange = request.params.upperPriceRange
    let lowerSellerRating = request.params.lowerSellerRating
    let upperSellerRating = request.params.upperSellerRating
    let province = request.params.province
    let city = request.params.city

    console.log("Keywords passed in: " + keywords);
    console.log("lowerPriceRange passed in: " + lowerPriceRange);
    console.log("upperPriceRange passed in: " + upperPriceRange);
    console.log("lowerSellerRating passed in: " + lowerSellerRating);
    console.log("upperSellerRating passed in: " + upperSellerRating);
    console.log("province passed in: " + province);
    console.log("city passed in: " + city);


    var Posting = Parse.Object.extend("Posting");
    var Book = Parse.Object.extend("Book")
    var Poster = Parse.Object.extend("_User")

    // Book Queries
    let bookNameQuery = new Parse.Query(Book)
    bookNameQuery.matches("name", keywords, 'i')

    let bookIsbnQuery = new Parse.Query(Book)
    bookIsbnQuery.matches("isbn", keywords, 'i')

    let bookAuthorQuery = new Parse.Query(Book)
    bookAuthorQuery.matches("author", keywords, 'i')

    let bookPublisherQuery = new Parse.Query(Book)
    bookPublisherQuery.matches("publisher", keywords, 'i')

    let bookQuery = new Parse.Query.or(bookNameQuery, bookIsbnQuery, bookAuthorQuery, bookPublisherQuery);

    // Posting query
    let mainQuery = new Parse.Query(Posting);
    mainQuery.include("book")
    mainQuery.include("poster")
    mainQuery.matchesQuery("book", bookQuery)

    if (province) {
        mainQuery.equalTo("province", province)
    }

    if (city) {
        mainQuery.equalTo("city", city)
    }

    if (lowerPriceRange && upperPriceRange) {
        lowerPrice = Number(lowerPriceRange)
        upperPrice = Number(upperPriceRange)
        mainQuery.greaterThanOrEqualTo("price", lowerPrice)
        mainQuery.lessThanOrEqualTo("price", upperPrice)
    }

    if (lowerSellerRating && upperSellerRating) {
        lowerRating = Number(lowerSellerRating)
        upperRating = Number(upperSellerRating)
        let posterRatingQuery = new Parse.Query(Poster)
        posterRatingQuery.greaterThanOrEqualTo("rating", lowerRating)
        posterRatingQuery.lessThanOrEqualTo("rating", upperRating)

        mainQuery.matchesQuery("poster", posterRatingQuery)
    }

    mainQuery.find({
        success: function (postings) {
            // Postings found. return them back with success response.
            console.log("search response success: ")
            console.log(postings)
            response.success(postings);
        },
        error: function (object, error) {
            console.log("search response error: ")
            console.log(error)
            response.error("Error: " + error.message)
        }
    })
}
)