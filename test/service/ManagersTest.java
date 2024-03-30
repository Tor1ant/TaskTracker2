package service;

import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Проверка класса Managers")
class ManagersTest {

    @Test
    @DisplayName("Проверка метода getDefault, который должен вернуть инициализированный инстанс TaskManagerService")
    void test_get_default_return_instant_TaskManagerService() {
        TaskManagerService taskManagerService = Managers.getDefault();
        Assertions.assertNotNull(taskManagerService);
    }

    @Test
    @DisplayName("Проверка метода getDefaultHistory, который должен вернуть инициализированный инстанс HistoryManagerService")
    void test_get_default_history_return_instant_TaskManagerService() {
        Assertions.assertEquals(Collections.EMPTY_LIST, Managers.getDefaultHistory().getHistory());
    }
}
