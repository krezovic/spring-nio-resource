package com.example.demo;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class DemoRestController implements SmartInitializingSingleton {
  private final ResourceLoader resourceLoader;
  public static FileSystem fs;

  @Override
  @SneakyThrows
  public void afterSingletonsInstantiated() {
    var resource = resourceLoader.getResource("classpath:example.zip");
      DemoRestController.fs = FileSystems.newFileSystem(URI.create("jar:" + resource.getURI()), Map.of());
  }

  @SneakyThrows
  @ResponseBody
  @GetMapping(value = "test", produces = MediaType.TEXT_PLAIN_VALUE)
  public Resource test() {
    return new FileSystemResource(fs.getPath("example.txt"));
  }

  @SneakyThrows
  @ResponseBody
  @GetMapping(value = "test-async", produces = MediaType.TEXT_PLAIN_VALUE)
  public Flux<DataBuffer> testAsync(ServerWebExchange exchange) {
    var response = exchange.getResponse();

    return DataBufferUtils.readByteChannel(
        () -> Files.newByteChannel(fs.getPath("example.txt"), StandardOpenOption.READ),
        response.bufferFactory(),
        8192);
  }
}
