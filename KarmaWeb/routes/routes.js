//import db
var User = require('../models/User.js');
var Vendor = require('../models/Vendor.js');
var Order = require('../models/Order.js');
var Menu = require('../models/Menu.js');
var Stripe = require('../models/Stripe.js');
var async = require('async');

const multer = require('multer');
var fs = require('fs');
var ISODate = require('isodate');

//set up stripe
const stripe = require('stripe')('sk_test_JvTBIzpebQiKHtAkKe7G3urH00OvjHYvQb');

//render login page
var getLogin = function(req, res) {
    res.render('login.ejs');
};

//validate login username and password
var checkLogin = function(req, res) {
    email = req.body.email;
    password = req.body.password;
    var query = {email: email}
    Vendor.findOne(query, function(err, vendors){
        if(err) {
            res.type('html').status(200);
            console.log(err);
            res.write('checkLoginError: ' + err);
        }
        console.log(vendors);
        if (vendors == null || password != vendors.password) {
            console.log('Incorrect username or password');
            res.render('login.ejs');
        } else {
            console.log('Email and password are correct');
            req.session.user = req.body.email;
            console.log('signed in as ' + req.session.user);
            res.render('home.ejs', {vendor : vendors});
        }
    });   
};

// checks the vendor login for the android side
var checkVendorLogin = function(req, res) {
    email = req.body.email;
    password = req.body.password;

    var query = {email: email}

    Vendor.findOne(query, function(err, vendors) {
        if(err) {
            console.log(err);
            res.status(200).json({error: err});
            res.end();
        }

        if (vendors == null) {
            console.log('Incorrect username or password');
            res.status(200).json({error: 'user doesnt exist'});
        } else {
            if (password == vendors.password) {
                console.log('Email and password are correct');
                res.status(200).json({response : true});
                res.end();
            }
        }
    });   
}

// sends back if the username and password are valid
var checkCustLogin = function(req, res) {
    email = req.body.email;
    password = req.body.password;
    var query = {email: email}

    User.findOne(query, function(err, users) {
        if (err) {
            res.status(200).json({error: error});
            res.end();
        }

        if (users == null) {
            console.log('email and password are not correct');
            res.status(200).json({response: false});
            res.end();
        } else {
            if (password == users.password) {
                console.log('Email and password are correct');
                res.status(200).json({response: true});
                res.end();
            }
        }
    });
}

var saveCustomerSignup = function(req, res) {
    var newUser = new User({
        email : req.body.email,
        password : req.body.password,
        firstName : req.body.firstName,
        lastName : req.body.lastName,
        follows : [],
    });

    var query = {email: req.body.email};

    User.findOne(query, function(err, users) {
        if (err) {
            res.status(200).json({error: error});
            res.end();
        } 

        if (users == null) {
            newUser.save(function(err, user) {
                if (err) {
                    res.status(200).json({error: error});
                    console.log(err);
                    res.end();
                } else {
                    res.status(200).json({response: true});
                    res.end();
                }
            });
        } else {
            res.status(200).json({error: 'email already registered to account'});
            res.end();
        }
    })
}

//render sign up page
var getSignup = function(req, res) {
	res.render('signup.ejs');
};

//save new vendor in database
var saveSignup = async (req, res) => {
    var newVendor = new Vendor({
        email : req.body.email,
        password: req.body.password,
        vendorName: req.body.vendorName,
        truckName: req.body.truckName,
        owner: req.body.owner,
        description: req.body.description,
        location: req.body.location,
    });

    var newMenu = new Menu({
        email : req.body.email,
        items : []
    });

    var newStripeAcct = new Stripe({
        email : req.body.email,
        account : "",
    });

    const stripe = require('stripe')('sk_test_JvTBIzpebQiKHtAkKe7G3urH00OvjHYvQb');
    try {
        const account = await stripe.accounts.create({
            country: 'US',
            type: 'custom',
            requested_capabilities: ['card_payments', 'transfers'],
        });
    } catch (error) {
        console.log("Error creating Vendor Stripe account");
        throw error
    }
    

    newVendor.save(function(err, vendor) {
        if (err) {
		    res.type('html').status(200);
		    res.write('saveSignUpError: ' + err);
		    console.log(err);
		    res.end();
		} else {
            newMenu.save(function(err, menu) {
                if (err) {
                    res.type('html').status(200);
                    res.write('saveMenuError: ' + err);
                    console.log(err);
                    res.end();
                } else {
                    console.log('menu created for ' + req.body.vendorName);
                    newStripeAcct.save(function(err, account) {
                        if (err) {
                            res.type('html').status(200);
                            res.write('saveStripeAcctError: ' + err);
                            console.log(err);
                            res.end();
                        } else {
                            // display the "successfull created" page using EJS
                            console.log('stripe created for ' + req.body.vendorName);
                            req.session.user = req.body.email;
		                    res.render('registered.ejs', {vendor : newVendor});
                        }
                    });
                }
            });
		}
	} ); 
};

