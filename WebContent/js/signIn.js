$(document).ready(function() {
	$("input").on("keydown", function(e) {
		if((e.which && e.which === 13) || (e.keyCode && e.keyCode === 13)) {
			return false;
		} else {
			return true;
		}
	});

	// sha256(""); // 時間がかかるので最初に初期化 → 効果なし
});

function hashPwd() {
	var rawPwd = $("#signInPwd").val(),
		hashPwd = sha256("hXm9" + rawPwd + "p]ed");
	$("#signInPwd").val(hashPwd);
	$("#signInBtn2").click();
}
