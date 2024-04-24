package service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import model.Task;
import service.HistoryManagerService;

public class InMemoryHistoryManagerService implements HistoryManagerService {

    private final LinkedHistoryHashMap browsingHistory = new LinkedHistoryHashMap();

    @Override
    public List<Task> getHistory() {
        return browsingHistory.getHistory();
    }

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }
        browsingHistory.removeNode(task.getId());
        browsingHistory.linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (browsingHistory.containsKey(id)) {
            browsingHistory.removeNode(id);
        }
    }

    private static class LinkedHistoryHashMap {

        private final Map<Integer, Node> historyMap = new HashMap<>();

        private Node head;
        private Node tail;

        private void linkLast(Task task) {
            Node taskNode = new Node(task);
            if (head != null) {
                taskNode.prevNode = tail;
                tail.nextNode = taskNode;
            } else {
                head = taskNode;
            }
            tail = taskNode;
            historyMap.put(task.getId(), taskNode);
        }

        public List<Task> getHistory() {
            if (tail == null) {
                return Collections.emptyList();
            }
            List<Task> history = Stream.iterate(tail, node -> node.prevNode != null, Node::getPrevNode)
                    .map(node -> node.task)
                    .collect(Collectors.toList());
            history.add(head.task);
            return history;
        }

        public void removeNode(int taskId) {
            Node nodeForRemove = historyMap.get(taskId);
            if (nodeForRemove == null) {
                return;
            }
            if (nodeForRemove.prevNode == null && nodeForRemove.nextNode == null) {
                head = null;
                tail = null;
            } else if (nodeForRemove.prevNode == null) {
                head = nodeForRemove.nextNode;
                head.prevNode = null;
            } else if (nodeForRemove.nextNode == null) {
                tail = nodeForRemove.prevNode;
                tail.nextNode = null;
            } else {
                nodeForRemove.prevNode.nextNode = nodeForRemove.nextNode;
                nodeForRemove.nextNode.prevNode = nodeForRemove.prevNode;
            }
        }

        public boolean containsKey(int taskId) {
            return historyMap.containsKey(taskId);
        }

        private static class Node {

            private Node nextNode;
            private final Task task;
            private Node prevNode;

            public Node(Task thisTask) {
                this.task = thisTask;
            }

            public Node getPrevNode() {
                return prevNode;
            }
        }
    }
}
