pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = "jenkins"
        DOCKER_HUB_USERNAME = "your-dockerhub-username"
        DOCKER_IMAGE = "${DOCKER_HUB_USERNAME}/todo-app"
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
                    // Build with Docker Hub username
                    bat "docker build -t ${DOCKER_IMAGE}:latest ."
                    bat "docker images | findstr ${DOCKER_HUB_USERNAME}/todo-app"
                }
            }
        }

        // ========== NEW STAGE - PUSH TO DOCKER HUB ==========
        stage('Push to Docker Hub') {
            steps {
                echo 'STAGE 7: PUSH TO DOCKER HUB'
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'DOCKER_HUB_CREDENTIALS',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        // Login to Docker Hub
                        bat 'docker login -u %DOCKER_USER% -p %DOCKER_PASS%'

                        // Push the image
                        bat "docker push ${DOCKER_IMAGE}:latest"

                        // Logout
                        bat 'docker logout'

                        echo "✅ Image pushed to: https://hub.docker.com/r/${DOCKER_HUB_USERNAME}/todo-app"
                    }
                }
            }
        }
        // ====================================================

        stage('Deploy Container') {
            steps {
                echo 'STAGE 8: DEPLOY CONTAINER'
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
                          ${DOCKER_IMAGE}:latest
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
