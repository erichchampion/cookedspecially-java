
function hideLogo() {
  //$('#top-title').show();
  $('#logo-image').height(0);
  $('.logo').css({'padding-top' : '0px', 'padding-bottom' : '0px'});
  $('#bs-example-navbar-collapse-1').css({'padding-top' : '0px', 'padding-bottom' : '0px', 'position' : 'fixed'});
  $('#navigation-bar').css({'margin-top' : '-1px'});
  $('a.section-title').css({'top' : '-50px'});
  //$('#nav-bar').css({'width' : '100%'});
  //$('.navbar-nav > li').css({'float' : 'none !important'});
  //setNavBarWidth();
  isLogoShowing = false;
  //$('#bs-example-navbar-collapse-1').css({'top' : $('#top-title').height() + 'px'});
}

function showLogo() {
  //$('#top-title').hide();
  $('#logo-image').height(125);
  $('#bs-example-navbar-collapse-1').css({'padding-top' : '15px', 'padding-bottom' : '15px', 'position' : 'relative'});
  $('.logo').css({'padding-top' : '20px', 'padding-bottom' : '20px'});
  $('#navigation-bar').css({'margin-top' : '0px'});
  $('a.section-title').css({'top' : '-210px'});
  //$('#nav-bar').css({'width' : 'auto'});
  //$('.navbar-nav > li').css({'float' : 'left'});
  isLogoShowing = true;
}

function handleLogoAsPerScrollPosition() {
  var top = $(window).scrollTop();
  if (top  > 5 ) {
    if ($(document).height() > $(window).height() + 200) {
      hideLogo();
    }
  } else if (top == 0) {
    showLogo();
  }
}

$(window).scroll(function() {
    handleLogoAsPerScrollPosition();
});

$(window).resize(function(){
  $('#bs-example-navbar-collapse-1').width($('body').width());
  alignNavBar();
});

$('#bs-example-navbar-collapse-1').width($('body').width());
