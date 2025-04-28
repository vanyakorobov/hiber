package org.example;
import org.example.operations.ConsolOperationType;
import org.example.operations.OperationCommandProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class OperationsConsoleListener {

    private final Scanner scanner;
    private  final Map<ConsolOperationType, OperationCommandProcessor> processorMap;

    public OperationsConsoleListener(Scanner scanner,
                                     List<OperationCommandProcessor> processorList) {
        this.scanner = scanner;
        this.processorMap = processorList
                        .stream()
                        .collect(Collectors.toMap(
                                processor -> processor.getOperationType(),
                                processor -> processor
                        ));;
    }

    public void listenUpdate() {
        while (!Thread.currentThread().isInterrupted()) {
            var operationType = listenNextOperation();
            if (operationType == null) {
                return;
            }
            processNextOperation(operationType);
        }
    }

    private ConsolOperationType listenNextOperation() {
        System.out.println("Enter operation: ");
        printAllAvailableOperations();
        System.out.println();
        while (!Thread.currentThread().isInterrupted()) {
            var nextOperation = scanner.nextLine();
            try {
                return ConsolOperationType.valueOf(nextOperation);
            } catch (IllegalArgumentException e) {
                System.out.println("no such command found");
            }
        }
        return null;
    }

    private void processNextOperation(ConsolOperationType operation){
        try {
            var processor = processorMap.get(operation);
            processor.processOperation();
        } catch (Exception e) {
            System.out.println("Error: %s: error=%s%n " + e.getMessage());
        }
    }

    private void printAllAvailableOperations() {
        processorMap.keySet()
                .forEach(it -> System.out.println(it));
    }

    public void start() {
        System.out.println("Starting operations console listener");
    }

    public void endListen() {
        System.out.println("Ending operations console listener");
    }
}
