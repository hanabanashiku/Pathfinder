<?php
    $servername = "localhost";
    $username = "uploader";
    $password = "Senior19!";
    $dbname = "SeniorCapstone";

    // Create connection
    $conn = mysqli_connect($servername, $username, $password, $dbname);
    // Check connection
    if (!$conn) {
        die("Connection failed: " . mysqli_connect_error());
    }

    // Escape user inputs for security
    $usersname = mysqli_real_escape_string($conn, $_POST['username']);
    $userPassword = mysqli_real_escape_string($conn, $_POST['password']);
    $email = '';
    $lname = '';
    $fname = '';

    $sql = "INSERT INTO users (username, userPassword, email, lname, fname) VALUES
    ('$usersname', '$userPassword', '$email', '$lname', '$fname')";

    if (mysqli_query($conn, $sql)) {
        // echo "New record created successfully";
    } else {
        // echo "Error: " . $sql . "<br>" . mysqli_error($conn);
    }
    mysqli_close($conn);
?>