/* Query log entry table for entries above threshold */
select * from log_entry logEntry where logEntry.ip in (select logEntry2.ip from log_entry logEntry2 where logEntry2.date between ? and ? group by logEntry2.ip having count(*) > ?) and logEntry.date between ? and ?;

/* Query above threshold table */
select * from log_entry inner join above_threshold on log_entry.id = above_threshold.log_entry_id;

/* Initial insert */
insert into log_entry (date, ip, request, status, user_agent) values (?, ?, ?, ?, ?);