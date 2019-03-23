<?php
    require_once("assets/Database.php");
    
    if(!isset($_GET["q"])){
        header("Location: index");
        die();
    }
    
    $code = mysqli_real_escape_string($_GET["q"]);
    
    $db = new Database();
    
    $q = $db->query("SELECT user, new_user FROM pending_emails WHERE code = `$code`");
    // TODO Error code page
    if($q->num_rows() !== 1){
        die("Email link no longer valid.");
    }
    
    $q = $q->fetch_assoc();
    $id = $q["user"];
    $new = $q["new_user"];
    
    try{
        $q = $db->query("DELETE FROM pending_emails WHERE user = `$id` LIMIT 1");
    }
    catch(mysqli_sql_exception $e){
         die($db->error);
     }
     
    // the user is verifying their email address
    if($new){
        // log them in
        $q = $q->query("SELECT * FROM users WHERE id = `$id`");
        $q = $q->fetch_assoc();
        
        session_start();
        $_SESSION["username"] = $q["username"];
        $_SESSION["first_name"] = $q["fname"];
        $_SESSION["last_name"] = $q["lname"];
        $_SESSION["expires_in"] = time() + 3600;
        header("Location: home");
    }
    
    // the user is resetting their password
    else{
        session_start();
        $_SESSION["user_id"] = $id;
        $_SESSION["reset_ok"] = true;
        header("Location: password_reset");
    }
?>