Requests Plugin

This plugin sets up a request center for non-admin users to be able to
ask that their job to be deleted or renamed, or a build to be deleted or
unlocked.

## Summary

The plugin adds 2 new actions to the job screen so that users who don't
have the required permissions to delete or to rename Jobs, to send a
request to the Jenkins administrator to do so.  It also adds 2 new
actions to the build screen so that users who don't have the required
permissions to delete or unlock builds can send a request to the Jenkins
administrator to do so. 

  
The Jenkins administrator then gets a dedicated screen to handle all the
incoming requests. If configured, an email will be sent to the assigned
admin person and the submitter of the request. The admin user can then
review the request and then apply the changes with just a click of a
button.

------------------------------------------------------------------------

## For Users

For users who don't have permissions to Delete a Job, Rename a Job,
Delete a Build, or Unlock a Build, the following four actions are
available if enabled.

-   Request For Job Deletion
-   Request For Build Deletion
-   Request To Unlock Build
-   Request Job Rename

  

###                               Project page                                               Project page                                                                           Build page

                       
  ![](docs/images/jobAction.png){.confluence-embedded-image
width="231"}       
 ![](docs/images/renameJob.png){.confluence-embedded-image
width="210"}                                      
  ![](docs/images/build-page.png){.confluence-embedded-image
width="200"}                             

  

------------------------------------------------------------------------

## For Administrators

The Global Settings allow the admin to enable/disable any of the 4
actions and to enable e-mail notifications (**Manage
Jenkins** \> **Configure System**):

-   It's important to note that when making a change to the "Enable the
    link for Delete Job Request" a restart of Jenkins is required so
    that all applicable jobs receive the action.
-   Make sure to save email configuration changes before clicking the
    "Send Test Email" button.
-   Discard old builds must be enabled in the job configuration before
    you can view the "Keep this build forever" option.

  

                           
 ![](docs/images/new_global.png){.confluence-embedded-image
width="1100"}

        

  
Administrators get a new **Pending  Requests** entry in Jenkins' main
configuration screen (**Manage Jenkins**):  

                         
 ![](docs/images/pendingRequets.png){.confluence-embedded-image
height="81"}

------------------------------------------------------------------------

                           
  ![](docs/images/pendingRequestPage.png){.confluence-embedded-image
width="700"}

------------------------------------------------------------------------

                        

#### In order to see Action                      Not Authorized

Delete Job Request                         Job.Delete = 0

Delete Build Request                       Run.Delete =0

Unlock Build Request                      Run.Delete = 0

Rename Job Request                      Job.Create = 1, Job.Delete = 0,
Job.Configure = 0 OR Job.Create = 0, Job.Delete = 1, Job.Configure = 0

  


  

  
