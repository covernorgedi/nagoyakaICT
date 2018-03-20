SELECT
 /*%expand*/*
FROM doc_minion
WHERE s_key=/*sKey*/''
/*%if excludeGone */
 AND gone_at IS NULL
/*%end*/
ORDER BY seq /*%if !sortOlder */DESC/*%end*/
