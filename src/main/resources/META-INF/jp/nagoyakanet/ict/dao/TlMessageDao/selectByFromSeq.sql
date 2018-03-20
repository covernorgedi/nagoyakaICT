SELECT
 /*%expand*/*
FROM tl_message
WHERE (updated_by_user_seq=/*userSeq*/1
  OR share_with_user_seq=/*userSeq*/1
  OR (share_with_user_seq IS NULL AND share_with_office_seq=/*officeSeq*/1)
  OR (share_with_user_seq IS NULL AND share_with_team_seq=/*teamSeq*/1)
  OR (share_with_user_seq IS NULL AND share_with_office_seq IS NULL AND share_with_team_seq IS NULL))
/*%if seq!=null && seq>0L */
 AND (emg_sort<100
  OR seq</*seq*/1)
/*%end*/
/*%if kinds!=null && kinds.size()>0 */
 AND kind IN/*kinds*/('aaa','bbb')
/*%end*/
ORDER BY
/*%if emgSort */
 emg_sort,seq DESC
/*%else*/
 seq DESC
/*%end*/
