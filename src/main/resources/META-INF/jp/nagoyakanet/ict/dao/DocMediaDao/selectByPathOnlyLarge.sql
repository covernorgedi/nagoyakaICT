SELECT
 seq,
 path,
 ext,
 orig_len,
 orig_path,
 large_bin,
 large_len,
 remarks,
 come_at,
 come_by_user_seq,
 come_by_user_cd,
 come_by_user_nm,
 come_by_office_seq,
 come_by_office_cd,
 come_by_office_nm,
 come_by_team_seq,
 come_by_team_cd,
 come_by_team_nm,
 gone_at,
 gone_by_user_seq,
 gone_by_user_cd,
 gone_by_user_nm,
 gone_by_office_seq,
 gone_by_office_cd,
 gone_by_office_nm,
 gone_by_team_seq,
 gone_by_team_cd,
 gone_by_team_nm,
 progenitor,
 ancestor,
 descendant,
 lifetime,
 cleaned_at
FROM doc_media
WHERE path=/*path*/''
/*%if excludeGone */
 AND gone_at IS NULL
/*%end*/
