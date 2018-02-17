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
            steps {
                withMaven {
                    script {
                        if (env.BRANCH_NAME == "develop") {
                            sh 'mvn -Pdocker clean deploy'
                        } else {
                            sh 'mvn clean verify'
                        }
                    }
                }
            }
        }
        stage('Docker Promote') {
            steps {
                withMaven {
                    if (env.BRANCH_NAME == "develop") {
                        sh 'mvn -Pdocker docker:push'
                    } else {
                        echo 'No Docker action required'
                    }
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
