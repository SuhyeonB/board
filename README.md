## Project
SNS-like Board (Spring Boot)

---

## Purpose
- Spring Boot 기반 백엔드 아키텍처 학습
- RESTful API 설계 및 구현
- 인증/인가 및 도메인 구조 설계 경험
- 성능 최적화 및 트래픽 대응 설계 학습

---

## Stage

- **Stage 1**: MVP Board (게시글 / 댓글 / 좋아요 / 북마크)
- **Stage 2**: 인증 / 인가 (Spring Security + JWT)
- **Stage 3**: 성능 최적화 (Pagination / N+1 / Index)
- **Stage 4**: 확장 기능 (예정)

---

## Performance Optimization Report

게시글 목록 조회 API(`/api/posts`)를 대상으로  
3단계 성능 최적화를 수행하고 JMeter로 성능을 측정하였다.

---

### Test Environment

| Item | Value |
|------|-------|
| Tool | JMeter 5.6.3 |
| Threads | 5 |
| Ramp-up | 5 sec |
| Loop | 50 |
| Samples | 250 |
| DB | MySQL |
| Sort | createdAt DESC |

---

## Optimization Steps

### 1. Pagination 적용

전체 조회 → Page 기반 조회로 변경

```java
Page<Post> posts = postRepository.findAll(pageable);
```

### 2. N+1 문제 해결
작성자 조회 시 발생하던 N+1 문제 해결

Before
```sql
post.getUser().getNickname()
```

After(DTO Projection)
```sql
select new PostResponseDto(...)
from Post p
join p.user u
```

### 3. 댓글/좋아요 Count 집계 최적화
Before (N+1)
```sql
for (Post post : posts) {
commentRepository.countByPostId(post.getId());
}
```

After (IN + GROUP BY)
```sql
where post_id in (...)
group by post_id
```

### 4. Index 적용
```sql
CREATE INDEX idx_comments_post_id_deleted_at 
ON comments (post_id, deleted_at);

CREATE INDEX idx_likes_post_id
ON likes (post_id);

CREATE INDEX idx_posts_deleted_at_created_at
ON posts (deleted_at, created_at);

CREATE INDEX idx_bookmark_user_id
ON bookmark (user_id);
```

## Performance Result

### Count 포함 최적화 전

| Metric | Value |
|--------|-------|
| Average | 358 ms |
| 95% Line | 483 ms |
| Throughput | 12.0 / sec |

---

### Index 적용 후

| Metric | Value |
|--------|-------|
| Average | 95 ms |
| 95% Line | 239 ms |
| Throughput | 32.2 / sec |

---

### Improvement

| Metric | Result |
|--------|--------|
| Average | ▼ 73% 감소 |
| 95% Line | ▼ 50% 감소 |
| Throughput | ▲ 168% 증가 |
