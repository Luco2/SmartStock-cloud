DROP TABLE IF EXISTS portfolio_stocks, portfolio, user_game, game, users CASCADE;

CREATE TABLE users (
   user_id serial PRIMARY KEY,
   username varchar(50) UNIQUE NOT NULL,
   password_hash varchar(200) NOT NULL,
   role varchar(50) NOT NULL
);

CREATE TABLE game (
   game_id serial PRIMARY KEY,
   game_name varchar(50) NOT NULL,
   organizer_name varchar(50) NOT NULL,
   end_date varchar(10) NOT NULL
);

CREATE TABLE user_game (
   user_game_id serial PRIMARY KEY,
   game_id int NOT NULL,
   user_id int NOT NULL,
   CONSTRAINT FK_user_game_game FOREIGN KEY (game_id) REFERENCES game (game_id),
   CONSTRAINT FK_user_game_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE portfolio (
   portfolio_id serial PRIMARY KEY,
   game_id int NOT NULL,
   user_id int NOT NULL,
   cash_balance DECIMAL NOT NULL,
   CONSTRAINT FK_portfolio_game FOREIGN KEY (game_id) REFERENCES game (game_id),
   CONSTRAINT FK_portfolio_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE portfolio_stocks (
  portfolio_stock_id serial PRIMARY KEY,
  portfolio_id int NOT NULL,
  ticker varchar(10) NOT NULL,
  quantity int,
  CONSTRAINT FK_portfolio_stocks_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolio (portfolio_id)
);


