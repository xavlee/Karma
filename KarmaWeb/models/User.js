//Set up mongoose connection
var mongoose = require('mongoose');
var mongoDB = 'mongodb+srv://xavlee:karmadb@karma-4qqqe.mongodb.net/Karma';
mongoose.connect(mongoDB, { useNewUrlParser: true });
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

var Schema = mongoose.Schema;

var userSchema = new Schema({
    email : {type : String, required : true, unique : true},
    password : {type : String, required : true},
    age : Number,
    firstName : {type : String, required : true},
    lastName : {type : String, required : true},
    follows : [String],
})

// export personSchema as a class called Person
module.exports = mongoose.model('User', userSchema);

userSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}