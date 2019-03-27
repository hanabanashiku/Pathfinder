<?php
    session_start();
    $servername = "localhost";
    $username = "uploader";
    $password = "Senior19!";
    $dbname = "Navigation";

    // Create connection
    $conn = mysqli_connect($servername, $username, $password, $dbname);
    // Check connection
    if (!$conn) {
        die("Connection failed: " . mysqli_connect_error());
    }

    $currentUser = $_SESSION['username'];
    $sql = "SELECT * FROM buildings WHERE owner = '$currentUser'"; 
    if ($res = mysqli_query($conn, $sql)) {
        if (mysqli_num_rows($res) > 0) {
            while ($row = mysqli_fetch_array($res)) {
                $buildingID = $row['id'];
            }
            // mysqli_free_res($res);  //This is breaking the .php file, not sure why...
        }
        else {
            // echo "No matching records are found.";
        }
    }
    else {  
        // echo "ERROR: Could not able to execute $sql. " .mysqli_error($conn);
    }

    // Escape user inputs for security
    $floorNumber = mysqli_real_escape_string($conn, $_POST['floorNumber']);
    $target_file = "../uploads/" . $_POST['folderName'] . '/' .  $_POST['fileName'];
    $pathToImage = mysqli_real_escape_string($conn, $target_file);

    $sql = "INSERT INTO floors (floor_number, buildingID, path_to_image) VALUES
    ('$floorNumber', '$buildingID', '$pathToImage')";

    if (mysqli_query($conn, $sql)) {
        echo "New record created successfully";
    } else {
        echo "Error: " . $sql . "<br>" . mysqli_error($conn);
    }
    mysqli_close($conn);

    
    move_uploaded_file($_FILES["file"]["tmp_name"], $target_file);
?>