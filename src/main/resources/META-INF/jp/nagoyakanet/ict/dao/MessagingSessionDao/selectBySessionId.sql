SELECT
 /*%expand*/*
FROM messaging_session
WHERE session_id=/*sessionId*/''
/*%if excludeExpired */
 AND expired_at IS NULL
/*%end*/
