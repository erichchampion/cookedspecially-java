/**
 * Author Abhishek Kumar
 * anshilabhi1991@gmail.com
 */

$(function() {
		var d = new Date();
		d.setMonth(d.getMonth() - 1);
		$("#fromDate").datepicker({
			dateFormat : "yy-mm-dd",
			maxDate : '0'
		})
		$("#toDate").datepicker({
			dateFormat : "yy-mm-dd",
			maxDate : '0'
		})

		$(".datePicker").datepicker({
			dateFormat : "yy-mm-dd",
			maxDate : '0'
		})
});