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
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
        <link rel="shortcut icon" href="assets/favicon.png" />
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
        <script src="js/regNewBuilding.js"></script>
    </head>

    <body>
        <div id="main">
            <?php
                include ('nav.html');
            ?>

            <br>
            
            <div class="container">
                <div class="card">
                    <div class="card-header">
                        <div class="form-group">
                            <label for="buildingName">Building Name:</label>
                            <input type="text" class="form-control" id="buildingName">
                        </div>
                    </div>
                    <div class="card-body">

                        <!-- Saved for Ref. -->
                        <!-- <form action="fileUP.php" method="post" enctype="multipart/form-data">
                            Select the floor map (if multiple floors exist, please select the first floor) map to upload<br>
                            <input type="file" name="fileToUpload" id="fileToUpload"><br>
                            <input type="submit" value="Upload Image" name="submit"><br>
                        </form> -->

                        <form action="fileUP.php" method="post" enctype="multipart/form-data" id="fileList">
                            <p>Select the floor map (if multiple floors exist, please select the first floor) map to upload:</p>
                            
                            <div id="listview">
                            </div>

                            <button type="button" class="btn btn-info btn-block" id="addFloorButton" onclick='addNewFloor()';>+</button>

                            <div class="mt-3">
                                <input type="submit" class="btn btn-success btn-block" value="Create Building" name="submit"><br>
                            </div>
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