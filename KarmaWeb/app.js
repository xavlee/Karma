var express = require('express');
var session = require('express-session')
//Set up mongoose connection
var mongoose = require('mongoose');
var mongoDB = 'mongodb+srv://xavlee:karmadb@karma-4qqqe.mongodb.net/Karma';
mongoose.connect(mongoDB, { useNewUrlParser: true,
	useUnifiedTopology: true,
	useFindAndModify : false});
mongoose.set('useUnifiedTopology', true);
mongoose.set('useCreateIndex', true);
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));
var routes = require('./routes/routes.js');
var app = express();

var multer  = require('multer')
var storage = multer.memoryStorage()
var upload = multer({ storage: storage })

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));
app.use(session({secret: 'thisIsMySecret',
	resave: true,
    saveUninitialized: true}));

// put our routes here
app.get('/', routes.get_login);
app.get('/home', routes.get_home);
app.post('/checklogin', routes.check_login)
app.get('/signup', routes.get_signup);
app.post('/signupvendor',routes.save_registered)
app.get('/configuretruck', routes.configure_truck);
app.get('/uploadmenu', routes.upload_menu);
app.post('/additem', upload.single('menu_pic'), routes.add_item);
app.get('/viewmenu', routes.view_menu);
app.post('/additem', routes.add_item)
app.post('/checkcustomerlogin', routes.check_cust_login);
app.post('/checkvendorlogin', routes.check_vendor_login);
app.post('/signupuser', routes.save_new_user);
app.get('/placeorder', routes.place_order)
app.post('/vendororderslist', routes.vendor_order_list);
app.get('/connect/oauth', routes.stripe_auth);
app.get('/vendorinfo', routes.get_vendor_info);
app.get('/profile', routes.get_vendor_profile);
app.get('/orders', routes.get_orders);
app.post('/graph', routes.get_graph);
app.get('/logout', routes.get_logout);
app.post('/getvendormenu', routes.get_vendor_menu);
app.post('/completeorder', routes.post_complete_order);
app.post('/placeneworder', routes.post_new_order);
app.post('/updateitem',  upload.single('menu_pic'), routes.update_menu_item);
app.post('/userorderslist', routes.get_users_orders);
app.post('/deleteitem', routes.delete_menu_item);
app.post('/getuserprofile', routes.get_orders_profile);
app.post('/getuserfeed', routes.get_orders_feed);
app.post('/addfriend', routes.add_friend);

app.listen(3000, () => {
	console.log('Listening on port 3000');
});