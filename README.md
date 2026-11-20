# Workflow execution across Spring services

A multi-module Java project combines a workflow interpreter, Kafka listeners and an HTTP interface for Kafka administration. Workflow definitions carry step names, types and parameters in JSON.

The workflow engine reads the steps of a definition and branches on their types. The CBI service registers consumers for service-calls and graphql-calls. Separately, the Kafka controller exposes topic and consumer-group operations. Reviewing these three boundaries makes it possible to distinguish interpretation, message handling and broker administration.

- [WorkflowEngine.java](workflow-service/src/main/java/com/kafka/workflow/service/WorkflowEngine.java)
- [CbiKafkaListener.java](cbi-service/src/main/java/com/kafka/cbi/service/CbiKafkaListener.java)
- [KafkaController.java](kafka-manager/src/main/java/com/kafka/kafka/controller/KafkaController.java)

## مسیر اجرای گردش کار

تعریف گردش کار، مرحله‌ها را در JSON نگه می‌دارد. موتور اجرا نام و نوع هر مرحله را می‌خواند و بسته به نوع آن، پارامترهایی مثل نام سرویس، زمان‌بندی یا متن اعلان را استخراج می‌کند. مصرف‌کنندهٔ CBI برای فراخوانی سرویس و GraphQL دو listener جدا دارد.

## License

The original README identifies this project as MIT licensed.
