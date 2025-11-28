package com.example.payment.application.handler;


/**
 * STRATEGY PATTERN + INTERFACE SEGREGATION PRINCIPLE
 *
 * НАЗНАЧЕНИЕ:
 * - Общий интерфейс для ВСЕХ обработчиков команд
 * - Каждый handler отвечает за ОДИН тип команды
 * - Позволяет легко добавлять новые handlers без изменения существующего кода
 *
 * @param <T> тип команды (Input)
 * @param <R> тип результата (Output)
 */
public interface PaymentCommandHandler <T,R>{
    /**
     * ОСНОВНОЙ МЕТОД - обработка команды
     *
     * @param command - команда для обработки
     * @return результат обработки
     * @throws Exception если обработка не удалась
     */
    R handle(T command);

    /**
     * ПРОВЕРКА - может ли этот handler обработать команду
     *
     * Используется PaymentProcessor для автоматического выбора handler'а
     *
     * @param command - команда для проверки
     * @return true если handler может обработать эту команду
     */
    boolean canHandle(Object command);
}
