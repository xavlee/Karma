//Set up mongoose connection
var mongoose = require('mongoose');
var mongoDB = 'mongodb+srv://xavlee:karmadb@karma-4qqqe.mongodb.net/Karma';
mongoose.connect(mongoDB, { useNewUrlParser: true });
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

var Schema = mongoose.Schema;

var orderSchema = new Schema({
    vendor : {type : String, required : true},
    user : {type : String, required : true},
    timestamp : Date,
    items : [{
        item : String,
        price : Number,
        quantity : Number,
    }],
    total : Number,
    code : String,
    completed : Boolean,
}, { timestamps: true });

// export personSchema as a class called Person
module.exports = mongoose.model('Order', orderSchema);

orderSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}
