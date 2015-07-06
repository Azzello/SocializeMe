<?php
header('Content-Type: text/html;charset=utf-8');
//podatci za mysql bazu
$mysql_host = "mysql4.000webhost.com";
$mysql_database = "a5618480_socme";
$mysql_user = "a5618480_toni";
$mysql_password = "g78dfc6";
//varijable prosljeden kroz url
$email = $_REQUEST['email'];
$content = $_REQUEST['content'];
date_default_timezone_set("Europe/Zagreb");//promjeni vremensku zonu
$date = date('Y-m-d H:i:s');

//spajanje na bazu
$conn = new mysqli($mysql_host, $mysql_user, $mysql_password, $mysql_database);

//Provjeri dali se uspjesno spojio na bazu
if($conn->connect_error)
{
      die("Connection Failed: " . $conn->connect_error);
}

//podesi encoding na UTF8
mysqli_query($conn, "SET NAMES 'UTF8'") or die("ERROR: ". mysqli_error($conn));




$sqlCommand = "INSERT INTO Posts(Email, Content, Date) VALUES('$email', '$content','$date')";
if($conn->query($sqlCommand) === TRUE)
{
  echo "Post Created!";
}
else
{
  die("Failed at creating new post!");
}

$conn->close();
?>	