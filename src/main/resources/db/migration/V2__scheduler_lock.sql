CREATE TABLE vrt.shedlock(name VARCHAR(64) NOT NULL, lock_until TIMESTAMP NOT NULL,
                      locked_at TIMESTAMP NOT NULL, locked_by VARCHAR(255) NOT NULL, PRIMARY KEY (name));
