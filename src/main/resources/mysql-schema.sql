CREATE TABLE log_entry
  (
     id         BIGINT NOT NULL auto_increment PRIMARY KEY,
     date       DATETIME,
     ip         VARCHAR(255),
     request    VARCHAR(255),
     status     VARCHAR(255),
     user_agent VARCHAR(255)
  );


CREATE TABLE above_threshold
  (
     id           BIGINT NOT NULL auto_increment PRIMARY KEY,
     log_entry_id BIGINT NOT NULL,
     FOREIGN KEY (log_entry_id) REFERENCES log_entry(id),
     comment      VARCHAR(255)
  );
