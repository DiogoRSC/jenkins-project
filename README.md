# Simple Spark Project with Jenkins for CI/CD pipeline

URL Jenkins: http://localhost:8070/

## Step 1: Set Up Jenkins Pipeline for Maven

1. Install Required Plugins in Jenkins:
  - Ensure that you have the following plugins installed:
    - Git plugin (for SCM).
    - Maven Integration plugin (for Maven builds).
    - JUnit plugin (for test reports).
2. Create a Jenkins Job:
  - Open Jenkins and click on "New Item".
  - Select "Pipeline" and give the project a name.
  - In the project configuration, go to the Pipeline section.
3. Jenkinsfile for the Pipeline:
  - Create a Jenkinsfile in your repository root. This file will define your pipeline steps.

Sample Jenkinsfile for Maven:

```shell
pipeline {
    agent any

    tools {
        maven 'Maven3' // Assuming Maven is configured in Jenkins
        jdk 'JDK11'    // JDK 11 or 8, depending on your setup
    }

    environment {
        MAVEN_OPTS = "-Xms256m -Xmx512m"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo/spark-scala-example.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml' // For test results
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application...'
                // Add deployment script or steps here
                // e.g., Spark submit, upload to AWS S3/EMR, etc.
            }
        }
    }

    post {
        always {
            echo 'Cleaning workspace...'
            cleanWs()
        }
        success {
            echo 'Build was successful!'
        }
        failure {
            echo 'Build failed.'
        }
    }
}
```

## Step 2: Configure Jenkins Job
Source Code Management:

Go to the Pipeline section and select Pipeline script from SCM.
Choose Git as the SCM.
Enter your repository URL and the correct branch (e.g., main).
Build Triggers:

You can trigger the pipeline based on changes in the repository (poll SCM or webhook) or scheduled builds (cron job).
Build Environment:

Add any build environment configurations, such as cleaning the workspace before starting or setting specific environment variables.
Post-build Actions:

Configure post-build actions like sending notifications, deploying artifacts, etc.


## Step 3: Running the Jenkins Job
After the setup is done, run the job manually or based on triggers.
Jenkins will:
Checkout your code.
Build the Scala project using Maven.
Run the tests and archive results.
Package the project as a JAR.
Optionally, deploy the JAR to a Spark cluster or another environment.


## Step 4: Deployment
To deploy your Spark application, you can:

Use Spark Submit in the Deploy stage of the pipeline:

```shell
sh """
    spark-submit \
    --class com.example.MainClass \
    --master spark://master-url:7077 \
    target/spark-scala-example-1.0-SNAPSHOT.jar
"""
```