var getConfigureTruck = function(req, res) {
    res.render('configure.ejs');
};

var getUploadMenu = function(req, res) {
    res.render('setmenu.ejs');
};

var getHome = function(req, res) {
    console.log('finding restaurant profile for ' + req.body);
    var query = req.session.user;
    console.log("@@@@@vendor" + query);
    if (query === null) {
        query = req.body.user;
    }

    Vendor.findOne({email : query}, function(err, vend) {
        if (err) {
            console.log(err);
        } else {
            console.log('vendor:' + vend);
            res.render('home.ejs', {vendor : vend});
        }
    })
};

var viewMenu = function(req, res){
    Menu.findOne({email : req.session.user}).then((menu) => {
        console.log("this is menu " + menu);
        console.log(req.session.user);
        if (menu == null || menu.items.length == 0) {
            res.render('setmenu.ejs');
        } else {
            items = [];
            
            async.each(menu.items, function(item, callbck) {
                items.push(item);
                console.log(item.img);
                callbck();
            }, function() {
                res.render('menu.ejs', { menus: items });
            });
        }
		
	}).catch((err) => {
        console.log(err);
    });
};

var addItem = function (req, res) {
    var base64Data = req.file.buffer.toString('base64');
    
    var newMenuItem = {
        item : req.body.item,
        price : req.body.price,
        id : Date.now(),
        img : {data : base64Data,
            contentType : 'image/png',
        }
    };

    Menu.update(
        {email : req.session.user},
        { $addToSet : {items : newMenuItem}}, function(err, menu) {
            if (err) {
                res.type('html').status(200);
                console.log('viewMenu' + err);
                res.write(err);
            } else {
                console.log('successfully added item ' + req.body.item);
                console.log('refreshing menu...');
                Menu.findOne({email : req.session.user}).then((menu) => {
                    console.log("this is menu " + menu);
                    if (menu.items.length == 0) {
                        res.render('setmenu.ejs');
                    } else {
                        items = [];
                        
                        async.each(menu.items, function(item, callbck) {
                            items.push(item);
                            callbck();
                        }, function() {
                            res.render('menu.ejs', { menus: items });
                        });
                    }
                    
                });
            }
        }
    )
};

var updateMenuItem = function(req, res){
    var base64Data = req.file.buffer.toString('base64');

    var newMenuItem = {
        item : req.body.item,
        price : req.body.price,
        id : req.body.id,
        img : {data : base64Data,
            contentType : 'image/png',
        }
    };
    
    
    Menu.findOne({email : req.session.user}).then((menu) => {
        var updated = [];
        var oldMenuItem;
        async.each(menu.items, function(item, callbck) {
            if(item.id == req.body.id){
                oldMenuItem = {
                    item : item.item,
                    price : item.price,
                    id : item.id,
                    img : item.img
                };

                item.item = newMenuItem.item;
                item.price = newMenuItem.price;
                item.img = newMenuItem.img;
            } 
            updated.push(item);
            callbck();
        }, function() {
             Menu.update (
                {email : req.session.user},
                {$pull : {"items" : {"id" : req.body.id}}}, function(err, menu) {
                    if (err) {
                        res.type('html').status(200);
                        console.log('viewMenu' + err);
                        res.write(err);
                    } else {
                        Menu.update (
                            {email : req.session.user},
                            { $addToSet : {items : newMenuItem}}, function(err, menu) {
                                if (err) {
                                    res.type('html').status(200);
                                    console.log('viewMenu' + err);
                                    res.write(err);
                                } else {
                                    res.render('menu.ejs', { menus: updated });
                                }
                            });
                    }
                });
        });
    });


};

