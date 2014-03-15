/**
 * Licensed Materials - Property of IBM 5724-O03 Copyright IBM Corp. 2013. US
 * Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 * 
 * The Program may contain sample source code or programs, which illustrate
 * programming techniques. You may only copy, modify, and distribute these
 * samples internally. These samples have not been tested under all conditions
 * and are provided to you by IBM without obligation of support of any kind.
 * 
 * IBM PROVIDES THESE SAMPLES "AS IS" SUBJECT TO ANY STATUTORY WARRANTIES THAT
 * CANNOT BE EXCLUDED. IBM MAKES NO WARRANTIES OR CONDITIONS, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OR CONDITIONS
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT
 * REGARDING THESE SAMPLES OR TECHNICAL SUPPORT, IF ANY.
 */

// The customer data, used by both list and details components
var customersData = [ {
	"id" : "2143",
	"name" : "Samantha Daryn",
	"status" : "Standard",
	"updated" : "07/01/2012",
	"balance" : "$2,350.00",
	"address" : "2001 West Rd.",
	"city" : "Salisbury",
	"phone" : "212-555-9876",
	"recentTransactions" : [ {
		"type" : "Purchase",
		"amount" : "$123.00",
		"location" : "Brookdale",
		"date" : "1/30/2012"
	}, {
		"type" : "Payment",
		"amount" : "$700.00",
		"location" : "Headquarters",
		"date" : "2/13/2012"
	} ]

}, {
	"id" : "2144",
	"name" : "Lucille Suarez",
	"status" : "Preferred",
	"updated" : "11/21/2012",
	"balance" : "$1,050.00",
	"address" : "123 Main St",
	"city" : "Concord",
	"phone" : "303-555-2435",
}, {
	"id" : "2145",
	"name" : "Amar Srivastava",
	"status" : "Inner Circle",
	"updated" : "08/12/2012",
	"balance" : "$7,235.00",
	"address" : "South Mill Pond Ave.",
	"city" : "Sherman Oaks",
	"phone" : "506-555-1212",
}, {
	"id" : "2146",
	"name" : "Ted Amado",
	"status" : "Standard",
	"updated" : "02/14/2012",
	"balance" : "$1,030.00",
	"address" : "44 Center St.",
	"city" : "Sydney",
	"phone" : "303-555-1234",
}, {
	"id" : "2147",
	"name" : "Dan Misawa",
	"status" : "Preferred",
	"updated" : "06/29/2012",
	"balance" : "$1,223.85",
	"address" : "1204 Mountain Boulevard",
	"city" : "Canterbury",
	"phone" : "601-555-8888",
} ];

// The jQuery object that's used for eventing, using bind/trigger
var customerQueue = {};

// Function called by jQuery when DOM has finished loading - display list of customers
$(function() {
	var items = [];
    $.each( customersData, function( key,value ) {
    	  var data ='<div class="customer" data-id="'+value.id+'">'+
  				'	<span class="name">'+value.name+'</span>'+
  				'	<span class="id">'+value.id+'</span>'+
  			    '   <br/><label class="statusLabel">Status:</label>&nbsp;<span class="status">'+value.status+'</span>' +
  				'	<br/><label class="updatedLabel">Updated:</label>&nbsp;<span class="updated">'+value.updated+'</span>'+
  				'	<div class="balance">'+value.balance+'</div>'+
  				'</div>'+
  				'<div class="customerDivider"></div>';
        items.push( data );
      });
	$("#customerList").html(items);

	// Attach a 'click" function on the customer element that calls "trigger", passing the id from the element
	$("#customerList .customer").click(function() {
		$(customerQueue).trigger('customerSelected', $(this).data("id"));
	});

});

