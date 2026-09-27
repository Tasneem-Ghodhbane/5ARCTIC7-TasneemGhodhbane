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
