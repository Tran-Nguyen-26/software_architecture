import http from 'k6/http';
import { check, sleep } from 'k6';

// Cấu hình 50 request đồng thời
export const options = {
    vus: 50,           // 50 Virtual Users
    duration: '5s',    // chạy 5 giây
};

export default function () {
    const url = 'http://localhost:8080/notification';
    const params = {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxdW9jZGluaDEyMzQiLCJpYXQiOjE3NjMxMzUyMTUsImV4cCI6MTc2MzE1MzIxNX0.v_vxKYZr0zHV-sz5FJUTGnwv4_yyymeGzNBwz5YWLdE', // Thay bằng token thật
        },
    };

    const payload = {
        email: 'nguyendinhquoc2kk5@gmail.com'  // Thay bằng email cần test
    };

    // Gửi POST request dạng x-www-form-urlencoded
    let res = http.post(url, payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    // sleep(0.1); // nghỉ 100ms giữa các request của cùng VU
}
