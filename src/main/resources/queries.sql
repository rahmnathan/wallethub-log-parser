/* Query log entry table for entries above threshold */
SELECT *
FROM   log_entry logEntry
WHERE  logEntry.ip IN (SELECT logEntry2.ip
                       FROM   log_entry logEntry2
                       WHERE  logEntry2.date BETWEEN ? AND ?
                       GROUP  BY logEntry2.ip
                       HAVING Count(*) > ?)
AND logEntry.date BETWEEN ? AND ?;


/* Query above threshold table */
SELECT *
FROM   log_entry
INNER JOIN above_threshold
ON log_entry.id = above_threshold.log_entry_id;

/* Query count for each ip in above threshold table */
SELECT log_entry.ip,count(*)
FROM log_entry
INNER JOIN above_threshold
ON log_entry.id = above_threshold.log_entry_id
group by log_entry.ip;

/* Query log entry table for specific ip */
SELECT *
FROM   log_entry logEntry
WHERE  logEntry.ip = ?;

/* Initial insert */
INSERT INTO log_entry (date, ip, request, status, user_agent)
VALUES (?, ?, ?, ?, ?);