var deleteMenuItem = function(req, res) {
    Menu.update (
        {email : req.session.user},
        {$pull : {"items" : {"id" : req.body.id}}}, function(err, menu) {
            if (err) {
                res.type('html').status(200);
                console.log('viewMenu' + err);
                res.write(err);
            } else {
                console.log('successfully deleted item ' + req.body.id);
                console.log('refreshing menu...');
                Menu.findOne({email : req.session.user}).then((menu) => {
                    console.log("this is menu " + menu);
                    if (menu.items.length == 0) {
                        res.render('setmenu.ejs');
                    } else {
                        items = [];
                        async.each(menu.items, function(item, callbck) {
                            items.push(item);
                            callbck();
                        }, function() {
                            res.render('menu.ejs', { menus: items });
                        });
                    }
                    
                });
            }
        });
};

var getVendorOrders = function (req, res) {
    query = {vendor: req.body.email,
            completed: false};

    Order.find(query, function(err, orders) {
        if (err) {
            res.status(200).json({error: err});
            res.end();
        }

        if (orders != null) {
            res.status(200).json({orders: orders});
            res.end();
        } else {
            res.status(200).json({error: 'null orders'});
            res.end();
        }
    });
}

// for vendors to mark that they have made an order and given it to the customer
async function completeOrder(req, res) {
    query = {createdAt: req.body.timestamp};

    const doc = await Order.findOne(query);

    doc.completed = true;

    doc.save(function (err, doc) {
        if (err) {
            console.log(err);
            res.status(200).json({error: 'couldn\'t save completion: ' + err});
            res.end();
        } else {
            if (doc != null) {
                res.status(200).json({completed: true});
                res.end();
            } else {
                res.status(200).json({completed: false});
                res.end();
            }
        }
    });
}

var getVendorMenu = function (req, res) {
    email = req.body.email;

    Menu.findOne({email : req.body.email}, function(err, items) {
        if (err) {
            res.status(200).json({error : err});
            res.end();
        }

        if (items.length == 0) {
            res.status(200).json({error : 'no menu'});
            res.end();
        } else {
            res.status(200).json({'menu' : items});
            res.end();
        }
    });
}


var getVendorInfo = function (req, res) {
    Vendor.find(function(err, allVendors) {
        if(err) {
            console.log(err);
            res.status(200).json({error: err});
            res.end();
        }
        if (allVendors == null) {
            console.log('No vendors');
            res.status(200).json({error: 'No vendors'});
        } else {
            console.log('displaying ' + allVendors.length + ' vendors');
            res.status(200).json({'vendors' : allVendors});
            res.end();
        }
    });   
}

var getUserOrders = function (req, res) {
    console.log('fetching orders');
    query = {user: req.body.email,
            completed: false};

    Order.find(query, function(err, orders) {
        if (err) {
            res.status(200).json({error: err});
            res.end();
        }

        if (orders != null) {
            res.status(200).json({orders: orders});
            res.end();
        } else {
            res.status(200).json({error: 'null orders'});
            res.end();
        }
    });
}

/*
 right now this function will just always add the same order
 and is also a get and not a post so you cant pass data into it
 so that i could just add the test data
 */
var placeOrder = function (req, res) {
    var body = req.body;
    var newOrder = new Order({
        vendor : 'group8@seas.com',
        user : 'amyg@gmail.com',
        items : [{
            item : 'sausage hotdog',
            price : 3.99,
            quantity: 1,
            }],
        total : 199,
        code : 'LMAO',
        completed : false,
    });

    newOrder.save(function(err, order) {
        if (err) {
            console.log(err);
            res.status(200).json({error: err});
            res.end();
        } else {
            console.log('order saved successfully');
            res.status(200).json({response: true});
            res.end();
        }
    });
}

var placeNewOrder = function (req, res) {
        var rawItems = req.body.items;

        function splitItems(stringToSplit) {
            var split = [];
            split = stringToSplit.split("!");
            return split;
        }

        var finalItems = [];

        function splitComps(string, result) {
            var splitArray = string.split(":");
            var currItem = {
                item : splitArray[0],
                price : splitArray[1],
                quantity : splitArray[2],
            }
            result.push(currItem);
        }
        var itemPieces = splitItems(rawItems);
        itemPieces.forEach(element => splitComps(element, finalItems));
        finalItems.pop();

    var newOrder = new Order({
        vendor : req.body.vendor,
        user : req.body.user,
        items : finalItems,
        total : req.body.total,
        code : req.body.code, 
        timestamp : req.body.time,
        completed : false
    });

    newOrder.save(function(err, order) {
        if (err) {
            console.log(err);
            res.status(200).json({error: err});
            res.end();
        } else {
            console.log('order saved successfully');
            res.status(200).json({response: true});
            res.end();
        }
    });
}

