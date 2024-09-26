# Jenkins Folder Authorize Project Strategy Plugin

This Jenkins plugin adds a new [Authorize Project Strategy](https://plugins.jenkins.io/authorize-project/) that allows setting up build access control on a folder level.

### Usage

1. Enable the `Access Control for Builds` folder property and select the desired strategy for this folder's items.
2. Either set the global `Access Control for Builds` setting under Security to `Run with Parent Folders Access Control for Builds`, or configured it as the job's authorization strategy via the job's Authorization panel.

If a job is configured with `Run with Parent Folders Access Control for Builds` (whether specifically or through the global default) the first strategy found walking up its parent folders is used. If there is no parent folder with `Access Control for Builds` configured then the job runs under the anonymous user.

### Development

Starting a development Jenkins instance with this plugin: `mvn hpi:run`

Building the plugin: `mvn package`
