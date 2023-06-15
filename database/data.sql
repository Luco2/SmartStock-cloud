BEGIN TRANSACTION;

INSERT INTO users (username,password_hash,role) VALUES ('user','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ROLE_USER');
INSERT INTO users (username,password_hash,role) VALUES ('admin','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ROLE_ADMIN');

INSERT INTO game (game_name, organizer_name, end_date) VALUES ('test game', 'user', '2023-12-01');

INSERT INTO game (game_name, organizer_name, end_date) VALUES ('ended game', 'user', '2023-05-05');

INSERT INTO user_game (game_id, user_id) VALUES (1, 1);

INSERT INTO portfolio (game_id, user_id, cash_balance) VALUES (1, 1, 100000);

COMMIT TRANSACTION;