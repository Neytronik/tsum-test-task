package ru.orlovich.service

import ru.orlovich.proto.SumHolderServiceOuterClass

/**
 * Класс предназначен для добавления обработки правил ответа
 *
 */
class RulesResponses {
    /**
     * Функция обработки реквеста для метода save в сервисе SumHolder
     *
     * */
    fun applyRulesSaveSum(request: SumHolderServiceOuterClass.SumSaveRequest): SumHolderServiceOuterClass.SumSaveResponse {

        //inner function for decomposition
        fun response(code: Int, status: String): SumHolderServiceOuterClass.SumSaveResponse {
            return SumHolderServiceOuterClass.SumSaveResponse
                .newBuilder()
                .setCode(code)
                .setStatus(status)
                .build()
        }

        // создание ответа по единственному флоу на значение суммы
        val s = request.sum
        return when {
            s > 1000 -> response(11, "Out of range")
            s == 0 -> response(10, "Zero sum operation")
            s < 0 -> response(9, "Negative sum operation")
            else -> response(0, "Sum successfully saved")
        }
    }
}