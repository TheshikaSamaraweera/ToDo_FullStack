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
                    // Update application properties to use postgres container name
                    sh '''
                        # Backup original
                        cp src/main/resources/application.properties src/main/resources/application.properties.bak

                        # Replace localhost with postgres container name (from docker-compose)
                        sed -i 's|localhost:5432|postgres:5432|g' src/main/resources/application.properties

                        # Verify change
                        echo "📝 Updated database URL:"
                        grep "spring.datasource.url" src/main/resources/application.properties
                    '''

                    try {
                        // Run tests
                        echo '🧪 Running Gradle tests...'
                        sh './gradlew test --info'
                        echo '✅ All tests passed!'
                    } catch (Exception e) {
                        echo "❌ Tests failed: ${e.message}"
                        throw e
                    } finally {
                        // Always restore original
                        sh '''
                            if [ -f src/main/resources/application.properties.bak ]; then
                                mv src/main/resources/application.properties.bak src/main/resources/application.properties
                                echo "✅ Restored original application.properties"
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

                    echo ""
                    echo "📏 JAR file size:"
                    du -h build/libs/*.jar
                '''

                echo '✅ Verification complete!'
            }
        }
    }

    post {
        success {
            echo '=========================================='
            echo '🎉 PIPELINE SUCCEEDED!'
            echo '=========================================='
            echo "📦 Build Number: #${BUILD_NUMBER}"
            echo "⏱️  Duration: ${currentBuild.durationString.replace(' and counting', '')}"
            echo ''
            echo '✅ Completed Stages:'
            echo '   ✓ Code checked out from GitHub'
            echo '   ✓ Project compiled successfully'
            echo '   ✓ Tests passed with PostgreSQL database'
            echo '   ✓ JAR artifact created and archived'
            echo '   ✓ Build artifacts verified'
            echo ''
            echo '📁 Download JAR from "Artifacts" section above'
            echo '🚀 Ready for Docker containerization & deployment!'
            echo ''
            echo '📊 Test Results: Check "Test Result" link above'
        }

        failure {
            echo '=========================================='
            echo '❌ PIPELINE FAILED!'
            echo '=========================================='
            echo 'Check console output above for error details'
            echo ''
            echo '🔍 Common issues:'
            echo '   - Database connection: Check PostgreSQL container'
            echo '   - Network: Verify jenkins-network configuration'
            echo '   - Tests: Review test logs above'
        }

        always {
            echo ''
            echo '📊 Build Summary:'
            echo "   Result: ${currentBuild.result ?: 'SUCCESS'}"
            echo "   Duration: ${currentBuild.durationString.replace(' and counting', '')}"
            echo "   Workspace: ${env.WORKSPACE}"
        }
    }
}