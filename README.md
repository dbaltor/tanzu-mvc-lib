# MVC application running on Tanzu Application Service (Former Pivotal Cloud Foundry)
<br>
This simple demo aims to show the power of Tanzu Application Service (TAS) platfom when it comes to scale out application instances (AIs) easily and attach backing services thereon.
<br>
<br>
<img src="MVC.png"> 
<br>
This simple application detetcs when it is running on Tanzu Application Service (TAS) and configures accordingly its home page.<br>
Different AIs will render the UI using different background colors when running on TAS, up to 4 different colors.<br>
<br>
You can generate 10 readers at a time clicking on the <i>Load Readers</i> button.<br>
You can generate 100 books at a time clicking on the <i>Load Books</i> button. The first 40 books will evenly be assigned to some readers.<br>
You can visualise the list of readers and books following the corresponding links. Use the browser's back buttom to return to the home page.<br>
<p/>
<p/>
<h2>Testing locally:</h2>
<br>
<code>java -jar build/libs/library-mvc-1.0.0.jar</code><br>
<p/>
<ins>URL:</ins><br>
localhost:8080
<p/>
<p/>
<h2>Testing on the Cloud:</h2>
<br>
1. Publish the application on TAS using the <code>cf push</code>command.<br>
<br>
Look for the route created on the output generated by the previous command, e.g.<br>
<i>
<br>Waiting for app to start...
<br>
<br>name:              tanzu-lib
<br>requested state:   started
<br><b>routes:            tanzu-lib-brash-shark-sb.cfapps.io</b>
</i>
<br>
<br>
2. Use the published route to access the application running on TAS. You will notice that the home page looks different now. The application has detected it is running on TAS :) <br>
<ins>URL:</ins><br>
<code>http://&ltrandom-name&gt.cfapps.io</code><br>
<br>
<br>
3. Scale out the application via the <code>cf scale app &ltrandom-name&gt -i 3</code>command.<br>
<br>
You will notice that data generated in one AI is NOT available to others. This is due the data being stored in each AI's memory.<br>
We can easily fix this if our TAS installation has been configured with a SQL database Service Broker.<br>
Assuming we have got <i>cleardb</i> service available, we shall bind a service instance (SI) to our application.<br>
<br>
4. Create a service instance through the <code>cf create-service cleardb spark library-db</code> command.<br>
<br>
5. Declare to the platform that the application now requires the newly created <b>library-db</b> service instance adding its name on the <i>services</i> section at the end of the <i>manifest.yml</i> file. The file should look like as below after being edited:<br>
<pre>
---
applications:
- name: tanzu-lib
  path: build/libs/library-mvc-1.0.0.jar
  random-route: true
  env:
    JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+ } }'
  buildpacks:
  - https://github.com/cloudfoundry/java-buildpack.git
  services:
  - library-db
</pre><br>
<br>
6. Redeploy the application using <code>cf push</code>.<br>
<br>
7. Go back to the application and generate more data. You can follow some of the links to lists and refresh the Browser multiple times. You will notice the same data shows up across all AIs (in different background colors).
<p/>
<p/>
<h2>Cleaning up:</h2>
<br>
8. <code>cf delete tanzu-lib -r</code><br>
9. <code>cf delete-service library-db</code><br>
<p/>
<p/>
<h2>Architectural Decisions:</h2>
<br>
1) The application implements the Model-View-Controller (MVC) architectural pattern which is easy when using the Spring framework.<br>
<br>
2) A SQL database has been chosen as data store. The H2 embedded in-memory database is used by default when running locally.<br>

