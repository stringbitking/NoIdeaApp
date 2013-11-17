
/*
 * Suggestions controller.
 */

 exports.suggestionsByCategory = function(models){
	return function(req, res) {
		var Suggestion = models.Suggestion;
		var minRating = req.body.minRating;
		var maxRating = req.body.maxRating;
		Suggestion.find({ _category: req.params.id })
			.populate('_author')
			.populate('votes')
			.exec(function(err, suggestions) {
				var ratings = [];
				var suggestionInRange = [];
				var resultSuggestions = [];
				for(var i = 0; i < suggestions.length; i++) {
					ratings.push(getRating(suggestions[i]));
				}
				
				for(var j = 0; j < ratings.length; j++) {
					if(minRating <= ratings[j] && ratings[j] <= maxRating && suggestions[j].flags < 3) {
						suggestionInRange.push(suggestions[j]);
					}
				}
			
				if(suggestionInRange.length <= 5) {
					res.json(200, suggestionInRange);
				}
				else {
					var random;
					var chosen = [];
					while(resultSuggestions.length < 5) {
						random = ~~(Math.random()*suggestionInRange.length);
						
						if(!alreadyChosen(chosen, random)){
							chosen.push(random);
							resultSuggestions.push(suggestionInRange[random]);
						}
					}
					
					res.json(200, resultSuggestions);
				}
			});
	}
};

exports.favouritesAll = function(models) {
    return function(req, res) {
		var facebookId = req.params.facebookId;
		var User = models.User;
		User.findOne({ facebookId: facebookId })
			.populate('favouriteSuggestions')
			.exec(function(err, user){
				if(!user) {
					res.json(404, {});
				}
				else {
					var favSuggestionIds = [];
					for(var i = 0; i < user.favouriteSuggestions.length; i++) {
						favSuggestionIds.push(user.favouriteSuggestions[i]._id);
					}
					
					models.Suggestion.find({
						'_id': { $in: favSuggestionIds }
					}).populate('_author').exec(function(err, resultSuggestions){
						res.json(200, resultSuggestions);
					});
				}
			});
	}
}

exports.createSuggestion = function(models, fs) {
    return function(req, res) {
		// get the temporary location of the file
		var fileName = Date.now() + req.files.image.name;
		var tmp_path = req.files.image.path;
		
		moveFile(fs, fileName, tmp_path);
		
		var Category = models.Category;
		var Suggestion = models.Suggestion;
		var User = models.User;
		var newSuggestion = req.body;
		
		Category.find({ _id: newSuggestion.categoryId }, function(err, catResults) {
		
			var categoryToUpdate = catResults[0];
			
			var suggestionEntity = new Suggestion({
				_category: categoryToUpdate._id,
				title: newSuggestion.title,
				description: newSuggestion.description,
				image: fileName,
				_author: newSuggestion.author
			});
			suggestionEntity.save(function(err, suggestion){
				if (err) return console.err(err);
				console.dir(suggestion);
				
				categoryToUpdate.suggestions.push(suggestion._id);
				categoryToUpdate.save(function(err, category){
					res.json(200, { suggestionId: suggestion._id });
				});
			});
			
			User.findOne({ _id: newSuggestion.author }, function(err, user){
				if(err) console.log(err);
				
				if(user) {
					if(!user.points) user.points = 0;
					user.points += 30;
					user.save(function(err, savedUser){});
				}
			});
		});
    };
};

exports.addToFavourites = function(models) {
    return function(req, res) {
	
		var facebookId = req.body.facebookId;
		var suggestionId = req.body.suggestionId;
		var User = models.User;
		var foundUser;
		
		User.find({ facebookId: facebookId }, function(err, usersArray) {
			if(!usersArray || usersArray.length == 0) {
				res.json(404, {});
			} else {
				foundUser = usersArray[0];
				foundUser.favouriteSuggestions.push(suggestionId);
				foundUser.save(function(err, user){
					res.json(200, user);
				});
			}
		});
	}
}

exports.removeFavourites = function(models) {
    return function(req, res) {
	
		var facebookId = req.body.facebookId;
		var suggestionId = req.body.suggestionId;
		var User = models.User;
		var foundUser;
		
		User.find({ facebookId: facebookId }, function(err, usersArray) {
			if(!usersArray || usersArray.length == 0) {
				res.json(404, {});
			} else {
				foundUser = usersArray[0];
				
				for(var i = 0; i < foundUser.favouriteSuggestions.length; i++) {
					if(foundUser.favouriteSuggestions[i] == suggestionId) {
						foundUser.favouriteSuggestions.splice(i, 1);
						break;
					}
				}
				
				foundUser.save(function(err, savedUser){
					if(err) console.err(err);
					res.json(200, savedUser);
				});
			}
		});
	}
}

exports.flagSuggestion = function(models) {
    return function(req, res) {
		var suggestionId = req.params.suggestionId;
		var facebookId = req.body.facebookId;
		var Suggestion = models.Suggestion;
		var User = models.User;
		
		Suggestion.findOne({ _id: suggestionId })
			.populate('_author')
			.exec(function(err, suggestion){
				if(err) console.log(err);
				
				if(!suggestion) {
					res.json(404, { message: "Suggestion not found." });
				}
				else {
					if(!suggestion.flags) {
						suggestion.flags = 0;
						suggestion.flaggedBy = [];
					}
					
					suggestion.flags = suggestion.flags + 1;
					if(suggestion.flags >= 3) {
						suggestion.save(function(err, saved){});
						var user = suggestion._author;
						user.points = user.points - 50;
						user.save(function(err, savedUser){
							if(err) console.log(err);
							res.json(200, {});
						});
					}
					else {
						User.findOne({ facebookId: facebookId }, function(err, foundUser){
							if(err) console.log(err);
							if(!foundUser) {
								res.json(404, {});
							}
							else {
								suggestion.flaggedBy.push(foundUser._id);
								suggestion.save(function(err, saved){});
								res.json(200, {});
							}
						});
						
					}
				}
			});
	}
}

exports.flagCheckSuggestion = function(models) {
    return function(req, res) {
		var suggestionId = req.params.suggestionId;
		var facebookId = req.body.facebookId;
		var Suggestion = models.Suggestion;
		var User = models.User;
		
		Suggestion.findOne({ _id: suggestionId })
			.populate('_author')
			.exec(function(err, suggestion){
				if(err) console.log(err);
				console.log(suggestion);
				if(!suggestion) {
					res.json(404, { message: "Suggestion not found." });
				}
				else {
					
				}
			});
	}
}

function moveFile(fs, fileName, tmp_path) {
	// set where the file should actually exists - in this case it is in the "images" directory
	var target_path = './public/images/' + fileName;
	// move the file from the temporary location to the intended location
	fs.rename(tmp_path, target_path, function(err) {
		if (err) throw err;
		// delete the temporary file
		fs.unlink(tmp_path, function() {
			
		});
	});
}

function getRating(suggestion) {
	var votes = suggestion.votes;
	var rating = 0;
	
	if(votes.length > 0) {
		for(var i = 0; i < votes.length; i++) {
			rating += votes[i].points;
		}
		
		rating = rating / votes.length;
	}
	
	return rating;
}
 
 function alreadyChosen(chosen, element) {
	if(!chosen || chosen.length == 0) {
		return false;
	}
	
	for(var i = 0; i < chosen.length; i++) {
		if(chosen[i] == element) {
			return true;
		}
	}
	
	return false;
}
 
 
 