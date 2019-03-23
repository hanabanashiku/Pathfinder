<?php

?>
<!doctype html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Project Hasher</title>
</head>
<body>
<p>
<?php

echo "<ul><li>Here is the password:</li></ul>";
echo "</br>";
$password="csi4999";
$hashed_password=password_hash($password, PASSWORD_DEFAULT);
echo $hashed_password;
?>
</p>
</body>
</html>