 <?php
 define('DBHOST', 'localhost');
 define('DBUSER', 'uploader');
 define('DBPASS', 'Senior19!');
 define('DBNAME', 'SeniorCapstone');
 $db = mysqli_connect(DBHOST,DBUSER,DBPASS,DBNAME);
 if ( !$db ) {
 die("Connection failed : " . mysql_error()); 
 }
?>