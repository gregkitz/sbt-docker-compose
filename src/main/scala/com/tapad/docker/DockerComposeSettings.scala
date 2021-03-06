package com.tapad.docker

import sbt._
import sbt.Keys._
import com.tapad.docker.DockerComposeKeys._
import com.tapad.docker.DockerComposePlugin._

object DockerComposeSettings extends DockerComposeSettingsLocal

trait DockerComposeSettingsLocal extends PrintFormatting {
  lazy val baseDockerComposeSettings = Seq(
    // Attempt to read the compose file from the resources folder followed by a docker folder off the base directory of the project followed by the root directory
    composeFile := {
      val dockerFileName = "docker-compose.yml"
      val dockerFileInResources = (resourceDirectory in Compile).value / dockerFileName toString ()
      val dockerFileInDir = s"${baseDirectory.value.absolutePath}/docker/$dockerFileName"
      if (new File(dockerFileInResources).exists) {
        dockerFileInResources
      } else if (new File(dockerFileInDir).exists) {
        dockerFileInDir
      } else {
        s"${baseDirectory.value.absolutePath}/$dockerFileName"
      }
    },
    // By default set the Compose service name to be that of the sbt Project Name
    composeServiceName := name.value.toLowerCase,
    composeNoBuild := false,
    composeRemoveContainersOnShutdown := true,
    composeRemoveNetworkOnShutdown := true,
    composeRemoveTempFileOnShutdown := true,
    composeContainerStartTimeoutSeconds := 500,
    dockerMachineName := "default",
    dockerImageCreationTask := printError("***Warning: The 'dockerImageCreationTask' has not been defined. " +
      "Please configure this setting to have Docker images built.***"),
    testTagsToExecute := "",
    testExecutionArgs := "",
    testDependenciesClasspath := {
      val fullClasspathCompile = (fullClasspath in Compile).value
      val classpathTestManaged = (managedClasspath in Test).value
      val classpathTestUnmanaged = (unmanagedClasspath in Test).value
      val testResources = (resources in Test).value
      (fullClasspathCompile.files ++ classpathTestManaged.files ++ classpathTestUnmanaged.files ++ testResources).map(_.getAbsoluteFile).mkString(":")
    },
    testCasesJar := artifactPath.in(Test, packageBin).value.getAbsolutePath,
    variablesForSubstitution := Map[String, String](),
    variablesForSubstitutionTask := Map[String, String](),
    commands ++= Seq(dockerComposeUpCommand, dockerComposeStopCommand, dockerComposeRestartCommand, dockerComposeInstancesCommand, dockerComposeTest)
  )
}