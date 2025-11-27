pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = "jenkins"
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'STAGE 1: CHECKOUT'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'STAGE 2: BUILD'
                bat 'gradlew.bat clean assemble'   // compile only, no tests
            }
        }

        stage('Build & Test') {
            steps {
                echo 'STAGE 3: TEST'
                withCredentials([usernamePassword(
                    credentialsId: 'POSTGRES_CREDENTIALS',
                    usernameVariable: 'DB_USER',
                    passwordVariable: 'DB_PASS'
                )]) {

                    bat """
                    set SPRING_DATASOURCE_USERNAME=%DB_USER%
                    set SPRING_DATASOURCE_PASSWORD=%DB_PASS%

                    gradlew.bat test
                    """
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'STAGE 4: PACKAGE'
                bat 'gradlew.bat bootJar'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }

        stage('Verify') {
            steps {
                echo 'STAGE 5: VERIFY'
                bat 'dir build\\libs'
            }
        }


        stage('Build Docker Image') {
            steps {
                echo 'STAGE 6: BUILD DOCKER IMAGE'
                script {
                    bat 'docker build -t todo-app:latest .'
                    bat 'docker images | findstr todo-app'
                }
            }
        }


        stage('Deploy Container') {
            steps {
                echo 'STAGE 7: DEPLOY CONTAINER'
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'POSTGRES_CREDENTIALS',
                        usernameVariable: 'DB_USER',
                        passwordVariable: 'DB_PASS'
                    )]) {
                        // Stop old container if exists
                        bat 'docker stop todo-app || echo "No container to stop"'
                        bat 'docker rm todo-app || echo "No container to remove"'

                        // Start new container
                        bat """
                        docker run -d --name todo-app -p 8081:8081 ^
                          -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/todos ^
                          -e SPRING_DATASOURCE_USERNAME=%DB_USER% ^
                          -e SPRING_DATASOURCE_PASSWORD=%DB_PASS% ^
                          todo-app:latest
                        """

                        // Wait a bit
                        sleep 10

                        // Check if it's running
                        bat 'docker ps | findstr todo-app'
                    }
                }
            }
        }

    }

    post {
        success {
            echo ' Pipeline Success! App is running at http://localhost:8081'
            bat 'docker ps | findstr todo-app'
        }
        failure {
            echo ' Pipeline Failed!'
            bat 'docker logs my-todo-app || echo "No logs available"'
        }
    }
}
