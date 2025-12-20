package backend.hobbiebackend.service.impl;

import backend.hobbiebackend.model.entities.Hobby;
import backend.hobbiebackend.service.CloudinaryService;
import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        // ---- CircuitBreaker config cho test chạy nhanh ----
        "resilience4j.circuitbreaker.instances.cloudinaryDelete.sliding-window-type=COUNT_BASED",
        "resilience4j.circuitbreaker.instances.cloudinaryDelete.sliding-window-size=4",
        "resilience4j.circuitbreaker.instances.cloudinaryDelete.minimum-number-of-calls=4",
        "resilience4j.circuitbreaker.instances.cloudinaryDelete.failure-rate-threshold=50",
        "resilience4j.circuitbreaker.instances.cloudinaryDelete.wait-duration-in-open-state=200ms",
        "resilience4j.circuitbreaker.instances.cloudinaryDelete.permitted-number-of-calls-in-half-open-state=2",
        "resilience4j.circuitbreaker.instances.cloudinaryDelete.record-exceptions=java.lang.RuntimeException"
})
class CloudinaryCircuitBreakerTest {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private Cloudinary cloudinary;

    private Api apiMock;

    @BeforeEach
    void setup() {
        apiMock = mock(Api.class);
        when(cloudinary.api()).thenReturn(apiMock);
    }

    private Hobby hobbyFixture() {
        Hobby hobby = new Hobby();
        hobby.setId(1L);
        hobby.setProfileImg_id("p1");
        hobby.setGalleryImg1_id("g1");
        hobby.setGalleryImg2_id("g2");
        hobby.setGalleryImg3_id("g3");
        return hobby;
    }

    @Test
    void circuitBreaker_shouldOpen_afterFailures_andShortCircuitNextCalls() throws Exception {
        Hobby hobby = hobbyFixture();
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("cloudinaryDelete");

        // Cloudinary luôn fail -> CircuitBreaker tính failure
        when(apiMock.deleteResources(anyList(), anyMap()))
                .thenThrow(new RuntimeException("boom"));

        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());

        // Gọi đủ 4 lần để đạt minimum-number-of-calls=4
        for (int i = 0; i < 4; i++) {
            cloudinaryService.deleteHobbyImages(hobby); // fallback sẽ chạy, không throw ra ngoài
        }

        // Sau 4 failures -> failure-rate 100% -> OPEN
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());

        // Reset counter để check short-circuit rõ ràng
        clearInvocations(apiMock);

        // Khi OPEN, call tiếp theo phải bị short-circuit => không chạm Cloudinary
        cloudinaryService.deleteHobbyImages(hobby);

        verify(apiMock, times(0)).deleteResources(anyList(), anyMap());
    }

    @Test
    void circuitBreaker_shouldHalfOpen_thenClose_whenServiceRecovers() throws Exception {
        Hobby hobby = hobbyFixture();
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("cloudinaryDelete");

        // 1) Làm OPEN trước
        when(apiMock.deleteResources(anyList(), anyMap()))
                .thenThrow(new RuntimeException("boom"));

        for (int i = 0; i < 4; i++) {
            cloudinaryService.deleteHobbyImages(hobby);
        }
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());

        // 2) Chờ hết thời gian open-state để sang HALF_OPEN
        Thread.sleep(250);

        // 3) Khi sang HALF_OPEN, cho Cloudinary "hồi phục" -> không throw nữa
        reset(apiMock);
        when(cloudinary.api()).thenReturn(apiMock);
        when(apiMock.deleteResources(anyList(), anyMap()))
                .thenReturn(null); // Cloudinary API thực tế trả Map, nhưng mock null vẫn ok vì bạn không dùng result

        // Gọi 2 lần (permitted-number-of-calls-in-half-open-state=2)
        cloudinaryService.deleteHobbyImages(hobby);
        cloudinaryService.deleteHobbyImages(hobby);

        // Sau 2 calls thành công -> CLOSE lại
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());

        // Và Cloudinary đã được gọi đúng 2 lần trong half-open
        verify(apiMock, times(2)).deleteResources(anyList(), anyMap());
    }
}
