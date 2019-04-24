-- CREATE TABLE CONNECTOR_FLOORS
CREATE TABLE IF NOT EXISTS connector_floors(
  id INT NOT NULL,
  floor INT NOT NULL,
  FOREIGN KEY fk_node3(id)
  REFERENCES nodes(id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT
);