var stripeSaveAccount = async (req, res) => {
    const { code, state } = req.query;
    var error;

    // Send the authorization code to Stripe's API.
    stripe.oauth.token({
        grant_type: 'authorization_code',
        code
    }).then(
        (response) => {
        var connected_account_id = response.stripe_user_id;
        saveAccountId(connected_account_id, req.session.user);

        // Render some HTML or redirect to a different page.
        //return res.status(200).json({success: true});
        res.render('home.ejs', {vendor : req.session.user});
        },
        (err) => {
        if (err.type === 'StripeInvalidGrantError') {
            return res.status(400).json({error: 'Invalid authorization code: ' + code});
        } else {
            return res.status(500).json({error: 'An unknown error occurred.'});
        }
    });
};
  
const saveAccountId = (id, vendorEmail) => {
    console.log('email: ' + vendorEmail);
    console.log('id: ' + id);
    Stripe.update(
        {email : vendorEmail},
        { $set: { account : id }}, function(err, stripes) {
            if (err) {
                throw new Error('error in saveAccountId');
            } else {
                console.log('successfully connected stripe account id ' + id);
            }
        }
    )
}

var getVendorProfile = (req, res) => {
    var vendorEmail = req.session.user;

    Vendor.findOne({email : vendorEmail}, function(err, vend) {
        if (err) {
            console.log(err)
        } else {
            console.log("displaying profile for " + vend.vendorName);
            res.render("profile.ejs", {vendor : vend});
        } });
}   

var getOrders = (req, res) => {
    var date = new Date();

    var currYear = date.getFullYear();
    var currMonth = date.getMonth();
    var currDate = date.getUTCDate();

    console.log("in day");
    var hours = []; //first is 5am to 6am
    var ordersPer = {};

    labels = ["5 am", "6 am", "7 am", "8 am", "9 am", "10 am", "11 am", "12 pm",
        "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm", "9 pm",
        "10 pm"]

    for (var i = 4; i < 22; i++) {
        hours.push(i)
    }

    async.forEach(hours, (hour, callbck) => {
        var start = new Date(currYear, currMonth, currDate, hour).toISOString();
        var end = new Date(currYear, currMonth, currDate, hour + 1).toISOString(); 
        Order.find({createdAt: {$gte: ISODate(start), $lt: ISODate(end)}}, (err, orders) => {
            if (err) {
                console.log(err);
            } else {
                ordersPer[hour] = orders.length;
                callbck();
            }
        });  
    }, () => {
        var email = req.session.user;

        Vendor.findOne({email : email}, function(err, vend) {
            if (err) {
                console.log(err);
            } else {
                Order.find({vendor : email, completed : false}, function(err, incOrders) {
                    if (err) {
                        console.log(err);
                    } else {
                        Order.find({vendor : email, completed : true}, function(err, comOrders) {
                            if (err) {
                                console.log(err);
                            } else {

                                var newHours = []

                                for (var i = 4; i < 22; i++) {
                                    newHours.push(ordersPer[i]);
                                }

                                console.log(newHours);
                                console.log(labels);
                                res.render("orders.ejs", {vendorEmail : vend.email, 
                                    vendorName : vend.vendorName, 
                                    incompleteOrders : incOrders,
                                    completeOrders : comOrders,
                                    labels : labels,
                                    dataset : newHours,
                                });
                            }
                        });
                    }
                });
            }
        });
    });
}

var getGraph = (req, res) => {
    timeframe = req.body.timeframe;
    console.log("this is timeframe: " + timeframe);
    var date = new Date();

    var currYear = date.getFullYear();
    var currMonth = date.getMonth() + 1;
    var currDate = date.getUTCDate();

    if (timeframe == "day") {
        console.log("in day");
        var hours = []; //first is 5am to 6am
        var ordersPer = [];

        labels = ["5 am", "6 am", "7 am", "8 am", "9 am", "10 am", "11 am", "12 pm",
            "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm", "9 pm",
            "10 pm"]

        for (var i = 4; i < 22; i++) {
            hours.push(i)
        }

        async.forEach(hours, (hour, callbck) => {
            var start = new Date(currYear, currMonth, currDate, hour).toISOString();
            var end = new Date(currYear, currMonth, currDate, hour + 1).toISOString(); 
            Order.find({
                email : req.session.user,
                created_on: {$gte: ISODate(start), $lt: ISODate(end)}}, (err, orders) => {
                if (err) {
                    console.log(err);
                } else {
                    ordersPer.push(orders.length);
                    console.log(start + "@@@@ " + end);
                    callbck;
                }
            }); 
        }, () => {
            var email = req.session.user;

            Vendor.findOne({email : email}, function(err, vend) {
                if (err) {
                    console.log(err);
                } else {
                    Order.find({vendor : email, completed : false}, function(err, incOrders) {
                        if (err) {
                            console.log(err);
                        } else {
                            Order.find({vendor : email, completed : true}, function(err, comOrders) {
                                if (err) {
                                    console.log(err);
                                } else {
                                    
                                    console.log("rendering orders for " + email);
                                    res.render("orders.ejs", {vendorEmail : vend.email, 
                                        vendorName : vend.vendorName, 
                                        incompleteOrders : incOrders,
                                        completeOrders : comOrders,
                                        labels : labels,
                                        dataset : ordersPer
                                    });
                                }
                            })
                        }
                    })
                }
            })
        });
    } else if (timeframe == "week") {

    } else if (timeframe == "month") {
        labels = ['jan', 'feb', 'mar', 'apr', 'may', 'jun', 'jul', 'aug',
            'sep', 'oct', 'nov', 'dec']
    }
}

