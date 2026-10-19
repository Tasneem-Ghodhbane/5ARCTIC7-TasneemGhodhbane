pipeline {
    agent any

    environment {
        BACKEND_IMAGE  = 'tasneemgh/ghodhbanetasneem-5arctic7-gestionprojets-backend'
        FRONTEND_IMAGE = 'tasneemgh/ghodhbanetasneem-5arctic7-gestionprojets-frontend'
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

        stage('SonarQube') {
            steps {
                dir('backend') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Package') {
            steps {
                dir('backend') { sh 'mvn package -DskipTests' }
            }
        }

        // ---------- CD ----------
        stage('Build Images') {
            steps {
                retry(3) {
                    sh 'docker build -t $BACKEND_IMAGE:$BUILD_NUMBER -t $BACKEND_IMAGE:latest ./backend'
                    sh 'docker build -t $FRONTEND_IMAGE:$BUILD_NUMBER -t $FRONTEND_IMAGE:latest ./frontend'
                }
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    retry(3) {
                        sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                    }
                    retry(3) {
                        sh 'docker push $BACKEND_IMAGE:$BUILD_NUMBER'
                        sh 'docker push $BACKEND_IMAGE:latest'
                        sh 'docker push $FRONTEND_IMAGE:$BUILD_NUMBER'
                        sh 'docker push $FRONTEND_IMAGE:latest'
                    }
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

        stage('Deploy Kubernetes') {
            steps {
                sh 'kubectl create namespace devops --dry-run=client -o yaml | kubectl apply -f -'
                sh 'kubectl apply -f mysql-deployment.yaml'
                sh 'sed "s|:1.0|:$BUILD_NUMBER|" spring-deployment.yaml | kubectl apply -f -'
                sh 'sed "s|:1.0|:$BUILD_NUMBER|" frontend-deployment.yaml | kubectl apply -f -'
                sh 'kubectl rollout status deployment/mysql -n devops --timeout=300s'
                sh 'kubectl rollout status deployment/spring -n devops --timeout=300s'
                sh 'kubectl rollout status deployment/frontend -n devops --timeout=300s'
                sh 'kubectl get pods,svc -n devops'
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
