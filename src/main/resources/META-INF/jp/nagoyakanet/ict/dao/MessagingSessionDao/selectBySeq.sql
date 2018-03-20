SELECT
 /*%expand*/*
FROM messaging_session
WHERE seq=/*seq*/''
/*%if excludeExpired */
 AND expired_at IS NULL
/*%end*/
