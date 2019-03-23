<?php
if(isset($_SESSION["username"]) || isset($_SESSION["expires_in"])){
    if($_SESSION["expires_in"] <= time()){
        unset($_SESSION["expires_in"]);
        unset($_SESSION["username"]);
    }
    else
        header("Location: home");
}
?>
<!DOCTYPE html>

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Login</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" type="text/css">
    <link rel="shortcut icon" href="assets/favicon.png" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.2/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="js/regNewAccount.js"></script>
    
    <style type="text/css">
        body {
            background: url(assets/background.jpg) no-repeat center center fixed;
            -webkit-background-size: cover;
            -moz-background-size: cover;
            -o-background-size: cover;
            background-size: cover;
            height: 100%;
            overflow: hidden;
        }
        .login-form {
                width: 340px;
            margin: 50px auto;
        }
        .login-form form {
            margin-bottom: 15px;
            background: #f7f7f7;
            box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
            padding: 30px;
        }
        .login-form h2 {
            margin: 0 0 15px;
        }
        .form-control, .btn {
            min-height: 38px;
            border-radius: 2px;
        }
        .btn {
            font-size: 15px;
            font-weight: bold;
        }
        img{
            /* max-height:160px; */
            max-width:290px;
            height:auto;
            width:auto;
        }
    </style>
</head>

<body>
    <div class="login-form">
        <form action="login" method="post">
            <img src="assets/logo4-final.png" class="img-fluid">

            <h2 class="text-center">Log in</h2>

            <div class="form-group">
                <input type="text" id='usernameField' name="username" class="form-control" placeholder="Username" required>
            </div>

            <div class="form-group">
                <input type="password" id='passwordField' name="password" class="form-control" placeholder="Password" required>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary btn-block">Log in</button>
            </div>

            <!-- <div class="clearfix">
                <label class="pull-left checkbox-inline"><input type="checkbox"> Remember me</label>
                <a href="#" class="pull-right">Forgot Password?</a>
            </div> 

            <br> -->
            <button type="button" class="btn btn-info btn-block" onclick='createAccount()' disabled>Create an Account</button>
        </form>
    </div>
</body>
</html>
