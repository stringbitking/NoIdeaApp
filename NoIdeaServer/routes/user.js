
/*
 * Users controller
 */

exports.favouritesCheck = function(models) {
    return function(req, res) {
		var facebookId = req.body.facebookId;
		var suggestionId = req.body.suggestionId;
		var User = models.User;
		var Suggestion = models.Suggestion;
		
		User.findOne({ facebookId: facebookId }, function(err, user){
				if(!user) {
					res.json(404, {});
				}
				else {
					var result = {};
					
					result.isFavourite = checkIsFavourite(user.favouriteSuggestions, suggestionId);
					
					Suggestion.findOne({ _id: suggestionId }, function(err, foundSuggestion){
						if(err) console.log(err);
						if(!foundSuggestion) {
							res.json(404, {});
						}
						else {
							result.isFlagged = checkIsFlagged(foundSuggestion.flaggedBy, user._id);
							
							res.json(200, result);
						}
					});
				}
			});
	}
}

exports.getOrCreateUser = function(models) {
    return function(req, res) {
	
		var facebookId = req.params.facebookId;
		var User = models.User;
		var foundUser;
		
		User.findOne({ facebookId: facebookId }, function(err, user) {
			if(!user) {
				var newUser = new User({
					facebookId: facebookId,
					username: req.body.name
				});
				newUser.save(function(err, user) {
					if (err) return console.err(err);
					console.dir(user);
					foundUser = user;
					res.json(200, foundUser);
				});
			} else {
				res.json(200, user);
			}
		});
	
	}
}

exports.addPoints = function(models) {
    return function(req, res) {
		var facebookId = req.params.facebookId;
		var points = parseInt(req.body.points);
		var User = models.User;
		User.findOne({ facebookId: facebookId }, function(err, user) {
			if(!user) {
				res.json(404, {});
			}
			else {
				user.points += points;
				user.save(function(err, savedUser) {
					if(err) console.log(err);
					res.json(200, savedUser);
				});
			}
		});
	}
}

function checkIsFavourite(suggestions, suggestionId) {
	for(var i = 0; i < suggestions.length; i++) {
		if(suggestions[i] == suggestionId) {
			return "true";
		}
	}
	
	return "false";
}

function checkIsFlagged(users, userId) {
	for(var i = 0; i < users.length; i++) {
		if(users[i].toString() == userId.toString()) {
			return "true";
		}
	}
	
	return "false";
}

