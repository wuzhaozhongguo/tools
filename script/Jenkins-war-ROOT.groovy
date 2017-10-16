/**参数*/
def PROJECT_NAME = 'yt'//项目名称
def SERVICE_NAME = 'im'//服务名称
def SERVICE_FOLDER_NAME = 'service-im'//发布服务文件夹名称,不配置默认使用服务名称作为服务文件夹名称
def MAVEN_ENV = 'beta-jhd'//Maven打包环境
def GIT_PATH = 'git@172.16.61.211:service/service-im.git'//git地址
def GIT_BRANCH = 'master'
def USER_EMAIL = '765105646@qq.com'//邮件接收人
def NODES = ['beta_facade_mq']//跳板机(在Jenkins中配置配置的节点名称,目标服务器从跳板机拷贝文件也用的这个名称)
def TARGETS = [['infra02']]//目标服务器
//端口配置
def SHUTDOWN_PORT=8029
def HTTP_PORT=8104
def AJP_PORT=8033
def SLEEP_TIME = 0//等待服务启动时间,秒,如果只有一个服务，或者不需要等待设置为0

@Library('jenkins-shared-library')
import com.jihuiduo.PublishWarInRoot

static void pub(PublishWarInRoot pubClass,PROJECT_NAME,SERVICE_NAME,SERVICE_FOLDER_NAME,MAVEN_ENV,GIT_PATH,GIT_BRANCH,USER_EMAIL,NODES,TARGETS,SHUTDOWN_PORT,HTTP_PORT,AJP_PORT,SLEEP_TIME){
    pubClass.publish(PROJECT_NAME,SERVICE_NAME,SERVICE_FOLDER_NAME,MAVEN_ENV,GIT_PATH,GIT_BRANCH,
            USER_EMAIL,NODES,TARGETS,SHUTDOWN_PORT,HTTP_PORT,AJP_PORT,SLEEP_TIME)
}
pub(new PublishWarInRoot(),PROJECT_NAME,SERVICE_NAME,SERVICE_FOLDER_NAME,MAVEN_ENV,GIT_PATH,GIT_BRANCH,USER_EMAIL,NODES,TARGETS,SHUTDOWN_PORT,HTTP_PORT,AJP_PORT,SLEEP_TIME)