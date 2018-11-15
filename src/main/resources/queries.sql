/* Query above threshold table */
select * from log_entry inner join above_threshold on log_entry.id = above_threshold.log_entry_id;

/* Initial insert */
insert into log_entry (date, ip, request, status, user_agent) values (?, ?, ?, ?, ?)