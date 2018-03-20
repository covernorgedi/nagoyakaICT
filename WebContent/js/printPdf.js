$(document).ready(function() {
	var width = $(window).width();
	$(".sheet").height(width * 1.41421);
	pt2Adjust(width);
});

function pt2Adjust(baseWidth) {
	var ratio = baseWidth / 1050; // A4 210mm×297mm 1050x1485
	$(document).ready(function() {
		var PT2_DEF = [
			[ "data-pt2-width", "pt2Width", "width" ],
			[ "data-pt2-height", "pt2Height", "height" ],
			[ "data-pt2-left", "pt2Left", "left" ],
			[ "data-pt2-top", "pt2Top", "top" ],
			[ "data-pt2-margin", "pt2Margn", "margin" ],
			[ "data-pt2-margin-left", "pt2MarginLeft", "margin-left" ],
			[ "data-pt2-margin-top", "pt2MarginTop", "margin-top" ],
			[ "data-pt2-margin-right", "pt2MarginRight", "margin-right" ],
			[ "data-pt2-margin-bottom", "pt2MarginBottom", "margin-bottom" ],
			[ "data-pt2-padding", "pt2Padding", "padding" ],
			[ "data-pt2-padding-left", "pt2PaddingLeft", "padding-left" ],
			[ "data-pt2-padding-top", "pt2PaddingTop", "padding-top" ],
			[ "data-pt2-padding-right", "pt2PaddingRight", "padding-right" ],
			[ "data-pt2-padding-bottom", "pt2PaddingBottom", "padding-bottom" ],
			[ "data-pt2-font-size", "pt2FontSize", "font-size" ],
			[ "data-pt2-line-height", "pt2LineHeight", "line-height" ],
			[ "data-pt2-letter-spacing", "pt2LetterSpacing", "letter-spacing" ],
			[ "data-pt2-text-indent", "pt2TextIndent", "text-indent" ]
		];
		$(PT2_DEF).each(function() {
			var pt2Keys = this;
			$("[" + pt2Keys[0] + "]").each(function() {
				var $elem = $(this),
					val = $elem.data(pt2Keys[1]);
				$elem.css(pt2Keys[2], (val * ratio).toFixed(5) + "px");
			})
		});
	});
}
