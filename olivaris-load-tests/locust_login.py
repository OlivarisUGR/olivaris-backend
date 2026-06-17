import os
from locust import HttpUser, between, task

class LoginUser(HttpUser):
    wait_time = between(1, 2)

    def on_start(self):
        self.email = os.getenv("LOCUST_EMAIL", "canomelero1@gmail.com")
        self.password = os.getenv("LOCUST_PASSWORD", "Prueba123")

    @task
    def login(self):
        with self.client.post(
            "/api/auth/login",
            json={"email": self.email, "password": self.password},
            name="/api/auth/login",
            catch_response=True,
        ) as response:
            if response.status_code != 201:
                response.failure(f"login fallo: {response.status_code} {response.text}")
                return

            data = response.json()
            
            if not data.get("accessToken"):
                response.failure("login sin accessToken")
