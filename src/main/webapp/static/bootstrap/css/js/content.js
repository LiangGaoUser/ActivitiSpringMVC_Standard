var licenseManApplyData;
var licenseManEndorsementData;
var taskManageData;

$(function() {
	var windowWidth = window.innerWidth;
	var leftWidth = $("#left").width() + 5;
	$('.right-div').width(windowWidth - leftWidth);
	// console.log(leftWidth)

})
$('.tpl-left-nav-link-list').on(
		'click',
		function() {
			var a = this;
			$('.tpl-left-nav-link-list ').each(
					function(index, ele, self) {
						if (ele != a) {
							$(ele).siblings('.tpl-left-nav-sub-menu').slideUp(
									80);
							$(ele).find('.tpl-left-nav-more-ico').removeClass(
									'tpl-left-nav-more-ico-rotate');
						}
					});
			$(a).siblings('.tpl-left-nav-sub-menu').slideToggle(80).end().find(
					'.tpl-left-nav-more-ico').toggleClass(
					'tpl-left-nav-more-ico-rotate');
			console.log("xiaoqiaoliushuihualala");
		});

$(".slider").on('click', function() {
	var windowWidth = window.innerWidth;
	var leftWidth = $("#left").width() + 15;
	console.log(leftWidth)
	console.log('ok')
	if ($(".tpl-left-nav").css("display") != "none") {
		$(".tpl-left-nav").hide();
		$(".right-div").width(windowWidth - 30);
	} else {
		$(".tpl-left-nav").show();
		$(".right-div").width(windowWidth - leftWidth);
	}
})

$(".thirdMenu").click(function() {
	console.log(123);
	$(".thirdMenu").removeClass("current");
	$(this).addClass("current");

})
