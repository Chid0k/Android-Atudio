# AI Interview Pro - Ứng dụng Luyện tập Phỏng vấn Ảo

Dưới đây là kế hoạch chi tiết để xây dựng ứng dụng luyện tập phỏng vấn tích hợp AI.

## 📌 Checklist Phát triển Dự án

### 1. Thiết lập Nền tảng & Giao diện (UI/UX)
- [ ] Thiết kế kiến trúc ứng dụng (MVVM/Clean Architecture).
- [ ] Thiết kế UI cho màn hình chính (Dashboard).
- [ ] Xây dựng màn hình hiển thị Người phỏng vấn ảo.
- [ ] Tích hợp Animation cho Avatar người phỏng vấn (Dùng Lottie hoặc Spine/Unity).
- [ ] Xây dựng các màn hình hướng dẫn phục trang và tài liệu học thuật.

### 2. Tính năng Người phỏng vấn ảo (AI Interviewer)
- [ ] Tích hợp Text-to-Speech (TTS) để AI có lời nói tự nhiên.
- [ ] Tích hợp Speech-to-Text (STT) để nhận diện câu trả lời của người dùng.
- [ ] Xây dựng logic xử lý hội thoại (Dùng Gemini API hoặc OpenAI API).
- [ ] Đồng bộ hóa hoạt ảnh (Animation) theo lời nói của AI.

### 3. Phân tích CV & Tạo câu hỏi cá nhân hóa
- [ ] Xây dựng chức năng tải lên và đọc file PDF/Word (CV).
- [ ] Sử dụng AI để trích xuất kỹ năng và kinh nghiệm từ CV.
- [ ] Thuật toán tạo bộ câu hỏi phỏng vấn dựa trên thông tin CV.

### 4. Các chế độ Phỏng vấn (Interview Modes)
- [ ] **Phỏng vấn cá nhân:** 1-1 với AI.
- [ ] **Phỏng vấn nhóm (Giả lập):** AI đóng vai nhiều người phỏng vấn với các phong cách khác nhau.
- [ ] **Tùy chọn luyện tập:** Cho phép chọn chủ đề (Technical, Soft skills, Behavior).

### 5. Hỗ trợ trong buổi phỏng vấn
- [ ] Tính năng "Gợi ý câu trả lời" (Hint): Hiển thị các ý chính khi người dùng gặp khó khăn.
- [ ] Ghi âm và lưu trữ tạm thời nội dung buổi phỏng vấn.

### 6. Đánh giá & Thống kê
- [ ] Hệ thống đánh giá kết quả (Feedback) sau khi kết thúc:
    - [ ] Chấm điểm dựa trên nội dung trả lời.
    - [ ] Nhận xét về thái độ, tốc độ nói.
- [ ] Xây dựng màn hình Thống kê:
    - [ ] Biểu đồ tiến bộ theo thời gian.
    - [ ] Số lượng buổi đã thực hiện.
    - [ ] Các kỹ năng cần cải thiện.

### 7. Kho nội dung & Hướng dẫn
- [ ] Xây dựng thư mục tài liệu học thuật (PDF/Video).
- [ ] Tích hợp module hướng dẫn phục trang (Hình ảnh, quy tắc ăn mặc chuyên nghiệp).

---

## 🚀 Công nghệ đề xuất
- **Ngôn ngữ:** Kotlin
- **AI Engine:** Google Gemini SDK / OpenAI API
- **Speech:** Google Cloud TTS & STT
- **Animation:** Lottie / Rive
- **Local Database:** Room (Lưu thống kê, lịch sử)
- **Networking:** Retrofit / Ktor
