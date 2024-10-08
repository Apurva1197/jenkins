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
                apt update
                apt install maven -y
                apt install unzip -y
                wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.96/bin/apache-tomcat-9.0.96.zip
                unzip apache-tomcat-9.0.96.zip
                cd studentapp-ui
                mvn clean package
                
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
                sudo aws s3 cp s3://sonarbucket1/studentapp/ apache-tomcat-9.0.96/webapps/
                sudo bash apache-tomcat-9.0.96/bin/catalina.sh start
                '''
                echo "we are configuring"
            }   
        }
    }
}