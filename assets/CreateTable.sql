DROP TABLE IF EXISTS TRFCVIOLATOR;
CREATE TABLE TRFCVIOLATOR
(
   ID             INTEGER PRIMARY KEY   AUTOINCREMENT,
   DESCRIPTION       TEXT,
   IMAGEPATH         TEXT,
   LATITUDE       DOUBLE,
   LONGITUDE      DOUBLE,
   TIMESTAMP        TEXT
);