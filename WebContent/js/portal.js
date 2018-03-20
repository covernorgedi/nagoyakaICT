$(document).ready(function() {
	$("#portalCal").monthly({
		mode: "event",
		dataType: "json",
		jsonUrl: $("#baseURI").val() + "/portal/schedule.ajax"
	});

	ajaxTlReload();

	$(window).bind("scroll", function() {
		var scrollHeight = $(document).height(),
			scrollPosition = $(window).height() + $(window).scrollTop();
		if(!tlLoading && (scrollHeight - scrollPosition) / scrollHeight < 0.05) {
			ajaxTlReload();
		}
	});

	sendClear();
	wsConnect();

	$("#newMessageAdvancedDlg").modal();
	$("#newScheduleItemDlg").modal();
	$("#selectUserDlg").css("z-index", "11001");
	$("#selectOfficeDlg").css("z-index", "11011");
	$("#selectTeamDlg").css("z-index", "11021");
	$("#selectOccupationDlg").css("z-index", "11031");
});

function ajaxNewScheduleItem() {
	var newList = [],
		scheduleItemStartDate = $("#scheduleItemStartDate").val(),
		scheduleItemEndDate = $("#scheduleItemEndDate").val(),
		scheduleItemStartTime = $("#scheduleItemStartTime").val(),
		scheduleItemEndTime = $("#scheduleItemEndTime").val(),
		newItem = {
			name: $("#scheduleItem\\.name\\.string").val(),
			startDate: scheduleItemStartDate.substr(0, 10) + " " + scheduleItemStartTime + ":00",
			endDate: scheduleItemEndDate.substr(0, 10) + " " + scheduleItemEndTime + ":00",
			description: $("#scheduleItem\\.description\\.string").val(),
		},
		scheduleItemActor = $("input[name=scheduleItemActor]:checked").val(),
		scheduleItemActorTeamOccupation = $("input[name=scheduleItemActorTeamOccupation]:checked").val(),
		scheduleItemActorOfficeOccupation = $("input[name=scheduleItemActorOfficeOccupation]:checked").val(),
		scheduleItemActorUser = $("input[name=scheduleItemActorUser]:checked").val();

	switch(scheduleItemActor) {
	case "U":
		newItem.actorUserSeq = $("#scheduleItemActorUser").val();
		break;
	case "O":
		newItem.actorOfficeSeq = $("#scheduleItemActorOffice").val();
		newItem.applyTo = {
				occupationList: scheduleItemActorOfficeOccupation
			};
		break;
	default:
		newItem.actorTeamSeq = $("#scheduleItemActorTeam").val();
		newItem.applyTo = {
			occupationList: scheduleItemActorTeamOccupation
		};
		break;
	}
	newList.push(newItem);

	$.ajax({
		url: $("#baseURI").val() + "/portal/schedule.ajax",
		method: "POST",
		dataType: "json",
		data: {
			"newList.minion": JSON.stringify(newList)
		}
	}).done(function(json) {
		$("#portalCal").empty();
		$("#portalCal").monthly({
			mode: "event",
			dataType: "json",
			jsonUrl: $("#baseURI").val() + "/portal/schedule.ajax"
		});
	});

	$("#newScheduleItemDlg").modal("close");
	var now = new Date(),
		ymd = dt2Ymd(now),
		hm = dt2hm(now);
	$("#scheduleItemStartDate").val(ymd);
	$("#scheduleItemEndDate").val(ymd);
	$("#scheduleItemStartTime").val(hm);
	$("#scheduleItemEndTime").val(hm);
	$("#scheduleItem\\.name\\.string").val("");
	$("#scheduleItem\\.description\\.string").val("");
	$("#scheduleItemActor\\.T").prop("checked", true);
	$("#scheduleItemActorTeamOccupation option:selected").prop("selected", false);
	$("#scheduleItemActorOfficeOccupation option:selected").prop("selected", false);
	$("#scheduleItemActorUser option:selected").prop("selected", false);

}

function ajaxTlReload() {
	var uri = ($("#baseURI").val() + "/portal/timeline.ajax"),
		dispKinds = getDisplayTlKinds(),
		conds = {
			seq: tlMinSeq,
			kinds: []
		};
	for(var kind in dispKinds) {
		if(dispKinds[kind]) {
			conds.kinds.push(kind);
		}
	}

	tlLoading = true;
	$.ajax({
		url: uri,
		method: "POST",
		dataType: "json",
		data: {
			conds: conds
		}
	}).done(function(list) {
		tlLoading = false;
		$(list).each(function() {
			formatMessage(this, dispKinds);
		})
	});

	updateDisplayTlKind();
}

