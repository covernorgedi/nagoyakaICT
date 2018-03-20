SELECT
 /*%expand*/*
FROM sign_in_session
WHERE seq=/*seq*/''
/*%if excludeExpired */
 AND expired_at IS NULL
/*%end*/
