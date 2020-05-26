var mongoose = require('mongoose');
var mongoDB = 'mongodb+srv://xavlee:karmadb@karma-4qqqe.mongodb.net/Karma';
mongoose.connect(mongoDB, { useNewUrlParser: true });
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

var Schema = mongoose.Schema;

var stripeSchema = new Schema({
    email : {type : String, required : true, unique : true},
    account : String,
})

// export personSchema as a class called Person
module.exports = mongoose.model('Stripe', stripeSchema);