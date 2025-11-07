Read these instructions:

https://support.comodo.com/index.php?_m=knowledgebase&_a=viewarticle&kbarticleid=1204&nav=0,95
https://support.comodo.com/index.php?_m=knowledgebase&_a=viewarticle&kbarticleid=1212

1. Download the Comodo "[New] PositiveSSL Plesk/cPanel Bundle" from this URL:

https://support.comodo.com/index.php?_m=downloads&_a=view&parentcategoryid=4&pcid=1&nav=0,1

The file is named New-PositiveSSL-bundle-12.ca-bundle and includes the root (AddTrustExternalCARoot.crt) and intermediate (PositiveSSLCA2.crt) certs.

Download cert, private key and intermediate cert from DreamHost:

https://panel.dreamhost.com/index.cgi?tree=domain.secure&current_step=Index&next_step=View&crt_type=comodo&crt_id=49037

Save the files from these websites to ~dev_user_prd as:

New-PositiveSSL-bundle-12.ca-bundle
AddTrustExternalCARoot.crt
PositiveSSLCA2.crt
cs-int.crt
cs.crt
cs.key

PositiveSSLCA2.crt and cookedspecially-intermediate.crt seem to be the same thing

2. Read this article:

http://jazzy.id.au/default/2010/01/21/configuring_tomcat_to_use_apache_ssl_certificates.html 

The trick is, rather than use a JKS repository, which is the native Java SSL certificate store, and what most of the documentation on the web talks about, is use a PKCS12 repository, which is an internet standard, and can be manipulated using standard tools such as openssl. This tool requires three files, which are easy to find from your Apache SSL configuration, one is the private key file, another is the certificate, and finally the certificate signer chain. The command to run is:

openssl pkcs12 -export -in cs.crt -inkey cs.key -out cs.p12 -name cookedspecially -CAfile New-PositiveSSL-bundle-12.ca-bundle  -caname root -chain

If you an error like this:

keytool error: java.security.cert.CertificateException: sun.security.pkcs.ParsingException: DerInputStream.getLength(): lengthTag=54, too big.

Remove the line breaks from the body of the cert, between the BEGIN and END lines.

4. Configure tomcat by adding these configurations to point directly at the p12 file:

    <!-- https://tomcat.apache.org/tomcat-6.0-doc/ssl-howto.html -->
    <Connector 
           port="8443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           keystoreFile="/home/dev_user_prd/cs.p12" keystoreType="PKCS12" keystorePass="DevU$3r"
           clientAuth="false" sslProtocol="TLS"/>
	<Connector 
           port="443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           keystoreFile="/home/dev_user_prd/cs.p12" keystoreType="PKCS12" keystorePass="DevU$3r"
           clientAuth="false" sslProtocol="TLS"/>

The tomcat server configuration file is located here:

sftp:/dev_user_prd:@www.bakedspecially.com/opt/apache-tomcat-7.0.37/conf/server.xml

Then restart tomcat and monitor the log to see that it does actually start:

export TOMCAT_HOME=/opt/apache-tomcat-7.0.37
cd $TOMCAT_HOME;ls;sudo /etc/init.d/tomcat.sh stop;sudo /etc/init.d/tomcat.sh start;
tail -f /opt/apache-tomcat-7.0.37/logs/catalina.out

Finally, visit these URLs to check the SSL configuration from the client side:

https://sslcheck.globalsign.com/en_US/sslcheck?host=www.cookedspecially.com#67.205.8.129-srv-misc-ssl-cert-reg
https://ssltools.websecurity.symantec.com/checker/views/certCheck.jsp

==========
BOGUS PROCEDURE TRIED ORIGINALLY FOLLOWS:
==========

1. Download cert, private key and intermediate cert from DreamHost:

https://panel.dreamhost.com/index.cgi?tree=domain.secure&current_step=Index&next_step=View&crt_type=comodo&crt_id=49037

2. Save them to ~dev_user_prd as:

cookedspecially.crt
cookedspecially-intermediate.crt
cookedspecially.key

3. Create a combined p12 certificate and private key:

openssl pkcs12 -export -in cookedspecially.crt -inkey cookedspecially.key -out cookedspecially-cert-and-key.p12 -name cookedspecially -CAfile cookedspecially-intermediate.crt -caname root

For more info, see:

http://cunning.sharp.fm/2008/06/importing_private_keys_into_a.html

4. Create a new keystore file.

[ps167937]$ keytool -genkey -alias cookedspecially -keyalg RSA
Enter keystore password:  
Re-enter new password: 
They don't match. Try again
Enter keystore password:  
Re-enter new password: 
What is your first and last name?
  [Unknown]:  Erich Champion
What is the name of your organizational unit?
  [Unknown]:  Internet Security Division
What is the name of your organization?
  [Unknown]:  Cooked Specially
What is the name of your City or Locality?
  [Unknown]:  Scotts Valley
What is the name of your State or Province?
  [Unknown]:  California
What is the two-letter country code for this unit?
  [Unknown]:  CA
Is CN=Erich Champion, OU=Internet Security Division, O=Cooked Specially, L=Scotts Valley, ST=California, C=CA correct?
  [no]:  yes

Enter key password for <tomcat>
	(RETURN if same as keystore password):  
Re-enter new password: 

4. Follow these instructions to import the intermediate cert:

http://tomcat.apache.org/tomcat-6.0-doc/ssl-howto.html#Installing_a_Certificate_from_a_Certificate_Authority

keytool -import -alias root -keystore .keystore -trustcacerts -file cookedspecially-intermediate.crt 

If you an error like this:

keytool error: java.security.cert.CertificateException: sun.security.pkcs.ParsingException: DerInputStream.getLength(): lengthTag=54, too big.

Remove the line breaks from the body of the cert, between the BEGIN and END lines.

If you follow the tomcat instructions and try to import our cert, you'll see an error about not having a matching private key.

** DOESN'T WORK **
keytool -import -alias cookedspecially -keystore .keystore -file cookedspecially.certificate

5. Import our cert and key combo:

keytool -importkeystore -deststorepass 'DevU$3r' -destkeypass 'DevU$3r' -destkeystore .keystore -srckeystore cookedspecially-cert-and-key.p12 -srcstoretype PKCS12 -srcstorepass 'DevU$3r' -alias cookedspecially

6. Check the keystore by running:

keytool -list -v -keystore .keystore

7. Configure and restart tomcat by adding:

    <!-- https://tomcat.apache.org/tomcat-6.0-doc/ssl-howto.html -->
	<Connector 
           port="8443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           keystoreFile="${user.home}/.keystore" keystorePass="DevU$3r"
           clientAuth="false" sslProtocol="TLS"/>

To:

sftp:/dev_user_prd:@www.bakedspecially.com/opt/apache-tomcat-7.0.37/conf/server.xml
