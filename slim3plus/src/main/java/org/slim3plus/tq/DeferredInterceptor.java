package org.slim3plus.tq;


import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slim3.datastore.Datastore;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;

public class DeferredInterceptor implements MethodInterceptor {

    private static ThreadLocal<Date> scheduleDate = new ThreadLocal<>();

    public static <T> T schedule(Date date, Callable<T> caller) throws Exception {
        T value;
        try {
            scheduleDate.set(date);
            value = caller.call();
        }
        finally {
            scheduleDate.set(null);
        }
        return value;
    }


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        if (!invocation.getMethod().getReturnType().equals(TaskHandle.class)) {
            throw new RuntimeException("Deferred Method must return TaskHandle");
        }

        String methodName = invocation.getMethod().getName();
        if (!methodName.endsWith("Deferred")) {
            throw new RuntimeException(
                    "Deferred Method name must endsWith Deferred");
        }

        Method invokeMethod = invocation
                .getMethod()
                .getDeclaringClass()
                .getMethod(
                        methodName.substring(0, methodName.length()
                                - "Deferred".length()),
                        invocation.getMethod().getParameterTypes());

        Deferred deferred = invocation.getMethod()
                .getAnnotation(Deferred.class);

        Queue queue;
        if (deferred.queueName().isEmpty()) {
            queue = QueueFactory.getDefaultQueue();
        } else {
            queue = QueueFactory.getQueue(deferred.queueName());

        }

        final TaskOptions taskOptions = TaskOptions.Builder.withDefaults();

        if (deferred.headerOption()) {
            taskOptions
                    .header("_class", invokeMethod.getDeclaringClass().getName());
            taskOptions.header("_method", invokeMethod.toGenericString());
            taskOptions.header("_arguments",
                    Arrays.toString(invocation.getArguments()));
        }

        if (scheduleDate.get() != null) {
            taskOptions.etaMillis(scheduleDate.get().getTime());
        } else if (0 < deferred.randomCountdownMillis()) {
            if (0 < deferred.countdownMillis()) {
                taskOptions.countdownMillis(deferred.countdownMillis()
                        + (long) (deferred.randomCountdownMillis() * Math
                        .random()));
            } else {
                taskOptions.countdownMillis((long) (deferred
                        .randomCountdownMillis() * Math.random()));
            }
        } else if (0 < deferred.countdownMillis()) {
            taskOptions.countdownMillis(deferred.countdownMillis());
        } else if (0 < deferred.etaMillis()) {
            taskOptions.etaMillis(deferred.etaMillis());
        }
        taskOptions.payload(new InvokeDeferredTask(invokeMethod, invocation
                .getArguments()));

        TaskHandle taskHandle;
        Transaction currentTx = Datastore.getCurrentTransaction();

        if (deferred.tx() && currentTx != null && currentTx.isActive()) {
            taskHandle = queue.add(currentTx, taskOptions);
        } else {
            taskHandle = queue.add(null, taskOptions);
        }

        return taskHandle;
    }

}
