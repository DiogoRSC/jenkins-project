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
                git branch: 'main', url: 'https://github.com/DiogoRSC/jenkins-project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        //SonarQube Example
        //stage('SonarQube Analysis') {
            //steps {
                //withSonarQubeEnv('SonarQube') {
                    //sh 'mvn sonar:sonar'
                //}
            //}
        //}

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
