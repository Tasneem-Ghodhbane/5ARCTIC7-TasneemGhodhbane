pipeline {
    agent any

    environment {
        BACKEND_IMAGE  = 'tasneemgh/ghodhbanetasneem-5arctic7-gestionprojets-backend:latest'
        FRONTEND_IMAGE = 'tasneemgh/ghodhbanetasneem-5arctic7-gestionprojets-frontend:latest'
    }

    stages {

        // ---------- CI ----------
        stage('Git') {
            steps { checkout scm }
        }

        stage('Compile') {
            steps {
                dir('backend') { sh 'mvn clean compile' }
            }
        }

        stage('SonarQube') {
            steps {
                dir('backend') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                dir('backend') { sh 'mvn test' }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                dir('backend') { sh 'mvn package -DskipTests' }
            }
        }

        stage('Build Images') {
            steps {
                sh 'docker build -t $BACKEND_IMAGE ./backend'
                sh 'docker build -t $FRONTEND_IMAGE ./frontend'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                    sh 'docker push $BACKEND_IMAGE'
                    sh 'docker push $FRONTEND_IMAGE'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up -d'
                sh 'docker compose ps'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'backend/target/*.jar, backend/target/site/jacoco/**', fingerprint: true
        }
        always {
            sh 'docker logout || true'
        }
    }
}
