<?php
    error_reporting(E_ALL);
    ini_set(`display_errors`, True);
    //ini_set(`display_startup_errors`, True);
    //$db = mysqli_connect('localhost', 'ksmith', 'Angela10!', 'ksmith')
    //or die('Error connecting to MySql server');
    //session_start();
    require('/var/www/html/dbConnect.php');
?>

<!doctype html>
<html lang="en">
    <head>
        <title>Project User Managment</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
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
                    <div class="card-header">Testing File Upload</div>
                    <div class="card-body">
                        <form action="fileUP.php" method="post" enctype="multipart/form-data">
                            Select a map pdf to upload
                            <input type="file" name="fileToUpload" id="fileToUpload">
                            <input type="submit" value="Upload Image" name="submit">
                        </form>
                    </div>
                </div>
            </div>

            <?php
                include ('footer.html');
            ?>
        </div>
    </body>
</html>