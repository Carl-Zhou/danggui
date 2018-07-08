package com.danggui.common.log4j;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

/**
 * �������Զ�����־�ļ�׷���࣬ʹ��־�ּ���ӡ����ֻ�ж��Ƿ���ȣ�������ʹ������<br>
 * @author GongXings
 * @date 2018��7��8��
 */
public class GradeLogDailyRollingFileAppender extends DailyRollingFileAppender {
    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        //ֻ�ж��Ƿ���ȣ������ж����ȼ�
        return this.getThreshold().equals(priority);
    }
}