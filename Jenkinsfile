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
                    // Update application properties to use host.docker.internal
                    sh '''
                        # Backup original application.properties
                        cp src/main/resources/application.properties src/main/resources/application.properties.bak

                        # Update database host for Jenkins environment
                        sed -i 's|localhost|host.docker.internal|g' src/main/resources/application.properties || true
                    '''

                    try {
                        // Run tests
                        sh './gradlew test'
                        echo '✅ All tests passed!'
                    } finally {
                        // Restore original application.properties
                        sh '''
                            if [ -f src/main/resources/application.properties.bak ]; then
                                mv src/main/resources/application.properties.bak src/main/resources/application.properties
                            fi
                        '''
                    }
                    // Container automatically stops and removes after this block
                }
            }
            post {
                always {
                    echo '📊 Publishing test results...'
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                }
            }
        }

        // Stage 4: Package
        stage('Package') {
            steps {
                echo '===== STAGE 4: PACKAGE ====='
                echo '📦 Creating JAR file...'

                sh './gradlew bootJar'

                echo '✅ JAR file created!'

                // Archive the artifact
                archiveArtifacts artifacts: '**/build/libs/*.jar',
                                fingerprint: true,
                                allowEmptyArchive: false
            }
        }

        // Stage 5: Verify
        stage('Verify') {
            steps {
                echo '===== STAGE 5: VERIFY ====='
                echo '🔍 Verifying build artifacts...'

                sh '''
                    echo "📦 Build artifacts:"
                    ls -lh build/libs/

                    echo ""
                    echo "✅ JAR file details:"
                    file build/libs/*.jar
                '''

                echo '✅ Verification complete!'
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