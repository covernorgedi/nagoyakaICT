SELECT
 /*%expand*/*
FROM sch_schedule_item
WHERE NOT(start_date>/*endDate*/'' OR end_date</*startDate*/'')
 OR actor_user_seq=/*userSeq*/1
 OR (actor_user_seq IS NULL AND actor_office_seq=/*officeSeq*/1)
 OR (actor_user_seq IS NULL AND actor_team_seq=/*teamSeq*/1)
 OR (actor_user_seq IS NULL AND actor_office_seq IS NULL AND actor_team_seq IS NULL)
ORDER BY start_date,end_date,seq
