package com.tiact.websocket.entity;



import com.tiact.websocket.utils.CommonUtils;

import java.util.HashMap;

/**
 *返回数据
 *
 * @author Tia_ct
 */
public class TiaResult extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "msg";

    /** 数据对象 */
    public static final String DATA_TAG = "data";

    /** 状态码 成功 */
    public static final Integer CODE_SUCCESS = 200;

    /** 状态码 错误 */
    public static final Integer CODE_ERROR = 500;

    /**
     * 初始化一个新创建的 TiaResult 对象，使其表示一个空消息。
     */
    public TiaResult()
    {
    }

    /**
     * 初始化一个新创建的 TiaResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public TiaResult(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 TiaResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public TiaResult(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (CommonUtils.isNotEmpty(data))
        {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static TiaResult success()
    {
        return TiaResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static TiaResult success(Object data)
    {
        return TiaResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static TiaResult success(String msg)
    {
        return TiaResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static TiaResult success(String msg, Object data)
    {
        return new TiaResult(CODE_SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static TiaResult error()
    {
        return TiaResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static TiaResult error(String msg)
    {
        return TiaResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static TiaResult error(String msg, Object data)
    {
        return new TiaResult(CODE_ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static TiaResult error(int code, String msg)
    {
        return new TiaResult(code, msg, null);
    }

}
