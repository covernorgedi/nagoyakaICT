SELECT
 /*%expand*/*
FROM mst_code_and_lang
WHERE act=/*act*/''
 AND type=/*type*/''
/*%if excludeExpired */
 AND expired_at IS NULL
/*%end*/
ORDER BY act,type,sort
