$(document).ready(function() {

	var originalAddClassMethod = jQuery.fn.addClass;
	jQuery.fn.addClass = function() {
		var res = originalAddClassMethod.apply(this, arguments);
		jQuery(this).trigger("classChanged");

		return res;
	}

	$(".tabSlide").bind("classChanged", function() {
		adjustTabHeight();
	});
	$("ul.tabs").tabs({
		swipeable: true,
		onShow: setTimeout(function() { adjustTabHeight(); }, 200) // ToDo: Chromeの初期表示時に高さ調整が効かない問題 とりあえずこうしておく
	});
	var tabSelected = $("#tabSelected").val();
	if(tabSelected.length > 0) {
		$("ul.tabs").tabs("select_tab", tabSelected);
	}

	var ITEM_LISTER_DEFS = [
		{
			edit: {
				selector: "ul.editListerPrimaryCareUser",
				ajaxPath: "/reg/medical/clinicDentistPharmacy/primaryCareUserFieldset.ajax",
				rowObjName: "user"
			},
			view: {
				selector: "table.viewListerPrimaryCareUser",
				ajaxPath: "/reg/medical/clinicDentistPharmacy/primaryCareUserTable.ajax",
				rowObjName: "user"
			}
		},
		{
			edit: {
				selector: "ul.editListerNecessaryMedicine",
				ajaxPath: "/reg/medical/diagnosisMedicineAllergy/necessaryMedicineFieldset.ajax",
				rowObjName: "medicine"
			},
			view: {
				selector: "table.viewListerNecessaryMedicine",
				ajaxPath: "/reg/medical/diagnosisMedicineAllergy/necessaryMedicineTable.ajax",
				rowObjName: "medicine"
			}
		}
	];
	for(var didx = 0; didx < ITEM_LISTER_DEFS.length; didx++) {
		var def = ITEM_LISTER_DEFS[didx];
		$.thingLister.initItemLister(def);
	}

	var BLOCK_LISTER_DEFS = [
		{
			edit: {
				sumSelector: "ul.editListerHospitalizedSummary",
				rowObjName: "hospitalized",
				sumRenderRow: function(rowObj, tgtID, idx, $prvRow) {
					var rowID = tgtID.replace(/\.(?:minion|demion)$/, "") + "." + idx,
						displayName = "（名称未入力）",
						period = "";
					if(rowObj.office) displayName = rowObj.office.name.string;
					if(rowObj.joinAction) period = iso8601toYmd(rowObj.joinAction.startTime.date) + " - " + iso8601toYmd(rowObj.leaveAction.startTime.date);

					return $("<li />").attr("id", rowID).addClass("collection-item")
						.html("<span class=\"truncate width200px btnHeightMiddle hide-on-small-only\">" + displayName + "</span>"
							+ "<span class=\"truncate widthPeriod btnHeightMiddle\">" + period + "</span>\n"
							+ "<a class=\"right btn-floating green darken-1 tooltipped\" data-position=\"bottom\" data-delay=\"50\" data-tooltip=\"削除\" href=\"#!\" onclick=\"$.thingLister.removeRow(this);\">\n"
							+ " <i class=\"material-icons\">remove</i>\n"
							+ "</a>\n"
							+ "<div class=\"clearfix\"></div>\n");
				},
				rowsAjaxPath: "/reg/medical/inOutHospital/hospitalizedFieldsetsRows.ajax",
				rowsSelector: "div#hospitalizedFieldsetRows",
				colSelector: "div.hospitalizedFieldsetCol",
				blkAjaxPath: "/reg/medical/inOutHospital/hospitalizedFieldsetBlock.ajax",
				itemListerDefs: [
					{
						edit: {
							selector: "ul.editListerHospitalizedUser",
							ajaxPath: "/reg/medical/inOutHospital/hospitalizedUserFieldset.ajax",
							rowObjName: "user"
						}
					}
				]
			},
			view: {
				sumSelector: "table.viewListerHospitalizedSummary",
				rowObjName: "hospitalized",
				sumRenderRow: function(rowObj, tgtID, idx, $prvRow) {
					var rowID = tgtID.replace(/\.(?:minion|demion)$/, "") + "." + idx,
						displayName = "（名称未入力）",
						period = "";
					if(rowObj.office) displayName = rowObj.office.name.string;
					if(rowObj.joinAction) period = iso8601toYmd(rowObj.joinAction.startTime.date) + " - " + iso8601toYmd(rowObj.leaveAction.startTime.date);

					var $tr = $("<tr />").attr("id", rowID).addClass("collection-item"),
						$spanName = $("<span />").addClass("truncate").html(displayName),
						$tdName = $("<td />").addClass("width200px hide-on-small-only").append($spanName),
						$spanPeriod = $("<span />").addClass("truncate").html(period),
						$tdPeriod = $("<td />").addClass("widthPeriod").append($spanPeriod);
					return $tr.append($tdName).append($tdPeriod);
				},
				sumRenderEmptyRow: function() {
					var $tr = $("<tr />").attr("id", rowID).addClass("collection-item"),
						$tdName = $("<td />").addClass("width200px hide-on-small-only").html(""),
						$tdPeriod = $("<td />").addClass("widthPeriod").html("なし");
					return $tr.append($tdName).append($tdPeriod);
				},
				rowsAjaxPath: "/reg/medical/inOutHospital/hospitalizedTablesRows.ajax",
				rowsSelector: "div#hospitalizedTableRows",
				colSelector: "div.hospitalizedTableCol",
				blkAjaxPath: "/reg/medical/inOutHospital/hospitalizedTableBlock.ajax",
				itemListerDefs: [
					{
						view: {
							selector: "table.viewListerHospitalizedUser",
							ajaxPath: "/reg/medical/inOutHospital/hospitalizedUserTable.ajax",
							rowObjName: "user"
						}
					}
				]
			}
		}
	];
	for(var didx = 0; didx < BLOCK_LISTER_DEFS.length; didx++) {
		var def = BLOCK_LISTER_DEFS[didx];
		$.thingLister.initBlockLister(def);
	}

	basicComponentsInitialize();

});
