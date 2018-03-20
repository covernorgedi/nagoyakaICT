SELECT
 /*%expand*/*
FROM sign_in_session
WHERE session_id=/*sessionId*/''
/*%if excludeExpired */
 AND expired_at IS NULL
/*%end*/
