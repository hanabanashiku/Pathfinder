CREATE DATABASE Navigation;

--Navigation tables


CREATE TABLE floor_connectors(
    id INT NOT NULL AUTO_INCREMENT,
    room_num VARCHAR(50) UNIQUE,
    name VARCHAR(255),
    nodeID INT,
    requires_auth BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (nodeID) REFERENCES nodes(id),
    PRIMARY KEY (id)
);

CREATE TABLE connector_floors(
  id INT NOT NULL,
  floor INT NOT NULL,
  FOREIGN KEY fk_node3(id)
  REFERENCES nodes(id)
);

CREATE TABLE nodes(
  id INT UNIQUE NOT NULL AUTO_INCREMENT,
  floorID INT,
  buildingID INT,
  x DECIMAL NOT NULL DEFAULT 0,
  y DECIMAL NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  FOREIGN KEY (floorID) REFERENCES floors(floor_ID),
  FOREIGN KEY (buildingID) REFERENCES buildings(id)
);
CREATE TABLE buildings(
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(255),
  map_image_path VARCHAR(255),
  owner VARCHAR(50),
  Primary key (id)
);

CREATE TABLE beacons(
  beaconID INT UNIQUE NOT NULL AUTO_INCREMENT,
  beaconName VARCHAR(50),
  floorID INT,
  buildingID INT,
  x DECIMAL NOT NULL DEFAULT 0,
  y DECIMAL NOT NULL DEFAULT 0,
  PRIMARY KEY (beaconID),
  FOREIGN KEY (floorID) REFERENCES floors(floor_ID),
  FOREIGN KEY (buildingID) REFERENCES buildings(id)
);


CREATE TABLE rooms(
  id INT UNIQUE NOT NULL AUTO_INCREMENT,
  room_num VARCHAR(50) UNIQUE,
  name VARCHAR(255),
  nodeID INT,
  requires_auth BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (nodeID) REFERENCES nodes(id)
);

Create TABLE ElectricalEnginerring(
  floorID INT NOT NULL,
  imageLocation VARCHAR NOT NULL(255),
  PRIMARY KEY floorID
  );

Create Table floors(
    floor_id INT NOT NULL AUTO_INCREMENT,
    floor_number INT NOT NULL,
    path_to_image varchar(255),
    buildingID INT,
    Primary Key (floor_id),
    FOREIGN KEY (buildingID) REFERENCES buildings(id)
);

  Create TABLE beacons(
    beaconID INT NOT NULL AUTO_INCREMENT,
    x INT,
    y INT,
    floorID INT,
    PRIMARY KEY beaconID,
    FOREIGN KEY (floorID) REFERENCES floors(floor_ID)
  );

  Create TABLE edges(
    id INT NOT NULL AUTO_INCREMENT,
    building_id INT,
    node1 INT,
    node2 INT,
    PRIMARY KEY (id),
    FOREIGN KEY (building_id) REFERENCES buildings(id)
  );

  Create TABLE intersections(
    id INT NOT NULL AUTO_INCREMENT,
    room_num VARCHAR(50) UNIQUE,
    name VARCHAR(255),
    nodeID INT,
    requires_auth BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (nodeID) REFERENCES nodes(id),
    PRIMARY KEY (id)
  );


--Alterations made

  ALTER TABLE nodes ADD COLUMN floorID int after buildings;
  ALTER TABLE nodes ADD FOREIGN KEY (floorID) REFERENCES floors(floorID);
  ALTER TABLE nodes DROP COLUMN buildings;
  ALTER TABLE nodes ADD COLUMN buildingID after floorID;
  ALTER TABLE nodes ADD FOREIGN KEY (buildingID) REFERENCES buildings(id); 
