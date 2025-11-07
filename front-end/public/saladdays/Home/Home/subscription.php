<?php

require_once 'swiftmailer/lib/swift_required.php';

$fname = $_POST['fname'];
$lname = $_POST['lname'];
$mobile = $_POST['mobile'];
$email = $_POST['email'];
$dd = $_POST['dd'];
$mm = $_POST['mm'];
$yyyy = $_POST['yyyy'];
$deliveryTime = $_POST['deliveryTime'];
$subscriptionType = $_POST['subscriptionType'];
$address = $_POST['address'];

$mailBody = "Email : " . $email . "\n";
$mailBody = $mailBody . "First Name : " . $fname . "\n";
$mailBody = $mailBody . "Last Name : " . $lname . "\n";
$mailBody = $mailBody . "Mobile : " . $mobile . "\n";
$mailBody = $mailBody . "Start Date : " . $dd . "/" . $mm . "/" . $yyyy ."\n";
$mailBody = $mailBody . "Delivery Time : " . $deliveryTime . "\n";
$mailBody = $mailBody . "Subscription Type : " . $subscriptionType . "\n";
$mailBody = $mailBody . "Address : " . $address . "\n";

$transport = Swift_SmtpTransport::newInstance('smtp.gmail.com', 465, "ssl")
  ->setUsername('saladdaysbot@gmail.com')
  ->setPassword('JVDisAMC10');

$mailer = Swift_Mailer::newInstance($transport);

$mailMessage = Swift_Message::newInstance($subjsct)
  ->setFrom(array('saladdaysbot@gmail.com' => 'Saladdays Bot'))
  ->setTo(array('hello@saladdays.co'))
  ->setSubject('Subscription Request')
  ->setBody($mailBody);

$result = $mailer->send($mailMessage);

?>