pipeline {
    agent any

    // Use Docker as a tool
    tools {
        dockerTool 'docker'
    }

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
                    // Use Docker Pipeline plugin syntax
                    docker.image('postgres:15-alpine').withRun(
                        '-e POSTGRES_DB=todos ' +
                        '-e POSTGRES_USER=postgres ' +
                        '-e POSTGRES_PASSWORD=postgres ' +
                        '-p 5432:5432'
                    ) { c ->

                        echo '⏳ Waiting for PostgreSQL to be ready...'

                        // Wait for database to be ready
                        sh """
                            sleep 10
                            for i in \$(seq 1 30); do
                                if docker exec ${c.id} pg_isready -U postgres > /dev/null 2>&1; then
                                    echo '✅ PostgreSQL is ready!'
                                    exit 0
                                fi
                                echo '⏳ Waiting for PostgreSQL... (\$i/30)'
                                sleep 2
                            done
                            echo '❌ PostgreSQL failed to start!'
                            exit 1
                        """

                        echo '✅ PostgreSQL container is running!'

                        // Run tests while database is running
                        echo '===== RUNNING TESTS WITH DATABASE ====='
                        sh './gradlew test'
                        echo '✅ Tests completed!'
                    }
                    // Container automatically stops and removes after this block
                }
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

    post {
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