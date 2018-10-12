#!/usr/bin/env groovy
@Library('jenkins-library@master') _

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
        stage('Docker Push') {
            steps {
                withMaven {
                    script {
                        if (env.BRANCH_NAME == "develop") {
                            sh 'mvn -Pdocker docker:push'
                            build 'docker_restart_develop_latest'
                        } else {
                            echo 'No Docker action required'
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            sendNotification currentBuild.result
        }
        failure {
            // notify users when the Pipeline fails
            mail to: 'gerd@aschemann.net',
                    subject: "Failed DukeCon Server Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}