var ws = null;

function wsConnect() {
	var uri = ($("#baseURI").val() + "/websocket/timeline")
			.replace("http://", "ws://").replace("https://", "wss://");

	if("WebSocket" in window) {
		ws = new WebSocket(uri);
	} else if("MozWebSocket" in window) {
		ws = new MozWebSocket(uri);
	} else {
		alert("WebSocket is not supported by this browser.");
		return;
	}

	ws.onopen = function() {
		console.log("Info: WebSocket connection opened.");
	};
	ws.onmessage = function(event) {
		wsReceive(event.data);
	};
	ws.onclose = function(event) {
		console.log("Info: WebSocket connection closed, Code: " + event.code
				+ (event.reason == "" ? "" : ", Reason: " + event.reason));
	};
}

function wsDisconnect() {
	if(ws != null) {
		ws.close();
		ws = null;
	}
}

function sendToOffice(officeSeq) {
	var obj = {
		shareWithOffice: "" + officeSeq,
		messageTo: "{}",
		emergency: "N",
		body: $("#newMessageText").val()
	};
	wsSend(obj);

	sendClear();
}

function sendToTeam(teamSeq) {
	var obj = {
		shareWithTeam: "" + teamSeq,
		messageTo: "{}",
		emergency: "N",
		body: $("#newMessageText").val()
	};
	wsSend(obj);

	sendClear();
}

function sendAdvanced() {
	var obj = {
			emergency: $("input[name=newMessageAdvancedEmergency]:checked").val(),
			body: $("#newMessageAdvancedText").val()
		},
		shareWith = $("input[name=newMessageAdvancedShareWith]:checked").val(),
		messageTo = {},
		messageToUser = $("#newMessageAdvancedMessageToUser").val(),
		messageToOccupation = $("#newMessageAdvancedMessageToOccupation").val();

	switch(shareWith) {
	case "U":
		obj.shareWithUser = $("#newMessageAdvancedShareWithUser").val();
		break;
	case "O":
		obj.shareWithOffice = $("#newMessageAdvancedShareWithOffice").val();
		break;
	default:
		obj.shareWithTeam = $("#newMessageAdvancedShareWithTeam").val();
		break;
	}

	if(messageToUser && messageToUser.length > 0) {
		messageTo.userList = [];
		$(messageToUser).each(function() {
			if(this != "") {
				messageTo.userList.push(this);
			}
		});
	}
	if(messageToOccupation && messageToOccupation.length > 0) {
		messageTo.occupationList = [];
		$(messageToOccupation).each(function() {
			if(this != "") {
				messageTo.occupationList.push(this);
			}
		});
	}
	obj.messageTo = JSON.stringify(messageTo);

	wsSend(obj);

	sendClear();
	$("#newMessageAdvancedDlg").modal("close");
}

function sendClear() {
	$("input[name=newMessageAdvancedEmergency]").val(["N"]);
	$("input[name=newMessageAdvancedShareWith]").val(["T"]);
	$("#newMessageAdvancedShareWithUser").val("");
	$("#newMessageAdvancedMessageToUser").val("[]");
	$("#newMessageAdvancedMessageToOccupation").val("[]");
	$("#newMessageText").val("").trigger("autoresize");
	$("#newMessageAdvancedText").val("").trigger("autoresize");
	$("select").material_select();
}

function wsSend(obj) {
	obj.kind = "message";
	obj.sessionId = $("#sessionId").val();
	var json = JSON.stringify(obj);
	ws.send(json);
}

function wsReceive(json) {
	if(!json) return;

	var msgObjList = JSON.parse(json),
		dispKinds = getDisplayTlKinds();
	if(msgObjList) {
		$(msgObjList).each(function() {
			formatMessage(this, dispKinds);
		});
	}
}

var tlMinSeq = Number.MAX_SAFE_INTEGER,
	tlLoading = false;

