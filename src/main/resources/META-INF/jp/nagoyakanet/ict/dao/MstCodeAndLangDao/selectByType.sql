SELECT
 /*%expand*/*
FROM mst_code_and_lang
WHERE type=/*type*/''
/*%if excludeExpired */
 AND expired_at IS NULL
/*%end*/
ORDER BY type,sort
