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

                sh 'chmod +x gradlew'  // Make gradlew executable
                sh './gradlew clean build -x test'

                echo 'Build completed successfully!'
            }
        }

        // Stage 3: Start PostgreSQL for Testing
        stage('Setup Test Database') {
            steps {
                echo 'STAGE 3: DATABASE SETUP'
                echo 'Starting PostgreSQL container for testing...'

                script {
                    // Stop and remove any existing test database
                    sh '''
                        docker-compose -f docker-compose.yml down || true
                    '''

                    // Start PostgreSQL container
                    sh '''
                        docker-compose -f docker-compose.yml up -d
                    '''

                    // Wait for PostgreSQL to be ready
                    echo 'Waiting for PostgreSQL to be ready...'
                    sh '''
                        for i in {1..30}; do
                            if docker exec postgres pg_isready -U postgres > /dev/null 2>&1; then
                                echo "PostgreSQL is ready!"
                                exit 0
                            fi
                            echo "Waiting for PostgreSQL... ($i/30)"
                            sleep 2
                        done
                        echo "PostgreSQL failed to start!"
                        exit 1
                    '''
                }

                echo '✅ PostgreSQL is ready for testing!'
            }
        }

        // Stage 3: Run tests
        stage('Test') {
            steps {
                echo 'STAGE 3: TEST'
                echo 'Running unit tests...'

                sh './gradlew test'

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
        always {
            echo 'Cleaning up...'
            script {
                // Stop and remove test database container
                sh '''
                    docker-compose -f docker-compose.yml down || true
                '''
            }
        }

        success {
            echo 'PIPELINE SUCCEEDED!'
            echo 'Build Number: ${BUILD_NUMBER}'
            echo 'All stages completed successfully!'
        }

        failure {

            echo 'PIPELINE FAILED!'
            echo 'Check the logs above to see what went wrong.'
        }
    }
}