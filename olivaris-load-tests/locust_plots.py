import os
from locust import HttpUser, between, task

class PlotUser(HttpUser):
    wait_time = between(1, 3)

    def on_start(self):
        self.email = os.getenv("LOCUST_EMAIL", "canomelero1@gmail.com")
        self.password = os.getenv("LOCUST_PASSWORD", "Prueba123")
        self.access_token = None
        self.user_id = None
        self.login()
        self.load_current_user()

    def auth_headers(self):
        return {"Authorization": f"Bearer {self.access_token}"}

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
            self.access_token = data.get("accessToken")

            if not self.access_token:
                response.failure("login sin accessToken")

    def load_current_user(self):
        with self.client.get(
            "/api/user/me",
            headers=self.auth_headers(),
            name="/api/user/me",
            catch_response=True,
        ) as response:
            if response.status_code != 202:
                response.failure(f"/api/user/me fallo: {response.status_code} {response.text}")
                return

            data = response.json()
            self.user_id = data.get("id")
            
            if self.user_id is None:
                response.failure("/api/user/me sin id")

    @task
    def get_my_plots(self):
        if self.user_id is None:
            self.load_current_user()
            if self.user_id is None:
                return

        with self.client.get(
            f"/api/plot/user/{self.user_id}",
            headers=self.auth_headers(),
            name="/api/plot/user/:userId",
            catch_response=True,
        ) as response:
            if response.status_code != 202:
                response.failure(
                    f"/api/plot/user/{{userId}} fallo: {response.status_code} {response.text}"
                )