function formatMessage(msgObj, dispKinds) {
	var baseURI = $("#baseURI").val(),
		msgLi = $("<li />")
			.addClass("collection-item")
			.addClass("avatar")
			.data("seq", msgObj.seq)
			.data("kind", msgObj.kind)
			.data("subKind", msgObj.subKind)
			.data("emergency", msgObj.emergency)
			.data("emgSort", msgObj.emgSort),
		iconImg = $("<img />")
			.attr("src", (msgObj.icon.startsWith("/") ? baseURI : "") + msgObj.icon)
			.attr("alt", "アイコン")
			.addClass("circle"),
		userDiv = $("<div />")
			.addClass("title")
			.text(msgObj.displayName),
		bodyDiv = $("<div />")
			.addClass("body")
			.html(msgObj.body),
		timestampDiv = $("<div />")
			.addClass("timestamp")
			.text(iso8601toYmdhm(msgObj.createdAt));

	msgLi.append(iconImg)
		.append(userDiv)
		.append(bodyDiv)
		.append(timestampDiv);

	var badgesDiv = $("<div />").addClass("tlBadges");
	if(msgObj.emergency == "E") {
		msgLi.addClass("red").addClass("lighten-4");
		var badge = $("<span />").addClass("tlBadgeDark").addClass("red").text("緊急");
		badgesDiv.append(badge);
	} else if(msgObj.emergency == "S") {
		msgLi.addClass("orange").addClass("lighten-4");
		var badge = $("<span />").addClass("tlBadgeDark").addClass("orange").text("至急");
		badgesDiv.append(badge);
	} else if(msgObj.emergency != "N") {
		var name = "";
		switch(msgObj.emergency) {
		case "R": name = "報告"; break;
		case "I": name = "連絡"; break;
		case "C": name = "相談"; break;
		case "W": name = "不急"; break;
		default: break;
		}
		var badge = $("<span />").addClass("tlBadgeLight").addClass("green").addClass("accent-1").text(name);
		badgesDiv.append(badge);
	}
	if(msgObj.messageTo) {
		var messageTo = msgObj.messageTo,
		// var messageTo = JSON.parse(msgObj.messageTo),
			found = false;
		if(messageTo.userList && messageTo.userList.length > 0) {
			var signInUserSeq = $("#signInUser\\.seq").val();
			$(messageTo.userList).each(function() {
				if(this.seq == signInUserSeq) {
					var badge = $("<span />").addClass("tlBadgeLight").addClass("blue").addClass("lighten-4").text("あなたへ");
					badgesDiv.append(badge);
					found = true;
					return false;
				}
			});
		}
		if(!found && messageTo.occupationList && messageTo.occupationList.length > 0) {
			var signInUserOccupationListMinion = $("#signInUser\\.hasOccupation\\.occupationList\\.minion").val(),
				signInUserOccupationList = JSON.parse(signInUserOccupationListMinion);
			$(messageTo.occupationList).each(function() {
				var msgOccupation = this;
				$(signInUserOccupationList).each(function() {
					var signInOccupation = this;
					// ToDo: 送信前に絞るようにしたい
					if(msgOccupation.code == signInOccupation.code) {
						var badge = $("<span />").addClass("tlBadgeLight").addClass("blue").addClass("lighten-4").text("あなたへ");
						badgesDiv.append(badge);
						found = true;
						return false;
					}
				});
				if(found) return false;
			});
		}
	}
	userDiv.after(badgesDiv);

	var hasIns = false,
		$timeline = $("#timeline");
	$timeline.children("li").each(function() {
		var seq = $(this).data("seq"),
			emgSort = $(this).data("emgSort");
		if(msgObj.emgSort >= emgSort && msgObj.seq >= seq) {
			$(this).before(msgLi);
			if(msgObj.seq == seq) {
				$timeline.remove(this);
			}
			hasIns = true;
			return false;
		}
	});
	if(!hasIns) {
		$("#timeline").append(msgLi);
	}

	if(tlMinSeq > msgObj.seq) {
		tlMinSeq = msgObj.seq;
	}

	updateDisplayTlKind();
}

function getDisplayTlKinds() {
	var dispKinds = {};
	$(".tlKindPanel").each(function() {
		var checkbox = $(this).children("input").get(0),
			kind = $(checkbox).attr("id"),
			checked = $(checkbox).prop("checked");
		dispKinds[kind] = checked;
	});

	return dispKinds;
}

function updateDisplayTlKind() {
	var dispKinds = getDisplayTlKinds();

	var dispCnt = 0;
	$("#timeline").children("li").each(function() {
		var kind = $(this).data("kind");
		if(dispKinds[kind]) {
			dispCnt++;
			$(this).show();
		} else {
			$(this).hide();
		}
	});
	if(dispCnt == 0) {
		$("#timelineNone").show();
	} else {
		$("#timelineNone").hide();
	}
}
