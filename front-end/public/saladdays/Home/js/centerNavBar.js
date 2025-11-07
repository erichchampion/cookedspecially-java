/**
 * Created by amitchowdhary on 9/13/15.
 */
function alignNavBar() {
  if ($('body').width() >= 767) {
    var leftMargin = ($('#bs-example-navbar-collapse-1').width() - $('#nav-bar').width()) / 2;
    $('#nav-bar').css({'margin-left' : leftMargin});
    $('.navbar-header').hide();
  } else {
    var leftMargin = ($('body').width() - $('#title-mobile').width()) / 2;
    $('#title-mobile').css({'left' : leftMargin + 'px'});
    $('.navbar-header').show();
  }
}

function setNavBarWidth() {
  var width = $('body').width();
  $('#bs-example-navbar-collapse-1').width(width);
  alignNavBar();
}

$(window).resize(function(){
  setNavBarWidth();
});

setNavBarWidth();

$(window).load(function () {
  setNavBarWidth();
  if (handleLogoAsPerScrollPosition) {
    handleLogoAsPerScrollPosition();
  }
});

$('html').click(function() {
  $('.mobile-nav').removeClass('active');
});

$('nav.mobile-nav.active').click(function(event){
  event.stopPropagation();
});

$('#mobile-nav').click(function(event) {
  $('.mobile-nav').toggleClass('active');
  event.stopPropagation();
});
