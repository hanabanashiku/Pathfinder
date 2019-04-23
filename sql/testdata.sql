--testing buildings table

Insert into buildings (id, name, address, map_image_path) values ('1', 'EC', '115 Library Drive', '/var/www/Pathfinder/website/html/uploads/EC');

--inserting test accounts to users table
Insert into users (id, username, userPassword, email, lname, fname) values ('2', 'dgstewart', '$2y$10$FNMPuDN/nN9HpeHwhO6MU.H7Z1DND3MZBI/taS0TS2bDQmky2Ea.a', 'dgstewart@oakland.edu', 'Stewart', 'Dave');

Insert into users (id, username, userPassword, email, lname, fname) values ('3', 'juspetran', '$2y$10$FNMPuDN/nN9HpeHwhO6MU.H7Z1DND3MZBI/taS0TS2bDQmky2Ea.a', 'jsupetran@oakland.edu', 'Supetran', 'John');

Insert into users (id, username, userPassword, email, lname, fname) values ('4', 'mmaclean', '$2y$10$FNMPuDN/nN9HpeHwhO6MU.H7Z1DND3MZBI/taS0TS2bDQmky2Ea.a', 'mmaclean@oakland.edu', 'Maclean', 'Michael');

Insert into users (id, username, userPassword, email, lname, fname) values ('5', 'zfsteffes', '$2y$10$FNMPuDN/nN9HpeHwhO6MU.H7Z1DND3MZBI/taS0TS2bDQmky2Ea.a', 'zfsteffes@oakland.edu', 'Steffes', 'Zachary');
