/**参数*/
def PROJECT_NAME = 'daq'//项目名称
def SERVICE_NAME = 'reservation'//服务名称
def SERVICE_FOLDER_NAME = 'service-reservation'//发布服务文件夹名称,不配置默认使用服务名称作为服务文件夹名称
//def SERVICE
def MAVEN_ENV = 'beta'//Maven打包环境
def GIT_PATH = 'git@172.16.61.211:service/service-reservation.git'//git地址
def USER_EMAIL = '13991544720@139.com'
def NODES = ['beta_facade_mq']//跳板机(在Jenkins中配置配置的节点名称,目标服务器从跳板机拷贝文件也用的这个名称)
def TARGETS = [['service01']]//目标服务器
def SLEEP_TIME = 0//等待服务启动时间,秒,如果只有一个服务，或者不需要等待设置为0

/**拼装的配置*/
//project
def PROJECT_PATH = "/home/jhd/app/${PROJECT_NAME}/"//项目所在目录
//service
//如果没有设置服务文件夹名称，则使用服务名称作为文件夹名称
def _service_folder = SERVICE_FOLDER_NAME.length() == 0?SERVICE_NAME:SERVICE_FOLDER_NAME;
def SERVICE_PATH = "${PROJECT_PATH}${_service_folder}/"//服务目录
def DUBBO_SH_NAME = 'dubbo_sh.zip'//dubbo启动脚本名称
def DUBBO_SH_FOLDER_NAME = 'dubbo_sh'//启动脚本文件夹名称
//pageage
def PACKAGE_NAME = "${SERVICE_NAME}-upgrade.zip"//包名
def PACKAGE_FOLDER_NAME = "$SERVICE_NAME-facade-impl"//包所在文件夹名称
//jenkins
def JENKINS_PATH = '/home/jhd/warFiles/jenkins/'//跳板机Jenkins临时目录
def JENKINS_USER = 'jhd'
def JENKINS_LOCAL_IP = 'infra01'
def JENKINS_LOCAL_MYTOOLS_PTAH = '/home/jhd/.jenkins/mytools/'//本地工具文件夹
def JENKINS_TOOLS_PATH = "${JENKINS_PATH}tools/"
//maven
def MAVEN_BIN_PATH = '/usr/local/maven/apache-maven-3.3.9/bin/'

/**配置详情*/
def _config = ['project': ['name':PROJECT_NAME,'path':PROJECT_PATH],
               'package':['name':PACKAGE_NAME,'folder_name':PACKAGE_FOLDER_NAME],
               'service': ['name':SERVICE_NAME,'path':SERVICE_PATH,'dubbo_sh_name':DUBBO_SH_NAME,'dubbo_sh_folder_name':DUBBO_SH_FOLDER_NAME],
               'node': ['nodes':NODES,'targets':TARGETS,'sleep_time':SLEEP_TIME],
               'jenkins':['path':JENKINS_PATH,'local_ip':JENKINS_LOCAL_IP,'user':JENKINS_USER,'tools_path':JENKINS_TOOLS_PATH,'local_tools_path':JENKINS_LOCAL_MYTOOLS_PTAH],
               'maven':['bin_path':MAVEN_BIN_PATH,'service_env':MAVEN_ENV],'user':['email':USER_EMAIL]]
