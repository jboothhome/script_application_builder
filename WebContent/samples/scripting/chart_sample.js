/**
Licensed Materials - Property of IBM 
5724-O03
Copyright IBM Corp. 2013.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

The Program may contain sample source code or programs, which illustrate programming techniques. 
You may only copy, modify, and distribute these samples internally. 
These samples have not been tested under all conditions and are provided to you by IBM without obligation of support of any kind.

IBM PROVIDES THESE SAMPLES "AS IS" SUBJECT TO ANY STATUTORY WARRANTIES THAT CANNOT BE EXCLUDED.
IBM MAKES NO WARRANTIES OR CONDITIONS, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
AND NON-INFRINGEMENT REGARDING THESE SAMPLES OR TECHNICAL SUPPORT, IF ANY.
*/

var defaultData = [ {
	"Month" : "January",
	"Goal" : 4000,
	"Actual" : 1233
}, {
	"Month" : "February",
	"Goal" : 5000,
	"Actual" : 5613
}, {
	"Month" : "March",
	"Goal" : 5000,
	"Actual" : 4302
} ];

var chartSample = {
	displayBarChart : function(id, data) {

		var chart = new cfx.Chart();
		chart.setGallery(cfx.Gallery.Bar);
		chart.getAllSeries().getPointLabels().setVisible(true);

		// set title
		var title = new cfx.TitleDockable();
		title.setText("Goal and Actual Sales by Month");
		chart.getTitles().add(title);

		chart.setDataSource(data);
		chartData = chart.getData();
		chartData.setSeries(2); // two series
		chartData.setPoints(data.length);
		chart.getLegendBox().setVisible(true);
		chart.create(id);
	},

	// 
	displayBarChartFromService : function(id, data) {
		console.log("data: " + JSON.stringify(data));
		// Transform the data to the way jqPlot Charts wants it:
		var jqPlotChartData = jQuery.map(data, function(row, index) {
			var newRow = {};
			newRow["Month"] = row.Month;
			newRow["Goal"] = parseFloat(row.Goal);
			newRow["Actual"] = parseFloat(row.Actual);
			return newRow;
		});
		chartSample.displayBarChart(id, jqPlotChartData);
	}
};

$(document).ready(
		function() {
			// see if "salesData" variable is available, with members for URLs for REST services
			if (typeof salesData == "undefined") {
				// Use default static data
				chartSample.displayBarChart('ChartDiv', defaultData);
			} else {
				// Fetch dynamic JSON sing WEF REST Enabled Data Service REST URL
				$.getJSON(salesData.getSalesReportsURL, {}, function(ajaxData) {
					chartSample.displayBarChartFromService('ChartDiv',
							ajaxData.SalesData.MonthData);
				});
			}
		});
