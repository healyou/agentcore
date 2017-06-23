------------------ agentA input table ------------------
CREATE TABLE if not exists inputdataA (
        id INTEGER PRIMARY KEY AUTOINCREMENT   NOT NULL,
        occupancyA     INTEGER                 NOT NULL,
        conditionA     INTEGER                 NOT NULL,
        condition_d1   INTEGER                 NOT NULL,
        occupancy_d1   INTEGER                 NOT NULL,
        condition_d2   INTEGER                 NOT NULL,
        occupancy_d2   INTEGER                 NOT NULL);

-------------------- agentB input table ------------------
CREATE TABLE if not exists inputdataB (
        id INTEGER PRIMARY KEY AUTOINCREMENT   NOT NULL,
        occupancyB     INTEGER                 NOT NULL,
        conditionB     INTEGER                 NOT NULL,
        condition_d1   INTEGER                 NOT NULL,
        occupancy_d1   INTEGER                 NOT NULL,
        condition_d2   INTEGER                 NOT NULL,
        occupancy_d2   INTEGER                 NOT NULL,
        condition_d3   INTEGER                 NOT NULL,
        occupancy_d3   INTEGER                 NOT NULL);

-------------------- agentC input table ------------------
CREATE TABLE if not exists inputdataC (
        id INTEGER PRIMARY KEY AUTOINCREMENT   NOT NULL,
        occupancyC     INTEGER                 NOT NULL,
        conditionC     INTEGER                 NOT NULL,
        condition_d3   INTEGER                 NOT NULL,
        occupancy_d3   INTEGER                 NOT NULL);

-------------------- test agent table --------------------
CREATE TABLE if not exists intsedent (
        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        shortinfo  TEXT,
        info       TEXT);