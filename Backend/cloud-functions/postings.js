// Define functions related to Postings here

const axios = require('axios');

// Get Postings
Parse.Cloud.define("getPostings", function (request, response) {
  var Posting = Parse.Object.extend("Posting");
  var query = new Parse.Query(Posting);
  query.descending("createdAt");
  query.include("book");
  query.include("poster");
  query.find().then(function (results) {
    console.log(results);
    response.success(results);
  }, function (error) {
    response.error("Could not fetch Postings");
  });
});

// Get Posting by objectId
Parse.Cloud.define("getPostingById", function (request, response) {
  let postingId = request.params.postingId

  let Posting = Parse.Object.extend("Posting")
  let query = new Parse.Query(Posting)
  query.include("book");
  query.include("poster");
  query.get(postingId, {
    success: function (posting) {
      // Posting found. return it back with success response.
      response.success(posting)
    },
    error: function (object, error) {
      console.log(error)
      response.error("Error: " + error.message)
    }
  })
}
)

// Create a Posting
Parse.Cloud.define("createPosting", function (request, response) {

  let isbn = request.params.isbn
  console.log('isbn is: ' + isbn)

  let edition = request.params.edition
  console.log('edition is: ' + edition)

  var isbnService = require('node-isbn');

  isbnService.resolve(isbn, function (err, bookData) {
    if (err) {
      console.log('Book not found', err);
      response.error("Could not find any book with ISBN: " + isbn)
    } else {
      console.log('Book found %j', bookData);

      // .... Play with book data here
      if (bookData.title == null) {
        console.log("No Book found.")
        response.error("No Book found with ISBN " + isbn)
      } else {
        console.log("book found")

        let Book = Parse.Object.extend("Book");
        console.log("now creating book")
        let book = new Book();

        console.log("now setting title to book.")
        console.log("book title is: " + bookData.title)
        book.set("name", bookData.title)
        book.set("nameLong", bookData.title)

        console.log("now setting author to book.")
        book.set("author", "N/A")

        if (bookData.authors) {
          if (bookData.authors.length > 0) {
            book.set("author", bookData.authors[0])
          }
        }

        console.log("now setting edition to book.")
        book.set("edition", edition)

        console.log("now setting publisher text to book.")
        if (bookData.publisher) {
          book.set("publisher", bookData.publisher + ": " + bookData.publishedDate)
        } else {
          book.set("publisher", "N/A")
        }

        console.log("now setting summary to book.")
        if (bookData.description) {
          book.set("summary", bookData.description)
        } else {
          book.set("summary", "N/A")
        }

        // Set book ISBN
        book.set("isbn", isbn)

        book.save(null, {
          success: function (book) {
            // Return the posting if the posting is saved.
            console.log('Book saved.')

            console.log("now creating Posting")
            var Posting = Parse.Object.extend("Posting")
            console.log("now creating posting")
            var posting = new Posting();

            console.log("now setting posting data to posting")

            console.log("Poster id is: " + request.params.posterId)
            posting.set('condition', request.params.condition)
            posting.set('city', request.params.city)
            posting.set('province', request.params.province)
            posting.set('price', Number(request.params.price))
            posting.set('notes', request.params.notes)
            let poster = new Parse.User()
            poster.id = request.params.posterId
            posting.set('poster', poster)
            posting.set('book', book)

            console.log("now saving posting")

            posting.save(null, {
              success: function (posting) {
                // Return the posting if the posting is saved.
                console.log('Posting saved.')
                response.success(posting);
              },
              error: function (posting, error) {
                // Execute any logic that should take place if the save fails.
                // error is a Parse.Error with an error code and message.
                console.log('Failed to create new posting object, with error code: ' + error.message);
                response.error('Could not post. Please try again later.')
              }
            });

          },
          error: function (book, error) {
            // Execute any logic that should take place if the save fails.
            // error is a Parse.Error with an error code and message.
            console.log('Failed to save Book object, with error code: ' + error.message);
            response.error('Could not post. Please try again later.')
          }
        });

      }
    }
  });

})

