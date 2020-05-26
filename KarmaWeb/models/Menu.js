var mongoose = require('mongoose');
var mongoDB = 'mongodb+srv://xavlee:karmadb@karma-4qqqe.mongodb.net/Karma';
mongoose.connect(mongoDB, { useNewUrlParser: true });
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

var Schema = mongoose.Schema;

var menuSchema = new Schema({
    email : {type : String, required : true, unique : true},
    items : [{
        item : String,
        price : Number,
        id : Number,
        img : { data: String, contentType : String}
    }]
})

// export personSchema as a class called Person
module.exports = mongoose.model('Menu', menuSchema);

menuSchema.methods.standardizeName = function() {
    this.item = this.item.toLowerCase();
    return this.item;
}