<html>
    <?php require_once("auth_header.php"); ?>
    <head>
        <title>Pathfinder</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
        <link rel="shortcut icon" href="assets/favicon.png" />
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
        
        <script src="https://cdnjs.cloudflare.com/ajax/libs/p5.js/0.7.3/p5.js"></script>
        <script src="js/adminPanel.js"></script>
    </head>
    <body>
        <div id="main">
            <?php
                include ('nav.html');
            ?>
            
            <div class="d-flex justify-content-center bg-light">
                <div class="form-group p-1">
                    <div class="input-group mt-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">Building Name:</span>
                        </div>
                        <select class="form-control" id="buildingList" onchange="populateFloorsChange()" onfocus="populateFloorsFocus()">
                            <option active id='tempOptionBuilding'>Select</option>
                        </select>
                    </div>
                </div>
                <div class="form-group p-1">
                    <div class="input-group mt-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">Floor Number:</span>
                        </div>
                        <select class="form-control" id="floorList" onchange="changeImageChange()" onfocus="changeImageFocus()" disabled>
                            <option active id='tempOptionFloor'>Select</option>
                        </select>
                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-center bg-light">
                <div class="btn-group-vertical p-2">
                    <button type="button" class="btn btn-outline-primary" id="addBeaconButton" onclick="setMode('addBeacon')">Place Beacon</button>
                    <button type="button" class="btn btn-outline-primary" id="removeBeaconButton" onclick="setMode('removeBeacon')">Remove Beacon</button>
                </div>
                <div class="btn-group p-2">
                    <button type="button" class="btn btn-outline-primary" id="addHallwayButton" onclick="setMode('addHallway')">Define Hallway</button>
                </div>
                <div class="btn-group-vertical p-2">
                    <button type="button" class="btn btn-outline-primary" id="addDestinationButton" onclick="setMode('addDestination')">Define Destination</button>
                    <button type="button" class="btn btn-outline-primary" id="removeDestinationButton" onclick="setMode('removeDestination')">Remove Destination</button>
                </div>
            </div>

            <div class="d-flex justify-content-between">

                <div class="container-fluid m-2">
                    <div class="row">
                        <div class="col-sm-6">
                            <div class="card">
                                <div class="card-header bg-dark text-white">Beacons:</div>
                                <div class="card-body" id="listviewBeacons">
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-6">
                            <div class="card">
                                <div class="card-header bg-dark text-white">Destinations:</div>
                                <div class="card-body" id="listviewNodes">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <span class="border border-dark m-2">
                    <div id="sketch-holder"></div>
                </span>
            </div>

            <div class="contianer-fluid m-2">
                <button type="button" class="btn btn-block btn-outline-success" id="submitButton" onclick="sendToDB()" disabled>Submit</button>
            </div>

        </div>

        <?php
            include ('footer.html');
        ?>        

    </body>
</html>