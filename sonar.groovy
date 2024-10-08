pipeline {
    agent{
        label 'dummy'
    } 
    stages{
        stage (pull){
            steps{
                echo "we are pulling from github"
                git "https://github.com/AnupDudhe/studentapp-ui"
            }   
        }
        stage (build){
            steps{
                sh '''
                sudo apt update
                sudo apt install maven -y
                sudo apt install unzip -y
                sudo wget https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.99/bin/apache-tomcat-8.5.99.zip
                sudo unzip apache-tomcat-8.5.99.zip
                sudo mvn clean package
                
                '''
                echo "we are building"
            }   
        }
        stage (test){
            steps{
                sh '''
                mvn sonar:sonar \
                    -Dsonar.projectKey=studentapp \
                    -Dsonar.host.url=http://3.108.193.79:9000 \
                    -Dsonar.login=aae1a3a9c7d13270b111994e5c535a4a15f94dda
                '''
                echo "we are testing"
            }   
        }
        stage (configure){
            steps{
                sh '''
                sudo aws s3 cp target/*.war  s3://sonarbucket1/studentapp/
                sudo aws s3 cp s3://sonarbucket1/studentapp/ apache-tomcat-8.5.99/webapps/
                sudo bash apache-tomcat-8.5.99/bin/catalina.sh start
                '''
                echo "we are configuring"
            }   
        }
    }










}