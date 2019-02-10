-- CREATE THE USERS TABLE

CREATE TABLE IF NOT EXISTS users(
  id INT NOT NULL AUTO_INCREMENT,
  username varchar(15) UNIQUE NOT NULL,
  userPassword varchar(255) NOT NULL,
  email varchar(255) UNIQUE NOT NULL
  lname varchar(50),
  fname varchar(50) NOT NULL,
  2fa_uri varchar(255) UNIQUE,
  PRIMARY KEY(id)
);
