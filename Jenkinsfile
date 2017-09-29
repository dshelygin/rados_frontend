pipeline {
  agent any
  stages {
    stage('Prepare') {
      steps {
        echo 'Hello'
        git(url: 'https://github.com/dshelygin/rados_frontend.git', branch: 'test', changelog: true)
      }
    }
  }
}