SELECT
 /*%expand*/*
FROM mst_code_and_lang
WHERE 0=0
/*%if excludeExpired */
 AND expired_at IS NULL
/*%end*/
ORDER BY act,type,sort
