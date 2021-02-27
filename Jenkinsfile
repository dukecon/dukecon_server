#!/usr/bin/env groovy
@Library('jenkins-library@master') _

pipeline {
    agent {
        node {
            label 'docker'
        }
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5', daysToKeepStr: '5'))
    }

    triggers {
        pollSCM('*/3 * * * *')
    }

    stages {
        stage('Build') {
            steps {
                withMaven {
                    script {
                        if (env.BRANCH_NAME == "javaland2021") {
                            sh './mvnw -Pdocker,doc,check-plugins clean install'
                            publishHTML target: [allowMissing         : false,
                                                 alwaysLinkToLastBuild: false,
                                                 keepAll              : true,
                                                 reportDir            : 'impl/target/generated-docs/html/',
                                                 reportFiles          : 'index.html',
                                                 reportName           : 'SwaggerDocumentation']
                        } else {
                            sh './mvnw clean verify'
                        }
                    }
                }
                findBuildScans()
            }
        }
        stage('Docker Push and Restart "latest"') {
            steps {
                withMaven {
                    script {
                        if (env.BRANCH_NAME == "javaland2021") {
                            sh './mvnw -Pdocker docker:push -Dscan=false'
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
