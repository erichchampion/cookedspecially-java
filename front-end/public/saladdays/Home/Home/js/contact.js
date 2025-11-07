var form = $('.contact-form');
jQuery(function($) {'use strict',
	form.submit(function () {'use strict',
		$this = $(this);
		$.post("sendemail.php", $(".contact-form").serialize(),function(result){
			if(result.type == 'success'){
				$this.prev().text(result.message).fadeIn().delay(3000).fadeOut();
			}
		});
		return false;
	});

});