var getOrdersProfile = (req, res) => {
    var email = req.body.email;

    var returnOrders = [];

    Order.find({user : email}, function(err, orders) {
        async.forEach(orders, (order, callbck) => {
            User.findOne({email : order.user}, function(err1, user) {
                Vendor.findOne({email : order.vendor}, function(err2, vendor) {
                    var name = user.firstName;
                    var vendorName = vendor.vendorName;
                    var item = order.items[0].item;

                    var orderObject = {
                        name : name,
                        vendorName : vendorName,
                        item : item,
                    }

                    returnOrders.push(orderObject);
                    callbck();
                });
            });
        }, () => {
            res.status(200).json({'orders' : returnOrders});
            res.end();
        });
    });
}

var getOrdersFeed = (req, res) => {
    email = req.body.email;

    var returnOrders = [];

    User.findOne({email : email}, (err1, user) => {
        async.forEach(user.follows, (followUser, callbck1) => {
            Order.find({user : followUser}, function(err2, orders) {
                async.forEach(orders, (order, callbck2) => {

                    User.findOne({email : order.user}, function(err3, user) {
                        if (err3) {
                            console.log("caught an error");
                        }

                        Vendor.findOne({email : order.vendor}, function(err4, vendor) {
                            if (err4) {
                                console.log("caught an error");
                            }

                            var name = user.firstName;
                            var vendorName = vendor.vendorName;
                            var item = order.items[0].item;
        
                            var orderObject = {
                                name : name,
                                vendorName : vendorName,
                                item : item,
                            }
        
                            returnOrders.push(orderObject);
                            callbck2();
                        });
                    });
                }, () => {
                    callbck1();
                });
            });
        }, () => {
            res.status(200).json({'orders' : returnOrders});
            res.end();
        });
    });
}

var addFriend = (req, res) => {
    var user = req.body.user;
    var followUser = req.body.followUser;

    User.updateOne(
        { email : user},
        { $addToSet : { follows : followUser}}, function (err, result) {
        	if (err) {
        		console.log(err);
        	} else {
        		console.log(result);
        	}
        }).exec();
    res.status(200).json
}


var getLogout = (req, res) => {
    req.session.destroy();
    res.redirect('/');
}

var routes = {
    get_login : getLogin,
    check_login : checkLogin,
    get_signup : getSignup,
    save_registered : saveSignup,
    configure_truck : getConfigureTruck,
    upload_menu : getUploadMenu,
    add_item : addItem,
    get_home : getHome,
    view_menu : viewMenu,
    check_cust_login: checkCustLogin,
    check_vendor_login: checkVendorLogin,
    save_new_user: saveCustomerSignup,
    vendor_order_list: getVendorOrders,
    get_vendor_info : getVendorInfo,
    stripe_auth: stripeSaveAccount,
    get_vendor_profile : getVendorProfile,
    place_order: placeOrder,
    get_orders : getOrders,
    get_graph : getGraph,
    get_logout : getLogout,
    get_vendor_menu : getVendorMenu,
    post_complete_order: completeOrder,
    post_new_order : placeNewOrder,
    update_menu_item : updateMenuItem,
    get_users_orders : getUserOrders,
    delete_menu_item : deleteMenuItem,
    get_orders_profile : getOrdersProfile,
    get_orders_feed : getOrdersFeed,
    add_friend : addFriend, 
};

module.exports = routes;