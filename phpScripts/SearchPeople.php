<?php
header('Content-Type: text/html;charset=utf-8');
//podatci za mysql bazu
$mysql_host = "mysql4.000webhost.com";
$mysql_database = "a5618480_socme";
$mysql_user = "a5618480_toni";
$mysql_password = "g78dfc6";
//varijable prosljeden kroz url
$firstname = $_REQUEST['firstname'];
$lastname = $_REQUEST['lastname'];
//spajanje na bazu
$conn = new mysqli($mysql_host, $mysql_user, $mysql_password, $mysql_database);


//podesi encoding na UTF8
mysqli_query($conn, "SET NAMES 'UTF8'") or die("ERROR: ". mysqli_error($conn));

//Provjeri dali email postoji u bazi podataka
$sqlCommand = "SELECT * FROM `Accounts` WHERE FirstName LIKE '%$firstname%' AND LastName LIKE '%$lastname%'";



$result = $conn->query($sqlCommand);

while($row = $result->fetch_array())
{
echo $row["FirstName"];
echo " ";
echo $row["LastName"];
echo " ";
echo $row["Country"];
echo " ";
echo $row["Email"];
echo "<br>";
}

$conn->close();
?>	