package com.jackson.modules.app.rabbitmq;

public class RabbitmqConstant {
    /**
     * 队列名称
     */
    public enum QueueName {
        /**
         * 工业计件算法识别程序，发送通知队列
         */
        LAND_INSPECTION_REQUEST("land_inspection_request"),
        /**
         * 工业计件算法识别程序，接收通知队列
         */
        LAND_INSPECTION_RESPONSE("land_inspection_response");

        private String value;
        QueueName(String value) { this.value = value; }
        public String getValue() { return value; }
    }
}
