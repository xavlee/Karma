//Set up mongoose connection
var mongoose = require('mongoose');
var mongoDB = 'mongodb+srv://xavlee:karmadb@karma-4qqqe.mongodb.net/Karma';
mongoose.connect(mongoDB, { useNewUrlParser: true });
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

var Schema = mongoose.Schema;

var vendorSchema = new Schema({
    email : {type : String, required : true, unique : true},
    password : {type : String, required : true},
    vendorName : String,
    truckName : String,
    owner : String,
    description : String,
    location : String,
})

// export personSchema as a class called Person
module.exports = mongoose.model('Vendor', vendorSchema);

vendorSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}