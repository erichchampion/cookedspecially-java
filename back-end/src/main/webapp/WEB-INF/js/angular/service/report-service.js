/**
 * Author Name :- Abhishek Kumar EmailId :- anshilabhi1991@gmail.com
 * 
 */

angular.module('myApp').service('ReportService',['RequestFactory','Constants','UtilService','ChartService','$filter', function(RequestFactory, Constants, UtilService, ChartService, $filter) { 
	
	var self = this;
	
	this.getStyle = function(orgLevel, levelName){
		if(orgLevel=="O"){
			return "<div style='color:red; font-style:italic'>"+levelName+'</div>';
		}
		else if(orgLevel=="R"){
			return "<div style='color:blue; font-style:italic'>"+levelName+'</div>';
		}
		else if(orgLevel=="F"){
			return "<div style='color:green; font-style:italic'>"+levelName+'</div>';
		}
		else
			return "<div style='color:black; font-style:italic'>"+levelName+'</div>';
	} 

	this.init_report_data = function(){
		var data={};
		data.columns=[];
		data.views={};
		//'height': '100%', 
		data.options = {'width': '100%', 'allowHtml' :true, 'showRowNumber': true, title:"Report"};
		data.dataTable = new google.visualization.DataTable(); 
		return data;
	}

	this.formatOrgData = function(employeeDtails){
		org_data = this.init_report_data();
		org_data.options['title'] = "Organization Structure"
			org_data.dataTable.addColumn("string", "Name");
		org_data.dataTable.addColumn("string", "Manager");
		org_data.dataTable.addColumn("string", "ToolTip");

		org_data.dataTable.addRow([{v:'Organisation^ORG-'+employeeDtails.orgId, f:employeeDtails.orgName+''+this.getStyle('O', 'Organisation')},  '', 'Organisation']);
		for(var i = 0, size = employeeDtails.restaurantList.length; i < size ; i++){
			var resturant = employeeDtails.restaurantList[i];
			org_data.dataTable.addRow([{v:resturant.restaurantName+"^REST-"+resturant.restaurantId, f:''+resturant.restaurantName+'('+resturant.city+')'+this.getStyle('R', 'Restaurant')}, 'Organisation^ORG-'+employeeDtails.orgId, 'Restaurant']);
			for(var j = 0, size_f = resturant.fulfillmentCenterList.length; j < size_f ; j++){
				var ffc = resturant.fulfillmentCenterList[j];
				org_data.dataTable.addRow([{v:ffc.fulfillmentCenterName+"^FFC-"+ffc.fulfillmentCenterId, f:''+ffc.fulfillmentCenterName+''+this.getStyle('F', 'FulfillmentCenter')}, resturant.restaurantName+"^REST-"+resturant.restaurantId, 'FFC']);
			}
		}
		return org_data;
	}

	this.formateRow = function(rows, r_data){
		for (var j=0; j<rows.length; j++){
			var row = [];
			for (var i=0; i<rows[j].length; i++){
				if(r_data.dataTable.getColumnType(i)=="datetime")
					row.push(new Date(rows[j][i]));
				else
					row.push(rows[j][i]);
			}
			r_data.dataTable.addRow(row);	
		}
	}

	this.createColumns = function(column, r_data){
		for (var i=0; i<column.length; i++){
			if(column[i].includes(':')){
				var d_a = column[i].split(':');
				r_data.dataTable.addColumn(d_a[1], d_a[0]);
			}else{
				r_data.dataTable.addColumn('string', column[i]);
			}
			r_data.columns.push(i);
		}
	}

	this.formateTabledata = function(data, slice){
		var r_data=this.init_report_data();
		this.createColumns(data.shift(), r_data);
		this.formateRow(data, r_data); 
		angular.forEach(slice, function(s){
			r_data.columns.splice(s, 1);
		});
		r_data.views["setColumns"]= r_data.columns;
		return r_data;
	}

	var charPixel = 10;
	this.formatBlobColumns = function(column){
		var response=[];
		var col=[];
		var col_px=[];
		var col_fmt=[];
		for (var i=0; i<column.length; i++){
			if(column[i].includes(':')){
				var d_a = column[i].split(':');
				col.push(d_a[0]);
				col_fmt.push(d_a[1]);
				if(d_a[1]=="number"){
					col_px.push({wpx: 8*charPixel});
				}else{
					col_px.push({wpx: 12*charPixel});
				}
				
			}else{
				col.push(column[i]);
				col_fmt.push('string');
				col_px.push({wpx: 18*charPixel});
			}
		}
		response.push(col);
		response.push(col_fmt);
		response.push(col_px);
		return response;
	}
	
	this.formateBlobRow = function(rows, header){
		var response = [];
		response.push(header[2]);
		response.push(header[0]);
		for (var j=0; j<rows.length; j++){
			var row = [];
			for (var i=0; i<rows[j].length; i++){
				if(header[1][i]=="datetime")
					row.push(new Date(rows[j][i]));
				else
					row.push(rows[j][i]);
			}
			response.push(row);	
		}
		return response;
	}


	this.formateExcelBlobData = function(data){
       var cols= this.formatBlobColumns(data.shift());
       return this.formateBlobRow(data, cols);
	}

	this.getSectedColumns = function(data, slice, max){
		var f_d_a = [];
		var isCol = true;
		var len = data.length>max?max:data.length;
		for (var i=0; i<len; i++){
			var d_a = [];
			angular.forEach(slice, function(s){
				if(isCol){
					if(data[i][s].includes(':')){
						d_a.push(data[i][s].split(':')[0]);
					}else{
						d_a.push(data[i][s]);
					}
				}else{
					d_a.push(data[i][s]);
				}
			});
			isCol = false;
			f_d_a.push(d_a)
		}
		return f_d_a;
	}

	this.formateSelectedTabledata = function(data, slice, max){
		var r_data=this.init_report_data();
		r_data.dataTable = google.visualization.arrayToDataTable(this.getSectedColumns(data, slice, max++));
		return r_data;
	}
	
	this.formateArrayTabledata = function(data){
		var r_data=this.init_report_data();
		r_data.dataTable = google.visualization.arrayToDataTable(data);
		r_data.dataTable.sort([{column: 0}]);
		return r_data;
	}

	/**
	 * 
	 * This method will shoot http get request to get data of delivery person,
	 * 
	 * @argument ffcId
	 * @return promise
	 * 
	 */
	this.getDeliveryBoyReportData = function(ffcId, fromDate, toDate){
		return RequestFactory.shootRequest(Constants.METHODS.GET, UtilService.addsToURL(UtilService.addPathToURL(Constants.REPORT_URL.FFC_LEVE.GET_DELIVERY_REPORT, [ffcId]), {fromDate: fromDate, toDate: toDate, inputDateTimeZone: new Date().getTimezoneOffset() }), null);
	}

	/**
	 * 
	 * This method will shoot http get request to get listOf InvoiceId delivered
	 * By DeliveryBoy,
	 * 
	 * @argument ffcId, deliveryBoyId
	 * @return promise
	 * 
	 */
	this.getInvoiceListDeliveredBuDB = function(ffcId, deliveryBoyId,  fromDate, toDate){
		return RequestFactory.shootRequest(Constants.METHODS.GET, UtilService.addsToURL(UtilService.addPathToURL(Constants.REPORT_URL.FFC_LEVE.GET_INVOICE_LIST_DELIVERED_BY_DB, [ffcId, deliveryBoyId]), {fromDate: fromDate, todate: toDate, inputDateTimeZone: Constants.DATE.TIMEZONE}), null);
	}

	/**
	 * 
	 * This method will shoot http get request to get top dishes with given count
	 * if count = 0 then it will list all dishes for which order has been placed
	 * 
	 * @argument ffcId, fromDate, toDate, count
	 * @return promise
	 * 
	 */
	this.getTopDishes = function(id,fromDate, toDate, count, level){
		return RequestFactory.shootRequest(Constants.METHODS.GET, UtilService.addsToURL(UtilService.addPathToURL(Constants.REPORT_URL.TOP_DISHES, [id, count, level]), {fromDate: fromDate, toDate: toDate, inputDateTimeZone: Constants.DATE.TIMEZONE}), null);
	}

	/**
	 * 
	 * This method will shoot http get request to get top dishes with given count
	 * if count = 0 then it will list all dishes for which order has been placed
	 * 
	 * @argument restaurantId, fromDate, toDate, count
	 * @return promise
	 * 
	 */
	this.getTopDishesOfREST = function(restaurantId,fromDate, toDate, count){
		return RequestFactory.shootRequest(Constants.METHODS.GET, UtilService.addsToURL(UtilService.addPathToURL(Constants.REPORT_URL.TOP_DISHES, [restaurantId, count, "REST"]), {fromDate: fromDate, todate: toDate, inputDateTimeZone: Constants.DATE.TIMEZONE}), null);
	}

	/**
	 * 
	 * This method will shoot http get request to get top dishes with given count
	 * if count = 0 then it will list all dishes for which order has been placed
	 * 
	 * @argument restaurantId, fromDate, toDate, count
	 * @return promise
	 * 
	 */
	this.getTopDishesOfORG = function(orgId,fromDate, toDate, count){
		return RequestFactory.shootRequest(Constants.METHODS.GET, UtilService.addsToURL(UtilService.addPathToURL(Constants.REPORT_URL.TOP_DISHES, [orgId, count, "ORG"]), {fromDate: fromDate, todate: toDate, inputDateTimeZone: Constants.DATE.TIMEZONE}), null);
	}
	
	this.getCategoryList = function(id, level){
		return RequestFactory.shootRequest(Constants.METHODS.GET, UtilService.addPathToURL(Constants.REPORT_URL.CAT_LIST, [id, level]), null);
	}
	
	this.getTopNDish = function(data, count, requiredFields){
		var result=[];
		var loop=-1;
		var others={};
		var r=[];
		while(loop++<data.length){
			if(loop>count-1){
				angular.forEach(data[loop], function(value, key){
					if(others[key]==null)
						if(typeof(value)=="string")
							others[key]="Others"
						else
							others[key]=value;
					else{
						if(typeof(value)=="number")
							others[key]+=value;
					}
				});
			}else{
				result.push(data[loop]);
			}
		}
		result.push(others);
		var tmp=[];
		angular.forEach(requiredFields, function(k){
			tmp.push(k);
		});
		r.push(tmp);
		angular.forEach(result, function(value, key){
			var tmp1=[];
			angular.forEach(value, function(v, key1){
			angular.forEach(requiredFields, function(k){
				if(k==key1){
					tmp1.push(v);
				}
			});
			});
			if(tmp1.length>0)
				r.push(tmp1);
		});
		return r;
	}
	
	this.groupByData = function(data, groupBy, requiredFields){
		var result=[];
		var tmp=[];
		angular.forEach(requiredFields, function(k){
			tmp.push(k);
		});
		result.push(tmp);
		var r=false;
		angular.forEach(data, function(data_row){
			r=false
			angular.forEach(result, function(result_row){
				if(result_row[0]==data_row[groupBy]){
					r=true;
					angular.forEach(requiredFields, function(field, index){
						if(typeof(result_row[index])=="number"){
							result_row[index]+=data_row[field];
						}
					});
				}
			});
			if(!r){
				var tmp1=[];
				angular.forEach(requiredFields, function(field, index){
					tmp1.push(data_row[field]);
				});
				if(tmp1.length>0){
					result.push(tmp1)
				}
			}
		});
		return result;
	}
	
	this.plotDishReport = function(maxLimit, id, from, to, level, scope){
		angular.element(document.querySelector('chart-div-3')).remove();
		angular.element(document.querySelector('chart-div-4')).remove();
		angular.element(document.querySelector('chart-div-1')).remove();
		$("#chart-div-3").hide();
		$("#chart-div-4").hide();
		$("#chart-grid-1").hide();
		var max=maxLimit || 10 ;
		this.getTopDishes(id, from, to, max, level).then(function(response){
			if(!UtilService.isValidArray(Object.assign([], response.data))){
				alert("No Data Found for given date range, Please select some other range.");
				return;
			}
			scope.data=Object.assign([], response.data);
			var data_g=self.formateTabledata(Object.assign([], response.data), []);
			data_g.chartType=scope.report.type;
			ChartService.plotGraph(data_g, 'chart-grid-411', null);
			scope.export_enable=true;
			$("#chart-div-4").show();

			var jsonData = UtilService.jsonify(scope.data);
			var dataOrderBySale=$filter('orderBy')(jsonData, "-TotalPrice");
			var dataOrderByQuantity=$filter('orderBy')(jsonData, "-Quantity");

			var top10DishByQuantity=self.getTopNDish(dataOrderByQuantity, 10, ["Dish-Name", "Quantity"]);
			var data_c1 = self.formateArrayTabledata(Object.assign([], top10DishByQuantity));
			data_c1.chartType='PieChart';
			data_c1.options={title: 'Top '+max+" Dishes By Quantity",is3D: true, height:400, width:600,
					titleTextStyle: { fontSize: 15, bold: true }}
			ChartService.plotGraph(data_c1, 'chart-grid-311', null);
			$("#chart-div-3").show();

			var top10DishBySale=self.getTopNDish(dataOrderBySale, 10, ["Dish-Name", "TotalPrice"]);
			data_c1 = self.formateArrayTabledata(Object.assign([], top10DishBySale));
			data_c1.chartType='PieChart';
			data_c1.options={title: 'Top '+max+" Dishes By Sale",is3D: true, height:400,width:600,
					titleTextStyle: { fontSize: 15, bold: true }
			}
			ChartService.plotGraph(data_c1, 'chart-grid-312', null);
			

			var dishCategoryBySale=self.groupByData(dataOrderBySale,"Category",  ["Category", "TotalPrice"]);
			var data_c3 = self.formateArrayTabledata(dishCategoryBySale);
			data_c3.chartType='PieChart';
			data_c3.options={title: "Dish Category By Sale",is3D: true, height:400,width:600,
					titleTextStyle: { fontSize: 15, bold: true }}
			ChartService.plotGraph(data_c3, 'chart-grid-321', null);

			var dishCategoryByQuantity=self.groupByData(dataOrderByQuantity,"Category",  ["Category", "Quantity"]);
			var data_c4 = self.formateArrayTabledata(dishCategoryByQuantity);
			data_c4.chartType='PieChart';
			data_c4.options={title: "Dish Category By Quantity",is3D: true, height:400,width:600,
					titleTextStyle: { fontSize: 15, bold: true }}
			ChartService.plotGraph(data_c4, 'chart-grid-322', null);
			
		},function(response){
			alert("Failed to get Top Dishes Data, Please try again!");
		}); 
	}
	
}]);