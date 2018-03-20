SELECT
 /*%expand*/*
FROM msg_timeline
WHERE seq>/*seq*/1
/*%if userSeq != null*/
 AND created_by_user_seq=/*userSeq*/1
/*%end*/
/*%if teamSeq != null*/
 AND created_by_team_seq=/*teamSeq*/1
/*%end*/
/*%if belongSeq != null*/
 AND created_by_belong_seq=/*belongSeq*/1
/*%end*/
