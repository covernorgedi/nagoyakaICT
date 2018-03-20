;(function($) {
	var PLUGIN_NAME = "thingLister",
		DEFAULTS = {
			parentTag: "ul",
			rowTag: "li",
			renderRow: function(rowObj, tgtID) {
				var displayName = rowObj.displayName && rowObj.displayName != "" ? rowObj.displayName : rowObj.name.string;
				return $("<li />").attr("id", tgtID + "." + rowObj.seq).addClass("collection-item").html(displayName);
			},
			renderEmptyRow: function() {
				return $("<li />").addClass("collection-item grey lighten-3").html("なし");
			}
		},
		METHODS = {
			init: function(options) {
				var settings = $.extend({}, DEFAULTS, options);
				this.each(function() {
					$(this).data(PLUGIN_NAME + ".settings", settings);
				});

				return METHODS.update.apply(this, Array.prototype.slice.call(arguments, 1));
			},
			update: function($prvRowList) {
				return this.each(function() {
					var $tgtParent = $(this),
						settings = $(this).data(PLUGIN_NAME + ".settings");
					if(settings.beforeUpdate) {
						settings.beforeUpdate($tgtParent);
					}

					var $tgtHdn = $.thingLister.getListHiddenWithin2($tgtParent),
						tgtID = $tgtHdn.attr("id"),
						tgtJson = $tgtHdn.val(),
						tgtList = JSON.parse(tgtJson);

					$tgtParent.empty();
					if(!tgtList || tgtList.length == 0) {
						var $emptyRow = settings.renderEmptyRow();
						$tgtParent.append($emptyRow);
					} else {
						for(var ridx = 0; ridx < tgtList.length; ridx++) {
							if(!tgtList[ridx] || Object.keys(tgtList[ridx]).length == 0) break;
							var $newRow,
								rowObj = tgtList[ridx];
							// var $newRow,
							//	rowObj = tgtList[ridx] ? tgtList[ridx] : {}; // 何も入力がないとJSONにnullが入る
							if($prvRowList && $prvRowList.length > ridx) {
								$newRow = settings.renderRow(rowObj, tgtID, ridx + 1, $prvRowList[ridx]);
							} else {
								$newRow = settings.renderRow(rowObj, tgtID, ridx + 1);
							}
							$tgtParent.append($newRow);
						}
					}

					if(settings.afterUpdate) {
						settings.afterUpdate($tgtParent);
					}
				});
			}
		};

	$.fn[PLUGIN_NAME] = function(method) {
		if(METHODS[method]) {
			return METHODS[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if(typeof method === "object" || !method) {
			return METHODS.init.apply(this, arguments);
		} else {
			$.error("Method " + method + " does not exist on jQuery." + PLUGIN_NAME + ".");
		}
	}

	$[PLUGIN_NAME] = {};

	$[PLUGIN_NAME].dlgRowSelect = function(elem, dlgID) {
		var $elemNext = $(elem).next("input[type='hidden']"),
			elemJson = $elemNext.val(),
			obj = JSON.parse(elemJson),
			$caller = $("a[href='#" + dlgID + "']"),
			$tgtParent = $caller.next("ul"),
			$tgtHdn = $.thingLister.getListHiddenWithin2($tgtParent),
			tgtID = $tgtHdn.attr("id"),
			tgtJson = $tgtHdn.val(),
			tgtList = JSON.parse(tgtJson),
			exists = false;

		$(tgtList).each(function() {
			if(obj.seq == this.seq) {
				exists = true;
				return false;
			}
		});
		if(!exists) {
			tgtList.push(obj);
			tgtJson = JSON.stringify(tgtList);
			$tgtHdn.val(tgtJson);
		}

		$tgtParent.thingLister("update");
		basicComponentsInitialize();
		$("#" + dlgID).modal("close");

		return false;
	}

	$[PLUGIN_NAME].addEmptyRow = function(elem) {
		var $tgtParent = $(elem).next("ul"),
			$tgtHdn = $.thingLister.getListHiddenWithin2($tgtParent),
			tgtID = $tgtHdn.attr("id"),
			tgtJson = $tgtHdn.val(),
			tgtList = JSON.parse(tgtJson),
			$prvRowList = [];

		if(tgtList.length > 0) {
			$tgtParent.children("li").each(function() {
				$prvRowList.push($(this));
			});
		}

		tgtList.push({idx:tgtList.length + 1});
		tgtJson = JSON.stringify(tgtList);
		$tgtHdn.val(tgtJson);

		$tgtParent.thingLister("update", $prvRowList);
		basicComponentsInitialize();

		return false;
	}

	$[PLUGIN_NAME].removeRow = function(elem) {
		var $tgtRow = $(elem).parent("li"),
			$tgtParent = $tgtRow.parent(),
			$tgtHdn = $.thingLister.getListHiddenWithin2($tgtParent),
			tgtID = $tgtHdn.attr("id"),
			rowID = $tgtRow.attr("id"),
			tgtJson = $tgtHdn.val(),
			tgtList = JSON.parse(tgtJson),
			idx = -1,
			$prvRowList = [];

		if(tgtList.length > 0) {
			$tgtParent.children("li").each(function() {
				$prvRowList.push($(this));
			});
		}

		for(idx = 0; idx < tgtList.length; idx++) {
			if(tgtID + "." + tgtList[idx].seq == rowID) {
				break;
			}
		}
		if(idx >= 0 && idx < tgtList.length) {
			tgtList.splice(idx, 1);
			tgtJson = JSON.stringify(tgtList);
			$tgtHdn.val(tgtJson);
			$prvRowList.splice(idx, 1);
		}

		$tgtParent.thingLister("update", $prvRowList);
		basicComponentsInitialize();

		return false;
	}

	$[PLUGIN_NAME].getListHiddenWithin2 = function(elem) {
		var $tgtNext = $(elem).next("input[type='hidden']"),
			$tgtNextNext = $tgtNext.next("input[type='hidden']"),
			$tgtHdn;
		if($tgtNext.size() > 0 && $tgtNext.val().startsWith("[")) {
			$tgtHdn = $tgtNext;
		} else if($tgtNextNext.size() > 0 && $tgtNextNext.val().startsWith("[")) {
			$tgtHdn = $tgtNextNext;
		}

		return $tgtHdn;
	}

	$[PLUGIN_NAME].getObjectHiddenWithin2 = function(elem) {
		var $tgtNext = $(elem).next("input[type='hidden']"),
			$tgtNextNext = $tgtNext.next("input[type='hidden']"),
			$tgtHdn;
		if($tgtNext.size() > 0 && $tgtNext.val().startsWith("{")) {
			$tgtHdn = $tgtNext;
		} else if($tgtNextNext.size() > 0 && $tgtNextNext.val().startsWith("{")) {
			$tgtHdn = $tgtNextNext;
		}

		return $tgtHdn;
	}

	$[PLUGIN_NAME].initItemLister = function(def) {
		if(def.edit) {
			$(def.edit.selector).thingLister({
				listerDef: def.edit,
				renderRow: function(rowObj, tgtID, idx, $prvRow) {
					if($prvRow) return $prvRow;

					var rowID = tgtID.replace(/\.(?:minion|demion)$/, "") + "." + idx,
						uri = $("#baseURI").val() + this.listerDef.ajaxPath,
						conds = {
							idx: idx,
							rowID: rowID
						};
					conds[this.listerDef.rowObjName] = rowObj;

					$.ajax({
						url : uri,
						method: "POST",
						dataType: "html",
						data: {
							conds: conds
						}
					}).done(function(html) {
						$("#" + rowID.replace(/\./g, "\\.")).html(html);
						basicComponentsInitialize();
					});

					return $("<li />").attr("id", rowID).addClass("collection-item");
				}
			});
		}
		if(def.view) {
			$(def.view.selector).thingLister({
				listerDef: def.view,
				renderRow: function(rowObj, tgtID, idx, $prvRow) {
					if($prvRow) return $prvRow;

					var rowID = tgtID.replace(/\.(?:minion|demion)$/, "") + "." + idx,
						$tr = $("<tr />").attr("id", rowID).addClass("collection-item"),
						$td = $("<td />"),
						uri = $("#baseURI").val() + this.listerDef.ajaxPath,
						conds = {
							idx: idx,
							rowID: rowID
						};
						conds[this.listerDef.rowObjName] = rowObj;

					$.ajax({
						url : uri,
						method: "POST",
						dataType: "html",
						data: {
							conds: conds
						}
					}).done(function(html) {
						$("#" + rowID.replace(/\./g, "\\.") + " td:first").html(html);
						basicComponentsInitialize();
					});

					return $tr.append($td);
				},
				renderEmptyRow: function() {
					var $tr = $("<tr />").addClass("collection-item grey lighten-3"),
						$td = $("<td />").html("なし");
					return $tr.append($td);
				}
			});
		}
	}

	$[PLUGIN_NAME].initBlockLister = function(def) {
		var defModes = [ def.edit, def.view ];
		for(var midx = 0; midx < 2; midx++) {
			var options = {
				listerDef: defModes[midx],
				renderRow: defModes[midx].sumRenderRow,
				afterUpdate: function($elem) {
					// 個別入力エリアの全行の枠のレンダリング
					var $tgtHdn = $.thingLister.getListHiddenWithin2($elem),
						tgtID = $tgtHdn.attr("id").replace(/^_+/, ""), // 先頭の_が付いていると無視される
						tgtJson = $tgtHdn.val(),
						uri = $("#baseURI").val() + this.listerDef.rowsAjaxPath,
						data = {},
						rowObjName = this.listerDef.rowObjName,
						rowsSelector = this.listerDef.rowsSelector,
						colSelector = this.listerDef.colSelector,
						blkAjaxPath = this.listerDef.blkAjaxPath,
						itemListerDefs = this.listerDef.itemListerDefs;
					data[tgtID] = tgtJson;

					$.ajax({
						url : uri,
						method: "POST",
						dataType: "html",
						data: data
					}).done(function(html) {
						var $rowsParent = $(rowsSelector),
							prvBlkList = [],
							bidx = 0;

						$rowsParent.find(colSelector).each(function() {
							if($(this).children().size() > 0) {
								prvBlkList.push($(this));
							}
						});
						$rowsParent.empty().html(html);
						$rowsParent.find(colSelector).each(function() {
							// 個別入力欄のレンダリング
							var $blkParent = $(this),
								$tgtHdn = $.thingLister.getObjectHiddenWithin2($blkParent),
								tgtID = $tgtHdn.attr("id").replace(/^_+/, ""), // 先頭の_が付いていると無視される
								tgtJson = $tgtHdn.val(),
								blkObj = JSON.parse(tgtJson),
								uri = $("#baseURI").val() + blkAjaxPath;
								data = {};

							if(!blkObj[rowObjName])
								return true;

							if(bidx < prvBlkList.length) {
								prvBlkList[bidx].children().appendTo($blkParent);
								basicComponentsInitialize();
							} else {
								$.ajax({
									url : uri,
									method: "POST",
									dataType: "html",
									data: {
										"conds.minion": tgtJson
									}
								}).done(function(html) {
									$blkParent.html(html);
									for(var didx = 0; didx < itemListerDefs.length; didx++) {
										var def = itemListerDefs[didx];
										$.thingLister.initItemLister(def);
									}
									basicComponentsInitialize();
								});
							}

							bidx++;
						});
					});

				}

			};
			if(defModes[midx].sumRenderEmptyRow) {
				options["renderEmptyRow"] = defModes[midx].sumRenderEmptyRow;
			}

			$(defModes[midx].sumSelector).thingLister(options);
		}
	}

}(jQuery));

function basicComponentsInitialize() {
	$("select").material_select();
	Materialize.updateTextFields();
	$(".materialize-textarea").trigger("autoresize");

	$(".datepicker").pickadate({
		monthsFull: [ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" ],
		selectMonths: true,
		selectYears: 250,
		min: new Date((new Date).getFullYear() - 120, 0, 1),
		max: new Date((new Date).getFullYear() + 20, 11, 31),
		format: "yyyy-mm-dd",
		today: "今日",
		clear: "クリア",
		close: "閉じる",
		closeOnSelect: true,
		onOpen: function() {
			var $frame = this.$root.find(".picker__frame");
			$frame.css("top", $(window).scrollTop() + "px");

			var ygmd = this.$node.val(),
				ymda = ygmd2Ymda(ygmd);
			if(ymda) {
				this.set("select", [ ymda[0], ymda[1] - 1, ymda[2] ]);
			}
		},
		onClose: function() {
			var obj = this.get("select"),
				id = this.$node.attr("id"),
				ygmd = "";
			if(obj) {
				ygmd = ymda2Ygmd([ obj.year, obj.month + 1, obj.date ])
			}
			this.$node.val(ygmd);
			if(id && id.length > 1 && id.substr(0, 1) == "_") {
				var id2 = id.substr(1),
					id3 = id2.replace(/\./g, "\\.");
				$("#" + id3).val(ygmd2Ymd(ygmd));
			}
		}
	});

	$(".timepicker").each(function() {
		var now = $(this).val(),
			options = {
				twentyFour: true,
				timeSeparator: ":",
				show: function() {
					var $wp = $(".wickedpicker");
					$wp.css("left", ($(window).width() - $wp.width()) / 2 + $(window).scrollLeft() + "px")
						.css("top", ($(window).height() - $wp.height()) / 2 + $(window).scrollTop() + "px");
					$("#wpModalOverlay").show();
					$("body").css("overflow", "hidden");
				},
				afterShow: function() {
					$("body").css("overflow", "auto");
					$("#wpModalOverlay").hide();
				}
			};
		if(now.length >= 5) {
			options.now = now;
		}
		$(this).wickedpicker(options);
	});

	var wpModalOverlay = $("div#wpModalOverlay");
	if(wpModalOverlay.size() == 0) {
		wpModalOverlay = $("<div />").attr("id", "wpModalOverlay")
			.css("z-index", "12001")
			.css("background-color", "#000")
			.css("opacity", 0.5)
			.css("position", "fixed")
			.css("left", -$(window).width() + "px")
			.css("top", -$(window).height() + "px")
			.css("width", ($(window).width() * 2) + "px")
			.css("height", ($(window).height() * 2) + "px")
			.hide();
	}
	$(".wickedpicker").css("z-index", "12002").after(wpModalOverlay);

	$("ul.editListerSimple").thingLister({
		renderRow: function(rowObj, tgtID) {
			var displayName = rowObj.displayName && rowObj.displayName != "" ? rowObj.displayName : rowObj.name.string;
			return $("<li />").attr("id", tgtID + "." + rowObj.seq).addClass("collection-item")
				.html((rowObj.code ? "<span class=\"truncate widthID btnHeightMiddle hide-on-small-only listerCode\">" + rowObj.code + "</span>" : "")
					+ "<span class=\"truncate width200px btnHeightMiddle\">" + displayName + "</span>\n"
					+ "<a class=\"right btn-floating green darken-1 tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"削除\" href=\"#!\" onclick=\"$.thingLister.removeRow(this);\">\n"
					+ " <i class=\"material-icons\">remove</i>\n"
					+ "</a>\n"
					+ "<div class=\"clearfix\"></div>\n");
		}
	});
	$("table.viewListerSimple").thingLister({
		renderRow: function(rowObj, tgtID) {
			var displayName = rowObj.displayName && rowObj.displayName != "" ? rowObj.displayName : rowObj.name.string,
				$tr = $("<tr />").attr("id", tgtID + "." + rowObj.seq).addClass("collection-item"),
				$spanCode = $("<span />").addClass("truncate").html(rowObj.code),
				$tdCode = $("<td />").addClass("widthID hide-on-small-only listerCode").append($spanCode),
				$spanName = $("<span />").addClass("truncate").html(displayName),
				$tdName = $("<td />").addClass("width200px").append($spanName);
			return $tr.append($tdCode).append($tdName);
		},
		renderEmptyRow: function() {
			var $tr = $("<tr />").addClass("collection-item grey lighten-3"),
				$tdCode = $("<td />").addClass("widthID hide-on-small-only listerCode").html(""),
				$tdName = $("<td />").addClass("width200px").html("なし");
			return $tr.append($tdCode).append($tdName);
		}
	});

	adjustTabHeight();
}

$(document).ready(function() {
	basicComponentsInitialize();

	$("#portalMenuDlg").modal();
	$("#docMenuDlg").modal();
	$("#regMenuDlg").modal();
	$("#selectUserDlg").modal({
		ready: function(modal, trigger) {
			ajaxSelectXXXDlgSearch("User");
		}
	});
	$("#selectOfficeDlg").modal({
		ready: function(modal, trigger) {
			ajaxSelectXXXDlgSearch("Office");
		}
	});
	$("#selectTeamDlg").modal({
		ready: function(modal, trigger) {
			ajaxSelectXXXDlgSearch("Team");
		}
	});
	$("#selectOccupationDlg").modal({
		ready: function(modal, trigger) {
			ajaxSelectXXXDlgSearch("Occupation");
		}
	});

	$(window).resize(function() {
		adjustTabHeight(); // ToDo: 効かないっぽい
	});

	$("input").on("keydown", function(e) {
		if((e.which && e.which === 13) || (e.keyCode && e.keyCode === 13)) {
			return false;
		} else {
			return true;
		}
	});

	$("#fileupload").fileupload({
		dataType: "json",
		done: function(e, data) {
			$.each(data.result.files, function(index, file) {
				$('<p/>').text(file.name).appendTo(document.body);
			});
		}
	});

	adjustTabHeight();
});

function userActionChanged() {
	$("select").material_select();
	switch($("#userActionSelect").val()) {
	case "signOut":
		$("#signOutBtn").click();
		break;
	default:
		break;
	}
}

function ygmd2Ymda(ygmd) {
	var mcs = ygmd.match(/^(\d+)（.+）年(\d+)月(\d+)日$/);
	if(mcs) {
		return [ mcs[1], mcs[2], mcs[3] ];
	} else {
		return null;
	}
}

function ygmd2Ymd(ygmd) {
	var ymda = ygmd2Ymda(ygmd);
	if(ymda) {
		return ("0000" + ymda[0]).slice(-4) + "-" + ("0" + ymda[1]).slice(-2) + "-" + ("0" + ymda[2]).slice(-2);
	} else {
		return "";
	}
}

function getGengo(ymda) {
	var GENGOS = [
		[ "平成", 1989, 1, 8 ],
		[ "昭和", 1926, 12, 25 ],
		[ "大正", 1912, 7, 30 ],
		[ "明治", 1868, 1, 1 ],
		[ "", -99999, 0, 0 ]
	];

	for(var gi = 0; gi < GENGOS.length - 1; gi++) {
		var gengo1 = GENGOS[gi],
			gengo2 = GENGOS[gi + 1];
		if(ymda[0] > gengo1[1]) {
			return gengo1[0] + (ymda[0] - gengo1[1] + 1);
		} else if(ymda[0] == gengo1[1]) {
			if(ymda[1] > gengo1[2] || (ymda[1] == gengo1[2] && ymda[2] >= gengo1[3])) {
				return gengo1[0] + "元";
			} else {
				return gengo2[0] + (ymda[0] - gengo2[1] + 1);
			}
		}
	}

	return "";
}

function ymda2Ygmd(ymda) {
	return ymda[0] + "（" + getGengo(ymda) + "）年" + ymda[1] + "月" + ymda[2] + "日";
}

function dt2Ymd(dt) {
	return dt.getFullYear() + "-" + ("0" + (dt.getMonth() + 1)).slice(-2) + "-" + ("0" + dt.getDate()).slice(-2);
}

function dt2hm(dt) {
	return ("0" + dt.getHours()).slice(-2) + ":" + ("0" + dt.getMinutes()).slice(-2);
}

function iso8601toYmdhm(iso8601) {
	if(!iso8601 || iso8601 == "") return "";
	var dt = new Date(iso8601);
	return dt.getFullYear() + "年" + (dt.getMonth() + 1) + "月" + dt.getDate() + "日 "
		+ ("0" + dt.getHours()).slice(-2) + ":" + ("0" + dt.getMinutes()).slice(-2);
}

function iso8601toYmd(iso8601) {
	if(!iso8601 || iso8601 == "") return "";
	var dt = new Date(iso8601);
	return dt.getFullYear() + "年" + (dt.getMonth() + 1) + "月" + dt.getDate() + "日";
}

function ajaxSelectXXXDlgSearch(tgt) {
	var uri = $("#baseURI").val() + "/dlg/select" + tgt + "DlgResultTBody.ajax";

	$.ajax({
		url : uri,
		method: "POST",
		dataType: "html"
	}).done(function(html) {
		$("#select" + tgt + "DlgResult tbody").remove();
		$("#select" + tgt + "DlgResult").append(html);
	});
}

function adjustTabHeight() {
	$(".tabSlide").each(function() {
		if(this.className.match(/active/)) {
			// console.log("adjustTabHeight: " + $(this).height());
			$(".tabs-content.carousel").css("height", $(this).height());
			return false;
		}
	});
}
