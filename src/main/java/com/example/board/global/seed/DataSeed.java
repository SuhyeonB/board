package com.example.board.global.seed;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("local") // local 프로필에서만 실행
public class DataSeed implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final SeedProperties props;

    // 재현 가능한 랜덤(항상 같은 분포/결과가 나오게)
    private final Random random = new Random(42);

    public DataSeed(JdbcTemplate jdbcTemplate, SeedProperties props) {
        this.jdbcTemplate = jdbcTemplate;
        this.props = props;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!props.isEnabled()) {
            System.out.println("[SEED] disabled (app.seed.enabled=false)");
            return;
        }

        long start = System.currentTimeMillis();

        System.out.printf("[SEED] start | users=%d posts=%d comments=%d batch=%d%n",
                props.getUserCount(), props.getPostCount(), props.getCommentCount(), props.getBatchSize());

        // ⚠️ 로컬/시딩용 DB에서만!
        truncateAll();

        seedUsers(props.getUserCount(), props.getBatchSize());
        seedPosts(props.getPostCount(), props.getUserCount(), props.getBatchSize());
        seedComments(props.getCommentCount(), props.getPostCount(), props.getUserCount(), props.getBatchSize());

        long end = System.currentTimeMillis();
        System.out.printf("[SEED] end | took=%d ms%n", (end - start));
    }

    private void truncateAll() {
        // 테이블명이 다르면 여기부터 수정!
        // FK 때문에 comment -> post -> user 순서가 안전
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        jdbcTemplate.execute("TRUNCATE TABLE comments");
        jdbcTemplate.execute("TRUNCATE TABLE posts");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        System.out.println("[SEED] truncated tables");
    }

    private void seedUsers(int userCount, int batchSize) {
        // 테이블/컬럼명이 다르면 SQL 수정!
        final String sql = """
            INSERT INTO users (email, password, nickname, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW())
            """;

        int inserted = 0;
        while (inserted < userCount) {
            int size = Math.min(batchSize, userCount - inserted);
            List<Object[]> params = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                int n = inserted + i + 1;
                params.add(new Object[]{
                        "user" + n + "@example.com",
                        "{noop}password",   // 시딩용 문자열. 네 실제 보안 로직과 무관
                        "user" + n
                });
            }

            jdbcTemplate.batchUpdate(sql, params);
            inserted += size;

            if (inserted % (batchSize * 2) == 0 || inserted == userCount) {
                System.out.printf("[SEED] users %d/%d%n", inserted, userCount);
            }
        }
    }

    private void seedPosts(int postCount, int userCount, int batchSize) {
        // posts 테이블/컬럼명이 다르면 SQL 수정!
        final String sql = """
            INSERT INTO posts (user_id, title, contents, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW())
            """;

        int inserted = 0;
        while (inserted < postCount) {
            int size = Math.min(batchSize, postCount - inserted);
            List<Object[]> params = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                long idx = inserted + i + 1;
                long userId = randomId(userCount); // 1..userCount

                params.add(new Object[]{
                        userId,
                        "Post title " + idx,
                        "Post contents " + idx + " ... lorem ipsum"
                });
            }

            jdbcTemplate.batchUpdate(sql, params);
            inserted += size;

            if (inserted % (batchSize * 2) == 0 || inserted == postCount) {
                System.out.printf("[SEED] posts %d/%d%n", inserted, postCount);
            }
        }
    }

    private void seedComments(int commentCount, int postCount, int userCount, int batchSize) {
        // comments 테이블/컬럼명이 다르면 SQL 수정!
        final String sql = """
            INSERT INTO comments (post_id, user_id, contents, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW())
            """;

        int inserted = 0;
        while (inserted < commentCount) {
            int size = Math.min(batchSize, commentCount - inserted);
            List<Object[]> params = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                long idx = inserted + i + 1;
                long postId = randomId(postCount); // 1..postCount
                long userId = randomId(userCount); // 1..userCount

                params.add(new Object[]{
                        postId,
                        userId,
                        "Comment " + idx
                });
            }

            jdbcTemplate.batchUpdate(sql, params);
            inserted += size;

            if (inserted % (batchSize * 2) == 0 || inserted == commentCount) {
                System.out.printf("[SEED] comments %d/%d%n", inserted, commentCount);
            }
        }
    }

    private long randomId(int maxInclusive) {
        // 1..maxInclusive
        return 1L + random.nextInt(maxInclusive);
    }
}