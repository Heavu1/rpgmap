/*
 * Copyright 2021 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */

import java.util.Stack;

/**
 * @author Zhenghy
 * @date 2021/7/29 16:50
 */
interface undostk {
    Stack<Object> undo_iconstk1 = new Stack<>();
    Stack<Object> undo_iconstk2 = new Stack<>();
    Stack<Object> undo_posstk1 = new Stack<>();
    Stack<Object> undo_posstk2 = new Stack<>();

}
