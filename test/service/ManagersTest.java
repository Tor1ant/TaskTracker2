package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.impl.InMemoryTaskManagerServiceImpl;

@DisplayName("Проверка класса Managers")
class ManagersTest {

    @Test
    @DisplayName("Проверка метода getDefault, который должен вернуть инициализированный инстанс TaskManagerService")
    void test_get_default_return_instant_TaskManagerService() {
        Assertions.assertTrue(() -> Managers.getDefault() instanceof InMemoryTaskManagerServiceImpl);
    }
}
