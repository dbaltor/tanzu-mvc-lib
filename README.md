# Denis MVC application running on Tanzu Application Service (Former Pivotal Cloud Foundry)
<br>
This simple demo aims to show the power of Tanzu Application Service (TAS) platfom when it comes to scale out easily application instances (AIs) and attach backing services thereon.
<br>
<br>
<img src="MVC.png"> 
<br>
This simple application detetcs when it is running on Tanzu Application Service (TAS) and configure its home page accordingly.
Different application instances (AIs) will render the UI using different background colors when running on TAS, up to 4 different colors.
<br>
You can generate 10 readers at a time clicking on the button <i>Load Readers</i>.<br>
You can generate 100 books at a time clicking on the button <i>Load Books</i>. The first 40 books will evenly assigned to some readers.<br>
You can visualise the list of readers and books following the corresponding links. Use the Browser's back buttom to return.<br>
<p/>
<p/>
*Testing locally:
=================
<code>java -jar build/libs/library-mvc-1.0.0.jar</code><br>
<p/>
*URL:
=====
localhost:8080
<p/>
<p/>
*Testing on the Cloud:
======================
1. Publish the application on TAS using the <code>cf push</code><br> command.<br>
<br>
Look for the route created on the output generated by the <code>cf push</code> previous command, e.g.<br>
<code>
Waiting for app to start...

name:              tanzu-lib
requested state:   started
routes:            tanzu-lib-brash-shark-sb.cfapps.io
</code>
<br>
2. Use the published route to access the application running on TAS. You will notice that the main page will look different now.<br>
The application has detected it is running on TAS :) <br>
*URL:
=====
http://<random-name>.cfapps.io<br>
<br>
3. Scale out the application via the <code>cf scale app tanzu-lib -i 2</code>command.<br>
<br>
You will notice that data generated in one application instance (AI) is not available to others, as expected. 
We can easily fix this if our TAS installation has been configured with a SQL database Service Broker.
Assuming we have got <i>cleardb</i> service available, we shall bind a service instance (SI) to our application.<br>
<br>
4. Create a service instance through the <code>cf create-service cleardb spark library-db</code> command.<br>
<br>
5. Declare to the platform that the application now requires the newly created <b>library-db</b> service instance adding its name on the <i>services</i> section the end of the <i>manifest.yml</i> file. It should look like as below after being edited:<br>
<code>
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
</code><br>
<br>
6. Redeploy the application using <code>cf push</code>.<br>
<br>
7. Go back to the application, regenerate the data, go to some list and refresh the Browser multiple times. You will notice the same data will show across all AIs (different background colors).
<p/>
<p/>
*Cleaning up:
=============
8. <code>cf unbind-service tanzu-lib library-db</code><br>
9. <code>cf delete-service library-db</code><br>
10.<code>cf delete tanzu-lib -r</code><br>
<p/>
<p/>
*Architectural Decisions:
=========================
1) The application implements the Model-View-Controller (MVC) architectural pattern which the Spring framework makes it really easy.

2) A SQL database has been chosen as data store. The default implementation is the embedded in-memory database H2.






