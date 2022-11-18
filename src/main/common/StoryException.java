package main.common;

/**
 * @desc
 * @author JuanWoo
 * @date 2021年1月28日
 **/

public class StoryException extends Exception {
    private static final long serialVersionUID = -7360691926239520832L;

    public final static int ERROR_CODE_UNHUMAN = 40352;

    public StoryException(String errorMessage) {
        super(errorMessage);
    }
}
