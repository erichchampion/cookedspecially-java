/**
 * Author Abhishek Kumar
 * anshilabhi1991@gmail.com
 */

angular.module('myApp').directive('myDatePicker', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
         link: function (scope, element, attrs, ngModelCtrl) {
            element.datepicker({
            	 autoclose: true,
                 keyboardNavigation: false,
                 todayHighlight: true,
                 format:'yyyy-mm-dd',
                onSelect: function (date) {
                    scope.date = date;
                    scope.$apply();
                }
            });
        }
    };
});

