<?php
header('Content-Type: text/html;charset=utf-8');
//podatci za mysql bazu
$mysql_host = "mysql4.000webhost.com";
$mysql_database = "a5618480_socme";
$mysql_user = "a5618480_toni";
$mysql_password = "g78dfc6";
//varijable prosljeden kroz url
$email = $_REQUEST['email'];
$lastPost = $_REQUEST['lastpost'];

//spajanje na bazu
$conn = new mysqli($mysql_host, $mysql_user, $mysql_password, $mysql_database);
$conn->set_charset("utf8");
//Provjeri dali se uspjesno spojio na bazu
if($conn->connect_error)
{
      die("Connection Failed: " . $conn->connect_error);
}

$sqlCommand = "SELECT Follow FROM Accounts WHERE Email = '$email'";
if($result = $conn->query($sqlCommand))
{
     $row = $result->fetch_row();
     $followers = explode(",",$row[0]);
     $getPostsCommand ="SELECT * FROM Posts WHERE Email = '$email' OR ";
     foreach($followers as $follower)
     {
          if($follower != "")
          {
              $getPostsCommand = $getPostsCommand."Email = '".$follower."' OR ";
          }
     }
     $getPostsCommand = substr($getPostsCommand, 0, -3);
     $getPostsCommand = $getPostsCommand."ORDER BY Date DESC LIMIT 10 OFFSET $lastPost";
     $resultPosts = $conn->query($getPostsCommand);
     while($row = $resultPosts->fetch_row())
     {
         $sqlCommandAccountName = "SELECT FirstName, LastName FROM Accounts Where Email = '$row[0]'";
         $resultAccountInfo = $conn->query($sqlCommandAccountName);
         $rowAccountInfo = $resultAccountInfo->fetch_row();
         printf("%s %s\n",$rowAccountInfo[0],$rowAccountInfo[1]);
         printf("%s\n",$row[0]);
         printf("%s\n",$row[1]);
         printf("%s\n",$row[2]);
     }

}

$conn->close();
?>		