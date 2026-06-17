# Load tests with Locust

This folder contains two separate Locust scripts so you can compare metrics independently.

## 1. Login only

```bash
cd /home/jorge/Escritorio/olivaris-load-tests
pip install -r requirements.txt
LOCUST_EMAIL="user@example.com" LOCUST_PASSWORD="Password1A" locust -f locust_login.py --host https://your-backend-host
```

## 2. Login + my plots

```bash
cd /home/jorge/Escritorio/olivaris-load-tests
pip install -r requirements.txt
LOCUST_EMAIL="user@example.com" LOCUST_PASSWORD="Password1A" locust -f locust_plots.py --host https://your-backend-host
```

## Notes

- The plots endpoint requires the authenticated user to match the `userId` in the URL.
- `GET /api/user/me` is used to obtain the current user id before requesting plots.
