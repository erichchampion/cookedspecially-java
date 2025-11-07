/**
 * Author Name :- Abhishek Kumar
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

angular.module('myApp').factory('Excel',['$window', function($window){
    var _uri='data:application/vnd.ms-excel;base64,',
    	_template='<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
        _base64=function(s){return $window.btoa(unescape(encodeURIComponent(s)));},
        _format=function(s,c){return s.replace(/{(\w+)}/g,function(m,p){return c[p];})};
      
    var _datenum = function (v, date1904) {
		if(date1904) v+=1462;
		var epoch = Date.parse(v);
		return (epoch - new Date(Date.UTC(1899, 11, 30))) / (24 * 60 * 60 * 1000);
	};
	
	var excelCellHeaderFormat = {
            v: "",
            t: "s",
            s: {
                fill: {
                    patternType: "none",
                    fgColor: { rgb: "#ffcc00" },
                    bgColor: { rgb: "#ffcc00" }
                },
                font: {
                    name: 'sans-serif',
                    sz: 16,
                    color: { rgb: "red" },
                    bold: true,
                    italic: false,                            
                    underline: false
                },
                alignment: {
                    wrapText: true
                },
                border: {
                    top: { style: "thin", color: { auto: 1} },
                    right: { style: "thin", color: { auto: 1} },
                    bottom: { style: "thin", color: { auto: 1} },
                    left: { style: "thin", color: { auto: 1} }
                }
            }
        };
	
	var excelCellRowFormat = {
            v: "",
            t: "s",
            s: {
                font: {
                    name: 'sans-serif',
                    sz: 12,                            
                    bold: false,
                    italic: false,
                    wrapText: true,
                    underline: false
                },
                alignment: {
                    wrapText: true
                },
                border: {
                    top: { style: "thin", color: { auto: 1} },
                    right: { style: "thin", color: { auto: 1} },
                    bottom: { style: "thin", color: { auto: 1} },
                    left: { style: "thin", color: { auto: 1} }
                },
                numFmt: "0"
            }
        };
	
	var _getSheet = function (data, opts) {
		var ws = {};
		var range = {s: {c:10000000, r:10000000}, e: {c:0, r:0 }};
		var co_s = data.shift();
		for(var R = 0; R != data.length; ++R) {
			for(var C = 0; C != data[R].length; ++C) {
				if(range.s.r > R) range.s.r = R;
				if(range.s.c > C) range.s.c = C;
				if(range.e.r < R) range.e.r = R;
				if(range.e.c < C) range.e.c = C;
				var cell = {};
				if(R==0){
					cell=Object.assign({}, excelCellHeaderFormat);
				}else{
					cell=Object.assign({}, excelCellRowFormat);
				}
				cell.v = data[R][C];
				if(cell.v == null) continue;
				
				
				if(typeof cell.v === 'number'){
					cell.s.numFmt = "0";
					cell.t = 's';
						}
				else if(typeof cell.v === 'boolean') cell.t = 'b';
				else if(cell.v instanceof Date) {
					cell.t = 'n'; cell.z = XLSX.SSF._table[14];
					cell.v = _datenum(cell.v);
				}
				else cell.t = 's';
				
				var cell_ref = XLSX.utils.encode_cell({c:C,r:R});
				ws[cell_ref] = cell;
			}
		}
		//ws['!ref'] = XLSX.utils.encode_range({s: {c:10000000, r:10000000}, e:{c:0, r:0}});
		ws['!cols'] = co_s;
		//ws['!rowBreaks'] = [12,24];
		//ws['!colBreaks'] = [3,6];
		//ws['!pageSetup'] = {scale: '140'};
		if(range.s.c < 10000000) ws['!ref'] = XLSX.utils.encode_range(range);
		return ws;
	};
	
	var _s2ab = function (s) {
		var buf = new ArrayBuffer(s.length);
		var view = new Uint8Array(buf);
		for (var i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
		return buf;
	}
	
	var Workbook = function(fileName){
		if (!(this instanceof Workbook)) return new Workbook();
		this.SheetNames = [];
		this.Sheets = {};
		this.fileName = fileName || "CookedSpecially-Report";
		this.save = function(){
			//var defaultCellStyle =  { font: { name: "Verdana", sz: 11, color: "FF00FF88"}, fill: {fgColor: {rgb: "FFFFAA00"}}};
			var wbout = XLSX.write(this, {bookType:'xlsx', bookSST:false, type: 'binary', compression:true, showGridLines: true});
			saveAs(new Blob([_s2ab(wbout)],{type:"application/octet-stream"}), this.fileName+'.xlsx');
			//var wbout = XLSX.write(this, {bookType:'xlsx', bookSST:false, type: 'binary', compression:true, showGridLines: true});
			//var wopts = { bookType:'xlsx', bookSST:false, type:'binary', defaultCellStyle: defaultCellStyle, showGridLines: false};

			//XLSX.writeFile(this, this.fileName+'.xlsx', wopts);
		}
	};
	
	var excelFactory = {}; 
	
	excelFactory.createWorkbook = function(fileName) {
          return new Workbook(fileName);
	}
        
        
    excelFactory.tableToExcel=function(tableId, worksheetName){
        var table=$(tableId),
            ctx={worksheet:worksheetName || 'Sheet',table:table.html()},
            href=_uri+_base64(_format(_template,ctx));
        return href;
    }
    
    excelFactory.dataArrayToExcel=function(data, workBook, sheetName){
    	var wb = workBook || new Workbook();
    	wb.SheetNames.push(sheetName);
    	ws =  _getSheet(data);
    	wb.Sheets[sheetName] = ws;
    	//workBook = wb;
    	ws['!rowBreaks'] = [12,24];
    	ws['!colBreaks'] = [3,6];
    	ws['!pageSetup'] = {scale: '140'};
    	workBook = wb;
    }
    
    return excelFactory;
    
}]);