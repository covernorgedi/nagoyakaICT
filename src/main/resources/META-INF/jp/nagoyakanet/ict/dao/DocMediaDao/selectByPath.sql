SELECT
 /*%expand*/*
FROM doc_media
WHERE path=/*path*/''
/*%if excludeGone */
 AND gone_at IS NULL
/*%end*/
