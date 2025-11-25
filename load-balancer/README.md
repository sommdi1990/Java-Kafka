# Load Balancer Configuration

این لود بالانسر با استفاده از Nginx پیاده‌سازی شده و تمام سرویس‌های میکروسرویس را مدیریت می‌کند.

## ویژگی‌ها

- **Load Balancing**: استفاده از الگوریتم `least_conn` برای توزیع بار
- **Health Checks**: بررسی سلامت سرویس‌ها با `max_fails` و `fail_timeout`
- **Reverse Proxy**: مسیریابی درخواست‌ها به سرویس‌های مناسب
- **Logging**: ثبت لاگ‌های دسترسی و خطا

## پورت‌ها

لود بالانسر روی پورت **80** اجرا می‌شود و به عنوان نقطه ورود اصلی به سیستم عمل می‌کند.

## مسیرهای در دسترس

| مسیر            | سرویس             | توضیحات                                 |
|-----------------|-------------------|-----------------------------------------|
| `/`             | UI Frontend       | رابط کاربری اصلی                        |
| `/api`          | Gateway Service   | API Gateway (مسیریابی به سایر سرویس‌ها) |
| `/api/cbi`      | CBI Service       | سرویس یکپارچه‌سازی کسب‌وکار             |
| `/api/schedule` | Schedule Service  | سرویس زمان‌بندی                         |
| `/api/workflow` | Workflow Service  | سرویس مدیریت گردش کار                   |
| `/api/kafka`    | Kafka Manager     | مدیریت Kafka                            |
| `/admin`        | Spring Boot Admin | پنل مدیریت و مانیتورینگ                 |
| `/kafka-ui`     | Kafka UI          | رابط کاربری Kafka                       |
| `/prometheus`   | Prometheus        | مانیتورینگ و متریک‌ها                   |
| `/grafana`      | Grafana           | داشبوردهای مانیتورینگ                   |
| `/health`       | Load Balancer     | بررسی سلامت لود بالانسر                 |

## استفاده

### راه‌اندازی

```bash
docker-compose up -d load-balancer
```

### بررسی سلامت

```bash
curl http://localhost/health
```

### دسترسی به سرویس‌ها

پس از راه‌اندازی، می‌توانید از طریق پورت 80 به تمام سرویس‌ها دسترسی داشته باشید:

```bash
# دسترسی به UI
http://localhost/

# دسترسی به API Gateway
http://localhost/api

# دسترسی به Spring Boot Admin
http://localhost/admin

# دسترسی به Prometheus
http://localhost/prometheus

# دسترسی به Grafana
http://localhost/grafana
```

## پیکربندی

فایل `nginx.conf` شامل تنظیمات زیر است:

- **Upstream Servers**: تعریف سرویس‌های بک‌اند
- **Load Balancing Method**: `least_conn` (کمترین اتصال)
- **Health Check**: `max_fails=3` و `fail_timeout=30s`
- **Timeouts**: 60 ثانیه برای اتصال، ارسال و دریافت

## مقیاس‌پذیری

برای افزودن نمونه‌های بیشتر از یک سرویس، می‌توانید در `docker-compose.yml` از `scale` استفاده کنید:

```yaml
cbi-service:
  # ... configuration
  deploy:
    replicas: 3
```

سپس در `nginx.conf` می‌توانید چندین سرور به upstream اضافه کنید:

```nginx
upstream cbi-service {
    least_conn;
    server cbi-service:8081 max_fails=3 fail_timeout=30s;
    server cbi-service-2:8081 max_fails=3 fail_timeout=30s;
    server cbi-service-3:8081 max_fails=3 fail_timeout=30s;
}
```

## لاگ‌ها

لاگ‌های Nginx در داخل کانتینر در مسیرهای زیر ذخیره می‌شوند:

- `/var/log/nginx/access.log` - لاگ دسترسی
- `/var/log/nginx/error.log` - لاگ خطا

برای مشاهده لاگ‌ها:

```bash
docker logs load-balancer
```

## عیب‌یابی

### بررسی وضعیت سرویس

```bash
docker ps | grep load-balancer
```

### بررسی پیکربندی Nginx

```bash
docker exec load-balancer nginx -t
```

### مشاهده لاگ‌های خطا

```bash
docker logs load-balancer 2>&1 | grep error
```

