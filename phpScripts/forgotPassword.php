<?php
$mysql_host = "mysql4.000webhost.com";
$mysql_database = "a5618480_socme";
$mysql_user = "a5618480_toni";
$mysql_password = "g78dfc6";

$email = $_REQUEST['email'];

//spajanje na bazu
$conn = new mysqli($mysql_host, $mysql_user, $mysql_password, $mysql_database);

$sqlCommand = "SELECT Password FROM Accounts WHERE Email = '$email' ";

$result = $conn->query($sqlCommand);
$rowResult = $result->fetch_row();

$message = "Password was requested for this account. Your current password is: ".$rowResult[0];
// In case any of our lines are larger than 70 characters, we should use wordwrap()
$message = wordwrap($message, 70, "\r\n");

// Send
mail('$email', 'Forgotten Password', $message, 'From: '. "info@socializeme.site90.com");
?>