package com.tariqweb.h2test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {
    public TestService() {
        log.info("TestService created");
    }
}
