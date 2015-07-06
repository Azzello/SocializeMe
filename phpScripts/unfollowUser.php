<?php
header('Content-Type: text/html;charset=utf-8');
//podatci za mysql bazu
$mysql_host = "mysql4.000webhost.com";
$mysql_database = "a5618480_socme";
$mysql_user = "a5618480_toni";
$mysql_password = "g78dfc6";
//varijable prosljeden kroz url
$loggedEmail = $_REQUEST['loggedEmail'];
$profileEmail = $_REQUEST['profileEmail'];

//spajanje na bazu
$conn = new mysqli($mysql_host, $mysql_user, $mysql_password, $mysql_database);

//Provjeri dali se uspjesno spojio na bazu
if($conn->connect_error)
{
      die("Connection Failed: " . $conn->connect_error);
}

$sqlCommand = "SELECT Follow FROM Accounts WHERE Email = '$loggedEmail' ";
if($result = $conn->query($sqlCommand))
{
     if($result->num_rows > 0)
     {
          $hasFound = 0;
          $row = $result->fetch_row();
          //echo $row[0];
          $followingEmails = explode(",",$row[0]);
          $newFollowingEmailsString = "";
          foreach($followingEmails as $followingEmail)
          {
               if($followingEmail != $profileEmail && $followingEmail != "")
               {
                   $newFollowingEmailsString = $newFollowingEmailsString.$profileEmail.",";
               }
          }
          echo $newFollowingEmailsString;
          $sqlCommand = "UPDATE Accounts SET Follow='$newFollowingEmailsString' WHERE Email='$loggedEmail'";
          $conn->query($sqlCommand);
          $result->close();
     } 
}

$conn->close();
?>		