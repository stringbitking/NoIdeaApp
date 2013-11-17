
/*
 * Votes controller
 */

 exports.getRating = function(models) {
    return function(req, res) {
		var suggestionId = req.params.suggestionId;
		var Suggestion = models.Suggestion;
		Suggestion.find({ _id: suggestionId })
			.populate('votes')
			.exec(function(err, suggestions){
				if(!suggestions || suggestions.length == 0) {
					res.json(404, {});
				}
				
				var searchedSuggestion = suggestions[0];
				
				var rating = getRating(searchedSuggestion);
				
				res.json(200, { rating: rating });
			});
	}
}

exports.getVote = function(models) {
    return function(req, res) {
		var suggestionId = req.params.suggestionId;
		var facebookId = req.body.facebookId;
		var Suggestion = models.Suggestion;
		var User = models.User;
		var Vote = models.Vote;
		User.find({ facebookId: facebookId })
			.populate('votes')
			.exec(function(err, users){
				if(!users || users.length == 0) {
					res.json(404, {});
				}
				else {
					var foundUser = users[0];
					var votes = foundUser.votes;
					var voteResult = new Vote();
					
					for(var i = 0; i < votes.length; i++) {
						if(votes[i]._suggestionId == suggestionId) {
							voteResult = votes[i];
							break;
						}
					}
					
					res.json(200, voteResult);
				}
			});
	}
}

exports.createVote = function(models, mongoose) {
    return function(req, res) {
		var suggestionId = req.body.suggestionId;
		var facebookId = req.body.facebookId;
		var points = req.body.points;
		var Suggestion = models.Suggestion;
		var User = models.User;
		var Vote = models.Vote;
		
		User.findOne({ facebookId: facebookId }, function(err, user){
			if(!user) { res.json(404, {}); }
			else {
				// Check if the user has already voted for this suggestion
				Vote.findOne({
					_userId: user._id,
					_suggestionId: suggestionId
				}, function(err, foundVote){
					if(!foundVote) {
						// Create new vote
						var vote = new Vote({
							_userId: user._id,
							_suggestionId: suggestionId,
							points: points
						});
						vote.save(function(err, savedVote){
							if (err) return console.err(err);
							
							user.votes.push(savedVote._id);
							user.save(function(err, savedUser){});
							
							Suggestion.find({ _id: suggestionId }, function(err, suggestions){
								if(!suggestions || suggestions.length == 0) {
									console.log(suggestionId);
									res.json(404, {});
								}
								else {
									var foundSuggestion = suggestions[0];
									foundSuggestion.votes.push(savedVote._id);
									foundSuggestion.save(function(err, savedSuggestion){
										res.json(200, {});
									});
								}
							});
						});	
					}
					else {
						// Modify the found vote
						foundVote.points = points;
						foundVote.save(function(err, savedVote){
							if (err) return console.err(err);
							res.json(200, {});
						});
					}
				});
			}
		});
	}
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



