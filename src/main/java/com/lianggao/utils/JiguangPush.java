package com.lianggao.utils;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.SMS;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


/**
 * java后台极光推送方式二：使用Java SDK
 */
@SuppressWarnings({ "deprecation", "restriction" })
public class JiguangPush {
    private static final Logger log = LoggerFactory.getLogger(JiguangPush.class);
    private static String masterSecret = "7c88abf1d2e2820314c8a41b";
    private static String appKey = "9a9849f3da48e4cf392c1577";
 //   private static final String ALERT = "推送信息";
    /**
     * 极光推送
     */
    JiguangPush(){

    }
    /**
     * 极光推送方法(采用java SDK)
     * @param alias 别名集合
     * @param tags 用户集合
     * @param alert 传递信息
     * @return PushResult
     */
    public static PushResult push(Collection<String> alias,  Collection<String> tags, String alert){
        ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, clientConfig);
        PushPayload payload = null;
        if(alias!=null&&!alias.isEmpty()){
            payload = buildPushObject_android_ios_alias_alert(alias,alert);
        }else {
            payload = buildPushObject_android_tag_alertWithTitle(tags,alert);
        }
        try {
            return jpushClient.sendPush(payload);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
            return null;
        }
    }

    /**
     * 生成极光推送对象PushPayload（采用java SDK） 针对alias推送
     * @param alias
     * @param alert
     * @return PushPayload
     */
    public static PushPayload buildPushObject_android_ios_alias_alert( Collection<String> alias
            ,String alert){
        if(alias!=null&&alert!=null){
            return PushPayload.newBuilder()
                    .setPlatform(Platform.android_ios())
                    .setSMS(SMS.content(1,10))
                    .setAudience(Audience.alias(alias))
                    .setNotification(Notification.newBuilder()
                            .addPlatformNotification(AndroidNotification.newBuilder()
                                    .addExtra("type", "infomation")
                                    .setAlert(alert)
                                    .build())
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .addExtra("type", "infomation")
                                    .setAlert(alert)
                                    .build())
                            .build())
                    .setOptions(Options.newBuilder()
                            .setApnsProduction(false)//true-推送生产环境 false-推送开发环境（测试使用参数）
                            .setTimeToLive(90)//消息在JPush服务器的失效时间（测试使用参数）
                            .build())
                    .build();
        } else if(alias==null&&alert!=null){
            return PushPayload.newBuilder()
                    .setPlatform(Platform.android_ios())
                    .setSMS(SMS.content(1,10))
                    .setNotification(Notification.newBuilder()
                            .addPlatformNotification(AndroidNotification.newBuilder()
                                    .addExtra("type", "infomation")
                                    .setAlert(alert)
                                    .build())
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .addExtra("type", "infomation")
                                    .setAlert(alert)
                                    .build())
                            .build())
                    .setOptions(Options.newBuilder()
                            .setApnsProduction(false)//true-推送生产环境 false-推送开发环境（测试使用参数）
                            .setTimeToLive(90)//消息在JPush服务器的失效时间（测试使用参数）
                            .build())
                    .build();
        } else if(alias!=null&&alert==null){
            return PushPayload.newBuilder()
                    .setPlatform(Platform.android_ios())
                    .setSMS(SMS.content(1,10))
                    .setAudience(Audience.alias(alias))
                    .setNotification(Notification.newBuilder()
                            .addPlatformNotification(AndroidNotification.newBuilder()
                                    .addExtra("type", "infomation")

                                    .build())
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .addExtra("type", "infomation")
                                    .setAlert(alert)
                                    .build())
                            .build())
                    .setOptions(Options.newBuilder()
                            .setApnsProduction(false)//true-推送生产环境 false-推送开发环境（测试使用参数）
                            .setTimeToLive(90)//消息在JPush服务器的失效时间（测试使用参数）
                            .build())
                    .build();
        }else {
            return null;
        }

    }
    /**
     * 生成极光推送对象PushPayload（采用java SDK） 针对alias推送
     * @param tags 推送目标标志
     * @param ALERT
     * @return PushPayload
     */
    public static PushPayload buildPushObject_android_tag_alertWithTitle( Collection<String> tags,String ALERT){
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setSMS(SMS.content(1,10))
                .setAudience(Audience.tag(tags))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .addExtra("type", "infomation")
                                .setAlert(ALERT)
                                .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .addExtra("type", "infomation")
                                .setAlert(ALERT)
                                .build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(false)//true-推送生产环境 false-推送开发环境（测试使用参数）
                        .setTimeToLive(90)//消息在JPush服务器的失效时间（测试使用参数）
                        .build())
                .build();
    }


}