$(document).ready(function(){
	setLayOut();
});

$(window).resize(function(){
	setLayOut();
});

function setLayOut(){
	if ($(window).height() > 900){
		$('#pageContainer').height($(window).height());
		$('#copyright').css({'position':'absolute', 'width':'1024px','bottom':0});
	}else{
		$('#pageContainer').height('890');
		$('#copyright').css('position','relative');
	}
}