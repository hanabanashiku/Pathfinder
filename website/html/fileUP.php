
<?php
    $target_dir = "uploads/";
    $target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
    $uploadOk = 1;
    $imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));

    // Check if image file is a actual image or fake image
    //if(isset($_POST["submit"])) {
    //    $check = getimagesize($_FILES["fileToUpload"]["tmp_name"]);
    //    if($check !== false) {
    //        echo "File is an image - " . $check["mime"] . ". ";
    //        $uploadOk = 1;
    //    } else {
    //        echo "File is not an image.";
    //        $uploadOk = 0;
    //  }
    //}

    // Check if file already exists
    if (file_exists($target_file)) {
        echo "Sorry, file already exists.";
        $uploadOk = 0;
    }

    // Check file size
    if ($_FILES["fileToUpload"]["size"] > 5000000) {
        echo "Sorry, your file is too large.";
        $uploadOk = 0;
    }

    // Allow certain file formats
    if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg"
    && $imageFileType != "gif" && $imageFileType != "pdf") {
        echo "Sorry, only JPG, JPEG, PNG & GIF files are allowed.";
        $uploadOk = 0;
    }

    // Check if $uploadOk is set to 0 by an error
    if ($uploadOk == 0) {
        //echo "Sorry, your file was not uploaded.";
        $_SESSION['uploadSuccess'] = false;
        // if everything is ok, try to upload file
    } else {
        if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)) {
            //echo "The file ". basename( $_FILES["fileToUpload"]["name"]). " has been uploaded.";
            $_SESSION['uploadSuccess'] = true;
        } else {
            // echo "Sorry, there was an error uploading your file.";
            // echo "<br>";
            // echo $target_file;
            // echo "<br>";
            // echo $_FILES["fileToUpload"]["name"];
            $_SESSION['uploadSuccess'] = false;
        }
    }
?>

<!doctype html>
<html lang="en">
    <head>
        <title>Project User Managment</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
        <link rel="shortcut icon" href="assets/favicon.png" />
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
    </head>

    <body>
        <div id="main">
            <?php
                include ('nav.html');
            ?>

            <br>
            
            <div class="container">
                <div class="card">
                    <div class="card-header">Adding New Building</div>
                    <div class="card-body">

                        <?php
                            if ($_SESSION['uploadSuccess'])
                                echo "The file was succesfully uploaded.";
                            else
                                echo "Upload Failed.";
                        ?>
                        
                    </div>
                </div>
            </div>

            <?php
                include ('footer.html');
            ?>
        </div>
    </body>
</html>