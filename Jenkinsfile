pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Maven Clean & Compile') {
            steps {
                dir('backend') { sh 'mvn clean compile' }
            }
        }

        stage('Maven Test') {
            steps {
                dir('backend') { sh 'mvn test' }
            }
        }


        stage('SonarQube Analysis') {
            steps {
                dir('backend') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                          mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                            -Dsonar.projectKey=5ARCTIC7-TasneemGhodhbane \
                            -Dsonar.host.url=http://localhost:9000 \
                            -Dsonar.token=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Maven Package') {
            steps {
                dir('backend') { sh 'mvn package -DskipTests' }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
        }
    }
}
