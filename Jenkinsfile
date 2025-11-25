pipeline {
    agent any

    stages {
        // Stage 1: Checkout code from GitHub
        stage('Checkout') {
            steps {
                echo 'STAGE 1: CHECKOUT'
                echo 'Pulling code from GitHub...'
                checkout scm
                echo 'Code checkout successful!'
            }
        }

        // Stage 2: Build the project
        stage('Build') {
            steps {
                echo 'STAGE 2: BUILD'
                echo 'Building the project using Gradle...'

                bat 'gradlew.bat clean build -x test'

                echo 'Build completed successfully!'
            }
        }

        // Stage 3: Run tests
        stage('Test') {
            steps {
                echo 'STAGE 3: TEST'
                echo 'Running unit tests...'

                bat 'gradlew.bat test'

                echo 'Tests completed!'
            }
            post {
                always {
                    // This publishes test results in Jenkins
                    echo 'Publishing test results...'
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }
    }

    // What happens after pipeline finishes
    post {
        success {
            echo 'PIPELINE SUCCEEDED!'
            echo 'Build Number: ${BUILD_NUMBER}'
            echo 'All stages completed successfully!'
        }

        failure {

            echo '❌ PIPELINE FAILED!'
            echo 'Check the logs above to see what went wrong.'
        }
    }
}