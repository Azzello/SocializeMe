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

//Provjeri dali se uspjesno spojio na bazu
if($conn->connect_error)
{
      die("Connection Failed: " . $conn->connect_error);
}

$sqlCommand = "SELECT * FROM Posts WHERE Email = '$email' ORDER BY Date DESC LIMIT 10 OFFSET $lastPost";
if($result = $conn->query($sqlCommand))
{
     if($result->num_rows > 0)
     {
          while($row = $result->fetch_row())
          {
                printf("%s\n",$row[1]);//napisi post
                printf("%s\n",$row[2]);//napisi datum posta
          }
          $result->close();
     }  
}

$conn->close();
?>		