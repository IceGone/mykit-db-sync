package io.mykit.db.common.exception;

/**
 * @description 自定义异常
 */
public class MykitDbSyncException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 异常状态码
     */
    private Integer errorCode;

    /**
     * 异常信息
     */
    private String errorMessage;


    public MykitDbSyncException(Integer errorCode) {
        super("errorCode: " + errorCode);
        this.errorCode = errorCode;
    }

    public MykitDbSyncException(String errorMessage) {
        super("errorMessage: " + errorMessage);
        this.errorMessage = errorMessage;
    }

    public MykitDbSyncException(Integer errorCode, String errorMessage) {
        super("errorCode: " + errorCode + ", errorMessage: " + errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