// Create a posting
// Parse.Cloud.define("createPosting", function (request, response) {

//   let isbn = request.params.isbn
//   console.log('isbn is: ' + isbn)

//   axios.get('http://isbndb.com/api/v2/json/3VSF9W4A/book/' + isbn)
//     .then(resp => {
//       console.log("ISBN response: ")
//       console.log(resp.data);
//       console.log(JSON.stringify(resp.data));

//       if (resp.data.error) {
//         response.error("No Book Found. Error: " + resp.data.error);
//       } else if (resp.data.data) {
//         let bookData = resp.data.data[0]

//         // .... Play with book data here
//         if (bookData.title == null) {
//           console.log("No Book found.")
//           response.error("No Book found with ISBN " + isbn)
//         } else {

//           console.log("book found")

//           console.log("now creating Book")
//           let Book = Parse.Object.extend("Book");
//           console.log("now creating book")
//           let book = new Book();

//           console.log("now setting title to book.")
//           console.log("book title is: " + bookData.title)
//           book.set("name", bookData.title)

//           if (bookData.title_long) {
//             book.set("nameLong", bookData.title_long)
//           } else {
//             book.set("nameLong", bookData.title)
//           }

//           console.log("now setting author to book.")

//           book.set("author", "N/A")

//           if (bookData.author_data) {
//             if (bookData.author_data.length > 0) {
//               book.set("author", bookData.author_data[0].name)
//             }
//           }

//           console.log("now setting edition to book.")
//           if (bookData.edition_info) {
//             book.set("edition", bookData.edition_info)
//           } else {
//             book.set("edition", "N/A")
//           }

//           console.log("now setting publisher text to book.")
//           if (bookData.publisher_text) {
//             book.set("publisher", bookData.publisher_text)
//           } else {
//             book.set("publisher", bookData.publisher_name)
//           }

//           console.log("now setting summary to book.")
//           if (bookData.summary) {
//             book.set("summary", bookData.summary)
//           } else {
//             book.set("summary", "N/A")
//           }

//           // Set book ISBN
//           book.set("isbn", isbn)

//           book.save(null, {
//             success: function (book) {
//               // Return the posting if the posting is saved.
//               console.log('Book saved.')

//               console.log("now creating Posting")
//               var Posting = Parse.Object.extend("Posting")
//               console.log("now creating posting")
//               var posting = new Posting();

//               console.log("now setting posting data to posting")

//               console.log("Poster id is: " + request.params.posterId)
//               posting.set('condition', request.params.condition)
//               posting.set('city', request.params.city)
//               posting.set('province', request.params.province)
//               posting.set('price', request.params.price)
//               posting.set('notes', request.params.notes)
//               let poster = new Parse.User()
//               poster.id = request.params.posterId
//               posting.set('poster', poster)
//               posting.set('book', book)

//               console.log("now saving posting")

//               posting.save(null, {
//                 success: function (posting) {
//                   // Return the posting if the posting is saved.
//                   console.log('Posting saved.')
//                   response.success(posting);
//                 },
//                 error: function (posting, error) {
//                   // Execute any logic that should take place if the save fails.
//                   // error is a Parse.Error with an error code and message.
//                   console.log('Failed to create new posting object, with error code: ' + error.message);
//                   response.error('Could not post. Please try again later.')
//                 }
//               });

//             },
//             error: function (book, error) {
//               // Execute any logic that should take place if the save fails.
//               // error is a Parse.Error with an error code and message.
//               console.log('Failed to save Book object, with error code: ' + error.message);
//               response.error('Could not post. Please try again later.')
//             }
//           });


//         }
//       } else {
//         response.error("Could not find a book with ISBN " + isbn)
//       }


//     })
//     .catch(error => {
//       console.log(error);

//       console.log("ISBN error: ")
//       console.log(error)
//       // .... Handle errors here
//       if (error.error) {
//         response.error(error.error)
//       }
//     });

// })
