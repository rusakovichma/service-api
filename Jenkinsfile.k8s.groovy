#!groovy

//String podTemplateConcat = "${serviceName}-${buildNumber}-${uuid}"
def label = "worker-${UUID.randomUUID().toString()}"
println("label")
println("${label}")

podTemplate(
        label: "${label}",
        containers: [
                containerTemplate(name: 'jnlp', image: 'jenkins/jnlp-slave:alpine'),
                containerTemplate(name: 'kaniko', image: 'gcr.io/kaniko-project/executor:debug', command: '/busybox/cat', ttyEnabled: true),
                containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
                containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:v3.0.2', command: 'cat', ttyEnabled: true),
                containerTemplate(name: 'httpie', image: 'blacktop/httpie', command: 'cat', ttyEnabled: true),
                containerTemplate(name: 'maven', image: 'maven:3.6.1-jdk-8-alpine', command: 'cat', ttyEnabled: true,
                        resourceRequestCpu: '500m',
                        resourceLimitCpu: '1500m',
                        resourceRequestMemory: '1024Mi',
                        resourceLimitMemory: '3072Mi'),
                containerTemplate(name: 'jre', image: 'openjdk:8-jre-alpine', command: 'cat', ttyEnabled: true)
        ],
        volumes: [
                secretVolume(mountPath: '/kaniko/.docker', secretName: 'dockerconfigjson-secret'),
                secretVolume(mountPath: '/etc/.sealights-token', secretName: 'sealights-token'),
                hostPathVolume(mountPath: '/root/.m2/repository', hostPath: '/tmp/jenkins/.m2/repository')
        ]
) {

    node("${label}") {

        def sealightsTokenPath = "/etc/.sealights-token/token"
//        def srvRepo = "quay.io/reportportal/service-api"
        def srvRepo = "reportportal/service-api-dev"
        def sealightsAgentUrl = "https://agents.sealights.co/sealights-java/sealights-java-latest.zip"
        def sealightsAgentArchive = sealightsAgentUrl.substring(sealightsAgentUrl.lastIndexOf('/') + 1)

        def k8sDir = "kubernetes"
        def ciDir = "reportportal-ci"
        def appDir = "app"
        def testDir = "tests"
        def serviceName = "service-api"
        def k8sNs = "reportportal"
        def sealightsDir = 'sealights'

        def branchToBuild = params.get('COMMIT_HASH', 'develop')

        parallel 'Checkout Infra': {
            stage('Checkout Infra') {
                sh 'mkdir -p ~/.ssh'
                sh 'ssh-keyscan -t rsa github.com >> ~/.ssh/known_hosts'
                sh 'ssh-keyscan -t rsa git.epam.com >> ~/.ssh/known_hosts'
                dir(k8sDir) {
                    git branch: "master", url: 'https://github.com/reportportal/kubernetes.git'

                }
                dir(ciDir) {
                    git credentialsId: 'epm-gitlab-key', branch: "master", url: 'git@git.epam.com:epmc-tst/reportportal-ci.git'
                }

            }
        }, 'Checkout Service': {
            stage('Checkout Service') {
                dir(appDir) {
                    git branch: branchToBuild, url: 'https://github.com/reportportal/service-api.git'
                }
            }
        }, 'Download Sealights': {
            stage('Download Sealights'){
                dir(sealightsDir) {
                    sh "wget ${sealightsAgentUrl}"
                    unzip sealightsAgentArchive
                }
            }
        }

        def test = load "${ciDir}/jenkins/scripts/test.groovy"
        def utils = load "${ciDir}/jenkins/scripts/util.groovy"
        def helm = load "${ciDir}/jenkins/scripts/helm.groovy"
        def docker = load "${ciDir}/jenkins/scripts/docker.groovy"

        helm.init()
        utils.scheduleRepoPoll()

        def snapshotVersion = utils.readProperty("app/gradle.properties", "version")
        def buildVersion = "BUILD-${env.BUILD_NUMBER}"
        def srvVersion = "${snapshotVersion}-${buildVersion}"
        def tag = "$srvRepo:$srvVersion"

        def sealightsToken = utils.execStdout("cat $sealightsTokenPath")
        def sealightsSession;
        stage ('Init Sealights') {
            dir(sealightsDir) {
                container ('jre') {
                    sh "java -jar sl-build-scanner.jar -config -tokenfile $sealightsTokenPath -appname service-api -branchname $branchToBuild -buildname $srvVersion -pi '*com.epam.ta.reportportal.*'"
                    sealightsSession = utils.execStdout("cat buildSessionId.txt")
                }
            }
        }

        stage('Build Docker Image') {
            dir(appDir) {
                container('kaniko') {
                    sh "/kaniko/executor -f `pwd`/docker/Dockerfile-develop -c `pwd` --cache=true --destination=$tag --build-arg sealightsToken=$sealightsToken --build-arg sealightsSession=$sealightsSession --build-arg buildNumber=$buildVersion --cache-repo=$srvRepo"
                }
            }


        }

        def jvmArgs = params.get('JVM_ARGS')
        if(jvmArgs == null){
            jvmArgs = '-Xms2G -Xmx3g -DLOG_FILE=app.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp'
        }

        stage('Deploy to Dev Environment') {
            container('helm') {
                dir("$k8sDir/reportportal/v5") {
                    sh 'helm dependency update'
                }
                sh "helm upgrade -n reportportal --reuse-values --set serviceapi.repository=$srvRepo --set serviceapi.tag=$srvVersion --set \"serviceapi.jvmArgs=$jvmArgs\" --wait reportportal ./$k8sDir/reportportal/v5"
            }
        }

        stage('Execute DVT Tests') {
            def srvUrls
            container('kubectl') {
                def srvName = utils.getServiceName(k8sNs, "reportportal-api")
                srvUrls = utils.getServiceEndpoints(k8sNs, srvName)
            }
            if (srvUrls == null) {
                error("Unable to retrieve service URL")
            }
            container('httpie') {
                srvUrls.each{ip ->
                    test.checkVersion("http://$ip:8585", "$srvVersion")
                }
            }
        }
    }
}
