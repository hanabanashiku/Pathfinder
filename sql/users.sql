-- CREATE THE USERS TABLE

CREATE TABLE users(
  id INT NOT NULL,
  username varchar(15) UNIQUE NOT NULL,
  password varchar(255) NOT NULL,
  email varchar(255) UNIQUE NOT NULL
  lname varchar(50),
  fname varchar(50) NOT NULL,
  2fa_uri varchar(255),
  PRIMARY KEY(id)
);