try {
    /**更新代码，打包*/
    node ('master'){
        stage ('Checkout'){
            timeout(time: 60, unit: 'SECONDS') {
                git credentialsId: 'ssh', url: GIT_PATH
            }
        }
        stage('Build'){
            sh "${_config.maven.bin_path}/mvn  -Dmaven.test.skip=true clean package -P ${_config.maven.service_env}"
        }
        stage('Stash'){
            stash includes: "${_config.package.folder_name}/target/*.zip", name:"${_config.package.name}"
        }

    }
    stage ('Publish'){
        def _package_path = "${_config.jenkins.path}/${_config.maven.service_env}/"//jenkins传包临时目录
        for (def i = 0;i< _config.node.nodes.size();i++){
            def __node = _config.node.nodes[i]
            def __targets = _config.node.targets[i]
            node("${__node}") {
                stage ('Unstash'){
                    unstash "${_config.package.name}"
                }

                //拼接拷贝容器脚本
                def __sh_target_cp_dubbo = new StringBuffer();
                __sh_target_cp_dubbo.append('#!/bin/bash \n')
                __sh_target_cp_dubbo.append(' source /etc/profile;')
                __sh_target_cp_dubbo.append(" if [ ! -x ${_config.jenkins.tools_path}${_config.service.dubbo_sh_folder_name} ]; then")
                __sh_target_cp_dubbo.append(" scp ${_config.jenkins.user}@${__node}:${_config.jenkins.tools_path}${_config.service.dubbo_sh_name} ${_config.jenkins.tools_path};")
                __sh_target_cp_dubbo.append(" unzip -o ${_config.jenkins.tools_path}${_config.service.dubbo_sh_name} -d ${_config.jenkins.tools_path};")
                __sh_target_cp_dubbo.append(' fi')
                //拼接重启脚本
                def __sh_target_restart_dubbo = new StringBuffer();
                __sh_target_restart_dubbo.append('source /etc/profile;')
                __sh_target_restart_dubbo.append(" if [ ! -x ${_config.service.path} ]; then")
                __sh_target_restart_dubbo.append(" cp -r ${_config.jenkins.tools_path}${_config.service.dubbo_sh_folder_name} ${_config.service.path};")
                __sh_target_restart_dubbo.append(' fi;')
                __sh_target_restart_dubbo.append(" rm -r ${_config.service.path}/zip/*;")
                __sh_target_restart_dubbo.append(" cp ${_package_path+_config.package.name} ${ _config.service.path}zip/;")
                __sh_target_restart_dubbo.append(" sh ${_config.service.path}/bin/restart.sh;")

                if (!__targets){//没有目标机器，发布在facade
                    echo '没有目标机器'
                    sh "mkdir -p ${_config.jenkins.tools_path}"//创建工具文件夹
                    sh "mkdir -p ${_config.project.path}"//创建项目目录
                    writeFile encoding: 'utf-8', file: 'cp_dubbo_sh.sh', text: "${__sh_target_cp_dubbo.toString()}"
                    sh "sh cp_dubbo_sh.sh"
                    sh "mkdir -p ${_package_path}"//在服务器创建jenkins传包临时目录
                    sh "cp ${_config.package.folder_name}/target/${_config.package.name} ${_package_path}"//传包到目标
                    //启动
                    writeFile encoding: 'utf-8', file: 'deploy.sh', text: '''#!/bin/bash
                    '''+
                            __sh_target_restart_dubbo.toString()+'''
                        '''
                    sh "sh deploy.sh"
                }else{//循环发布到目标机器
                    for (def __targetNode in __targets){

                        sh "ssh jhd@${__targetNode} 'mkdir -p ${_config.jenkins.tools_path}'"//创建工具文件夹
                        sh "ssh jhd@${__targetNode} 'mkdir -p ${_config.project.path}'"//创建项目目录
                        //判断目标机器工具文件夹下启动脚本是否存在不存在就拷贝一个并解压
                        writeFile encoding: 'utf-8', file: 'cp_dubbo_sh.sh', text: "${__sh_target_cp_dubbo.toString()}"
                        sh "ssh jhd@${__targetNode} 'bash -s' < cp_dubbo_sh.sh"

                        sh "ssh ${_config.jenkins.user}@${__targetNode} mkdir -p ${_package_path}"//在目标服务器创建jenkins传包临时目录
                        sh "scp ${_config.package.folder_name}/target/${_config.package.name} ${_config.jenkins.user}@${__targetNode}:${_package_path}"//传包到目标服务器
                        //启动
                        writeFile encoding: 'utf-8', file: 'deploy.sh', text: '''#!/bin/bash
                    '''+
                                __sh_target_restart_dubbo.toString()+'''
                        '''
                        sh "ssh jhd@${__targetNode} 'bash -s' < deploy.sh"
                        /***等待服务启动时间*/
                        sleep _config.node.sleep_time
                    }
                }
            }
        }
    }
} catch (e) {
    message = "构建失败"
    failMessage = e
    throw e
}finally {
    stage ('Mail'){
        try {
            mail bcc: '', body: "Job '${env.JOB_NAME}' (${env.BUILD_NUMBER}) ${env.BUILD_URL} ${failMessage}", cc: '', from: '1323548361@qq.com', replyTo: '', subject: message, to: "${_config.user.email}"
        } catch (e) {
            e.printStackTrace()
        }
    }
}