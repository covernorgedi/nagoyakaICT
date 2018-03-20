SELECT
 /*%expand*/*
FROM doc_media
WHERE seq=/*seq*/''
/*%if excludeGone */
 AND gone_at IS NULL
/*%end*/
