# CI/CD Pipeline

این سند روند استقرار خودکار پروژه Java-Kafka را توضیح می‌دهد. کلیه سناریوهای CI/CD مبتنی بر GitHub Actions طراحی شده‌اند
تا با قراردادن تغییرات روی شاخه‌ی `product`، نسخه‌ی سالم و تست‌شده به صورت خودکار روی سرور عملیاتی اجرا شود.

## Branch Strategy

- `main`: شاخه‌ی پایدار، مناسب انتشار داخلی یا staging.
- `develop`: محل ادغام Feature Branchها قبل از آماده شدن برای انتشار.
- `product`: شاخه‌ی مخصوص Production. هر Push به این شاخه، فرآیند Build، تست و استقرار روی سرور را فعال می‌کند. برای
  ایجاد شاخه: `git checkout -b product origin/main`.

## Workflows

### `.github/workflows/ci.yml`

- اجرا روی Pull Request و Push به `main`, `develop`, `feature/**`.
- مراحل: نصب Node 18، اجرای `npm ci`, lint و type-check فرانت، اجرای `mvn clean verify`, آپلود گزارش تست و خروجی `dist`
  برای بررسی PR.
- هدف: جلوگیری از ورود کد ناسالم به شاخه‌های اصلی.

### `.github/workflows/deploy-product.yml`

- تریگر: هر Push روی `product`.
- Job اول (verify): دقیقاً همان تست‌های CI.
- Job دوم (build-and-push): ساخت ایمیج Docker برای هر سرویس و Push به GHCR با تگ‌های `${GITHUB_SHA}` و `product`.
- Job سوم (deploy): اتصال SSH به سرور عملیاتی، به‌روزرسانی ریپو روی همان commit و اجرای `scripts/deploy-on-server.sh`.

## Required Secrets/Variables

| Name                | Type             | توضیح                                                   |
|---------------------|------------------|---------------------------------------------------------|
| `PROD_HOST`         | Secret           | آدرس IP یا دامنه‌ی سرور عملیاتی                         |
| `PROD_SSH_USERNAME` | Secret           | یوزر SSH                                                |
| `PROD_SSH_KEY`      | Secret           | کلید خصوصی (PEM) جهت اتصال                              |
| `PROD_SSH_PORT`     | Secret (اختیاری) | درگاه SSH (پیش‌فرض 22)                                  |
| `PROD_APP_PATH`     | Secret           | مسیر ریپوی کلون‌شده روی سرور، مثال: `/opt/java-kafka`   |
| `PROD_COMPOSE_FILE` | Secret (اختیاری) | نام فایل Compose روی سرور، پیش‌فرض `docker-compose.yml` |
| `PROD_PUBLIC_URL`   | Variable         | لینک مانیتورینگ یا گیت‌وی برای نمایش در تب Deployments  |

> برای Push کردن ایمیج‌ها به GHCR فقط به `GITHUB_TOKEN` نیاز است (در Workflow از قبل فراهم است). اگر رجیستری دیگری مد
> نظر است، `REGISTRY`, `REGISTRY_USERNAME`, `REGISTRY_PASSWORD` را به صورت Secret تعریف و در Workflow اصلاح کنید.

## Preparing the Production Server

1. نصب Docker و Docker Compose v2.
2. ایجاد کاربر محدود و افزودن به گروه Docker.
3. کلون ریپو در مسیر ثابت (مثلاً `/opt/java-kafka`) و تنظیم دسترسی‌ها:
   ```bash
   sudo mkdir -p /opt/java-kafka
   sudo chown -R deploy:deploy /opt/java-kafka
   git clone git@github.com:<ORG>/Java-Kafka.git /opt/java-kafka
   git checkout product
   ```
4. تنظیم دسترسی Pull برای سرور (Deploy Key یا PAT Read-Only).
5. ورود اولیه به رجیستری (در صورت نیاز): `echo $TOKEN | docker login ghcr.io -u <user> --password-stdin`.

## Deployment Script (`scripts/deploy-on-server.sh`)

این اسکریپت روی سرور اجرا می‌شود (با SSH توسط Workflow یا دستی):

```bash
APP_HOME=/opt/java-kafka COMPOSE_FILE=docker-compose.yml ./scripts/deploy-on-server.sh
```

کارهایی که انجام می‌دهد:

1. اجرای `docker compose pull` برای ایمیج‌هایی که از رجیستری می‌آیند (در صورت تعریف).
2. `docker compose build --pull` برای ساخت مجدد سرویس‌هایی که از سورس اجرا می‌شوند.
3. `docker compose up -d --remove-orphans` برای راه‌اندازی مجدد سرویس‌ها.
4. پاکسازی ایمیج‌های قدیمی (بیش از ۷ روز).

## Verification & Rollback

- نتیجه‌ی هر استقرار در تب **Actions → Deploy Product** و همچنین در تب **Environments → production** قابل مشاهده است.
- در صورت نیاز به بازگشت، کافی است commit قبلی را روی شاخه‌ی `product` Revert یا cherry-pick کنید؛ Workflow به صورت
  خودکار نسخه‌ی جدید را مستقر خواهد کرد.

## نکات پایانی

- قبل از Merge به `product` حتماً PR از `develop` بسازید تا تمام چک‌ها اجرا شوند.
- برای مانیتورینگ سلامت سرویس پس از استقرار، می‌توانید اسکریپت‌های Smoke Test را به Job `deploy` اضافه کنید (TODO).
- هر تغییری در ساختار Compose یا Secrets باید هم در سرور و هم در تنظیمات ریپو اعمال شود.

