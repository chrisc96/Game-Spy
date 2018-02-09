

<?php
$start = microtime(true);

$servername = "45.55.230.56";
$username = "lockie";
$password = "lockie";
$dbname = "site";

$time_start = microtime(true); 

$lockie = array('USD','EUR', 'JPY', 'GBP', 'AUD', 'CAD', 'CHF', 'CNY', 
'MXN','SEK','NZD','SGD','HKD','NOK','KRW','TRY','INR','RUB','BRL','ZAR','DKK','PLN','TWD','THB','MYR',);

foreach ($lockie as $tag) {

$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$json = file_get_contents("http://www.apilayer.net/api/live?access_key=16f4c1a10e0734d4768b7a2e2882f133&format=1&currencies=".$tag."");
$data = json_decode($json);
$value = $data->quotes->{'USD' . $tag};
echo $tag . " - " . $value . PHP_EOL;

$sql = "UPDATE currencies SET USD_base='$value' WHERE tag='$tag'";

if ($conn->query($sql) === TRUE) {
} else {
    echo "Error updating record: " . $conn->error;
}
}

$end = microtime(true);
$time = number_format(($end - $start), 2);
 
echo 'This page loaded in ', $time, ' seconds';

?>

