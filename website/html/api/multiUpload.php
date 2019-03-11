<?php
    // session_start();
    // $servername = "localhost";
    // $username = "uploader";
    // $password = "Senior19!";
    // $dbname = "Navigation";

    // // Create connection
    // $conn = mysqli_connect($servername, $username, $password, $dbname);
    // // Check connection
    // if (!$conn) {
    //     die("Connection failed: " . mysqli_connect_error());
    // }

    // // Escape user inputs for security
    // $name = mysqli_real_escape_string($conn, $_POST['folderName']);
    // $addr = '';
    // $mip = '';
    // $owner = mysqli_real_escape_string($conn, $_SESSION['username']);

    // $sql = "INSERT INTO buildings (name, address, map_image_path, owner) VALUES
    // ('$name', '$addr', '$mip', '$owner')";

    // if (mysqli_query($conn, $sql)) {
    //     echo "New record created successfully";
    // } else {
    //     echo "Error: " . $sql . "<br>" . mysqli_error($conn);
    // }
    // mysqli_close($conn);

    $target_file = "../uploads/" . $_POST['folderName'] . '/' .  $_POST['name'];
    move_uploaded_file($_FILES["file"]["tmp_name"], $target_file);
?>