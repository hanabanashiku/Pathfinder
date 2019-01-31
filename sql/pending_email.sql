-- Create the table pending_emails

CREATE TABLE IF NOT EXISTS pending_emails(
    user INT PRIMARY KEY UNIQUE NOT NULL,
    code VARCHAR(20) NOT NULL,
    new_user BOOLEAN DEFAULT TRUE,
    FOREIGN KEY fk_user(user)
    REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);