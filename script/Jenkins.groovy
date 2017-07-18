/**参数*/
def PROJECT_NAME = 'yt'//项目名称
def SERVICE_NAME = 'message'//服务名称
def SERVICE_FOLDER_NAME = ''//发布服务文件夹名称,不配置默认使用服务名称作为服务文件夹名称
def MAVEN_ENV = 'beta-jhd'//Maven打包环境
def GIT_PATH = 'git@172.16.61.211:domain/domain-message.git'//git地址
def GIT_BRANCH = 'dev'
def USER_EMAIL = 'zhao.wu@jihuiduo.com'//邮件接收人
def NODES = ['BETA']//跳板机(在Jenkins中配置配置的节点名称,目标服务器从跳板机拷贝文件也用的这个名称)
def TARGETS = [['domain03']]//目标服务器
def SLEEP_TIME = 0//等待服务启动时间,秒,如果只有一个服务，或者不需要等待设置为0

@Library('jenkins-shared-library')
import com.jihuiduo.Publish

static void pub(Publish pubClass,PROJECT_NAME,SERVICE_NAME,SERVICE_FOLDER_NAME,MAVEN_ENV,GIT_PATH,GIT_BRANCH,USER_EMAIL,NODES,TARGETS,SLEEP_TIME){
    pubClass.publish(PROJECT_NAME,SERVICE_NAME,SERVICE_FOLDER_NAME,MAVEN_ENV,GIT_PATH,GIT_BRANCH,USER_EMAIL,NODES,TARGETS,SLEEP_TIME)
}
pub(new Publish(),PROJECT_NAME,SERVICE_NAME,SERVICE_FOLDER_NAME,MAVEN_ENV,GIT_PATH,GIT_BRANCH,USER_EMAIL,NODES,TARGETS,SLEEP_TIME)