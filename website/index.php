<!DOCTYPE html>
<html lang="en">
<head>
    <title>Pathfinder</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>

    <?php
    	include ('functions.php');
        include ('sideNav.html');
    ?>

    <div id="main">
        
        <?php
            include ('banner.html');
        ?>

        <div class="container">

            <!-- <div class="panel panel-primary" style="margin-top: 20px;">
                <div class="panel-heading">System Information</div>
                <div class="panel-body">
                    <div class="panel-group">
                        <div class="row">
                            <div class="col-md-8">
                                <div class="panel panel-default">
                                    <div class="panel-heading">System Type</div>
                                    <div class="panel-body">SD3 Servo</div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="panel panel-success">
                                    <div class="panel-heading">IP Address</div>
                                    <div class="panel-body"><?php echo $_SERVER['SERVER_ADDR']; ?></div>
                                </div>  
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-md-12">
                                <div class="panel panel-danger">
                                    <div class="panel-heading">Project Information</div>
                                    <div class="panel-body"><?php echo program_project_info() ?></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div> -->

            <!-- <div class="panel panel-default">
                <div class="panel-heading">System Status</div>
                <div class="panel-body">
                    <table class="table">
                        <tbody>
                            <tr>
                                <td>Up time</td>
                                <td><?php echo uptime(); ?></td>
                            </tr>
                            <tr>
                                <td>SD Card Health</td>
                                <td><?php echo card_is_ok(); ?></td>
                            </tr>
                            <tr>
                                <td>Knight Firmware</td>
                                <td><?php echo isrunning('sd3 -f KNIGHT'); ?></td>
                            </tr>
                            <tr>
                                <td>User program</td>
                                <td><?php echo isrunning('user_program'); ?></td>
                            </tr>
                            <tr>
                                <td>I/O Client</td>
                                <td><?php echo isrunning('io_client'); ?></td>
                            </tr>
                            <tr>
                                <td>Modbus server</td>
                                <td><?php echo isrunning('mbsrv'); ?></td>
                            </tr>
                            <tr>
                                <td>S7 server</td>
                                <td><?php echo isrunning('s7srv'); ?></td>
                            </tr>
                            <tr>
                                <td>Eth/IP server</td>
                                <td><?php echo isrunning('eipsrv'); ?></td>
                            </tr>
                        </tbody>    
                    </table>
                </div>
            </div> -->

        </div>

    </div>

    <?php
        include ('footer.html');
    ?>

</body>
</html>