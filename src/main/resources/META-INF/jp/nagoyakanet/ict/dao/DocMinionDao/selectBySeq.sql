SELECT
 /*%expand*/*
FROM doc_minion
WHERE seq=/*seq*/''
/*%if excludeGone */
 AND gone_at IS NULL
/*%end*/
