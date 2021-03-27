package com.lianggao.service.Impl;

import cn.jpush.api.push.PushResult;
import com.lianggao.bean.Application;
import com.lianggao.bean.ApplicationInstance;
import com.lianggao.bean.DangerApplication;
import com.lianggao.bean.UserInfo;
import com.lianggao.dao.ActivitiMapper;
import com.lianggao.dao.ApplicationMapper;
import com.lianggao.dao.UserInfoMapper;
import com.lianggao.service.AssociateMangementService;
import com.lianggao.utils.DBOperator;
import com.lianggao.utils.DBUtils;
import com.lianggao.utils.DBo;
import com.lianggao.utils.JiguangPush;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class AssociateMangementServiceImpl implements AssociateMangementService {
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private ActivitiMapper activitiMapper;
    @Override
    public String insertApplicationTask(Application application, List<String> ApproverList) {
        try{
            //String Applicant = "a"+application.getApplicant().toString();
            String Applicant = "a1";
            // 创建开始
            StartEvent startEvent = new StartEvent();
            startEvent.setId("startEvent");
            startEvent.setName("startEvent");
            System.out.println("------>startEvent");
            // 创建危险作业申请
            UserTask applyTask = new UserTask();
            applyTask.setId(Applicant);
            applyTask.setName(Applicant);
            applyTask.setAssignee(Applicant);
            System.out.println("------>a1");
            // 创建次级审批成员
            List<UserTask> userTaskList = createUserTaskList(ApproverList);
            //System.out.println("------>size: "+userTaskList.size());
            // 创建结束点
            EndEvent endEvent = new EndEvent();
            endEvent.setId("endEvent");
            endEvent.setName("endEvent");
            System.out.println("------>endEvent");
            System.out.println("=================================");
            // 创建连线: startEvent->用户申请
            SequenceFlow s1 = new SequenceFlow();
            s1.setId("s1");
            s1.setName("s1");
            s1.setSourceRef("startEvent");
            s1.setTargetRef(Applicant);
            System.out.println("tartEvent->a1");
            List<SequenceFlow> sequenceFlowList = createSequenceFlowList(Applicant, ApproverList);
            System.out.println("创建连线成功 sequenceFlowList size"+sequenceFlowList.size());
            // 连接Task
            List<SequenceFlow> sequenceFlowList1 = new ArrayList<>();
            sequenceFlowList1.add(s1);
            startEvent.setOutgoingFlows(sequenceFlowList1);
            applyTask.setIncomingFlows(sequenceFlowList1);
            System.out.println("=================================");
            System.out.println("startEvent-->a1");
            int userTaskNumber = 0;
            int sequenceFlowNumber = 1;
            System.out.println("sequenceFlowList size"+sequenceFlowList.size());
            for(SequenceFlow sequenceFlow:sequenceFlowList){
                if(sequenceFlowNumber==1){//包含申请节点
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    applyTask.setOutgoingFlows(currentSequenceFlow);
                    userTaskList.get(userTaskNumber).setIncomingFlows(currentSequenceFlow);
                    System.out.println("a1-->"+userTaskList.get(userTaskNumber).getAssignee());
                }else if(sequenceFlowNumber==sequenceFlowList.size()){//最后一个节点
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    userTaskList.get(userTaskNumber-1).setOutgoingFlows(currentSequenceFlow);
                    endEvent.setIncomingFlows(currentSequenceFlow);
                    System.out.println(userTaskList.get(userTaskNumber-1).getAssignee()+"-->EndEvent");
                }else{
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    userTaskList.get(userTaskNumber-1).setOutgoingFlows(currentSequenceFlow);
                    userTaskList.get(userTaskNumber).setIncomingFlows(currentSequenceFlow);
                    System.out.println(userTaskList.get(userTaskNumber-1).getAssignee()+"-->"+userTaskList.get(userTaskNumber).getAssignee());
                }
                userTaskNumber++;
                sequenceFlowNumber++;
            }

            System.out.println("完成进行进出设置");
            // 创建流程
            org.activiti.bpmn.model.Process process = new Process();
            process.setName("Apply");
            process.setId("Apply");
            System.out.println("=================================");
            process.addFlowElement(startEvent);
            System.out.println("------>startEvent");
            process.addFlowElement(applyTask);
            System.out.println("------>a1");
            for(UserTask userTask: userTaskList){
                process.addFlowElement(userTask);
                System.out.println("------>"+userTask.getAssignee());
            }
            process.addFlowElement(endEvent);
            System.out.println("------>endEvent");
            process.addFlowElement(s1);
            System.out.println("------>s1");
            for(SequenceFlow sequenceFlow: sequenceFlowList){
                process.addFlowElement(sequenceFlow);
                System.out.println("------>"+sequenceFlow.getId());
            }
            // 创建Bpmnmodel
            BpmnModel bpmnModel = new BpmnModel();
            bpmnModel.addProcess(process);
            System.out.println("开始部署");
            org.activiti.engine.repository.Deployment deployment = processEngine.getRepositoryService().createDeployment()
                    .name("bpmn")
                    .addBpmnModel("Apply.bpmn", bpmnModel) // 这个addBpmnModel第一个参数一定要带后缀.bpmn
                    .deploy();
            System.out.println("部署完成");
            System.out.println(deployment.getId()+" "+deployment.getName()+" "+deployment.getTenantId());
            System.out.println("==============");
            return startProcessByID(deployment.getId());
        }catch (Exception e){
            return "insertApplicationTask_failed";
        }

    }

    @Override
    public String insertApplicationTaskByUserList(UserInfo userInfo, Application application, List<UserInfo> ApproverList, String BusinessKey) {
        try{
            System.out.println("=======================进入了insertApplicationTaskByUserList");
            List<String>idList = new ArrayList<>();//创建存放用户id的list,_1,_2
            List<String>nameList = new ArrayList<>();
            for(UserInfo userInfo1:ApproverList){
                idList.add("_"+userInfo1.getUserId());
                nameList.add(userInfo1.getUserName());
                System.out.println("===="+userInfo1.getUserId()+"====="+userInfo1.getUserName());
            }

            //String Applicant = "a"+application.getApplicant().toString();
            String Applicant = "_"+userInfo.getUserId().toString();
            // 创建开始
            StartEvent startEvent = new StartEvent();
            startEvent.setId("startEvent");
            startEvent.setName("startEvent");
            System.out.println("------>startEvent");
            // 创建危险作业申请
            UserTask applyTask = new UserTask();
            applyTask.setId(Applicant);
            applyTask.setName(userInfo.getUserName());
            applyTask.setAssignee(Applicant);
            System.out.println(userInfo.getUserName());
            // 创建次级审批成员
            List<UserTask> userTaskList = createUserTaskListByUserList(idList, nameList);
            //System.out.println("------>size: "+userTaskList.size());
            // 创建结束点
            EndEvent endEvent = new EndEvent();
            endEvent.setId("endEvent");
            endEvent.setName("endEvent");
            System.out.println("------>endEvent");
            System.out.println("=================================");
            // 创建连线: startEvent->用户申请
            SequenceFlow s1 = new SequenceFlow();
            s1.setId("s1");
            s1.setName("s1");
            s1.setSourceRef("startEvent");
            s1.setTargetRef(Applicant);
            System.out.println("StartEvent->_1");
            List<SequenceFlow> sequenceFlowList = createSequenceFlowList(Applicant, idList);
            System.out.println("创建连线成功 sequenceFlowList size"+sequenceFlowList.size());
            // 连接Task
            List<SequenceFlow> sequenceFlowList1 = new ArrayList<>();
            sequenceFlowList1.add(s1);
            startEvent.setOutgoingFlows(sequenceFlowList1);
            applyTask.setIncomingFlows(sequenceFlowList1);
            System.out.println("=================================");
            System.out.println("startEvent-->a1");
            int userTaskNumber = 0;
            int sequenceFlowNumber = 1;
            System.out.println("sequenceFlowList size"+sequenceFlowList.size());
            for(SequenceFlow sequenceFlow:sequenceFlowList){
                if(sequenceFlowNumber==1){//包含申请节点
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    applyTask.setOutgoingFlows(currentSequenceFlow);
                    userTaskList.get(userTaskNumber).setIncomingFlows(currentSequenceFlow);
                    System.out.println("a1-->"+userTaskList.get(userTaskNumber).getAssignee());
                }else if(sequenceFlowNumber==sequenceFlowList.size()){//最后一个节点
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    userTaskList.get(userTaskNumber-1).setOutgoingFlows(currentSequenceFlow);
                    endEvent.setIncomingFlows(currentSequenceFlow);
                    System.out.println(userTaskList.get(userTaskNumber-1).getAssignee()+"-->EndEvent");
                }else{
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    userTaskList.get(userTaskNumber-1).setOutgoingFlows(currentSequenceFlow);
                    userTaskList.get(userTaskNumber).setIncomingFlows(currentSequenceFlow);
                    System.out.println(userTaskList.get(userTaskNumber-1).getAssignee()+"-->"+userTaskList.get(userTaskNumber).getAssignee());
                }
                userTaskNumber++;
                sequenceFlowNumber++;
            }

            System.out.println("完成进行进出设置");
            // 创建流程
            org.activiti.bpmn.model.Process process = new Process();
            process.setName("Apply");
            process.setId("Apply");
            System.out.println("=================================");
            process.addFlowElement(startEvent);
            System.out.println("------>startEvent");
            process.addFlowElement(applyTask);
            System.out.println("------>fengxinxin");
            for(UserTask userTask: userTaskList){
                process.addFlowElement(userTask);
                System.out.println("------>"+userTask.getAssignee());
            }
            process.addFlowElement(endEvent);
            System.out.println("------>endEvent");
            process.addFlowElement(s1);
            System.out.println("------>s1");
            for(SequenceFlow sequenceFlow: sequenceFlowList){
                process.addFlowElement(sequenceFlow);
                System.out.println("------>"+sequenceFlow.getId());
            }
            // 创建Bpmnmodel
            BpmnModel bpmnModel = new BpmnModel();
            bpmnModel.addProcess(process);
            System.out.println("开始部署");
            org.activiti.engine.repository.Deployment deployment = processEngine.getRepositoryService().createDeployment()
                    .name("bpmn")
                    .addBpmnModel("Apply.bpmn", bpmnModel) // 这个addBpmnModel第一个参数一定要带后缀.bpmn
                    .deploy();
            System.out.println("部署完成");
            System.out.println(deployment.getId()+" "+deployment.getName()+" "+deployment.getTenantId());
            System.out.println("==============");
            String InstanceId =  startProcessByID(deployment.getId(), BusinessKey);
            this.finishFirstTask(InstanceId, userInfo.getUserId().toString());
            return InstanceId;
        }catch (Exception e){
            return "insertApplicationTask_failed";
        }
    }

    /**
     * 根据危险作业审批人列表创建审批节点列表
     * List<String>ApproverList:危险作业审批人列表;List<UserTask>:工作流审批节点列表
     * List<UserTask>:返回任务列表
     */
    public List<UserTask> createUserTaskList(List<String>ApproverList){
        List<UserTask> userTaskList = new ArrayList<>();
        for(String approver: ApproverList){
            UserTask userTask = new UserTask();
            userTask.setId(approver);
            userTask.setName(approver);
            userTask.setAssignee(approver);
            userTaskList.add(userTask);
            System.out.println("------>"+userTask.getAssignee());
        }
        return userTaskList;
    }
    /**
     * 创建连线,用户申请->审批人列表->结束节点
     * String applicant:申请人;List<Integer>ApproverList:危险作业审批人列表
     * List<SequenceFlow>:SequenceFlow列表
     */
    public List<SequenceFlow> createSequenceFlowList(String applicant, List<String>ApproverList){
        List<SequenceFlow> sequenceFlowList = new ArrayList<>();
        int sequenceNumber = 1;
        String lastSequenceFlow = " ";
        for(String approver: ApproverList){
            SequenceFlow sequenceFlow = new SequenceFlow();
            if(sequenceNumber == 1){
                sequenceFlow.setId("s"+approver);
                sequenceFlow.setName(approver);
                sequenceFlow.setSourceRef(applicant);
                sequenceFlow.setTargetRef(approver);
                System.out.println(applicant+"-->"+approver);
                lastSequenceFlow =  approver;

            }else{

                sequenceFlow.setId("s"+approver);
                sequenceFlow.setName(approver);
                sequenceFlow.setSourceRef(lastSequenceFlow);
                sequenceFlow.setTargetRef(approver);
                System.out.println(lastSequenceFlow+"-->"+approver);
                lastSequenceFlow =  approver;

            }
            sequenceNumber++;
            sequenceFlowList.add(sequenceFlow);
        }

        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId("s"+lastSequenceFlow);
        sequenceFlow.setName(lastSequenceFlow);
        sequenceFlow.setSourceRef(lastSequenceFlow);
        sequenceFlow.setTargetRef("endEvent");
        System.out.println(lastSequenceFlow+"-->"+"endEvent");
        return sequenceFlowList;
    }
    /**
     * 根据deployment_Id找到procdef_Id,根据act_re_procedef中的ID进行启动
     * String deploymentID:部署id; String BusinessKey:存储application表中的DangerTaskId
     * String:返回InstanceID
     */
    public String startProcessByID(String deploymentID, String BusinessKey){

        //150001
        Application application = new Application();


        //这里需要存入Application表里面,返回得到BusinessKey
        Map<String, Object> variables = new HashMap<>();
        variables.put("application", application);

        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploymentID).singleResult();
        System.out.println(processDefinition.getId());



        //部署完成后会有一个部署ID
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId(), BusinessKey, variables);
        if(processInstance!=null){
            System.out.println(processInstance.getId()+"启动成功");
            return  processInstance.getId();
        }
        return " ";

    }
    /**
     * 根据deployment_Id找到procdef_Id,根据act_re_procedef中的ID进行启动
     * String deploymentID:部署id;
     * String:返回InstanceID
     */
    public String startProcessByID(String deploymentID){

        //150001
        Application application = new Application();


        //这里需要存入Application表里面,返回得到BusinessKey
        Map<String, Object> variables = new HashMap<>();
        variables.put("application", application);

        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploymentID).singleResult();
        System.out.println(processDefinition.getId());


        String BusinessKey = "123";
        //部署完成后会有一个部署ID
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId(), BusinessKey, variables);
        if(processInstance!=null){
            System.out.println(processInstance.getId()+"启动成功");
            return  processInstance.getId();
        }
        return " ";

    }
    /**
     * 根据危险作业审批人列表创建审批节点列表
     * List<String>idList:危险作业审批人编号; List<String>nameList: 审批人员名称列表
     * List<UserTask>:返回任务列表
     */
    public List<UserTask> createUserTaskListByUserList(List<String>idList, List<String>nameList){
        List<UserTask> userTaskList = new ArrayList<>();
        for(int i =0;i<idList.size();i++){
            UserTask userTask = new UserTask();
            userTask.setId(idList.get(i));
            userTask.setName(nameList.get(i));
            userTask.setAssignee(idList.get(i));
            userTaskList.add(userTask);
            System.out.println("------>"+userTask.getName()+userTask.getAssignee());
        }
        return userTaskList;
    }

    /**
     * 查看所有的申请
     * Application application:申请实体;List<UserInfo> ApproverList：申请人列表
     * String:
     */
    @Override
    public List<ApplicationInstance> getAllApprovalListInfo(){
        System.out.println("===================进入getAllApprovalListInfo中");
        List<HistoricProcessInstance> historicProcessInstanceList = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .list();
        List<ApplicationInstance>applicationInstanceList = new ArrayList<>();
        ApplicationInstance applicationInstance ;
        for(HistoricProcessInstance historicProcessInstance:historicProcessInstanceList){
            applicationInstance = new ApplicationInstance();
            System.out.println("实例id"+historicProcessInstance.getId());
            System.out.println("实例name"+historicProcessInstance.getName());
            System.out.println("实例deploymentid"+historicProcessInstance.getDeploymentId());
            System.out.println("实例businesskey"+historicProcessInstance.getBusinessKey());
            System.out.println("实例startTime"+historicProcessInstance.getStartTime());
            System.out.println("实例endTime"+historicProcessInstance.getEndTime());
            if(!isDeleted(historicProcessInstance.getId())){
                applicationInstance.setApplication(this.getApplicationByBusinessKey(Integer.parseInt(historicProcessInstance.getBusinessKey())));//设置每一个实例对应的申请记录
                applicationInstance.setInstanceId(historicProcessInstance.getId());//设置每一个实例id
                applicationInstance.setApproveList(this.findAllNode(historicProcessInstance.getProcessDefinitionId()));//找到该实例的所有参与者，包含申请者和审批人。
                applicationInstanceList.add(applicationInstance);
            }else{
                ;//该实例如果已经被删除则不添加
            }




        }
        return applicationInstanceList;
    }
    /**
     * 根据BusinessKey查询得到单条申请记录
     * String BusinessKey:
     * Application:返回的记录
     */
    @Override
    public Application getApplicationByBusinessKey(int BusinessKey){
        System.out.println("========进入getAppliationByBusinesKey"+BusinessKey);
        Application application = new Application();
        application = applicationMapper.selectByBusinessKey(BusinessKey);
        System.out.println("获得单条记录成功"+application.toString());
        return application;
    }
    /**
     * 根据proceDefinedId找到所有的节点.这里当前节点以前的部分根据查询历史任务获得，
     * 当前节点以后的部分根据getBpmnModel获得，因为这里可能会出现改派的情况。
     * List<UserInfo> :返回找到的审核人员，放入到列表中
     */
    public List<UserInfo> findAllNode(String processDefinedID){
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(processDefinedID);
        List<UserInfo> userInfoList = new ArrayList<>();
        if(bpmnModel!=null){
            Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
            for(FlowElement e : flowElements) {
                if((e.getClass().toString()).equals("class org.activiti.bpmn.model.UserTask")){
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(Integer.parseInt(e.getId().substring(1)));//id为_1,_2去掉下划线
                    userInfo.setUserName(e.getName());//需要在这里修改一下，根据Assign获取id，然后获得姓名

                    //UserInfo userInfo1 = userInfoMapper.selectByPrimaryKey(e.get)
                    userInfoList.add(userInfo);
                    System.out.println("flowelement id:" + e.getId() + "  name:" + e.getName() + "   class:" + e.getClass().toString());
                }
            }
        }
        return userInfoList;
    }
    /**
     * 根据登录的用户，查询所有该用户待审批的任务
     * String userid:待查询的用户id
     * List<ApplicationInstance>:用户待审批的任务
     */
    @Override
    public List<ApplicationInstance> getUserTask(String userid) {
        List<Task> taskList = processEngine.getTaskService().createTaskQuery().taskAssignee("_"+userid).list();
        List<ApplicationInstance>applicationInstanceList = new ArrayList<>();
        ApplicationInstance applicationInstance;
        ProcessInstance processInstance;
        for(Task task:taskList){







            applicationInstance = new ApplicationInstance();
            processInstance = findInstanceByTask(task);
            boolean suspend = processInstance.isSuspended();
            if(!suspend){
                System.out.println("==================该实例没有被挂起");
                System.out.println("实例id"+processInstance.getId());
                System.out.println("实例name"+processInstance.getName());
                System.out.println("实例deploymentid"+processInstance.getDeploymentId());
                System.out.println("实例businesskey"+processInstance.getBusinessKey());
                applicationInstance.setApplication(this.getApplicationByBusinessKey(Integer.parseInt(processInstance.getBusinessKey())));//设置每一个实例对应的申请记录
                applicationInstance.setInstanceId(processInstance.getProcessInstanceId());//设置每一个实例id
                applicationInstance.setApproveList(this.findAllNode(processInstance.getProcessDefinitionId()));//找到该实例的所有参与者，包含申请者和审批人。
                applicationInstanceList.add(applicationInstance);
            }else{
                System.out.println("==================该实例被挂起");

            }

        }
        return applicationInstanceList;
    }
    /**
     * 根据Task,找到对应的Instance,再找到实例对应的Businesskey
     * Task task:用户的任务
     * ProcessInstance:返回实例
     */
    public ProcessInstance  findInstanceByTask(Task task){
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        return processInstance;
    }
    /**
     * 查询个人的申请
     * @param userid
     * @return
     */
    @Override
    public List<Application> getUserApplication(String userid) {
        System.out.println("==============开始查询该用户"+userid+"的个人申请");
        List<Application>applicationList = new ArrayList<>();
        applicationList = applicationMapper.getUserApplication(Integer.parseInt(userid));
        for(Application application:applicationList){
            System.out.println("========================"+application.toString());
        }
        return applicationList;
    }
    /**
     * 申请者完成个人任务,在启动之后会调用该方法，首先完成申请节点
     * String instanceId:实例id; String userid:用户id
     */
    public void finishFirstTask(String instanceId, String userid){
        TaskQuery taskQuery = processEngine.getTaskService().createTaskQuery();
        taskQuery.taskAssignee("_"+userid);
        taskQuery.processInstanceId(instanceId);
        Task task = taskQuery.singleResult();
        processEngine.getTaskService().complete(task.getId());
        System.out.println("=============申请者任务已经完成");
    }
    /**
     * 审批人处理申请.根据实例号和用户id找到用户当前的任务进行完成.暂时先不添加评论
     * String InstanceId:实例id;UserInfo userInfo:用户信息;String comment:用户的评论
     */
    @Override
    public void finishMyTask(String InstanceId, UserInfo userInfo, String comment) {
        TaskQuery taskQuery = processEngine.getTaskService().createTaskQuery();
        taskQuery.taskAssignee("_"+userInfo.getUserId());
        taskQuery.processInstanceId(InstanceId);
        Task task = taskQuery.singleResult();
        String BusinessKey = this.getProcessInstanceById(InstanceId).getBusinessKey();
        processEngine.getTaskService().addComment(task.getId(), InstanceId, comment);//增加评论
        processEngine.getTaskService().complete(task.getId());
        //判断是否完成审批,如果完成审批,设置application中的状态
        String status = this.IsInstanceFinished(InstanceId);
        Application application = new Application();
        application.setDangerTaskId(Integer.parseInt(BusinessKey));//得到该实例的DangerTaskId

        if(status.equals("finished")){
            application.setState(1);
            applicationMapper.setApplicationState(application);
            System.out.println("====================该实例已经全部完成");
        }else{

        }
        System.out.println("=============该节点任务已经完成");
    }
    /**
     *查看该实例的所有审批信息，包括审批人员的名称，审批结束时间，审批意见。必须首先判断该实例是否结束
     * 如果该实例已经结束必须重新获得实例，获得的不是运行的实例
     */
    @Override
    public ApplicationInstance getSingleApproval(String InstanceId) {

        ProcessInstance pi = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象
                .createProcessInstanceQuery()//创建流程实例查询
                .processInstanceId(InstanceId)//使用流程实例ID查询
                .singleResult();
        if(pi==null){//实例已经结束
            HistoricProcessInstance processInstance = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(InstanceId).singleResult();
            ApplicationInstance applicationInstance = new ApplicationInstance();
            /*ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(InstanceId).singleResult();*/
            System.out.println("实例id"+processInstance.getId());
            System.out.println("实例name"+processInstance.getName());
            System.out.println("实例deploymentid"+processInstance.getDeploymentId());
            System.out.println("实例businesskey"+processInstance.getBusinessKey());
            applicationInstance.setApplication(this.getApplicationByBusinessKey(Integer.parseInt(processInstance.getBusinessKey())));//设置每一个实例对应的申请记录
            applicationInstance.setInstanceId(processInstance.getId());//设置每一个实例id
            //List<UserInfo>userInfoList = this.findAllNode(processInstance.getProcessDefinitionId());
            List<UserInfo>userInfoList = this.getRealNode( processInstance.getId(), processInstance.getProcessDefinitionId());

            userInfoList.remove(0);
            //applicationInstance.setApproveList(this.findAllNode(processInstance.getProcessDefinitionId()));//找到该实例的所有参与者，包含申请者和审批人。
            applicationInstance.setApproveList(userInfoList);
            applicationInstance.setCommentList(this.getSingleApprovalComment(InstanceId));
            applicationInstance.setTimeList(this.getSingleApprovalCommentTime(InstanceId));
            //applicationInstance.setTimeList(this.getSingleApprovalTasKFinishedTime(InstanceId));
            return applicationInstance;
        }else{//实例还没有结束
            ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(InstanceId).singleResult();
            ApplicationInstance applicationInstance = new ApplicationInstance();
            /*ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(InstanceId).singleResult();*/
            System.out.println("实例id"+processInstance.getId());
            System.out.println("实例name"+processInstance.getName());
            System.out.println("实例deploymentid"+processInstance.getDeploymentId());
            System.out.println("实例businesskey"+processInstance.getBusinessKey());
            applicationInstance.setApplication(this.getApplicationByBusinessKey(Integer.parseInt(processInstance.getBusinessKey())));//设置每一个实例对应的申请记录
            applicationInstance.setInstanceId(processInstance.getId());//设置每一个实例id
            //List<UserInfo>userInfoList = this.findAllNode(processInstance.getProcessDefinitionId());
            List<UserInfo>userInfoList = this.getRealNode( processInstance.getId(), processInstance.getProcessDefinitionId());
            userInfoList.remove(0);
            //applicationInstance.setApproveList(this.findAllNode(processInstance.getProcessDefinitionId()));//找到该实例的所有参与者，包含申请者和审批人。
            applicationInstance.setApproveList(userInfoList);
            applicationInstance.setCommentList(this.getSingleApprovalComment(processInstance.getProcessInstanceId()));
            applicationInstance.setTimeList(this.getSingleApprovalCommentTime(processInstance.getProcessInstanceId()));
            //applicationInstance.setTimeList(this.getSingleApprovalTasKFinishedTime(InstanceId));
            return applicationInstance;
        }



    }
    /**
     * 得到所有审批人的审批意见
     * String InstanceId:实例id
     */
    @Override
    public List<String> getSingleApprovalComment(String InstanceId) {
        List<Comment> commentList = processEngine.getTaskService().getProcessInstanceComments(InstanceId);
        List<String>comments= new ArrayList<>();

        for(Comment comment:commentList){
            comments.add(comment.getFullMessage());
        }
        Collections.reverse(comments);
        return comments;
    }
    /**
     * 得到所有审批人的审批意见时间
     * String InstanceId:实例id
     */
    @Override
    public List<String> getSingleApprovalCommentTime(String InstanceId) {
        List<Comment> commentList = processEngine.getTaskService().getProcessInstanceComments(InstanceId);
        List<String>timeList= new ArrayList<>();

        for(Comment comment:commentList){
            timeList.add(comment.getTime().toString());
        }
        Collections.reverse(timeList);
        return timeList;
    }
    /**
     * 转发,将待审批的任务转发给不再审批列表中的其他人
     * String InstanceId:实例, String userid:用户id; String userid2:转发的用户id
     */
    @Override
    public void forward(String InstanceId, String userid, UserInfo userInfo) {
        TaskQuery taskQuery = processEngine.getTaskService().createTaskQuery();
        taskQuery.taskAssignee("_"+userid);
        taskQuery.processInstanceId(InstanceId);
        Task task = taskQuery.singleResult();
/*        task.setAssignee("_"+userInfo.getUserId());
        task.setName(userInfo.getUserName());*/
        processEngine.getTaskService().setAssignee(task.getId(), "_"+userInfo.getUserId().toString());
        processEngine.getTaskService().setOwner(task.getId(), userInfo.getUserName());


    }
    /**
     *最后一个审批人动态增加审批人,不需要增加申请Application,BusinessKey可以通过被复制的实例获得
     */
    @Override
    public String insertApplicationTaskByCopy(UserInfo userInfo, List<UserInfo> ApproverList, String BusinessKey) {
        try{
            System.out.println("=======================进入了insertApplicationTaskByCopy");
            List<String>idList = new ArrayList<>();//创建存放用户id的list,_1,_2
            List<String>nameList = new ArrayList<>();
            for(UserInfo userInfo1:ApproverList){
                idList.add("_"+userInfo1.getUserId());
                nameList.add(userInfo1.getUserName());
                System.out.println("===="+userInfo1.getUserId()+"====="+userInfo1.getUserName());
            }

            //String Applicant = "a"+application.getApplicant().toString();
            String Applicant = "_"+userInfo.getUserId().toString();
            // 创建开始
            StartEvent startEvent = new StartEvent();
            startEvent.setId("startEvent");
            startEvent.setName("startEvent");
            System.out.println("------>startEvent");
            // 创建危险作业申请
            UserTask applyTask = new UserTask();
            applyTask.setId(Applicant);
            applyTask.setName(userInfo.getUserName());
            applyTask.setAssignee(Applicant);
            System.out.println(userInfo.getUserName());
            // 创建次级审批成员
            List<UserTask> userTaskList = createUserTaskListByUserList(idList, nameList);
            //System.out.println("------>size: "+userTaskList.size());
            // 创建结束点
            EndEvent endEvent = new EndEvent();
            endEvent.setId("endEvent");
            endEvent.setName("endEvent");
            System.out.println("------>endEvent");
            System.out.println("=================================");
            // 创建连线: startEvent->用户申请
            SequenceFlow s1 = new SequenceFlow();
            s1.setId("s1");
            s1.setName("s1");
            s1.setSourceRef("startEvent");
            s1.setTargetRef(Applicant);
            System.out.println("StartEvent->_1");
            List<SequenceFlow> sequenceFlowList = createSequenceFlowList(Applicant, idList);
            System.out.println("创建连线成功 sequenceFlowList size"+sequenceFlowList.size());
            // 连接Task
            List<SequenceFlow> sequenceFlowList1 = new ArrayList<>();
            sequenceFlowList1.add(s1);
            startEvent.setOutgoingFlows(sequenceFlowList1);
            applyTask.setIncomingFlows(sequenceFlowList1);
            System.out.println("=================================");
            System.out.println("startEvent-->a1");
            int userTaskNumber = 0;
            int sequenceFlowNumber = 1;
            System.out.println("sequenceFlowList size"+sequenceFlowList.size());
            for(SequenceFlow sequenceFlow:sequenceFlowList){
                if(sequenceFlowNumber==1){//包含申请节点
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    applyTask.setOutgoingFlows(currentSequenceFlow);
                    userTaskList.get(userTaskNumber).setIncomingFlows(currentSequenceFlow);
                    System.out.println("a1-->"+userTaskList.get(userTaskNumber).getAssignee());
                }else if(sequenceFlowNumber==sequenceFlowList.size()){//最后一个节点
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    userTaskList.get(userTaskNumber-1).setOutgoingFlows(currentSequenceFlow);
                    endEvent.setIncomingFlows(currentSequenceFlow);
                    System.out.println(userTaskList.get(userTaskNumber-1).getAssignee()+"-->EndEvent");
                }else{
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    userTaskList.get(userTaskNumber-1).setOutgoingFlows(currentSequenceFlow);
                    userTaskList.get(userTaskNumber).setIncomingFlows(currentSequenceFlow);
                    System.out.println(userTaskList.get(userTaskNumber-1).getAssignee()+"-->"+userTaskList.get(userTaskNumber).getAssignee());
                }
                userTaskNumber++;
                sequenceFlowNumber++;
            }

            System.out.println("完成进行进出设置");
            // 创建流程
            org.activiti.bpmn.model.Process process = new Process();
            process.setName("Apply");
            process.setId("Apply");
            System.out.println("=================================");
            process.addFlowElement(startEvent);
            System.out.println("------>startEvent");
            process.addFlowElement(applyTask);
            System.out.println("------>fengxinxin");
            for(UserTask userTask: userTaskList){
                process.addFlowElement(userTask);
                System.out.println("------>"+userTask.getAssignee());
            }
            process.addFlowElement(endEvent);
            System.out.println("------>endEvent");
            process.addFlowElement(s1);
            System.out.println("------>s1");
            for(SequenceFlow sequenceFlow: sequenceFlowList){
                process.addFlowElement(sequenceFlow);
                System.out.println("------>"+sequenceFlow.getId());
            }
            // 创建Bpmnmodel
            BpmnModel bpmnModel = new BpmnModel();
            bpmnModel.addProcess(process);
            System.out.println("开始部署");
            org.activiti.engine.repository.Deployment deployment = processEngine.getRepositoryService().createDeployment()
                    .name("bpmn")
                    .addBpmnModel("Apply.bpmn", bpmnModel) // 这个addBpmnModel第一个参数一定要带后缀.bpmn
                    .deploy();
            System.out.println("部署完成");
            System.out.println(deployment.getId()+" "+deployment.getName()+" "+deployment.getTenantId());
            System.out.println("==============");
            String InstanceId =  startProcessByID(deployment.getId(), BusinessKey);
            this.finishFirstTask(InstanceId, userInfo.getUserId().toString());
            return InstanceId;
        }catch (Exception e){
            return "insertApplicationTask_failed";
        }
    }
    @Override
    public void addInstanceNode(String InstanceId, List<UserInfo>userInfoListAdd){
        //创建被复制的实例
        ProcessInstance processInstance = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象
                .createProcessInstanceQuery()//创建流程实例查询
                .processInstanceId(InstanceId)//使用流程实例ID查询
                .singleResult();
        System.out.println("==========================创建被复制的实例");
        //这里只能找到已经运行完成的实例，包括申请者和审批人
        System.out.println("**************************");
        String ProcessDefined = processInstance.getProcessDefinitionId();
        List<UserInfo>userInfoList = getRealNode(InstanceId,ProcessDefined);
        for(UserInfo userInfo :userInfoList){
            System.out.println("***"+userInfo.getUserId());
        }

        //生成实例
        UserInfo applicant = userInfoList.get(0);
        userInfoList.remove(0);
        for(UserInfo userInfo:userInfoListAdd){
            userInfoList.add(userInfo);
        }

        /*UserInfo userInfo = new UserInfo();
        userInfo.setUserName("caoxiufeng");
        userInfo.setUserId(11);
        userInfo.setUserPassword("123");
        userInfoList.add(userInfo);
        UserInfo userInfo2 = new UserInfo();
        userInfo2.setUserName("gaolaoshi");
        userInfo2.setUserId(12);
        userInfo2.setUserPassword("123");
        userInfoList.add(userInfo2);*/

        String NewInstanceId = this.insertApplicationTaskByCopy(applicant,userInfoList, processInstance.getBusinessKey());
        System.out.println("==========================生成新实例完成"+NewInstanceId);
        //获得刚才创建的实例
        ProcessInstance newProcessInstance = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象
                .createProcessInstanceQuery()//创建流程实例查询
                .processInstanceId(NewInstanceId)//使用流程实例ID查询
                .singleResult();
        //获得被复制实例，所有的评论,包括最后一个人的评论
        List<String>comoments = this.getSingleApprovalComment(InstanceId);
        System.out.println("==========================获得所有的评论"+comoments.size());
        //获得被复制实例的所有完成任务
        List<HistoricTaskInstance> historicTaskInstances = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(InstanceId)
                .finished()
                .list();
        System.out.println("==========================获得所有的历史任务"+historicTaskInstances.size());
        historicTaskInstances.remove(0);//去掉申请者
        List<String>ApproverList = new ArrayList<>();
        //遍历所有节点
        for(int number = 0; number<historicTaskInstances.size();number++){
            HistoricTaskInstance historicTaskInstance = historicTaskInstances.get(number);
            System.out.println(historicTaskInstance.getAssignee());
            System.out.println(historicTaskInstance.getStartTime());
            System.out.println(historicTaskInstance.getEndTime());
            ApproverList.add(historicTaskInstance.getAssignee());
            Task task  = processEngine.getTaskService().createTaskQuery().taskAssignee(historicTaskInstance.getAssignee()).processInstanceId(NewInstanceId).singleResult();
            processEngine.getTaskService().addComment(task.getId(), NewInstanceId, comoments.get(number));//增加评论
            processEngine.getTaskService().complete(task.getId());
            //修改评论的时间
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            String dateStr = historicTaskInstance.getEndTime().toString();
            Date date = null;
            try {
                date = (Date) sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String formatStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

            activitiMapper.updateCommentTime(formatStr, task.getId(), NewInstanceId);

        }


    }
    /**
     * 判断该实例是否完成
     * @return
     */
    public String IsInstanceFinished(String InstanceId){
        ProcessInstance pi = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象
                .createProcessInstanceQuery()//创建流程实例查询
                .processInstanceId(InstanceId)//使用流程实例ID查询
                .singleResult();
        if(pi==null) {//实例已经结束
            return "finished";
        }else{
            return "unfinished";
        }
    }
    /**
     * 根据InstanceId返回该实例
     */
    public ProcessInstance getProcessInstanceById(String processId){
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processId).singleResult();
        return processInstance;
    }
    /**
     * 找到当前任务节点之前的所有审批人
     * @return
     */
    public List<UserInfo> getBeforeNode(String processId){

        List<HistoricTaskInstance>historicTaskInstanceList = processEngine.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(processId).orderByTaskCreateTime().asc().list();
        List<UserInfo> userInfoList = new ArrayList<>();
        for(HistoricTaskInstance task:historicTaskInstanceList){

            UserInfo userInfo = userInfoMapper.selectByPrimaryKey(Integer.parseInt(task.getAssignee().substring(1)));
            userInfoList.add(userInfo);
            //根据id获得所有的
        }
        return userInfoList;
    }
    /**
     * 根据当前任务节点之前的所有审批人和该任务和该任务及该任务以后的节点,创建真正的审核人列表
     */
    public List<UserInfo> getRealNode(String ProcessId, String ProceeDefined){
        List<UserInfo> userInfoList1 = this.getBeforeNode(ProcessId);
        for(UserInfo userInfo:userInfoList1){
            System.out.println(userInfo.toString());
        }

        List<UserInfo> userInfoList2 = this.findAllNode(ProceeDefined);
        for(UserInfo userInfo:userInfoList2){
            System.out.println(userInfo.toString());
        }
        for(int i=0;i<userInfoList1.size();i++){
            userInfoList2.set(i,userInfoList1.get(i));
        }
        return userInfoList2;
    }
    /**
     *动态增加审批人,最后一个节点增加审批人.由于Activiti没有直接增加动态审批人的功能，所有复制实例
     * 修改相关的内容
     * String InstanceId:实例id;String ProcessDefined:部署id
     */
    /**
     * 得到所有审批人的审批完成时间，之间是通过得到评论完成的时间进行设置的
     * 但是增加审批人之后,评论时间无法进行修改，所有考虑时间未任务完成的时间
     * String InstanceId:实例id
     */
    public List<String> getSingleApprovalTasKFinishedTime(String InstanceId) {
        List<HistoricTaskInstance> historicTaskInstances = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(InstanceId)
                .finished()
                .list();
        List<String>timeList= new ArrayList<>();
        for(HistoricTaskInstance historicTaskInstance:historicTaskInstances){
            timeList.add(historicTaskInstance.getEndTime().toString());
        }
        return timeList;
    }
    /**
     * 判断该实例是否是被删除的实例
     */
    public Boolean isDeleted(String InstanceId){
        String result = activitiMapper.selectDeleteReason(InstanceId);
        System.out.println("=======================result"+result);
        if(result ==null){
            return false;
        }else{
            return true;
        }
    }





    //////////////////////////////////////////////////////////////////////////////////////////////
    //获取危险作业类型
    public Map<String,Object> getCategory() {
        try{
            DBo dbo =new DBo();
            String sql = "select * from datadictionary_t where DDCategoryNum='CompanyDangerTask'";
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("CompanyDangerTask",dbo.executeQuery(sql));
            sql="select * from datadictionary_t where DDCategoryNum='WorkShopDangerTask'";
            dataMap.put("WorkShopDangerTask",dbo.executeQuery(sql));
            return dataMap;
        }catch(Exception e){
            return null;
        }
    }
    //
    public Map[] getAlltaskListInfo(String conditions, int pageindex, String username, String UserInstitution) throws SQLException {
        if (conditions.equals("")) {
            DBOperator dbOperator = new DBOperator("");
            return dbOperator.getApplyList(pageindex, "0", UserInstitution);
        } else {
            if (conditions.contains("TaskInstitution")) {
                DBOperator dbOperator = new DBOperator("");
                String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + "";
                return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");
            } else {

                String sql = "select InstitutionCategoryNum from institution_t where institutionNum='" + UserInstitution + "'";
                List<Map<String, Object>> list = DBUtils.query(sql);
                String InstitutionCategoryNum = list.get(0).get("InstitutionCategoryNum").toString();
                if (InstitutionCategoryNum.equals("3")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (institutionCategoryNum='3' or institutionCategoryNum='4' or institutionCategoryNum='5')";
                    return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");

                } else if (InstitutionCategoryNum.equals("4")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (TaskInstitution='" + UserInstitution + "' or (institutionCategoryNum='5' and InstitutionPrefix='" + UserInstitution + "'))";
                    return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");

                } else if (InstitutionCategoryNum.equals("5")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and TaskInstitution='" + UserInstitution + "'";
                    return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");
                } else {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (institutionCategoryNum='3' or institutionCategoryNum='4' or institutionCategoryNum='5')";
                    return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");
                }
            }

        }
    }

    public Map[] getAlltaskList(String conditions, String username, String UserInstitution) throws SQLException {
        if (conditions.equals("")) {
            DBOperator dbOperator = new DBOperator("");
            return dbOperator.getAllApplyList("0", UserInstitution);
        } else {
            if (conditions.contains("TaskInstitution")) {
                DBOperator dbOperator = new DBOperator("");
                String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + "";
                return dbOperator.executeQuery(sqlQuery);
            } else {

                String sql = "select InstitutionCategoryNum from institution_t where institutionNum='" + UserInstitution + "'";
                List<Map<String, Object>> list = DBUtils.query(sql);
                String InstitutionCategoryNum = list.get(0).get("InstitutionCategoryNum").toString();
                if (InstitutionCategoryNum.equals("3")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (institutionCategoryNum='3' or institutionCategoryNum='4' or institutionCategoryNum='5')";
                    return dbOperator.executeQuery(sqlQuery);
                } else if (InstitutionCategoryNum.equals("4")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (TaskInstitution='" + UserInstitution + "' or (institutionCategoryNum='5' and InstitutionPrefix='" + UserInstitution + "'))";
                    return dbOperator.executeQuery(sqlQuery);
                } else if (InstitutionCategoryNum.equals("5")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and TaskInstitution='" + UserInstitution + "'";
                    return dbOperator.executeQuery(sqlQuery);
                } else {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (institutionCategoryNum='3' or institutionCategoryNum='4' or institutionCategoryNum='5')";
                    return dbOperator.executeQuery(sqlQuery);
                }
            }

        }
    }

    //添加申请任务
    public int insertApplicationTask(String ApplicantNum, String TaskInstitution, String ApplyingTime, String StartTime, String EndTime, String DangerTaskName, String Category, String Archived, String uploadfilename, String ApproverList,String DangerTaskLevel,String DangerTaskNum) {
        String sql = "insert into DangerTaskApplicationTable_T(DangerTaskNum,DangerTaskName,DangerTaskLevel,Category,Applicant,TaskInstitution,ApplyingTime,StartTime,EndTime,UploadFileName,State,Archived,ApproverList) values('"+DangerTaskNum+"','" + DangerTaskName + "','"+DangerTaskLevel+"','" + Category + "','" + ApplicantNum + "','" + TaskInstitution + "','" + ApplyingTime + "','" + StartTime + "','" + EndTime + "','" + uploadfilename + "','0','" + Archived + "','" + ApproverList + "')";
        DBOperator db = new DBOperator(sql);
        return db.executeUpdate();
    }

    //获取申请任务的详细信息
    public Map[] getTaskInfo(String taskID) {
        String sql = "select * from DangerTask_View where TaskID=" + taskID + "";
        DBOperator db = new DBOperator(sql);
        return db.executeQuery();
    }
    //删除前——获取待删除任务的详细信息
    public List<Map<String,Object>> getTaskList(String tasklist) throws Exception {
        String sql = "select * from DangerTaskApplicationTable_T where TaskID in (" + tasklist + ")";
//        DBOperator db = new DBOperator(sql);
        DBo dbo = new DBo();
        return dbo.executeQuery(sql);
    }

    public List<Map<String,Object>> getApplicant(String tasklist) throws Exception {
        String sql = "select distinct Applicant from DangerTaskApplicationTable_T where TaskID in (" + tasklist + ")";
        DBo dbo = new DBo();
        return dbo.executeQuery(sql);
    }

    //添加申请任务2
    public int insertApplicationTask2(String ApplicantNum, String TaskInstitution, String ApplyingTime, String StartTime, String EndTime, String DangerTaskName, String Category, String Archived, String uploadfilename, String ApproverList,String DangerTaskLevel,String DangerTaskNum) {
        String sql = "insert into DangerTaskApplicationTable_T(DangerTaskNum,DangerTaskName,DangerTaskLevel,Category,Applicant,TaskInstitution,ApplyingTime,StartTime,EndTime,UploadFileName,State,Archived,ApproverList) values('"+DangerTaskNum+"','" + DangerTaskName + "','"+DangerTaskLevel+"','" + Category + "','" + ApplicantNum + "','" + TaskInstitution + "','" + ApplyingTime + "','" + StartTime + "','" + EndTime + "','" + uploadfilename + "','1','" + Archived + "','" + ApproverList + "')";
        DBOperator db = new DBOperator(sql);
        return db.executeUpdate();
    }
    //添加——提交申请任务
    public int Add_submitApplicantTask(String ApplicantNum, String ApproverList, String TaskID) {
        try {
            String sql = "insert into DangerTaskApprovalTable_T (TaskID,SubmitterID,ApproveInstitution,ApproveSuggestion,ApproveResult,ApproveName,ApproveSignature,ApproveTime,Finished) values ('" + TaskID + "','" + ApplicantNum + "','" + ApproverList.split(",")[0] + "','','','" + ApproverList.split(",")[0] + "','','1970-01-01','0')";

            return DBUtils.update(sql);
        } catch (Exception e) {
            //throw e;
            return 0;
        }
    }
    //修改——提交申请任务
    public int Edit_submitApplicantTask(String ApplicantNum, String TaskInstitution, String ApplyingTime, String StartTime, String EndTime, String DangerTaskName, String Category, String Archived, String uploadfilename, String State, String ApproverList, String TaskID,String DangerTaskLevel,String DangerTaskNum) {
        try {
            String sql;
            if (State.equals("0"))    //第一次提交
            {
                sql = "update DangerTaskApplicationTable_T set DangerTaskLevel = '"+DangerTaskLevel+"', DangerTaskNum = '"+DangerTaskNum+"',DangerTaskName='" + DangerTaskName + "',Category='" + Category + "',Applicant='" + ApplicantNum + "',TaskInstitution='" + TaskInstitution + "',ApplyingTime='" + ApplyingTime + "',StartTime='" + StartTime + "',EndTime='" + EndTime + "',UploadFileName='" + uploadfilename + "',State='1',Archived='" + Archived + "',ApproverList='" + ApproverList + "' where TaskID='" + TaskID + "';" +
                        "insert into DangerTaskApprovalTable_T (TaskID,SubmitterID,ApproveInstitution,ApproveSuggestion,ApproveResult,ApproveName,ApproveSignature,ApproveTime,Finished) values ('" + TaskID + "','" + ApplicantNum + "','" + ApproverList.split(",")[0] + "','','','" + ApproverList.split(",")[0] + "','','1970-01-01','0')";

            } else {                   //被退回的重新提交  State=4
                sql = "update DangerTaskApplicationTable_T set DangerTaskLevel = '"+DangerTaskLevel+"', DangerTaskNum = '"+DangerTaskNum+"',DangerTaskName='" + DangerTaskName + "',Category='" + Category + "',Applicant='" + ApplicantNum + "',TaskInstitution='" + TaskInstitution + "',ApplyingTime='" + ApplyingTime + "',StartTime='" + StartTime + "',EndTime='" + EndTime + "',UploadFileName='" + uploadfilename + "',State='1',Archived='" + Archived + "',ApproverList='" + ApproverList + "' where TaskID='" + TaskID + "';" +
                        "delete from DangerTaskApprovalTable_T where TaskID='" + TaskID + "';" +
                        "insert into DangerTaskApprovalTable_T (TaskID,SubmitterID,ApproveInstitution,ApproveSuggestion,ApproveResult,ApproveName,ApproveSignature,ApproveTime,Finished) values ('" + TaskID + "','" + ApplicantNum + "','" + ApproverList.split(",")[0] + "','','','" + ApproverList.split(",")[0] + "','','1970-01-01','0');";

            }

            return DBUtils.update(sql);
        } catch (Exception e) {
            //throw e;
            return 0;
        }
    }
    //修改——保存申请任务
    public int updateApplicationTask(String ApplicantNum, String TaskInstitution, String ApplyingTime, String StartTime, String EndTime, String DangerTaskName, String Category, String Archived, String uploadfilename, String State, String ApproverList, String TaskID,String DangerTaskLevel,String DangerTaskNum) throws Exception {
        String sql = "update DangerTaskApplicationTable_T set DangerTaskLevel = '"+DangerTaskLevel+"', DangerTaskNum = '"+DangerTaskNum+"',DangerTaskName='" + DangerTaskName + "',Category='" + Category + "',Applicant='" + ApplicantNum + "',TaskInstitution='" + TaskInstitution + "',ApplyingTime='" + ApplyingTime + "',StartTime='" + StartTime + "',EndTime='" + EndTime + "',UploadFileName='" + uploadfilename + "',State='" + State + "',Archived='" + Archived + "',ApproverList='" + ApproverList + "' where TaskID='" + TaskID + "'";
        DBo dbo = new DBo();
        return dbo.executeUpdate(sql);
    }



    //////////////////////////////
    //获得审批页面所有记录
    //待批作业
    public Map[] getAllApprovalListInfo(String conditions, int pageindex, String username, String UserInstitution) throws SQLException {

        if (conditions.equals("")) {
            DBOperator dbOperator = new DBOperator("");
            String sqlQuery = "select row_number() over(order by ApprovalID desc) as rn, * from DangerApproval_View where (Finished='0' or Finished='3') and ApproveInstitution='" + username + "'";
            return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");
        } else {
            DBOperator dbOperator = new DBOperator("");
            String sqlQuery = "select row_number() over(order by ApprovalID desc) as rn, * from DangerApproval_View where (Finished='0' or Finished='3') and  ApproveInstitution='" + username + "' and " + conditions + "";
            return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");
        }
    }
    //待批作业页数
    public Map[] getAllApprovalList(String conditions,String username) throws SQLException {

        if (conditions.equals("")) {
            DBOperator dbOperator = new DBOperator("");
            String sqlQuery = "select row_number() over(order by ApprovalID desc) as rn, * from DangerApproval_View where (Finished='0' or Finished='3') and ApproveInstitution='" + username + "'";
            return dbOperator.executeQuery(sqlQuery);
        } else {
            DBOperator dbOperator = new DBOperator("");
            String sqlQuery = "select row_number() over(order by ApprovalID desc) as rn, * from DangerApproval_View where (Finished='0' or Finished='3') and  ApproveInstitution='" + username + "' and " + conditions + "";
            return dbOperator.executeQuery(sqlQuery);
        }
    }
    //获得审批功能界面的信息
    public List<Map<String, Object>> getApplyInfo(String ApprovalID) throws Exception {
        String sql = "select * from DangerTask_View where TaskID in ( select TaskID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "')";
        //DBOperator db = new DBOperator(sql);
        DBo db = new DBo();
        return db.executeQuery(sql);
    }
    //获得回退界面的信息
    public Map[] getSubmitter(String ApprovalID) {
        String sql = "select  SubmitterID,employee_t.EmployeeName as SubmitterName,DangerTaskApplicationTable_T.Applicant,Finished from  DangerTaskApprovalTable_T inner join employee_t on  ApprovalID='" + ApprovalID + "'  and DangerTaskApprovalTable_T.SubmitterID=employee_t.EmployeeNum inner join  DangerTaskApplicationTable_T  on DangerTaskApplicationTable_T.TaskID= DangerTaskApprovalTable_T.TaskID";
        DBOperator db = new DBOperator(sql);
        return db.executeQuery();
    }
    //审批
    public int Approve(String ApprovalID, String TaskID, String Approver, String ApproverList, String ApprovalResult,String ApproveTime,String ApprovalFinished, String ApproveSuggestion,String ApproveSignature,String flag) {
        try {
            DBo db = new DBo();
            String sql = "select Finished from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "'";
            String Finished = db.getString(sql, "Finished");
            String Applicant = db.getString("select Applicant from DangerTaskApplicationTable_T where TaskID='"+TaskID+"'","Applicant");

            String ApprovalList = db.getString("select ApproverList from DangerTaskApplicationTable_T where TaskID='" + TaskID + "'", "ApproverList");
            //已完成的人
            String List=  db.getString("select stuff((select CONVERT(varchar, ','+ApproveInstitution) from DangerTaskApprovalTable_T where taskID= "+TaskID+"  for xml path('')),1,1,'')  as list","list")+",";

            if (Finished.equals("0"))      // 第一次审批
            {

                if (ApprovalResult.equals("0"))    //不同意
                {
                    //ApprovalList=ApprovalList.substring(0, ApprovalList.indexOf(Approver)+Approver.length());
                    ApprovalList=List;
                    sql = "update DangerTaskApplicationTable_T set  State='3',ApproverList='" + ApprovalList + "' where TaskID='" + TaskID + "';" +
                            "update DangerTaskApprovalTable_T set ApproveResult='"+ApprovalResult+"',ApproveTime='" + ApproveTime+ "',Finished='"+ApprovalFinished+"',ApproveSuggestion='"+ApproveSuggestion+"',ApproveSignature='"+ApproveSignature+"' where ApprovalID='" + ApprovalID + "';" ;
                    Collection<String> alias = new ArrayList<>();
                    Collection<String> tags= new ArrayList<>();
                    alias.add( Applicant);
                    PushResult TST= JiguangPush.push(alias,null,"您有一条危险作业申请未通过！");

                    //db.executeUpdate(sql);
                }
                else {                           //同意
                    if(flag.equals("true")||flag.equals("1"))     //当前审批人为审批人序列的最后一个
                    {
                        if(ApprovalFinished.equals("2"))       //结束
                        {
                            sql = "update DangerTaskApplicationTable_T set  State='2' where TaskID='" + TaskID + "';" +
                                    "update DangerTaskApprovalTable_T set ApproveResult='"+ApprovalResult+"',ApproveTime='" + ApproveTime+ "',Finished='"+ApprovalFinished+"',ApproveSuggestion='"+ApproveSuggestion+"',ApproveSignature='"+ApproveSignature+"' where ApprovalID='" + ApprovalID + "';" ;
                            Collection<String> alias = new ArrayList<>();
                            Collection<String> tags= new ArrayList<>();
                            alias.add( Applicant);
                            PushResult TST= JiguangPush.push(alias,null,"您有一条危险作业申请已通过！");
                        }
                        else {                                 //未结束，并选择了下一审批部门
                            ApprovalList+=","+ApproverList;
                            sql = "update DangerTaskApplicationTable_T set  ApproverList='" + ApprovalList + "' where TaskID='" + TaskID + "';" +
                                    "update DangerTaskApprovalTable_T set ApproveResult='"+ApprovalResult+"',ApproveTime='" + ApproveTime+ "',Finished='1',ApproveSuggestion='"+ApproveSuggestion+"',ApproveSignature='"+ApproveSignature+"' where ApprovalID='" + ApprovalID + "';" +
                                    "insert into DangerTaskApprovalTable_T (TaskID,SubmitterID,ApproveInstitution,ApproveSuggestion,ApproveResult,ApproveName,ApproveSignature,ApproveTime,Finished) values ('" + TaskID + "','" + Approver + "','" + ApproverList.split(",")[0] + "','','','" + ApproverList.split(",")[0] + "','','1970-01-01','0');";
                            Collection<String> alias = new ArrayList<>();
                            Collection<String> tags= new ArrayList<>();
                            alias.add( ApproverList.split(",")[0]);
                            PushResult TST= JiguangPush.push(alias,null,"您有一条新的审批待处理！");
                            // db.executeUpdate(sql);
                        }
                    }
                    else{                       //当前审批人不是审批人序列的最后一个
                        String[] list=ApprovalList.split(",");
                        String nextApprover="";
                        for(int i=0;i<list.length;i++)
                        {
                            if(list[i].equals(Approver))
                            {
                                nextApprover=list[i+1];
                                // break;
                            }
                        }
                        String SubmitterID=db.getString("select SubmitterID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "'","SubmitterID");
                        sql = "update DangerTaskApprovalTable_T set ApproveResult='"+ApprovalResult+"',ApproveTime='" + ApproveTime+ "',Finished='1',ApproveSuggestion='"+ApproveSuggestion+"',ApproveSignature='"+ApproveSignature+"' where ApprovalID='" + ApprovalID + "';" +
                                "insert into DangerTaskApprovalTable_T (TaskID,SubmitterID,ApproveInstitution,ApproveSuggestion,ApproveResult,ApproveName,ApproveSignature,ApproveTime,Finished) values ('" + TaskID + "','" + SubmitterID + "','" + nextApprover + "','','','" + nextApprover + "','','1970-01-01','0');";
                        Collection<String> alias = new ArrayList<>();
                        Collection<String> tags= new ArrayList<>();
                        alias.add( nextApprover);
                        PushResult TST= JiguangPush.push(alias,null,"您有一条新的审批待处理！");
                        //db.executeUpdate(sql);
                    }

                }
            } else {             //    被退回后再次审批  Finished=3
                ApprovalList=ApprovalList.substring(0, ApprovalList.lastIndexOf(Approver)+Approver.length());
                if (ApprovalResult.equals("0"))    //不同意
                {
                    //ApprovalList.substring(0, ApprovalList.indexOf(Approver));
                    sql = "update DangerTaskApplicationTable_T set  State='3',ApproverList='" + ApprovalList + "' where TaskID='" + TaskID + "';" +
                            "update DangerTaskApprovalTable_T set ApproveResult='"+ApprovalResult+"',ApproveTime='" + ApproveTime+ "',Finished='"+ApprovalFinished+"',ApproveSuggestion='"+ApproveSuggestion+"',ApproveSignature='"+ApproveSignature+"' where ApprovalID='" + ApprovalID + "';" +
                            "delete from DangerTaskApprovalTable_T where TaskID ='"+TaskID+"' and SubmitterID='" + Approver + "'";
                    Collection<String> alias = new ArrayList<>();
                    Collection<String> tags= new ArrayList<>();
                    alias.add( Applicant);
                    PushResult TST= JiguangPush.push(alias,null,"您有一条危险作业申请未通过！");
                }
                else {                           //同意
                    if(ApprovalFinished.equals("2"))       //结束
                    {
                        //ApprovalList.substring(0, ApprovalList.indexOf(Approver));
                        sql = "update DangerTaskApplicationTable_T set  State='2',ApproverList='" + ApprovalList + "' where TaskID='" + TaskID + "';" +
                                "update DangerTaskApprovalTable_T set ApproveResult='"+ApprovalResult+"',ApproveTime='" + ApproveTime+ "',Finished='"+ApprovalFinished+"',ApproveSuggestion='"+ApproveSuggestion+"',ApproveSignature='"+ApproveSignature+"' where ApprovalID='" + ApprovalID + "';" +
                                "delete from DangerTaskApprovalTable_T where TaskID ='"+TaskID+"' and SubmitterID='" + Approver + "'";
                        //db.executeUpdate(sql);
                        Collection<String> alias = new ArrayList<>();
                        Collection<String> tags= new ArrayList<>();
                        alias.add( Applicant);
                        PushResult TST= JiguangPush.push(alias,null,"您有一条危险作业申请已通过！");
                    }
                    else {                                 //未结束，并选择了下一审批部门
                        //ApprovalList.substring(0, ApprovalList.indexOf(Approver));
                        ApprovalList+=","+ApproverList;
                        sql = "update DangerTaskApplicationTable_T set  ApproverList='" + ApprovalList + "' where TaskID='" + TaskID + "';" +
                                "update DangerTaskApprovalTable_T set ApproveResult='"+ApprovalResult+"',ApproveTime='" + ApproveTime+ "',Finished='1',ApproveSuggestion='"+ApproveSuggestion+"',ApproveSignature='"+ApproveSignature+"' where ApprovalID='" + ApprovalID + "';" +
                                "delete from DangerTaskApprovalTable_T where TaskID ='"+TaskID+"' and SubmitterID='" + Approver + "'"+
                                "insert into DangerTaskApprovalTable_T (TaskID,SubmitterID,ApproveInstitution,ApproveSuggestion,ApproveResult,ApproveName,ApproveSignature,ApproveTime,Finished) values ('" + TaskID + "','" + Approver + "','" + ApproverList.split(",")[0] + "','','','" + ApproverList.split(",")[0] + "','','1970-01-01','0');";
                        Collection<String> alias = new ArrayList<>();
                        Collection<String> tags= new ArrayList<>();
                        alias.add( ApproverList.split(",")[0]);
                        PushResult TST= JiguangPush.push(alias,null,"您有一条新的审批待处理！");
                    }

                }
            }

            return db.executeUpdate(sql);
        } catch (Exception e) {
            //throw e;
            return 0;
        }
    }
    //获得转发界面的信息
    public Map[] getrelayPeopleList(String InstitutionNum, String usernum) throws Exception {
        String sql = "select e.EmployeeNum,RTRIM(e.EmployeeName)+'-'+RTRIM(EmployeeID) EmployeeName from Employee_t as e\n" +
                "inner join userinfo_t \n" +
                "on InstitutionNum='"+InstitutionNum+"' and e.EmployeeNum<>'"+usernum+"' and OutCompanyOrNot<>'1' and IsRetire<>'1' and e.EmployeeNum=userinfo_t.EmployeeNum";
        DBOperator db = new DBOperator(sql);
        return db.executeQuery();
    }
    //转发
    public int replayApplication(String[] ApprovalIDList, String username, String relayPeople, String ApproveSuggestion) {
        try {
            if(relayPeople.endsWith(",")){
                relayPeople=relayPeople.substring(0,relayPeople.length()-1);
            }
            DBo db = new DBo();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql="";
            int count=0;
            for(int i = 0; i<ApprovalIDList.length;i++)
            {
                String ApprovalID = ApprovalIDList[i];
                String TaskID = db.getString("select TaskID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "'", "TaskID");
                String SubmitterID = db.getString("select SubmitterID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "'", "SubmitterID");
                String Finished = db.getString("select Finished from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "'", "Finished");
                String ApproverList = db.getString("select ApproverList from DangerTaskApplicationTable_T where TaskID='" + TaskID + "'", "ApproverList");

                if (Finished.equals("0"))      // 待批时转发
                {
                    //已完成的人
                    String list=  db.getString("select stuff((select CONVERT(varchar, ','+ApproveInstitution) from DangerTaskApprovalTable_T where taskID= "+TaskID+"  for xml path('')),1,1,'')  as list","list");
                    System.out.println(list);
                    if(!ApproverList.substring(ApproverList.length()-1,ApproverList.length()).equals(","))
                    {
                        if(list.endsWith(",")){
                            list = list.substring(0,list.length()-1);
                        }
                        System.out.println(list);
                        if(ApproverList.replace(list,"").equals(""))
                        {System.out.println(relayPeople);
                            System.out.println(ApproverList);
                            ApproverList=list+","+relayPeople;
                            System.out.println(ApproverList);
                        }
                        else {
                            System.out.println(relayPeople);
                            System.out.println(ApproverList);
                            ApproverList=list+","+relayPeople+ApproverList.replace(list,"");
                            System.out.println(ApproverList);
                        }
                    }
                    else {
                        if(!list.endsWith(",")){
                            list = list+",";
                        }
                        ApproverList=list+relayPeople+ApproverList.replace(list,"");
                    }
//                    ApproverList=ApproverList.substring(0, ApproverList.indexOf(username)+username.length())+","+relayPeople+ApproverList.substring(ApproverList.indexOf(username)+username.length(),ApproverList.length());
                    sql = "update DangerTaskApplicationTable_T set ApproverList='" + ApproverList + "' where TaskID='" + TaskID + "';" +
                            "update DangerTaskApprovalTable_T set Finished='5',ApproveSuggestion='"+ApproveSuggestion+"',ApproveTime='" + dateformat.format(new Date()) + "' where ApprovalID='" + ApprovalID + "';" +
                            "insert into DangerTaskApprovalTable_T (TaskID,SubmitterID,ApproveInstitution,ApproveSuggestion,ApproveResult,ApproveName,ApproveSignature,ApproveTime,Finished) values ('" + TaskID + "','" + SubmitterID + "','" + relayPeople + "','','','" + relayPeople + "','','1970-01-01','0');";
                } else {             //    被退回时转发  Finished=3
                    ApproverList=ApproverList.substring(0, ApproverList.lastIndexOf(username)+username.length());
                    ApproverList=ApproverList+","+relayPeople;
                    sql = "update DangerTaskApplicationTable_T set ApproverList='" + ApproverList + "' where TaskID='" + TaskID + "';" +
                            "delete from DangerTaskApprovalTable_T where TaskID='" + TaskID + "' and SubmitterID='" + username + "';" +
                            "update DangerTaskApprovalTable_T set Finished='5',ApproveSuggestion='"+ApproveSuggestion+"',ApproveTime='" + dateformat.format(new Date()) + "' where ApprovalID='" + ApprovalID + "';" +
                            "insert into DangerTaskApprovalTable_T (TaskID,SubmitterID,ApproveInstitution,ApproveSuggestion,ApproveResult,ApproveName,ApproveSignature,ApproveTime,Finished) values ('" + TaskID + "','" + SubmitterID + "','" + relayPeople + "','','','" + relayPeople + "','','1970-01-01','0');";

                }
                count+=db.executeUpdate(sql);
            }

            return count==ApprovalIDList.length?1:0;
        } catch (Exception e) {
            //throw e;
            return 0;
        }
    }
    //回退
    public int returnApplication(String ApprovalID, String SubmitterID, String Applicant, String ApproveSuggestion, String Finished) {
        try {
            DBo db = new DBo();
            //String sql = "";
            String sql = "select Finished from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "'";
            Finished = db.getString(sql, "Finished");

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (Finished.equals("0"))      // 第一次退回
            {
                if (SubmitterID.equals(Applicant))   //退回给申请人
                {
                    sql = "update DangerTaskApprovalTable_T set Finished='4' , ApproveSuggestion='" + ApproveSuggestion + "',ApproveTime='" + dateformat.format(new Date()) + "' where ApprovalID='" + ApprovalID + "' ;\n" +
                            "update DangerTaskApplicationTable_T set State='4' where TaskID in (select TaskID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "')";
                } else {                              //退回给提交人
                    sql = "update DangerTaskApprovalTable_T set Finished='4' , ApproveSuggestion='" + ApproveSuggestion + "',ApproveTime='" + dateformat.format(new Date()) + "' where ApprovalID='" + ApprovalID + "' ;\n" +
                            "update DangerTaskApprovalTable_T set Finished='3' where TaskID in (select TaskID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "') and ApproveInstitution='" + SubmitterID + "'";
                }
            } else {             //    退回的退回  Finished=3
                if (SubmitterID.equals(Applicant))   //退回给申请人
                {
                    sql = "update DangerTaskApprovalTable_T set Finished='4' , ApproveSuggestion='" + ApproveSuggestion + "',ApproveTime='" + dateformat.format(new Date()) + "' where ApprovalID='" + ApprovalID + "' ;\n" +
                            "update DangerTaskApplicationTable_T set State='4' where TaskID in (select TaskID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "');" +
                            "delete from DangerTaskApprovalTable_T where TaskID in (select TaskID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "') and SubmitterID='" + SubmitterID + "'";
                } else {                              //退回给提交人
                    sql = "update DangerTaskApprovalTable_T set Finished='4' , ApproveSuggestion='" + ApproveSuggestion + "',ApproveTime='" + dateformat.format(new Date()) + "' where ApprovalID='" + ApprovalID + "' ;\n" +
                            "update DangerTaskApprovalTable_T set Finished='3' where TaskID in (select TaskID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "') and ApproveInstitution='" + SubmitterID + "';" +
                            "delete from DangerTaskApprovalTable_T where TaskID in (select TaskID from DangerTaskApprovalTable_T where ApprovalID='" + ApprovalID + "') and SubmitterID='" + SubmitterID + "'";
                }
            }

            //DBOperator db = new DBOperator(sql);
            return db.executeUpdate(sql);
        } catch (Exception e) {
            //throw e;
            return 0;
        }
    }

    //根据Activiti重新写的方法
    /////////////////////////////////////////////////////////////////////////////////////////
    //添加申请任务
    public int insertApplicationTaskActiviti(String ApplicantNum, String TaskInstitution, String ApplyingTime, String StartTime, String EndTime, String DangerTaskName, String Category, String Archived, String uploadfilename, String ApproverList,String DangerTaskLevel,String DangerTaskNum) {
        String sql = "insert into DangerTaskApplicationTable_T(DangerTaskNum,DangerTaskName,DangerTaskLevel,Category,Applicant,TaskInstitution,ApplyingTime,StartTime,EndTime,UploadFileName,State,Archived,ApproverList) values('"+DangerTaskNum+"','" + DangerTaskName + "','"+DangerTaskLevel+"','" + Category + "','" + ApplicantNum + "','" + TaskInstitution + "','" + ApplyingTime + "','" + StartTime + "','" + EndTime + "','" + uploadfilename + "','0','" + Archived + "','" + ApproverList + "')";
        DBOperator db = new DBOperator(sql);
        return db.executeUpdate();
    }

    public Map[] getAlltaskListInfoActiviti(String conditions, int pageindex, String username, String UserInstitution) throws SQLException {
        if (conditions.equals("")) {
            DBOperator dbOperator = new DBOperator("");
            return dbOperator.getApplyList(pageindex, "0", UserInstitution);
        } else {
            if (conditions.contains("TaskInstitution")) {
                DBOperator dbOperator = new DBOperator("");
                String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + "";
                return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");
            } else {

                String sql = "select InstitutionCategoryNum from institution_t where institutionNum='" + UserInstitution + "'";
                List<Map<String, Object>> list = DBUtils.query(sql);
                String InstitutionCategoryNum = list.get(0).get("InstitutionCategoryNum").toString();
                if (InstitutionCategoryNum.equals("3")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (institutionCategoryNum='3' or institutionCategoryNum='4' or institutionCategoryNum='5')";
                    return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");

                } else if (InstitutionCategoryNum.equals("4")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (TaskInstitution='" + UserInstitution + "' or (institutionCategoryNum='5' and InstitutionPrefix='" + UserInstitution + "'))";
                    return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");

                } else if (InstitutionCategoryNum.equals("5")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and TaskInstitution='" + UserInstitution + "'";
                    return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");
                } else {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (institutionCategoryNum='3' or institutionCategoryNum='4' or institutionCategoryNum='5')";
                    return dbOperator.getQueryResult(sqlQuery, pageindex, 10, "");
                }
            }

        }
    }

    public Map[] getAlltaskListActiviti(String conditions, String username, String UserInstitution) throws SQLException {
        if (conditions.equals("")) {
            DBOperator dbOperator = new DBOperator("");
            return dbOperator.getAllApplyList("0", UserInstitution);
        } else {
            if (conditions.contains("TaskInstitution")) {
                DBOperator dbOperator = new DBOperator("");
                String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + "";
                return dbOperator.executeQuery(sqlQuery);
            } else {

                String sql = "select InstitutionCategoryNum from institution_t where institutionNum='" + UserInstitution + "'";
                List<Map<String, Object>> list = DBUtils.query(sql);
                String InstitutionCategoryNum = list.get(0).get("InstitutionCategoryNum").toString();
                if (InstitutionCategoryNum.equals("3")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (institutionCategoryNum='3' or institutionCategoryNum='4' or institutionCategoryNum='5')";
                    return dbOperator.executeQuery(sqlQuery);
                } else if (InstitutionCategoryNum.equals("4")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (TaskInstitution='" + UserInstitution + "' or (institutionCategoryNum='5' and InstitutionPrefix='" + UserInstitution + "'))";
                    return dbOperator.executeQuery(sqlQuery);
                } else if (InstitutionCategoryNum.equals("5")) {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and TaskInstitution='" + UserInstitution + "'";
                    return dbOperator.executeQuery(sqlQuery);
                } else {
                    DBOperator dbOperator = new DBOperator("");
                    String sqlQuery = "select row_number() over(order by TaskID desc) as rn, * from DangerTask_View where " + conditions + " and (institutionCategoryNum='3' or institutionCategoryNum='4' or institutionCategoryNum='5')";
                    return dbOperator.executeQuery(sqlQuery);
                }
            }

        }
    }
    /**
     * 添加申请任务,在用户点击提交时开始运行实例
     * Application application:申请实体;List<UserInfo> ApproverList:申请人实体列表;String BusinessKey:表中DangerTaskId
     * String:返回启动实例id
     */
    public String insertApplicationTaskByUserList(UserInfo userInfo, DangerApplication dangerApplication, List<UserInfo> ApproverList, String BusinessKey){
        try{
            System.out.println("进入了insertApplicationTaskByUserList");
            List<String>idList = new ArrayList<>();//创建存放用户id的list,_1,_2
            List<String>nameList = new ArrayList<>();
            for(UserInfo userInfo1:ApproverList){
                idList.add("_"+userInfo1.getUserId());
                nameList.add(userInfo1.getUserName());
                System.out.println("===="+userInfo1.getUserId()+"====="+userInfo1.getUserName());
            }
            //String Applicant = "a"+application.getApplicant().toString();
            String Applicant = "_"+userInfo.getUserId().toString();
            // 创建开始
            StartEvent startEvent = new StartEvent();
            startEvent.setId("startEvent");
            startEvent.setName("startEvent");
            System.out.println("------>startEvent");
            // 创建危险作业申请
            UserTask applyTask = new UserTask();
            applyTask.setId(Applicant);
            applyTask.setName(userInfo.getUserName());
            applyTask.setAssignee(Applicant);
            System.out.println("------>"+userInfo.getUserName()+Applicant);
            // 创建次级审批成员
            List<UserTask> userTaskList = createUserTaskListByUserList(idList, nameList);
            //System.out.println("------>size: "+userTaskList.size());
            // 创建结束点
            EndEvent endEvent = new EndEvent();
            endEvent.setId("endEvent");
            endEvent.setName("endEvent");
            System.out.println("------>endEvent");
            System.out.println("*****创建节点成功******");
            // 创建连线: startEvent->用户申请
            SequenceFlow s1 = new SequenceFlow();
            s1.setId("s1");
            s1.setName("s1");
            s1.setSourceRef("startEvent");
            s1.setTargetRef(Applicant);
            System.out.println("StartEvent->"+Applicant);
            List<SequenceFlow> sequenceFlowList = createSequenceFlowList(Applicant, idList);
            System.out.println("*****创建连线成功*****");
            // 连接Task
            List<SequenceFlow> sequenceFlowList1 = new ArrayList<>();
            sequenceFlowList1.add(s1);
            startEvent.setOutgoingFlows(sequenceFlowList1);
            applyTask.setIncomingFlows(sequenceFlowList1);
            System.out.println("startEvent-->"+Applicant);
            int userTaskNumber = 0;
            int sequenceFlowNumber = 1;

            for(SequenceFlow sequenceFlow:sequenceFlowList){
                if(sequenceFlowNumber==1){//包含申请节点
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    applyTask.setOutgoingFlows(currentSequenceFlow);
                    userTaskList.get(userTaskNumber).setIncomingFlows(currentSequenceFlow);
                    System.out.println(Applicant+"-->"+userTaskList.get(userTaskNumber).getAssignee());
                }else if(sequenceFlowNumber==sequenceFlowList.size()){//最后一个节点
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    userTaskList.get(userTaskNumber-1).setOutgoingFlows(currentSequenceFlow);
                    endEvent.setIncomingFlows(currentSequenceFlow);
                    System.out.println(userTaskList.get(userTaskNumber-1).getAssignee()+"-->EndEvent");
                }else{
                    List<SequenceFlow> currentSequenceFlow = new ArrayList<>();
                    currentSequenceFlow.add(sequenceFlow);
                    userTaskList.get(userTaskNumber-1).setOutgoingFlows(currentSequenceFlow);
                    userTaskList.get(userTaskNumber).setIncomingFlows(currentSequenceFlow);
                    System.out.println(userTaskList.get(userTaskNumber-1).getAssignee()+"-->"+userTaskList.get(userTaskNumber).getAssignee());
                }
                userTaskNumber++;
                sequenceFlowNumber++;
            }

            System.out.println("*****进行进出设置成功*****");
            // 创建流程
            org.activiti.bpmn.model.Process process = new Process();
            process.setName("Apply");
            process.setId("Apply");
            process.addFlowElement(startEvent);
            System.out.println("------>startEvent");
            process.addFlowElement(applyTask);
            System.out.println("------>"+Applicant);
            for(UserTask userTask: userTaskList){
                process.addFlowElement(userTask);
                System.out.println("------>"+userTask.getAssignee());
            }
            process.addFlowElement(endEvent);
            System.out.println("------>endEvent");
            System.out.println("*****process添加Task节点成功*****");
            process.addFlowElement(s1);
            System.out.println("------>s"+Applicant);
            for(SequenceFlow sequenceFlow: sequenceFlowList){
                process.addFlowElement(sequenceFlow);
                System.out.println("------>"+sequenceFlow.getId());
            }
            System.out.println("*****process添加SequenceFlow成功*****");
            // 创建Bpmnmodel
            BpmnModel bpmnModel = new BpmnModel();
            bpmnModel.addProcess(process);
            System.out.println("*****开始部署*****");
            org.activiti.engine.repository.Deployment deployment = processEngine.getRepositoryService().createDeployment()
                    .name("bpmn")
                    .addBpmnModel("Apply.bpmn", bpmnModel) // 这个addBpmnModel第一个参数一定要带后缀.bpmn
                    .deploy();
            System.out.println("*****部署完成*****");
            System.out.println(deployment.getId()+" "+deployment.getName()+" "+deployment.getTenantId());
            System.out.println("==============");
            String InstanceId =  startProcessByID(deployment.getId(), BusinessKey);
            this.finishFirstTask(InstanceId, userInfo.getUserId().toString());
            return InstanceId;
        }catch (Exception e){
            return "insertApplicationTask_failed";
        }
    }
}
