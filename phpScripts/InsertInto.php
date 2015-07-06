<?php
header('Content-Type: text/html;charset=utf-8');
//podatci za mysql bazu
$mysql_host = "mysql4.000webhost.com";
$mysql_database = "a5618480_socme";
$mysql_user = "a5618480_toni";
$mysql_password = "g78dfc6";
//varijable prosljeden kroz url
$firstname = $_REQUEST['firstname'];
$lastname = $_REQUEST['lastname'];//10
$email = $_REQUEST['email'];
$password = $_REQUEST['password'];
$country = $_REQUEST['country'];
//spajanje na bazu
$conn = new mysqli($mysql_host, $mysql_user, $mysql_password, $mysql_database);

//Provjeri dali se uspjesno spojio na bazu
if($conn->connect_error)
{
      die("Connection Failed: " . $conn->connect_error);//20
}

//podesi encoding na UTF8
mysqli_query($conn, "SET NAMES 'UTF8'") or die("ERROR: ". mysqli_error($conn));

//Provjeri dali email postoji u bazi podataka
$sqlCommand = "SELECT * FROM Accounts WHERE Email = '$email'";
$result = $conn->query($sqlCommand);
$numberofrows = $result->num_rows;

if($numberofrows > 0)
{
die ("Email in use!");
}


$sqlCommand = "INSERT INTO Accounts(FirstName, LastName, Email, Password, Country) VALUES('$firstname','$lastname','$email','$password','$country')";
if($conn->query($sqlCommand) === TRUE)
{
  echo "Account Created!";
}
else
{
  die("Failed at creating new account");
}

$conn->close();
?>	