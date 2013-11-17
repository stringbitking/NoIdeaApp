
/**
 * Module dependencies.
 */

var express = require('express');
var routes = require('./routes');
var user = require('./routes/user');
var vote = require('./routes/vote');
var category = require('./routes/category');
var suggestion = require('./routes/suggestion');
var http = require('http');
var path = require('path');
var url = require('url');
var mongoose = require('mongoose');
var models = require('./models')(mongoose);
var fs = require('fs');
// var connectionUri = url.parse(process.env.MONGOHQ_URL);
//var connectionUri = 'mongodb://localhost/noideadb';
var connectionUri = "mongodb://stringbitking:101010@dharma.mongohq.com:10060/noideadb";
var db = mongoose.connection;

db.on('error', console.error);

mongoose.connect(connectionUri);

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(express.cookieParser('your secret here'));
app.use(express.bodyParser({uploadDir:'./uploads'}));
app.use(express.session());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', routes.index);

// Categories controller
app.get('/categories/all', category.categorieslist(models));
app.get('/category/:id', category.getCategory(models));

// Users controller
app.post('/favourites/check', user.favouritesCheck(models));
app.post('/users/:facebookId', user.getOrCreateUser(models));
app.post('/users/points/:facebookId', user.addPoints(models));

// Suggestions controller
app.get('/favourites/:facebookId', suggestion.favouritesAll(models));
app.post('/suggestions/category/:id', suggestion.suggestionsByCategory(models));
app.post('/suggestions/create', suggestion.createSuggestion(models, fs));
app.post('/favourites/add', suggestion.addToFavourites(models));
app.post('/favourites/remove', suggestion.removeFavourites(models));
app.post('/flag/:suggestionId', suggestion.flagSuggestion(models));
app.post('/flag/check/:suggestionId', suggestion.flagCheckSuggestion(models));

// Votes controller
app.get('/rating/:suggestionId', vote.getRating(models));
app.post('/vote/:suggestionId', vote.getVote(models));
app.post('/votes/create', vote.createVote(models));



http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});

//dbInit();

function dbInit() {
	db.once('open', function() {
	
		var Category = models.Category;
		var Suggestion = models.Suggestion;
		var User = models.User;
		var Vote = models.Vote;
		
		var superadmin = new User({
			username: 'superadmin'
		});
		superadmin.save(function(err, user) {
		
			if (err) return console.err(err);
			console.dir(user);
			superadmin = user;
			
		});

		var categoryMovies = new Category({
			name: "Movies",
			verb: "watch"
		});
		categoryMovies.save(function(err, category){
			if (err) return console.err(err);
			console.dir(category);
			
			var thorMovie = new Suggestion({
				_category: categoryMovies._id,
				_author: superadmin._id,
				title: "Thor",
				description: "The mighty god with the hammer has come to Earth.",
				image: "bamboo.jpg"
			});
			thorMovie.save(function(err, suggestion){
				if (err) return console.err(err);
				console.dir(suggestion);
				
				categoryMovies.suggestions.push(suggestion._id);
				categoryMovies.save(function(err, category){});
				
			});
			
		});
		
		var categoryBooks = new Category({
			name: "Books",
			verb: "read"
		});
		categoryBooks.save(function(err, category){
			if (err) return console.err(err);
			console.dir(category);
			
			var magicianBook = new Suggestion({
				_category: categoryBooks._id,
				_author: superadmin._id,
				title: "Magician",
				description: "The bridge between two worlds is leading to a clash of cultures.",
				image: "bamboo.jpg"
			});
			magicianBook.save(function(err, suggestion){
				if (err) return console.err(err);
				console.dir(suggestion);
				
				categoryBooks.suggestions.push(suggestion._id);
				categoryBooks.save(function(err, category){});
				
			});
			
		});
		
		var categoryCooking = new Category({
			name: "Cooking",
			verb: "cook"
		});
		categoryCooking.save(function(err, category){
			if (err) return console.err(err);
			console.dir(category);
			
			var soupRecipe = new Suggestion({
				_category: categoryCooking._id,
				_author: superadmin._id,
				title: "Soup",
				description: "Do some cooking on your own : )",
				image: "bamboo.jpg"
			});
			soupRecipe.save(function(err, suggestion){
				if (err) return console.err(err);
				console.dir(suggestion);
				
				categoryCooking.suggestions.push(suggestion._id);
				categoryCooking.save(function(err, category){});
				
			});
			
		});
	  
	});
}
