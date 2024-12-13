pipeline {
    agent { label 'agent-1' }
     // Specify the agent using a label
    // options {
    //     ansiColor('xterm') // Enable ANSI color in logs
    // }


    parameters {
        string(name: 'component', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')

    //     text(name: 'BIOGRAPHY', defaultValue: '', description: 'Enter some information about the person')

    //     booleanParam(name: 'TOGGLE', defaultValue: true, description: 'Toggle this value')

    //     choice(name: 'CHOICE', choices: ['One', 'Two', 'Three'], description: 'Pick something')

    //     password(name: 'PASSWORD', defaultValue: 'SECRET', description: 'Enter a password')
    }
    environment {
      version = ''
   }
    stages {

       stage('set version'){
           steps{
              dir('${params.component}'){
           script {
                    def version = sh(script: "node -p \"require('./package.json').version\"", returnStdout: true).trim()
                    echo "Version: ${version}"
                }}
           }
     

       }
        stage('install dependecies') {
          
            steps {
                dir('${params.version}'){
                echo "Version: ${env.version}"
                sh 'npm install' // Fixed missing space and quotes
            }
            }
        }
        // stage('scan the code'){
        //     steps{
        //         dir('catalogue'){
                
        //         sh 'sonar-scanner'
        //     }
        // }
        // }  

   
        stage('build') {
          
            steps {
                sh '''
                pwd
              ls -latr
              
              zip -r ${params.component}.zip ${params.version}/* -x ".git" "*.zip" "Jenkinsfile"

              cp ${params.version}.zip /tmp/


              ''' // Fixed missing space and quotes
              
            }
            }

        // stage("artificat upload"){
        //         steps{

        //                 nexusArtifactUploader(
        //                 nexusVersion: 'nexus3',
        //                 protocol: 'http',
        //                 nexusUrl: '3.86.244.92:8081',
        //                 groupId: 'com.roboshop',
        //                 version: '${version}',
        //                 repository: 'catalogue',
        //                 credentialsId: 'nexus',
        //                 artifacts: [
        //                     [artifactId: 'catalogue',
        //                     classifier: '',
        //                     file: "catalogue.zip",
        //                     type: 'zip']
        // ])
        //         }
        //     }
//downstream job should run before this  , wait  until it finished 

        stage('deploy') {
          
            steps {
               echo "Deployment"
               echo "Version: ${env.version}"
               script{
                    echo "Deployment"
                    def params = [
                        string(name: 'version', value: "${env.version}")
                    ]
                    build job: "../${params.component}-deploy", wait: true, parameters: params
                }

                 
                // Trigger the downstream pipeline and pass the VERSION parameter
                
          
              
            }
            }
       
    }
    post {
       
        success {
            echo "it will work for success"
        }
        failure {
            echo "it will work for failure"
        }
    }
}
