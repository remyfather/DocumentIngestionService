package com.rag.documentingestionservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 작업의 상태를 관리하기 위해 TaskStatusService를 사용합니다.
 * 이 서비스는 작업 ID와 상태를 관리하는 ConcurrentHashMap을 사용하여 작업 상태를 저장하고 조회합니다..
 */

@Service
public class TaskStatusService {

    private final Map<String, String> taskStatusMap = new ConcurrentHashMap<>();

    public void updateTaskStatus(String taskId, String status) {
        taskStatusMap.put(taskId, status);
    }

    public String getTaskStatus(String taskId) {
        return taskStatusMap.getOrDefault(taskId, "Unknown Task ID");
    }
}
