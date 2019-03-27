<?php
    // error_reporting(E_ALL);
    // ini_set(`display_errors`, True);
    //ini_set(`display_startup_errors`, True);
    //$db = mysqli_connect('localhost', 'ksmith', 'Angela10!', 'ksmith')
    //or die('Error connecting to MySql server');
    // require('/var/www/html/dbConnect.php');
    session_start();
?>
<?php require_once("auth_header.php"); ?>
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

            <!-- Success Toast -->
            <div class="position-absolute w-100 d-flex flex-column p-4">
                <div class="toast ml-auto" data-autohide="false">
                    <div class="toast-header bg-success">
                        <strong class="mr-auto">Upload Complete</strong>
                        <small class="text-muted">just now</small>
                        <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="toast-body">
                    You may leave this page at any time.
                    </div>
                </div>
            </div>

            <br>

            <div class="container">

                <div class="card">
                    <div class="card-header bg-light">
                        <div class="form-group">
                            <label for="buildingName">Building Name:</label>
                            <input type="text" class="form-control" id="buildingName" onkeyup="checkName()">
                        </div>
                    </div>
                    <div class="card-body">
                    
                        <!-- Saved for Ref. -->
                        <!-- <form action="fileUP.php" method="post" enctype="multipart/form-data">
                            Select the floor map (if multiple floors exist, please select the first floor) map to upload<br>
                            <input type="file" name="fileToUpload" id="fileToUpload"><br>
                            <input type="submit" value="Upload Image" name="submit"><br>
                        </form> -->

                        <form enctype="multipart/form-data" id="fileList">
                            <p>Give the building a name and then begin to add floors along with their floor number:</p>
                            
                            <div id="listview">
                            </div>

                            <button type="button" class="btn btn-info btn-block" id="addFloorButton" onclick='addNewFloor()' disabled='true'>+</button>
                            <button type="button" class="btn btn-success btn-block" id="uploadButton" onclick='uploadFiles()' disabled='true'>Upload All</button>
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