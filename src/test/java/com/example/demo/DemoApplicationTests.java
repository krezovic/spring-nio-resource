package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileSystemUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {
  @Autowired private WebTestClient client;

  @Test
  void test() {
    var result = client.get().uri("/test").exchange().returnResult(DataBuffer.class);
    var out = new ByteArrayOutputStream();
    DataBufferUtils.write(result.getResponseBody(), out).blockLast();
    assertThat(out.toString(StandardCharsets.UTF_8)).isEqualTo("This is an example TXT file");
  }

  @Test
  void testAsync() {
    var result = client.get().uri("/test-async").exchange().returnResult(DataBuffer.class);
    var out = new ByteArrayOutputStream();
    DataBufferUtils.write(result.getResponseBody(), out).blockLast();
    assertThat(out.toString(StandardCharsets.UTF_8)).isEqualTo("This is an example TXT file");
  }

  @Test
  @SneakyThrows
  void testCopy() {
    var tgt = Path.of(".");
    FileSystemUtils.copyRecursively(DemoRestController.fs.getPath("/"), tgt);
  }
}
