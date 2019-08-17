/*
 * Copyright 2018 fastjdbc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fastjdbc.test.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainTestExecutor {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
//        executorService.execute(new InsertTestThread());
//        executorService.execute(new UpdateTestThread());
//        executorService.execute(new DeleteTestThread());
//        executorService.execute(new SelectTestThread());
        executorService.shutdown();
    }

}
