#!/usr/bin/env groovy

pipeline {
    agent {
        node {
            label 'docker'
        }
    }

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Build') {
            when {
                not {
                    branch "develop"
                }
            }
            steps {
                withMaven {
                    sh 'mvn clean verify'
                }
            }
            // There should be an "otherwise"
            when {
                    branch "develop"
            }
            steps {
                withMaven {
                    sh 'mvn clean deploy'
                }
            }
        }
    }
    post {
        failure {
            // notify users when the Pipeline fails
            mail to: 'gerd@aschemann.net',
                    subject: "Failed DukeCon Server Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}
