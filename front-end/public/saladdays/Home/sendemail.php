<?php

require_once 'swiftmailer/lib/swift_required.php';

$email = $_POST['email'];
$subject = $_POST['subject'];
$message = $_POST['message'];

$transport = Swift_SmtpTransport::newInstance('smtp.gmail.com', 465, "ssl")
  ->setUsername('saladdaysbot@gmail.com')
  ->setPassword('JVDisAMC10');

$mailer = Swift_Mailer::newInstance($transport);

$mailMessage = Swift_Message::newInstance($subjsct)
  ->setFrom(array('saladdaysbot@gmail.com' => 'Saladdays Bot'))
  ->setTo(array('hello@saladdays.co'))
  ->setSubject($subject)
  ->setBody("From : " . $email . "\n" . $message);

$result = $mailer->send($mailMessage);

?>