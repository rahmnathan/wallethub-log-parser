create table log_entry (id bigint not null auto_increment primary key, date datetime, ip varchar(255), request varchar(255), status varchar(255), user_agent varchar(255))
create table above_threshold (id bigint not null auto_increment primary key, log_entry_id bigint not null, foreign key (log_entry_id) references log_entry(id))
