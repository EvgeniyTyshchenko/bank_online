package ru.bankonline.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.bankonline.project.utils.exceptions.*;

import javax.mail.MessagingException;

/***
 * Класс для обработки исключений
 */
@RestControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    /***
     * Обработчик исключения, возникающего при отсутствии информации о клиенте в базе данных
     * @param e исключение CustomerMissingFromDBException
     * @return ответ с ошибкой и HTTP статусом NOT FOUND
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(CustomerMissingFromDBException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /***
     * Обработчик исключения, возникающего при невозможности создать объект
     * @param e исключение NotCreatedException
     * @return ответ с ошибкой и HTTP статусом BAD REQUEST
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotCreatedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /***
     * Обработчик исключения, возникающего при невозможности обновить объект
     * @param e исключение NotUpdatedException
     * @return ответ с ошибкой и HTTP статусом BAD REQUEST
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotUpdatedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /***
     * Обработчик исключения, возникающего при вводе данных карты отсутвующих у клиента
     * @param e исключение EnteringCardDataException
     * @return ответ с ошибкой и HTTP статусом NOT FOUND
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EnteringCardDataException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /***
     * Обработчик исключения, возникающего при неправильном использовании функционала карт
     * (в зависимости от контекста, например: карта заблокирована, но клиент хочет закрыть карту)
     * @param e исключение ClosingCardException
     * @return ответ с ошибкой и HTTP статусом BAD REQUEST
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ClosingCardException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /***
     * Обработчик исключения, возникающего при взаимодействии менеджера с клиентом,
     * который ранне был заблокирован или закрыт
     * @param e исключение CustomerBlockingException
     * @return ответ с ошибкой и HTTP статусом BAD REQUEST
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(CustomerBlockingException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Клиент заблокирован или удален! " +
                        "Для точного уточнения статуса, сделайте общий запрос по клиенту.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /***
     * Обработчик исключения, возникающего при совершении транзакции, когда на счете
     * недостаточно денежных средств
     * @param e исключение InsufficientFundsException
     * @return ответ с ошибкой и HTTP статусом BAD REQUEST
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(InsufficientFundsException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /***
     * Обработчик исключения, возникающего при вводе данных номера сберегательного счета отсутвующего у клиента
     * @param e исключение EnteringSavingsAccountDataException
     * @return ответ с ошибкой и HTTP статусом NOT FOUND
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EnteringSavingsAccountDataException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /***
     * Обработчик исключения, возникающего при неправильном использовании функционала сберегательного счета
     * (в зависимости от контекста, например: сберегательный счет закрыт, но клиент хочет пополнить
     * счет денежными средствами)
     * @param e исключение ClosingSavingsAccountException
     * @return ответ с ошибкой и HTTP статусом BAD REQUEST
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ClosingSavingsAccountException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /***
     * Обработчик исключения, возникающего при неправильном использовании аккаунта во время удалении учетной записи
     * @param e исключение CustomerBalanceNotZeroException
     * @return ответ с ошибкой и HTTP статусом CONFLICT
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(CustomerBalanceNotZeroException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /***
     * Обработчик исключения, которое может возникнуть при отправке уведомления на почту клиента
     * @param e исключение MessagingException
     * @return ответ с ошибкой и HTTP статусом INTERNAL SERVER ERROR
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(MessagingException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /***
     * Обработчик исключения, возникающего при неправильном пользовании сберегательного счета
     * @param e исключение ViolationTermsDepositException
     * @return ответ с ошибкой и HTTP статусом BAD REQUEST
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ViolationTermsDepositException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /***
     * Обработчик исключения, возникающего при создании учетной записи клиента, который уже имеется в базе данных банка
     * @param e исключение PassportDuplicateException
     * @return ответ с ошибкой и HTTP статусом CONFLICT
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PassportDuplicateException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /***
     * Обработчик исключения, возникающего при запросе в базу данных банка и отсутствия необходимой информации
     * @param e исключение NotFoundInBaseException
     * @return ответ с ошибкой и HTTP статусом NO CONTENT
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotFoundInBaseException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NO_CONTENT);
    }
}