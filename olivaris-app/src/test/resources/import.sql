-- Initial test data for H2 database
INSERT INTO role(name) VALUES ('ROLE_ADMIN');
INSERT INTO role(name) VALUES ('ROLE_BASIC');
INSERT INTO role(name) VALUES ('ROLE_FARMER');

INSERT INTO entity_role(name) VALUES ('ROLE_ADMIN');
INSERT INTO entity_role(name) VALUES ('ROLE_FARMER');
