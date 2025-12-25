import http from "k6/http";
import { check } from "k6";

export const options = {
  scenarios: {
    steady: {
      executor: "constant-vus",
      vus: 2,
      duration: "3m",
      gracefulStop: "10m",
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<600000"],
  },
};

const BASE_URL = "http://localhost:8080";
const TOKEN =
  "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxdW9jZGluaDEyMzQiLCJpYXQiOjE3NjYyODYyMzcsImV4cCI6MTc2NjMwNDIzN30.zjHBp_UnA7jATxVaOYVmyKN-ibsywHSfNNn-10Jb_QE";

let printed = 0;

export default function () {
  const url = `${BASE_URL}/campaign/direct-test-50`;

  const payload = JSON.stringify({
    subject: "k6 steady load",
    content: "hello",
    username: "mock",
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${TOKEN}`, // ⭐ quan trọng
    },
    timeout: "15m",
  };

  const res = http.post(url, payload, params);

  const ok = check(res, {
    "status is 200": (r) => r.status === 200,
  });

  if (!ok && printed < 5) {
    printed++;
    console.error(
      `FAIL status=${res.status} body=${(res.body || "").slice(0, 200)}`
    );
  }
}
