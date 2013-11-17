
/*
 * Categories controller.
 */

 exports.getCategory = function(models){
	return function(req, res) {
		var Category = models.Category;
		Category.findOne({ _id: req.params.id }, function(err, catResult) {
			res.json(200, catResult);
		});
	}
};
 
 exports.categorieslist = function(models) {
    return function(req, res) {
		res.header("Access-Control-Allow-Origin", "*");
		res.header("Access-Control-Allow-Headers", "X-Requested-With");
		var Category = models.Category;
	
		Category.find({}).exec(function(err, categories) {
			res.json(200, categories);
		});
    };
};