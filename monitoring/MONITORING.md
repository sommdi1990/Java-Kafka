# Monitoring with Prometheus and Grafana

این سیستم با استفاده از Prometheus و Grafana برای مانیتورینگ و آنالیز متریک‌های سرویس‌ها پیاده‌سازی شده است.

## 🔑 اطلاعات ورود (Credentials)

### Grafana

- **Username:** `admin`
- **Password:** `admin123`
- **URL:** http://localhost:9091

### Prometheus

- **URL:** http://localhost:9090
- **Access:** بدون نیاز به ورود

---

## 📊 دسترسی‌ها (Service Access)

| Service           | URL                   | Description               |
|-------------------|-----------------------|---------------------------|
| Prometheus        | http://localhost:9090 | مرکز جمع‌آوری متریک‌ها    |
| Grafana           | http://localhost:9091 | داشبورد مانیتورینگ و تجسم |
| Spring Boot Admin | http://localhost:8080 | مانیتورینگ سرویس‌ها       |
| Kafka UI          | http://localhost:8086 | مدیریت Kafka              |
| UI Frontend       | http://localhost:3000 | رابط کاربری اصلی          |

---

## 🏗️ معماری مانیتورینگ

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Services      │    │   Prometheus    │    │    Grafana      │
│  (Micrometer)   │───►│  (Scraping)     │───►│  (Dashboards)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### فرآیند مانیتورینگ:

1. **سرویس‌ها** با استفاده از **Micrometer Prometheus** متریک‌های خود را در مسیر `/actuator/prometheus` منتشر می‌کنند
2. **Prometheus** هر 15 ثانیه یک بار به این مسیرها دسترسی می‌گیرد و داده‌ها را جمع‌آوری می‌کند
3. **Grafana** از Prometheus به عنوان منبع داده استفاده کرده و داشبوردهای تعاملی را نمایش می‌دهد

---

## 📈 متریک‌های قابل مانیتورینگ

### متریک‌های JVM

- `jvm_memory_used_bytes` - میزان استفاده از حافظه
- `jvm_memory_max_bytes` - حداکثر حافظه
- `jvm_memory_committed_bytes` - حافظه اختصاص داده شده
- `jvm_gc_pause_seconds` - زمان GC
- `jvm_threads_live` - تعداد Thread های زنده

### متریک‌های HTTP

- `http_server_requests_seconds_count` - تعداد درخواست‌ها
- `http_server_requests_seconds_sum` - مجموع زمان پاسخ
- `http_server_requests_seconds_max` - حداکثر زمان پاسخ

### متریک‌های سیستم

- `process_cpu_usage` - استفاده از CPU
- `system_cpu_usage` - استفاده از CPU سیستم
- `process_uptime_seconds` - زمان اجرا

### متریک‌های Kafka

- `spring_kafka_consumer_lag` - تأخیر مصرف کننده
- `spring_kafka_consumer_records` - تعداد رکوردهای مصرف شده

---

## 🎨 داشبوردهای Grafana

### داشبورد پیش‌فرض

- **نام:** Microservices Overview
- **مسیر:** Dashboards → Microservices → Microservices Overview
- **شامل:**
    - Memory Usage by Service
    - CPU Usage by Service
    - Service Health Status
    - HTTP Request Count

---

## 🔧 تنظیمات

### تنظیمات Prometheus

فایل تنظیمات: `monitoring/prometheus/prometheus.yml`

```yaml
global:
  scrape_interval: 15s  # فاصله زمانی جمع‌آوری متریک‌ها

scrape_configs:
  - job_name: 'spring-boot-admin'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['spring-boot-admin:8080']
```

### تنظیمات Grafana

فایل تنظیمات: `monitoring/grafana/datasources/prometheus.yml`

```yaml
datasources:
  - name: Prometheus
    type: prometheus
    url: http://prometheus:9090
    isDefault: true
```

---

## 🚀 راه‌اندازی

### راه‌اندازی کامل سیستم

```bash
# استارت همه سرویس‌ها شامل Prometheus و Grafana
docker-compose up -d

# بررسی وضعیت
docker-compose ps

# مشاهده لاگ‌ها
docker-compose logs -f prometheus grafana
```

### دسترسی به Grafana

1. مرورگر را باز کنید و به http://localhost:9091 بروید
2. با `admin / admin123` وارد شوید
3. داشبوردهای از پیش تعریف شده را مشاهده کنید

---

## 📊 Query های نمونه

### بررسی سلامت سرویس‌ها

```promql
up{service=~".*"}
```

### میانگین استفاده از CPU

```promql
rate(process_cpu_usage[5m])
```

### تعداد درخواست‌های HTTP

```promql
sum(rate(http_server_requests_seconds_count[5m])) by (service)
```

### استفاده از حافظه

```promql
jvm_memory_used_bytes / jvm_memory_max_bytes
```

---

## 🔔 اعلان‌ها (Alerts)

برای تنظیم اعلان‌ها:

### Grafana Alerts

1. در Grafana به بخش **Alerting** بروید
2. **Alert Rule** جدید ایجاد کنید
3. شرط و آستانه را تعریف کنید

### نمونه شرط:

- CPU > 80%
- Memory > 90%
- Service Down

---

## 📚 منابع بیشتر

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Micrometer Documentation](https://micrometer.io/docs)

---

**نکات امنیتی:**

- ✅ در محیط تولید حتماً رمزهای عبور را تغییر دهید
- ✅ از HTTPS استفاده کنید
- ✅ دسترسی به Prometheus و Grafana را محدود کنید
- ✅ از آتش‌دیوار مناسب استفاده کنید







