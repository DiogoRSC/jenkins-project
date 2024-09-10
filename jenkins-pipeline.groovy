pipeline {
    agent any

    tools {
        maven 'Maven3' // Assuming Maven is configured in Jenkins
        jdk 'JDK11'    // JDK 11 or 8, depending on your setup
    }

    environment {
        MAVEN_OPTS = "-Xms256m -Xmx512m"
        GIT_REPO_URL = 'https://github.com/DiogoRSC/jenkins-project.git' // Git repo to push JAR
        GIT_BRANCH = 'main' // Branch to deploy the JAR
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: GIT_BRANCH, url: $GIT_REPO_URL
            }
        }

        stage('Build') {
            steps {
                echo 'Building...'
                sh 'mvn clean compile'
                echo 'Build Complete'                
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
                echo 'Testing ...'
                sh 'mvn test'
                echo 'Test Complete'
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
                script {
                    echo 'Deploying JAR to Git repository...'

                    // Clone the Git repository
                    sh """
                        git clone $GIT_REPO_URL deploy-repo
                        cd deploy-repo
                        git checkout $GIT_BRANCH
                    """

                    // Copy the JAR file to the cloned repo
                    sh """
                        cp target/my-app.jar deploy-repo/
                        cd deploy-repo
                        git add my-app.jar
                        git commit -m 'Deploy JAR file from Jenkins pipeline'
                    """

                    // Push the changes to the repository
                    withCredentials([usernamePassword(credentialsId: 'git-credentials-id', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh """
                            git remote set-url origin https://$GIT_USERNAME:$GIT_PASSWORD@github.com/your-username/your-deploy-repo.git
                            git push origin $GIT_BRANCH
                        """
                    echo 'Deploy to Git Complete'
                }
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
