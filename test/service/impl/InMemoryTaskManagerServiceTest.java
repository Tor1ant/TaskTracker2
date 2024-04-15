package service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Тестирование класса InMemoryTaskManagerServiceImpl")
class InMemoryTaskManagerServiceTest extends TaskManagerTest<InMemoryTaskManagerService> {

    @BeforeEach
    void setUp() {
        super.taskManagerService = new InMemoryTaskManagerService();
    }
}