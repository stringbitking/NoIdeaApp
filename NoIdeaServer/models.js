module.exports = function(mongoose) {

	var ObjectId = mongoose.Types.ObjectId;

	var suggestionSchema = new mongoose.Schema({
		title: String,
		description: String,
		image: String,
		flags: { type: Number, default: 0 },
		_category: { type: mongoose.Schema.Types.ObjectId, ref: 'Category' },
		_author: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
		votes: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Vote' }],
		flaggedBy: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }]
	});

	var categorySchema = new mongoose.Schema({
		name: String,
		verb: String,
		suggestions: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Suggestion' }]
	});
	
	var userSchema = new mongoose.Schema({
		username: String,
		facebookId: String,
		points: { type: Number, default: 0 },
		favouriteSuggestions: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Suggestion' }],
		votes: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Vote' }]
	});
	
	var voteSchema = new mongoose.Schema({
		_userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
		_suggestionId: { type: mongoose.Schema.Types.ObjectId, ref: 'Suggestion' },
		points: { type: Number, default: 0 }
	});
	
	var Suggestion = mongoose.model('Suggestion', suggestionSchema);
	var Category = mongoose.model('Category', categorySchema);
	var User = mongoose.model('User', userSchema);
	var Vote = mongoose.model('Vote', voteSchema);

	var models = {
	  Category: Category,
	  Suggestion: Suggestion,
	  User: User,
	  Vote: Vote
	};

	return models;
	
}