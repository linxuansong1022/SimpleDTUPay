pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                script {
                    try {
                        // 1. 启动 Quarkus (后台)
                        sh 'nohup mvn quarkus:dev -Dquarkus.http.host=0.0.0.0 > quarkus.log 2>&1 &'
                        
                        // 2. 循环检查 (用更通用的 while 循环)
                        sh '''
                            count=0
                            while [ $count -lt 30 ]; do
                                sleep 10
                                count=$((count+1))
                                echo "Checking status... ($count/30)"
                                if curl -s http://localhost:8080/q/health > /dev/null; then
                                    echo "Quarkus is UP!"
                                    exit 0
                                fi
                            done
                            echo "Timeout waiting for Quarkus!"
                            exit 1
                        '''

                        // 3. 运行测试
                        sh 'mvn test -Djava.net.preferIPv4Stack=true'

                    } finally {
                        sh 'pkill -f quarkus:dev || true'
                        sh 'cat quarkus.log || true'
                    }
                }
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}