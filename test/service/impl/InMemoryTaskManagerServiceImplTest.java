package service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Тестирование класса InMemoryTaskManagerServiceImpl")
class InMemoryTaskManagerServiceImplTest extends TaskManagerTest<InMemoryTaskManagerServiceImpl> {

    @BeforeEach
    void setUp() {
        super.taskManagerService = new InMemoryTaskManagerServiceImpl();
    }
}