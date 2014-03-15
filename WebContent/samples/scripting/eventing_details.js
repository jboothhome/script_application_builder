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

// Find element in array by "id"
var findById = function(customerArray, id) {
	for (var i = 0; i < customerArray.length; i++) {
		if (customerArray[i].id === ("" + id)) {
			return customerArray[i];
		}
	}
	return null;
};

//Function called by jQuery when DOM has finished loading - bind to "customerSelected" on customerQueue
$(function() {
  $(customerQueue).bind('customerSelected', function(e, id) {
	  // console.log("id: " + id);
	  data = findById(customersData, id);
	  // console.log("data: " + data);
	  if (data) {
   	      var html =
  	        '<div class="customerOverviewLeft">'+
  	        ' <div class="name">'+data.name+'</div>'+
  	        ' <div class="balance">'+data.balance+'</div>'+
  	        ' <div class="phone">'+data.phone+'</div>'+
  	        ' <div class="address">'+data.address+'&nbsp;'+data.city+'</div>'+
  	        '</div>'+
  	        '<div class="customerOverviewRightTop">'+
  	        ' <span class="id">'+data.id+'</span><br/>'+
  	        ' <span class="status">'+data.status+'</span><br/>'+
  	        ' <label class="updatedLabel">Updated:</label>&nbsp;<span class="updated">'+data.updated+'</span>'+
  	        '</div>'+
  	        '<div class="customerOverviewRightBottom">'+
  	        ' <div class="print blue">Print Documents</div>'+
  	        '</div>';
  	      $("#customerOverview").html(html);
	  }
          	      
	  });
});
