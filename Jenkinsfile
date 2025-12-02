pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = "jenkins"
        DOCKER_HUB_USERNAME = "theshikanavod"
        DOCKER_IMAGE = "${DOCKER_HUB_USERNAME}/todo-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
        SONAR_PROJECT_KEY = "todo-app"
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
                echo '=========================================='
                bat 'gradlew.bat clean assemble'
            }
        }

        stage('Code Quality Analysis') {
            steps {
                echo '=========================================='
                echo 'STAGE 3: SONARQUBE CODE QUALITY ANALYSIS'
                echo '=========================================='
                script {
                    // Run tests and generate coverage report first
                    withCredentials([usernamePassword(
                        credentialsId: 'POSTGRES_CREDENTIALS',
                        usernameVariable: 'DB_USER',
                        passwordVariable: 'DB_PASS'
                    )]) {
                        bat """
                        set SPRING_DATASOURCE_USERNAME=%DB_USER%
                        set SPRING_DATASOURCE_PASSWORD=%DB_PASS%
                        gradlew.bat test jacocoTestReport
                        """
                    }

                    // Run SonarQube analysis
                    withSonarQubeEnv('SonarQube-Local') {
                        bat """
                        gradlew.bat sonar ^
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} ^
                        -Dsonar.projectName="Todo Application" ^
                        -Dsonar.java.binaries=build/classes/java/main
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '=========================================='
                echo 'STAGE 4: WAITING FOR SONARQUBE QUALITY GATE'
                echo '=========================================='
                script {
                    timeout(time: 5, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            echo "⚠️ Quality Gate failed: ${qg.status}"
                            echo "⚠️ Pipeline will continue, but please review SonarQube report"
                            // Uncomment below to fail the build on Quality Gate failure
                            // error "Quality Gate failed: ${qg.status}"
                        } else {
                            echo "✅ Quality Gate passed!"
                        }
                    }
                }
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
                echo "Building image with tag: ${IMAGE_TAG}"
                script {
                    // Build with both version tag and latest tag
                    bat "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest ."
                    bat "docker images | findstr ${DOCKER_HUB_USERNAME}/todo-app"
                    echo " Built image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
                }
            }
        }


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
                        bat 'echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin'

                        // Push both the versioned tag and latest tag
                        bat "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                        bat "docker push ${DOCKER_IMAGE}:latest"

                        // Logout
                        bat 'docker logout'

                        echo " Image pushed: ${DOCKER_IMAGE}:${IMAGE_TAG}"
                        echo " Image pushed: ${DOCKER_IMAGE}:latest"
                        echo " View at: https://hub.docker.com/r/${DOCKER_HUB_USERNAME}/todo-app"
                    }
                }
            }
        }


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
                        bat 'docker stop todo-app 2>nul || echo Container not running'
                        bat 'docker rm todo-app 2>nul || echo No container to remove'

                        // Start new container
                        bat """
                        docker run -d ^
                          --name todo-app ^
                          -p 8081:8081 ^
                          -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/todos ^
                          -e SPRING_DATASOURCE_USERNAME=%DB_USER% ^
                          -e SPRING_DATASOURCE_PASSWORD=%DB_PASS% ^
                          ${DOCKER_IMAGE}:${IMAGE_TAG}
                        """

                        // Wait for container to start
                        echo '⏳ Waiting for container to start...'
                        timeout(time: 30, unit: 'SECONDS') {
                            waitUntil {
                                script {
                                    def result = bat(returnStatus: true, script: 'docker ps | findstr todo-app')
                                    return result == 0
                                }
                            }
                        }

                        // Check if it's running
                        bat 'docker ps | findstr todo-app'

                        echo " Deployed version: ${IMAGE_TAG}"
                        echo " Access at: http://localhost:8081"
                    }
                }
            }
        }

    }

    post {
        success {
            echo '=========================================='
            echo ' PIPELINE SUCCEEDED!'
            echo '=========================================='
            echo " Build: #${BUILD_NUMBER}"
            echo " Docker Image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
            echo " App running at: http://localhost:8081"
            echo "📈 SonarQube Report: http://localhost:9000/dashboard?id=${SONAR_PROJECT_KEY}"
            echo '=========================================='
            bat 'docker ps | findstr todo-app'
        }
        failure {
            echo '=========================================='
            echo ' PIPELINE FAILED!'
            echo '=========================================='
            echo 'Container logs:'
            bat 'docker logs todo-app 2>nul || echo No logs available'
            echo "📈 Check SonarQube: http://localhost:9000/dashboard?id=${SONAR_PROJECT_KEY}"
        }
        always {
            echo "Pipeline duration: ${currentBuild.durationString}"
        }
    }
}
